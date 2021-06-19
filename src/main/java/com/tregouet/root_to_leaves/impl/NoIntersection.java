package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.api.hyperdrive.NArrayByte;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.utils.NArrayBool;

public class NoIntersection<T> implements IRootToLeavesTreeFinder<T> {

	private final Set<ITree<T>> trees;
	private final T root;
	private final List<T> leaves;
	private final List<List<List<T>>> chains = new ArrayList<List<List<T>>>();
	private final int[] arrayDimensions;
	private final NArrayBool evaluationArray;
	
	public NoIntersection(IUpperSemiLattice<T> upperSemiLattice) {
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
		evaluationArray = new NArrayBool(arrayDimensions);
		trees = setTrees();
	}

	@Override
	public Set<ITree<T>> getRootToLeavesTrees() {
		return trees;
	}
	
	private Set<ITree<T>> setTrees() {
		return null;
	}
	
	private void closeAreaInEvaluationArray(int[] initialCoords) {
		boolean[] constants = new boolean[initialCoords.length];
		for (int i = 0 ; i < initialCoords.length ; i++) {
			if (initialCoords[i] == -1)
				initialCoords[i] = 0;
			else constants[i] = true;
		}
		while (advanceInSpecifiedArea(initialCoords, arrayDimensions, constants))
			evaluationArray.set(initialCoords, false);
	}
	
	private boolean intersectionFound(List<T> chain1, List<T> chain2) {
		Set<T> checkedElements = new HashSet<T>(chain1);
		Iterator<T> chain2Ite = chain2.iterator();
		while (chain2Ite.hasNext() && !checkedElements.add(chain2Ite.next())) {
			//traversal of common stem
		}
		//out of common stem : from now on, no common element must be found
		while(chain2Ite.hasNext() && checkedElements.add(chain2Ite.next())) {
		}
		return chain2Ite.hasNext();
	}
	
	private static final boolean advanceInSpecifiedArea(int[] coords, int[] dimensions, boolean[] constants) {
		for(int i=0;i<coords.length;++i) {
	    	if (!constants[i]) {
	    		if (++coords[i] < dimensions[i])
	    			return true;
	    		else coords[i] = 0;
	    	}
	    }
	    return false;
	}

}
