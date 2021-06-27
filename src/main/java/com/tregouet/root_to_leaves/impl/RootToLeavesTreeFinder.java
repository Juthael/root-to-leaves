package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.data.impl.Tree;
import com.tregouet.root_to_leaves.utils.CoordAdvancer;
import com.tregouet.root_to_leaves.utils.NArrayBool;

import fjab.poset.Poset;

public class RootToLeavesTreeFinder<T> implements IRootToLeavesTreeFinder<T> {

	private List<ITree<T>> trees = new ArrayList<ITree<T>>();
	private List<T> sortedElements;
	private int setSize;
	private T root;
	private List<Integer> leaves;
	private List<List<List<Integer>>> listsOfIdxChainsWithSameLeaf = new ArrayList<List<List<Integer>>>();
	private int[] arrayDimensions = null;
	private NArrayBool intersectionArray = null;
	
	public RootToLeavesTreeFinder() {
	}

	@Override
	public void input(IUpperSemiLattice<T> upperSemiLattice) {
		sortedElements = upperSemiLattice.getSortedSet();
		setSize = sortedElements.size();
		root = upperSemiLattice.getMaximum();
		leaves = new ArrayList<Integer>(upperSemiLattice.getMinimalElementsIndexes());
		for (int i = 0 ; i < leaves.size() ; i++)
			listsOfIdxChainsWithSameLeaf.add(new ArrayList<List<Integer>>());
		Set<List<Integer>> rootToLeafChains = upperSemiLattice.getMaxChainsIndexesFrom(root);
		int leafIdx;
		for (List<Integer> chain : rootToLeafChains) {
			leafIdx = leaves.indexOf(chain.get(chain.size() - 1));
			listsOfIdxChainsWithSameLeaf.get(leafIdx).add(chain);
		}
		arrayDimensions = new int[listsOfIdxChainsWithSameLeaf.size()];
		for (int i = 0 ; i < listsOfIdxChainsWithSameLeaf.size() ; i ++) {
			arrayDimensions[i] = listsOfIdxChainsWithSameLeaf.get(i).size();
		}
		intersectionArray = new NArrayBool(arrayDimensions);
		setEvaluationArray();
		setTrees();
	}
	
	private void setTrees(){
		int[] coords = new int[arrayDimensions.length];
		do {
			if (intersectionArray.get(coords) == false) {
				int[][] transitiveReduction = new int[setSize][setSize];
				for (int i = 0 ; i < setSize ; i++) {
					transitiveReduction[i][i] = 1; 		//reflexivity
				} 
				Integer prevElemIdx;
				Integer nextElemIdx;
				for (int i = 0 ; i < coords.length ; i++) {
					Iterator<Integer> idxChainIterator;
					for (List<Integer> idxChain : listsOfIdxChainsWithSameLeaf.get(i)) {
						idxChainIterator = idxChain.iterator();
						prevElemIdx = idxChainIterator.next();
						while (idxChainIterator.hasNext()) {
							nextElemIdx = idxChainIterator.next();
							transitiveReduction[prevElemIdx][nextElemIdx] = 1;
							if (idxChainIterator.hasNext())
								prevElemIdx = nextElemIdx;
						}
					}
				}
				trees.add(new Tree<T>(sortedElements, transitiveReduction, Poset.SKIP_CHECKS, Poset.SKIP_SORTING));
			}
		} while (CoordAdvancer.advance(coords, arrayDimensions));
	}
	
	private final void setEvaluationArray() {
		for (int i = 0 ; i < listsOfIdxChainsWithSameLeaf.size() - 1 ; i++) {
			for (int j = 0 ; j < listsOfIdxChainsWithSameLeaf.get(i).size() ; j++) {
				for (int k = i + 1 ; k < listsOfIdxChainsWithSameLeaf.size() ; k++) {
					for (int l = 0 ; l < listsOfIdxChainsWithSameLeaf.get(k).size() ; l++) {
						if (!noLateIntersectionFound(
								listsOfIdxChainsWithSameLeaf.get(i).get(j), listsOfIdxChainsWithSameLeaf.get(k).get(l))) {
							int[] closedAreaInitial = new int[listsOfIdxChainsWithSameLeaf.size()];
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
	public List<ITree<T>> output() {
		return trees;
	}
}
