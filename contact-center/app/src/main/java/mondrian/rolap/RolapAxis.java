/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2005-2005 Julian Hyde
// Copyright (C) 2005-2011 Pentaho
// All Rights Reserved.
*/

package mondrian.rolap;

import mondrian.calc.TupleList;
import mondrian.olap.*;

import java.util.AbstractList;
import java.util.List;

/**
 * Implementation of the Axis interface.
 *
 * @author Richard M. Emberson
 * @author Julian Hyde
 */
public class RolapAxis implements Axis {
    private final TupleList list;

    public RolapAxis(TupleList list) {
        this.list = list;
    }

    public TupleList getTupleList() {
        return list;
    }

    public List<Position> getPositions() {
        return new PositionList(list);
    }

    public static String toString(Axis axis) {
        List<Position> pl = axis.getPositions();
        return toString(pl);
    }

    public static String toString(List<Position> pl) {
        StringBuilder buf = new StringBuilder();
        for (Position p : pl) {
            buf.append('{');
            boolean firstTime = true;
            for (Member m : p) {
                if (! firstTime) {
                    buf.append(", ");
                }
                buf.append(m.getUniqueName());
                firstTime = false;
            }
            buf.append('}');
            buf.append('\n');
        }
        return buf.toString();
    }

    /**
     * List of positions.
     */
    private static class PositionList extends AbstractList<Position> {
        private final TupleList list;

        PositionList(TupleList list) {
            this.list = list;
        }

        public boolean isEmpty() {
            // may be considerably cheaper than computing size
            return list.isEmpty();
        }

        public int size() {
            return list.size();
        }

        public Position get(int index) {
            return new PositionImpl(list, index);
        }
    }

    /**
     * Implementation of {@link Position} that reads from a given location in
     * a {@link TupleList}.
     */
    private static class PositionImpl
        extends AbstractList<Member>
        implements Position
    {
        private final TupleList tupleList;
        private final int offset;

        PositionImpl(TupleList tupleList, int offset) {
            this.tupleList = tupleList;
            this.offset = offset;
        }

        public Member get(int index) {
            return tupleList.get(index, offset);
        }

        public int size() {
            return tupleList.getArity();
        }
    }

	@Override
	public int getDataSize() {
		// TODO Auto-generated method stub
		return list.getCount();
	}
}

// End RolapAxis.java
