package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.utils.NArrayBool;

public class RootToLeavesTreeFinderMatrix<T> implements IRootToLeavesTreeFinder<T> {

	private List<T> sortedElements;
	private T root;
	private List<T> leaves;
	private List<List<List<Integer>>> chainsIndexes = new ArrayList<List<List<Integer>>>();
	private int[] arrayDimensions = null;
	private NArrayBool intersectionArray = null;
	
	public RootToLeavesTreeFinderMatrix() {
	}

	@Override
	public void input(IUpperSemiLattice<T> upperSemiLattice) {
		leaves = new ArrayList<T>(upperSemiLattice.getMinimalElements());
		for (int i = 0 ; i < leaves.size() ; i++)
			chainsIndexes.add(new ArrayList<List<Integer>>());
		root = upperSemiLattice.getRoot();
		Set<List<Integer>> rootToLeafChainsIndexes = upperSemiLattice.
		//HERE
	}

	@Override
	public Set<ITree<T>> output() {
		// TODO Auto-generated method stub
		return null;
	}

}
