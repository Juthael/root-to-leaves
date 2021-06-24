package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.utils.NArrayBool;

public class RootToLeavesTreeFinderMatrix<T> implements IRootToLeavesTreeFinder<T> {

	private Set<ITree<T>> trees;
	private List<T> sortedElements;
	private T root;
	private List<Integer> leaves;
	private List<List<List<Integer>>> chains = new ArrayList<List<List<Integer>>>();
	private int[] arrayDimensions = null;
	private NArrayBool intersectionArray = null;
	
	public RootToLeavesTreeFinderMatrix() {
	}

	@Override
	public void input(IUpperSemiLattice<T> upperSemiLattice) {
		leaves = new ArrayList<Integer>(upperSemiLattice.getMinimalElementsIndexes());
		for (int i = 0 ; i < leaves.size() ; i++)
			chains.add(new ArrayList<List<Integer>>());
		root = upperSemiLattice.getRoot();
		Set<List<Integer>> rootToLeafChains = upperSemiLattice.getMaxChainsIndexesFrom(root);
		int leafIdx;
		for (List<Integer> chain : rootToLeafChains) {
			leafIdx = leaves.indexOf(chain.get(chain.size() - 1));
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
		// TODO Auto-generated method stub
		return null;
	}
	
	private void 

}
