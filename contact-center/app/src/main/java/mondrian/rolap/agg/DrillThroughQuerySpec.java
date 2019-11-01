/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2005-2005 Julian Hyde
// Copyright (C) 2005-2013 Pentaho
// All Rights Reserved.
*/

package mondrian.rolap.agg;

import mondrian.olap.MondrianDef;
import mondrian.olap.Util;
import mondrian.rolap.*;
import mondrian.rolap.sql.SqlQuery;
import mondrian.util.Pair;

import java.util.*;

/**
 * Provides the information necessary to generate SQL for a drill-through
 * request.
 *
 * @author jhyde
 * @author Richard M. Emberson
 */
class DrillThroughQuerySpec extends AbstractQuerySpec {
    private final DrillThroughCellRequest request;
    private final List<StarPredicate> listOfStarPredicates;
    private final List<String> columnNames;
    private final int maxColumnNameLength;

    public DrillThroughQuerySpec(
        DrillThroughCellRequest request,
        StarPredicate starPredicateSlicer,
        boolean countOnly)
    {
        super(request.getMeasure().getStar(), countOnly);
        this.request = request;
        if (starPredicateSlicer != null) {
            this.listOfStarPredicates =
                Collections.singletonList(starPredicateSlicer);
        } else {
            this.listOfStarPredicates = Collections.emptyList();
        }
        int tmpMaxColumnNameLength =
            getStar().getSqlQueryDialect().getMaxColumnNameLength();
        if (tmpMaxColumnNameLength == 0) {
            // From java.sql.DatabaseMetaData: "a result of zero means that
            // there is no limit or the limit is not known"
            maxColumnNameLength = Integer.MAX_VALUE;
        } else {
            maxColumnNameLength = tmpMaxColumnNameLength;
        }
        this.columnNames = computeDistinctColumnNames();
    }

    private List<String> computeDistinctColumnNames() {
        final List<String> columnNames = new ArrayList<String>();
        final Set<String> columnNameSet = new HashSet<String>();

        final RolapStar.Column[] columns = getColumns();
        for (RolapStar.Column column : columns) {
            addColumnName(column, columnNames, columnNameSet);
        }

        addColumnName(request.getMeasure(), columnNames, columnNameSet);

        return columnNames;
    }

    private void addColumnName(
        final RolapStar.Column column,
        final List<String> columnNames,
        final Set<String> columnNameSet)
    {
        String columnName = makeAlias(column, columnNames, columnNameSet);
        columnNames.add(columnName);
    }

    private String makeAlias(
        final RolapStar.Column column,
        final List<String> columnNames,
        final Set<String> columnNameSet)
    {
        String columnName = column.getName();
        if (columnName != null) {
            // nothing
        } else if (column.getExpression() instanceof MondrianDef.Column) {
            columnName = ((MondrianDef.Column) column.getExpression()).name;
        } else {
            columnName = "c" + Integer.toString(columnNames.size());
        }
        // Register the column name, and if it's not unique, append numeric
        // suffixes until it is. Also make sure that it is within the
        // range allowed by this SQL dialect.
        String originalColumnName = columnName;
        if (columnName.length() > maxColumnNameLength) {
            columnName = columnName.substring(0, maxColumnNameLength);
        }
        for (int j = 0; !columnNameSet.add(columnName); j++) {
            final String suffix = "_" + Integer.toString(j);
            columnName = originalColumnName;
            if (originalColumnName.length() + suffix.length()
                > maxColumnNameLength)
            {
                columnName =
                    originalColumnName.substring(
                        0, maxColumnNameLength - suffix.length());
            }
            columnName += suffix;
        }

        return columnName;
    }

    @Override
    protected boolean isPartOfSelect(RolapStar.Column col) {
        return request.includeInSelect(col);
    }

    @Override
    protected boolean isPartOfSelect(RolapStar.Measure measure) {
        return request.includeInSelect(measure);
    }

    public int getMeasureCount() {
        return request.getDrillThroughMeasures().size() > 0
            ? request.getDrillThroughMeasures().size()
            : 1;
    }

    public RolapStar.Measure getMeasure(final int i) {
        return request.getDrillThroughMeasures().size() > 0
            ? request.getDrillThroughMeasures().get(i)
            : request.getMeasure();
    }

    public String getMeasureAlias(final int i) {
        return request.getDrillThroughMeasures().size() > 0
            ? request.getDrillThroughMeasures().get(i).getName()
            : columnNames.get(columnNames.size() - 1);
    }

    public RolapStar.Column[] getColumns() {
        return request.getConstrainedColumns();
    }

    public String getColumnAlias(final int i) {
        return columnNames.get(i);
    }

    public StarColumnPredicate getColumnPredicate(final int i) {
        final StarColumnPredicate constr = request.getValueAt(i);
        return (constr == null)
            ? LiteralStarPredicate.TRUE
            : constr;
    }

    public Pair<String, List<SqlStatement.Type>> generateSqlQuery() {
        SqlQuery sqlQuery = newSqlQuery();
        nonDistinctGenerateSql(sqlQuery);
        return sqlQuery.toSqlAndTypes();
    }

    protected void addMeasure(final int i, final SqlQuery sqlQuery) {
        RolapStar.Measure measure = getMeasure(i);

        if (!isPartOfSelect(measure)) {
            return;
        }

        Util.assertTrue(measure.getTable() == getStar().getFactTable());
        measure.getTable().addToFrom(sqlQuery, false, true);

        if (!countOnly) {
            String expr = measure.generateExprString(sqlQuery);
            sqlQuery.addSelect(expr, null, getMeasureAlias(i));
        }
    }

    protected boolean isAggregate() {
        return false;
    }

    protected boolean isOrdered() {
        return true;
    }

    protected List<StarPredicate> getPredicateList() {
        return listOfStarPredicates;
    }

    protected void extraPredicates(SqlQuery sqlQuery) {
        super.extraPredicates(sqlQuery);

        if (countOnly) {
            return;
        }
        // generate the select list
        final Set<String> columnNameSet = new HashSet<String>();
        columnNameSet.addAll(columnNames);

        List<StarPredicate> predicateList = getPredicateList();
        for (StarPredicate predicate : predicateList) {
            for (RolapStar.Column column
                : predicate.getConstrainedColumnList())
            {
                sqlQuery.addSelect(
                    column.generateExprString(sqlQuery),
                    column.getInternalType(),
                    makeAlias(column, columnNames, columnNameSet));
            }
        }
    }
}