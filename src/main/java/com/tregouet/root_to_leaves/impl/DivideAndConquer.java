package com.tregouet.root_to_leaves.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.data.impl.Tree;

public class DivideAndConquer<T> implements IRootToLeavesTreeFinder<T> {

	private static boolean nextCoord(int[] coords, int[] dimensions){
		for(int i=0;i<coords.length;++i) {
			if (++coords[i] < dimensions[i])
				return true;
			else coords[i] = 0;
	    }
	    return false;
    }
	private final IUpperSemiLattice<T> upperSemiLattice;
	private final T root;
	private final Set<T> leaves;
	private final Set<List<T>> permutationsOfSucc;
	
	private final Set<ITree<T>> trees = new HashSet<ITree<T>>();
	
	public DivideAndConquer(IUpperSemiLattice<T> upperSemiLattice) {
		this.upperSemiLattice = upperSemiLattice;
		this.root = upperSemiLattice.getMaximum();		
		this.leaves = upperSemiLattice.getMinimalElements();
		Set<T> rootSucc = upperSemiLattice.getSuccessorsOf(root);
		permutationsOfSucc = permute(new ArrayList<T>(rootSucc), rootSucc.size());
		for (List<T> perm : permutationsOfSucc)
			trees.addAll(getTreesForSpecifiedPermOfSuccessors(perm));
	}
	
	/* (non-Javadoc)
	 * @see com.tregouet.context_interpreter.context.utils.ITreeFinder#getTrees()
	 */
	@Override
	public Set<ITree<T>> getRootToLeavesTrees() {
		return trees;
	}
	
	private List<Set<T>> getLeafReachingNonOverlappingSubSemiLatt(List<T> successors) {
		List<Set<T>> leafReachingSubSemiLatt = new ArrayList<Set<T>>();
		List<Set<T>> nonOverlappingSubSemiLatts = getNonOverlappingSubSemiLatt(successors);
		for (Set<T> subSemiLatt : nonOverlappingSubSemiLatts) {
			Set<T> returnedSubSemiLatt = getUpperBoundsOfALeaf(subSemiLatt);
			//a successor may be a dead end and lead to no leaf
			if (!returnedSubSemiLatt.isEmpty())
				leafReachingSubSemiLatt.add(returnedSubSemiLatt);
		}
		return leafReachingSubSemiLatt;
	}
	
	//returns no empty set
	private List<Set<T>> getNonOverlappingSubSemiLatt(List<T> list) {
		List<Set<T>> subSemiLattices = new ArrayList<Set<T>>();
		Set<T> alreadyPickedUp = new HashSet<T>();
		for (int i = 0 ; i < list.size() ; i++) {
			Set<T> nonOverlappingSemiLatt = upperSemiLattice.getLowerSet(list.get(i));
			nonOverlappingSemiLatt.removeAll(alreadyPickedUp);
			if (i < list.size() - 1)
				alreadyPickedUp.addAll(nonOverlappingSemiLatt);
			subSemiLattices.add(nonOverlappingSemiLatt);				
		}
		return subSemiLattices;
	}
	
	private Set<ITree<T>> getTreesForSpecifiedPermOfSuccessors(List<T> permutationOfSucc) {
		Set<ITree<T>> trees = new HashSet<ITree<T>>();
		List<List<ITree<T>>> listsOfSubTrees = new ArrayList<List<ITree<T>>>();
		//no empty set
		List<Set<T>> subSemiLattices = getLeafReachingNonOverlappingSubSemiLatt(permutationOfSucc);
		for (Set<T> subSemiLatt : subSemiLattices) {
			Set<ITree<T>> treesInCurrSubSemiLatt = new HashSet<ITree<T>>();
			if (subSemiLatt.size() > 1) {
				IRootToLeavesTreeFinder<T> tF = new DivideAndConquer<T>(upperSemiLattice.getRestrictionTo(subSemiLatt));
				treesInCurrSubSemiLatt.addAll(tF.getRootToLeavesTrees());
			}
			else treesInCurrSubSemiLatt.add(new Tree<T>(subSemiLatt.iterator().next()));
			listsOfSubTrees.add(new ArrayList<>(treesInCurrSubSemiLatt));
		}
		//assemble new trees from subtrees
		int[] listDimensions = new int[listsOfSubTrees.size()];
		for (int i = 0 ; i < listsOfSubTrees.size() ; i++)
			listDimensions[i] = listsOfSubTrees.get(i).size();
		int[] coords = new int[listsOfSubTrees.size()];
		coords[0] = -1;
		while (nextCoord(coords, listDimensions)) {
			Set<ITree<T>> subTrees = new HashSet<ITree<T>>();
			for (int j = 0 ; j < listsOfSubTrees.size() ; j++) {
				subTrees.add(listsOfSubTrees.get(j).get(coords[j]));
			}
			trees.add(new Tree<T>(root, subTrees));
		}
		return trees;
	}	
	
	//may return an empty set
	private Set<T> getUpperBoundsOfALeaf(Set<T> subset) {	
		Set<T> leavesInSubset = subset.stream()
				.filter(n -> leaves.contains(n))
				.collect(Collectors.toSet());
		Set<T> upperBoundsOfALeaf = new HashSet<T>();
		for (T leaf : leavesInSubset)
			upperBoundsOfALeaf.addAll(upperSemiLattice.getUpperSet(leaf));
		upperBoundsOfALeaf.retainAll(subset);
		return upperBoundsOfALeaf;
	}	
	
	//Heap's algorithm
	private Set<List<T>> permute(List<T> objects, int n){
		Set<List<T>> permutations = new HashSet<List<T>>();
		if (n == 1) {
			permutations.add(new ArrayList<T>(objects));
		}
		else {
			for (int i = 0 ; i < n ; i++) {
				permutations.addAll(permute(objects, n-1));
				if (n % 2 == 1)
					swap(objects, 0, n-1);
				else swap(objects, i, n-1);
			}
		}
		return permutations;
	}
	
	private void swap(List<T> objects, int i, int j) {
		T swapped = objects.get(i);
		objects.set(i, objects.get(j));
		objects.set(j, swapped);
	}	

}
