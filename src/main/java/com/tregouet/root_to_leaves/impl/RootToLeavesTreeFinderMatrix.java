package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.data.impl.matrix.TreeMatrix;
import com.tregouet.root_to_leaves.utils.CoordAdvancer;
import com.tregouet.root_to_leaves.utils.NArrayBool;

import fjab.poset.Poset;

public class RootToLeavesTreeFinderMatrix<T> implements IRootToLeavesTreeFinder<T> {

	private List<ITree<T>> trees;
	private List<T> sortedElements;
	private int setSize;
	private T root;
	private List<Integer> leaves;
	private List<List<List<Integer>>> listsOfChainsWithSameLeaf = new ArrayList<List<List<Integer>>>();
	private int[] arrayDimensions = null;
	private NArrayBool intersectionArray = null;
	
	public RootToLeavesTreeFinderMatrix() {
	}

	@Override
	public void input(IUpperSemiLattice<T> upperSemiLattice) {
		sortedElements = upperSemiLattice.getSortedSet();
		setSize = sortedElements.size();
		root = upperSemiLattice.getRoot();
		leaves = new ArrayList<Integer>(upperSemiLattice.getMinimalElementsIndexes());
		for (int i = 0 ; i < leaves.size() ; i++)
			listsOfChainsWithSameLeaf.add(new ArrayList<List<Integer>>());
		Set<List<Integer>> rootToLeafChains = upperSemiLattice.getMaxChainsIndexesFrom(root);
		int leafIdx;
		for (List<Integer> chain : rootToLeafChains) {
			leafIdx = leaves.indexOf(chain.get(chain.size() - 1));
			listsOfChainsWithSameLeaf.get(leafIdx).add(chain);
		}
		arrayDimensions = new int[listsOfChainsWithSameLeaf.size()];
		for (int i = 0 ; i < listsOfChainsWithSameLeaf.size() ; i ++) {
			arrayDimensions[i] = listsOfChainsWithSameLeaf.get(i).size();
		}
		intersectionArray = new NArrayBool(arrayDimensions);
		setEvaluationArray();
	}
	
	private List<ITree<T>> setTrees(){
		List<ITree<T>> trees = new ArrayList<ITree<T>>();
		int[] coords = new int[arrayDimensions.length];
		do {
			if (intersectionArray.get(coords) == false) {
				int[][] transitiveReduction = new int[setSize][setSize];
				for (int i = 0 ; i < setSize ; i++) {
					transitiveReduction[i][i] = 1;
				} 
				Integer antecedentIdx;
				Integer consequentIdx;
				for (int i = 0 ; i < coords.length ; i++) {
					Iterator<Integer> idxChainIterator;
					for (List<Integer> idxChain : listsOfChainsWithSameLeaf.get(i)) {
						idxChainIterator = idxChain.iterator();
						antecedentIdx = idxChainIterator.next();
						while (idxChainIterator.hasNext()) {
							consequentIdx = idxChainIterator.next();
							transitiveReduction[antecedentIdx][consequentIdx] = 1;
							if (idxChainIterator.hasNext())
								antecedentIdx = consequentIdx;
						}
					}
				}
				trees.add(new TreeMatrix<>(sortedElements, transitiveReduction, Poset.SKIP_CHECKS, Poset.SKIP_SORTING));
			}
		} while (true);
	}
	
	private final void setEvaluationArray() {
		for (int i = 0 ; i < listsOfChainsWithSameLeaf.size() - 1 ; i++) {
			for (int j = 0 ; j < listsOfChainsWithSameLeaf.get(i).size() ; j++) {
				for (int k = i + 1 ; k < listsOfChainsWithSameLeaf.size() ; k++) {
					for (int l = 0 ; l < listsOfChainsWithSameLeaf.get(k).size() ; l++) {
						if (!noLateIntersectionFound(
								listsOfChainsWithSameLeaf.get(i).get(j), listsOfChainsWithSameLeaf.get(k).get(l))) {
							int[] closedAreaInitial = new int[listsOfChainsWithSameLeaf.size()];
							closedAreaInitial[i] = j;
							closedAreaInitial[k] = l;
							do {
								intersectionArray.set(closedAreaInitial, true);
							}
							while (CoordAdvancer.advanceInSpecifiedArea(closedAreaInitial, arrayDimensions, i, k));
						}
					}
				}
			}
		}		
	}	
	
	private boolean noLateIntersectionFound(List<Integer> chain1, List<Integer> chain2) {
		Set<Integer> checkedElements = new HashSet<Integer>();
		Iterator<Integer> chain2Ite = chain2.iterator();
		while (chain2Ite.hasNext() && !checkedElements.add(chain2Ite.next())) {
			//traversal of common stem
		}
		//out of common stem : from now on, every common element is a late intersection
		boolean noLateIntersection = true;
		while(chain2Ite.hasNext() && noLateIntersection) {
			noLateIntersection = checkedElements.add(chain2Ite.next());
		}
		return noLateIntersection;
	}

	@Override
	public Set<ITree<T>> output() {
		// TODO Auto-generated method stub
		return null;
	}
}
