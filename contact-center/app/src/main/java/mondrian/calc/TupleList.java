/*
* This software is subject to the terms of the Eclipse Public License v1.0
* Agreement, available at the following URL:
* http://www.eclipse.org/legal/epl-v10.html.
* You must accept the terms of that agreement to use this software.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package mondrian.calc;

import mondrian.olap.Member;

import java.util.List;

/**
 * List of tuples.
 *
 * <h2>Design notes</h2>
 *
 * <ul>
 *
 * <li>Consider changing
 * {@link TupleCalc#evaluateTuple(mondrian.olap.Evaluator)}
 * and {@link mondrian.olap.Evaluator.NamedSetEvaluator#currentTuple()}
 * to List&lt;Member&gt;</li>
 *
 * <li>Search for potential uses of {@link TupleList#get(int, int)}</li>
 *
 * <li>Worth creating {@link TupleList}.addAll(TupleIterator)?</li>
 *
 * </ul>
 *
 * @author jhyde
 */
public interface TupleList
    extends List<List<Member>>, TupleIterable
{
    /**
     * Returns a particular column of a particular row.
     *
     * <p>Note that {@code list.get(row, column)}
     * is equivalent to {@code list.slice(column).get(row)}
     * and {@code list.get(row).get(column)}
     * but is more efficient for most implementations of TupleList.
     *
     * @param slice Column ordinal
     * @param index Row ordinal
     * @return Member at given row and column
     */
    Member get(int slice, int index);

    /**
     * Returns a list of the members at a given column.
     *
     * <p>The list is modifiable if and only if this TupleList is modifiable.
     * Adding an element to a slice will create a tuple whose members in other
     * columns are null.
     * Removing an element from a slicer will remove a tuple.
     *
     * @param column Ordinal of the member in each tuple to project
     * @return List of members
     * @throws IllegalArgumentException if column is not less than arity
     */
    List<Member> slice(int column);

    /**
     * Creates a copy of this list that has the same type and has a given
     * capacity.
     *
     * <p>If capacity is negative, populates the list. A deep copy is made,
     * so that it the contents of the list are not affected to changes to any
     * backing collections.
     *
     * @param capacity Capacity
     * @return Copy of list, empty if capacity is non-negative
     */
    TupleList cloneList(int capacity);

    void addTuple(Member... members);

    TupleList project(int[] destIndices);

    void addCurrent(TupleCursor tupleIter);

    // override, refining return type
    TupleList subList(int fromIndex, int toIndex);

    TupleList withPositionCallback(PositionCallback positionCallback);

    /**
     * Fixes the tuples of this list, so that their contents will not change
     * even if elements of the list are reordered or removed. Returns this
     * list if possible.
     *
     * @return List whose tuples are invariant if the list is sorted or filtered
     */
    TupleList fix();

    interface PositionCallback {
        void onPosition(int index);
    }
    
    void setCount(int count);
    
    int getCount();
}

// End TupleList.java
