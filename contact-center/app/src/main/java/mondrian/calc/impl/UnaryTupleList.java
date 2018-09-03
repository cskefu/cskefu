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
import mondrian.olap.Evaluator;
import mondrian.olap.Member;

import java.util.*;

/**
 * Implementation of {@link TupleList} where the tuples are unary (each tuple
 * consists of just one {@link Member}).
 *
 * <p>It is implemented as a straightforward wrapper on a backing list. You
 * can provide the backing list explicitly using the
 * {@link #UnaryTupleList(java.util.List)} constructor, and you can access the
 * backing list by calling {@link #slice}(0).
 *
 * @author jhyde
*/
public class UnaryTupleList
    extends AbstractList<List<Member>>
    implements TupleList
{
    final List<Member> list;
    private int count;
    /**
     * Creates an empty UnaryTupleList.
     */
    public UnaryTupleList() {
        this(new ArrayList<Member>());
    }

    /**
     * Creates a UnaryTupleList with a given backing list.
     *
     * @param list Backing list
     */
    public UnaryTupleList(List<Member> list) {
        this.list = list;
    }

    public Member get(int slice, int index) {
        assert slice == 0;
        return list.get(index);
    }

    @Override
    public List<Member> get(int index) {
        return Collections.singletonList(list.get(index));
    }

    @Override
    public void add(int index, List<Member> element) {
        list.add(index, element.get(0));
    }

    @Override
    public boolean add(List<Member> element) {
        return list.add(element.get(0));
    }

    public TupleList fix() {
        return this;
    }

    @Override
    public List<Member> set(int index, List<Member> element) {
        final Member member = list.set(index, element.get(0));
        return member == null
            ? null
            : Collections.singletonList(member);
    }

    @Override
    public List<Member> remove(int index) {
        final Member member = list.remove(index);
        return member == null
            ? null
            : Collections.singletonList(member);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public int size() {
        return list.size();
    }

    public int getArity() {
        return 1;
    }

    public List<Member> slice(int column) {
        return list;
    }

    public TupleList cloneList(int capacity) {
        return new UnaryTupleList(
            capacity < 0
                ? new ArrayList<Member>(list)
                : new ArrayList<Member>(capacity));
    }

    public TupleCursor tupleCursor() {
        return tupleIterator();
    }

    public TupleIterator tupleIterator() {
        return new UnaryIterator();
    }

    public final Iterator<List<Member>> iterator() {
        return tupleIterator();
    }

    public TupleList project(int[] destIndices) {
        // REVIEW: Is 0-ary valid?
        assert destIndices.length == 1;
        assert destIndices[0] == 0;
        return this;
    }

    public void addTuple(Member... members) {
        assert members.length == 1;
        list.add(members[0]);
    }

    public void addCurrent(TupleCursor tupleIter) {
        list.add(tupleIter.member(0));
    }

    @Override
    public TupleList subList(int fromIndex, int toIndex) {
        return new ListTupleList(
            1,
            list.subList(fromIndex, toIndex));
    }

    public TupleList withPositionCallback(
        final PositionCallback positionCallback)
    {
        return new UnaryTupleList(
            new AbstractList<Member>() {
                public Member get(int index) {
                    positionCallback.onPosition(index);
                    return list.get(index);
                }

                public int size() {
                    return list.size();
                }

                public Member set(int index, Member element) {
                    positionCallback.onPosition(index);
                    return list.set(index, element);
                }

                public void add(int index, Member element) {
                    positionCallback.onPosition(index);
                    list.add(index, element);
                }

                public Member remove(int index) {
                    positionCallback.onPosition(index);
                    return list.remove(index);
                }
            }
        );
    }

    /**
     * Implementation of {@link mondrian.calc.TupleIterator} for {@link UnaryTupleList}.
     * Based upon AbstractList.Itr, but with concurrent modification checking
     * removed.
     */
    private class UnaryIterator implements TupleIterator {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        public boolean hasNext() {
            return cursor != size();
        }

        public List<Member> next() {
            try {
                List<Member> next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void currentToArray(Member[] members, int offset) {
            members[offset] = list.get(lastRet);
        }

        public boolean forward() {
            if (cursor == size()) {
                return false;
            }
            lastRet = cursor++;
            return true;
        }

        public List<Member> current() {
            return get(lastRet);
        }

        public int getArity() {
            return 1;
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            try {
                UnaryTupleList.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }
                lastRet = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void setContext(Evaluator evaluator) {
            evaluator.setContext(list.get(lastRet));
        }

        public Member member(int column) {
            assert column == 0;
            return list.get(lastRet);
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

// End UnaryTupleList.java
