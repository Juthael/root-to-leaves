package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.api.hyperdrive.Coord;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.data.impl.Tree;
import com.tregouet.root_to_leaves.utils.NArrayBool;

public class RootToLeavesTreeFinder<T> implements IRootToLeavesTreeFinder<T> {

	public static final boolean advanceInSpecifiedArea(int[] coords, int[] dimensions, int constant1, int constant2) {
		for(int i=0;i<coords.length;++i) {
	    	if (i != constant1 && i != constant2) {
	    		if (++coords[i] < dimensions[i])
	    			return true;
	    		else coords[i] = 0;
	    	}
	    }
	    return false;
	}
	private Set<ITree<T>> trees;
	private T root;
	private List<T> leaves;
	private List<List<List<T>>> chains = new ArrayList<List<List<T>>>();
	private int[] arrayDimensions = null;
	
	private NArrayBool intersectionArray = null;
	
	public RootToLeavesTreeFinder(){
	}

	@Override
	public void input(IUpperSemiLattice<T> upperSemiLattice) {
		leaves = new ArrayList<T>(upperSemiLattice.getMinimalElements());
		for (int i = 0 ; i < leaves.size() ; i++)
			chains.add(new ArrayList<List<T>>());
		root = upperSemiLattice.getRoot();
		Set<List<T>> rootToLeafChains = upperSemiLattice.getMaxChainsFrom(root);
		int leafIdx;
		for (List<T> chain : rootToLeafChains) {
			leafIdx = leaves.indexOf(chain.get(chain.size() -1));
			chains.get(leafIdx).add(chain);
		}
		arrayDimensions = new int[chains.size()];
		for (int i = 0 ; i < chains.size() ; i ++) {
			arrayDimensions[i] = chains.get(i).size();
		}
		intersectionArray = new NArrayBool(arrayDimensions);
		setEvaluationArray();
		trees = setTrees();
	}
	
	@Override
	public Set<ITree<T>> output() {
		return trees;
	}
	
	private boolean noIntersectionFound(List<T> chain1, List<T> chain2) {
		Set<T> checkedElements = new HashSet<T>(chain1);
		Iterator<T> chain2Ite = chain2.iterator();
		while (chain2Ite.hasNext() && !checkedElements.add(chain2Ite.next())) {
			//traversal of common stem
		}
		//out of common stem : from now on, no common element must be found
		boolean noCommonElemFound = true;
		while(chain2Ite.hasNext() && noCommonElemFound) {
			noCommonElemFound = checkedElements.add(chain2Ite.next());
		}
		return noCommonElemFound;
	}
	
	private final void setEvaluationArray() {
		int[] chainIdxs = new int[chains.size()];
		//HERE
		try {
		for (int i = 0 ; i < chainIdxs.length - 1 ; i++) {
			for (int j = 0 ; j < chains.get(i).size() ; j++) {
				for (int k = i + 1 ; k < chainIdxs.length ; k++) {
					for (int l = 0 ; l < chains.get(k).size() ; l++) {

							if (!noIntersectionFound(chains.get(i).get(j), chains.get(k).get(l))) {
								int[] closedAreaInitial = new int[chainIdxs.length];
								closedAreaInitial[i] = j;
								closedAreaInitial[k] = l;
								do {
									intersectionArray.set(closedAreaInitial, true);
								}
								while (advanceInSpecifiedArea(closedAreaInitial, arrayDimensions, i, k));
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}		
	}
	
	
	private Set<ITree<T>> setTrees() {
		Set<ITree<T>> trees = new HashSet<ITree<T>>();
		int[] coords = new int[arrayDimensions.length];
		do {
			if (!intersectionArray.get(coords)) {
				Map<T, Set<T>> relation = new HashMap<T, Set<T>>();
				for (int i = 0 ; i < coords.length ; i++) {
					List<T> chain = chains.get(i).get(coords[i]);
					for (int j = 0 ; j < chain.size() ; j++) {
						T key = chain.get(j);
						if (!relation.containsKey(key))
							relation.put(key, new HashSet<T>());
						for (int k = j + 1 ; k < chain.size() ; k++)
							relation.get(key).add(chain.get(k));
					}
				}
				trees.add(new Tree<T>(relation));
			}
		}
		while (Coord.advance(coords, arrayDimensions));
		return trees;
	}

}
