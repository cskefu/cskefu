/*
* This software is subject to the terms of the Eclipse Public License v1.0
* Agreement, available at the following URL:
* http://www.eclipse.org/legal/epl-v10.html.
* You must accept the terms of that agreement to use this software.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package mondrian.calc.impl;

import mondrian.calc.TupleIterator;
import mondrian.calc.TupleList;
import mondrian.olap.Member;
import mondrian.olap.Util;

import java.util.*;

/**
 * Implementation of {@link mondrian.calc.TupleList} that stores tuples
 * end-to-end in a backing list.
 *
 * <pre>
 * l1: {A,B,C},{D,E,F}
 * l2: {a,b},{c,d},{e,f}
 *
 * externally looks like:
 *  [] <- {A,B,C,a,b}
 *  [] <- {A,B,C,c,d}
 *  [] <- {A,B,C,e,f}
 *  [] <- {D,E,F,a,b}
 *  [] <- {D,E,F,c,d}
 *  [] <- {D,E,F,e,d}
 *
 * but internally is:
 *  A,B,C,a,b,A,B,C,c,d,A,B,C,e,f,D,E,F,a,b,D,E,F,c,d,D,E,F,e,d
 * </pre>
 *
 * @author jhyde
 */
public class ListTupleList extends AbstractEndToEndTupleList
{
    private final List<Member> list;
    private int count;
    /**
     * Creates a ListTupleList.
     *
     * @param arity Arity
     * @param list Backing list
     */
    public ListTupleList(int arity, List<Member> list) {
        super(arity);
        this.list = list;
    }

    protected List<Member> backingList() {
        return list;
    }

    public Member get(int slice, int index) {
        return list.get(index * arity + slice);
    }

    public List<Member> get(int index) {
        final int startIndex = index * arity;
        final List<Member> list1 =
            new AbstractList<Member>() {
                public Member get(int index) {
                    return list.get(startIndex + index);
                }

                public int size() {
                    return arity;
                }
            };
        if (mutable) {
            return Util.flatList(list1);
        }
        return list1;
    }

    public void add(int index, List<Member> element) {
        assert mutable;
        list.addAll(index * arity, element);
    }

    public void addTuple(Member... members) {
        assert mutable;
        list.addAll(Arrays.asList(members));
    }

    @Override
    public void clear() {
        assert mutable;
        list.clear();
    }

    @Override
    public List<Member> remove(int index) {
        assert mutable;
        for (int i = 0, n = index * arity; i < arity; i++) {
            list.remove(n);
        }
        return null; // breach of List contract
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        assert mutable;
        list.subList(fromIndex * arity, toIndex * arity).clear();
    }

    public int size() {
        return list.size() / arity;
    }

    public List<Member> slice(final int column) {
        if (column < 0 || column >= arity) {
            throw new IllegalArgumentException();
        }
        return new AbstractList<Member>() {
            @Override
            public Member get(int index) {
                return ListTupleList.this.get(column, index);
            }

            @Override
            public int size() {
                return ListTupleList.this.size();
            }
        };
    }

    public TupleList cloneList(int capacity) {
        return new ListTupleList(
            arity,
            capacity < 0
                ? new ArrayList<Member>(list)
                : new ArrayList<Member>(capacity * arity));
    }

    public TupleIterator tupleIteratorInternal() {
        return new AbstractTupleListIterator();
    }

	@Override
	public void setCount(int count) {
		// TODO Auto-generated method stub
		this.count = count;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	
}

// End ListTupleList.java
