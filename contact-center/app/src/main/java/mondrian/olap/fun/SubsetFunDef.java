/*
* This software is subject to the terms of the Eclipse Public License v1.0
* Agreement, available at the following URL:
* http://www.eclipse.org/legal/epl-v10.html.
* You must accept the terms of that agreement to use this software.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package mondrian.olap.fun;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mondrian.calc.*;
import mondrian.calc.impl.AbstractListCalc;
import mondrian.calc.impl.DelegatingTupleList;
import mondrian.mdx.ResolvedFunCall;
import mondrian.olap.Evaluator;
import mondrian.olap.FunDef;
import mondrian.olap.Member;
import mondrian.olap.Util;
import mondrian.util.CartesianProductList;

/**
 * Definition of the <code>Subset</code> MDX function.
 *
 * @author jhyde
 * @since Mar 23, 2006
 */
class SubsetFunDef extends FunDefBase {
    static final ReflectiveMultiResolver Resolver =
        new ReflectiveMultiResolver(
            "Subset",
            "Subset(<Set>, <Start>[, <Count>])",
            "Returns a subset of elements from a set.",
            new String[] {"fxxn", "fxxnn"},
            SubsetFunDef.class);

    public SubsetFunDef(FunDef dummyFunDef) {
        super(dummyFunDef);
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final ListCalc listCalc =
            compiler.compileList(call.getArg(0));
        final IntegerCalc startCalc =
            compiler.compileInteger(call.getArg(1));
        final IntegerCalc countCalc =
            call.getArgCount() > 2
            ? compiler.compileInteger(call.getArg(2))
            : null;
        return new AbstractListCalc(
            call, new Calc[] {listCalc, startCalc, countCalc})
        {
            public TupleList evaluateList(Evaluator evaluator) {
                final int savepoint = evaluator.savepoint();
                try {
                    evaluator.setNonEmpty(false);
                    final TupleList list = listCalc.evaluateList(evaluator);
                    final int start = startCalc.evaluateInteger(evaluator);
                    int size = list.size();
                    int end;
                    if (countCalc != null) {
                        final int count = countCalc.evaluateInteger(evaluator);
                        end = start + count;
                    } else {
                        end = list.size();
                    }
                    if (end > list.size()) {
                        end = list.size();
                    }
                    if (start >= end || start < 0) {
                    	TupleList li = TupleCollections.emptyList(list.getArity());
                        li.setCount(size);
                        return li;
                    }
                    if (start == 0 && end == list.size()) {
                    	list.setCount(size);
                        return list;
                    }
                    assert 0 <= start;
                    assert start < end;
                    assert end <= list.size();
                    /***
                     * 需要特殊處理，如果有 合计，需要把合计的成员加入进来
                     */
//                    final TupleList retTList = list.subList(start, end);
//                    Map<String, Member> tempMembersMap = new HashMap<String, Member>();
//                    final List<List<Member>> tempArrayList  = new ArrayList<List<Member>>();
//                    for(List<Member> tempTupList : retTList){
//                    	for(Member levelMember : tempTupList){
//                    		for(int i=list.size()-1 ; i>0 ; i--){
//		                    	List<Member> members = list.get(i) ;
//		                    	for(Member member : members){
//		                    		Member parentMember = levelMember;
//		                    		while((parentMember = parentMember.getParentMember())!=null){
//		                    			if(parentMember.isChildOrEqualTo(member) && tempMembersMap.get(member.getName()) == null){
//		                    				tempArrayList.add(members) ;
//		                    				tempMembersMap.put(member.getName(), member) ;
//		                    				break ;
//		                    			}
//		                    		}
//		                    	}
//		                    }
//                    	}
//                    }
                    TupleList li = list.subList(start, end);
                    li.setCount(size);
                    return li;
                } finally {
                    evaluator.restore(savepoint);
                }
            }
        };
    }
}

// End SubsetFunDef.java
