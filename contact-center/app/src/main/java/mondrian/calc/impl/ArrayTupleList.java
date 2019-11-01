/*
* This software is subject to the terms of the Eclipse Public License v1.0
* Agreement, available at the following URL:
* http://www.eclipse.org/legal/epl-v10.html.
* You must accept the terms of that agreement to use this software.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package mondrian.calc.impl;

import mondrian.calc.*;
import mondrian.olap.*;

import java.util.*;

/**
 * Implementation of {@link TupleList} that stores tuples end-to-end in an
 * array.
 *
 * @author jhyde
*/
public class ArrayTupleList extends AbstractEndToEndTupleList
{
    private transient Member[] objectData;
    private int size;
    private int count;

    /**
     * Creates an empty ArrayTupleList with an initial capacity of 10 tuples.
     *
     * @param arity Arity
     */
    public ArrayTupleList(int arity) {
        this(arity, 10 * arity);
        assert arity > 1 : "Probably better to use a UnaryTupleList";
    }

    /**
     * Creates an empty ArrayTupleList.
     *
     * @param arity Arity
     * @param initialCapacity Initial capacity
     */
    public ArrayTupleList(int arity, int initialCapacity) {
        this(arity, new Member[initialCapacity * arity], 0);
    }

    private ArrayTupleList(int arity, Member[] members, int size) {
        super(arity);
        assert members.length % arity == 0;
        this.objectData = members;
        this.size = size;
    }

    @Override
    protected List<Member> backingList() {
        return new AbstractList<Member>() {
            @Override
            public Member get(int index) {
                return objectData[index];
            }

            @Override
            public int size() {
                return size * arity;
            }
        };
    }

    @Override
    public Member get(int slice, int index) {
        return objectData[index * arity + slice];
    }

    @Override
    public List<Member> get(int index) {
        final int startIndex = index * arity;
        final List<Member> list =
            new AbstractList<Member>() {
                public Member get(int index) {
                    return objectData[startIndex + index];
                }

                public int size() {
                    return arity;
                }
            };
        if (mutable) {
            return Util.flatList(list);
        }
        return list;
    }

    @Override
    public List<Member> set(int index, List<Member> element) {
        assert mutable;
        for (int i = 0, startIndex = index * arity; i < arity; i++) {
            objectData[startIndex + i] = element.get(i);
        }
        return null; // not compliant with List contract
    }

    @Override
    public void addCurrent(TupleCursor tupleIter) {
        assert mutable;
        int n = size * arity;
        ensureCapacity(n + arity);
        tupleIter.currentToArray(objectData, n);
        ++size;
    }

    public int size() {
        return size;
    }

    @Override
    public boolean add(List<Member> members) {
        assert mutable;
        if (members.size() != arity) {
            throw new IllegalArgumentException(
                "Tuple length does not match arity");
        }
        int n = size * arity;
        ensureCapacity(n + arity);
        for (int i = 0; i < members.size(); i++) {
            objectData[n++] = members.get(i);
        }
        ++size;
        return true;
    }

    @Override
    public void add(int index, List<Member> members) {
        assert mutable;
        if (members.size() != arity) {
            throw new IllegalArgumentException(
                "Tuple length does not match arity");
        }
        int n = index * arity;
        ensureCapacity((size + 1) + arity);
        System.arraycopy(objectData, n, objectData, n + arity, arity);
        for (Member member : members) {
            objectData[n++] = member;
        }
        ++size;
    }

    @Override
    public boolean addAll(int index, Collection<? extends List<Member>> c) {
        assert mutable;
        final int size1 = c.size();
        ensureCapacity(size * arity + size1 * arity);
        int n = index * arity;
        System.arraycopy(
            objectData, n, objectData, n + size1 * arity, size * arity - n);
        for (List<Member> members : c) {
            for (Member member : members) {
                objectData[n++] = member;
            }
        }
        size += size1;
        return size1 > 0;
    }

    public void addTuple(Member... members) {
        assert mutable;
        if (members.length != arity) {
            throw new IllegalArgumentException(
                "Tuple length does not match arity");
        }
        ensureCapacity(size * arity + arity);
        System.arraycopy(members, 0, objectData, size * arity, arity);
        ++size;
    }

    @Override
    public List<Member> remove(int index) {
        assert mutable;
        final int n = index * arity;
        // Strict compliance with List API:
        // List<Member> previous = get(index);
        System.arraycopy(objectData, n + arity, objectData, n, arity);
        --size;
        return null; // previous;
    }

    public List<Member> slice(final int column) {
        if (column < 0 || column >= arity) {
            throw new IllegalArgumentException();
        }
        return new AbstractList<Member>() {
            @Override
            public Member get(int index) {
                return objectData[index * arity + column];
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    public TupleList cloneList(int capacity) {
        if (capacity < 0) {
            // copy of this list with the same contents
            return new ArrayTupleList(arity, objectData.clone(), size());
        } else {
            // empty copy of this list with given capacity
            return new ArrayTupleList(arity, capacity);
        }
    }

    public TupleIterator tupleIteratorInternal() {
        // Improve the base class implementation of setContext. It is cheaper
        // to call evaluator.setContext several times than to create a
        // temporary list or array.
        return new AbstractTupleListIterator() {
            public void setContext(Evaluator evaluator) {
                for (int i = 0, x = lastRet * arity; i < arity; i++) {
                    evaluator.setContext(objectData[x + i]);
                }
            }

            public Member member(int column) {
                return objectData[lastRet * arity + column];
            }

            public void currentToArray(Member[] members, int offset) {
                System.arraycopy(
                    objectData,
                    lastRet * arity,
                    members,
                    offset,
                    arity);
            }
        };
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = objectData.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            // Up to next multiple of arity.
            final int rem = newCapacity % arity;
            newCapacity += (arity - rem);
            objectData = Util.copyOf(objectData, newCapacity);
        }
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public void setCount(int count) {
		// TODO Auto-generated method stub
		this.count = count;
	}
}

// End ArrayTupleList.java
