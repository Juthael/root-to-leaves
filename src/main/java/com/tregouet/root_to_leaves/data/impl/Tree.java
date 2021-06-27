package com.tregouet.root_to_leaves.data.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.error.InvalidTreeException;

public class Tree<T> extends OutfittedPoset<T> implements ITree<T> {

	//UNSAFE : no validation of arguments
	public Tree(List<T> elements, int[][] incidenceMatrix, boolean skipChecks, boolean skipSorting) {
		super(elements, incidenceMatrix);
	}
	
	//Safe
	public Tree(Map<T, Set<T>> relation) throws InvalidTreeException {
		super(relation);
		validateArgument();
	}
	
	public boolean isATreeElement(T elem) {
		int elemIdx = elements.indexOf(elem);
		return isATreeElement(elemIdx);
	}
	
	public boolean isATreeElement(int elemIdx) {
		if (elements.get(elemIdx).equals(getRoot()))
			return true;
		boolean discarded = true;
		int setIdx = 0;
		while (discarded && setIdx < elements.size()) {
			discarded = (transitiveExpansion[setIdx][elemIdx] == 1 && setIdx != elemIdx);
			setIdx++;
		}
		return !discarded;
	}

	@Override
	public Set<T> getTreeLeaves() {
		return getMinimalElementsIndexes().stream().filter(i -> isATreeElement(i)).collect(Collectors.toList())
				.stream().map(n -> elements.get(n)).collect(Collectors.toSet());
	}
	
	public Set<T> getTreeSubset(){
		IntStream treeElemIdxStream = IntStream.range(0, elements.size()).filter(i -> isATreeElement(i));
		return treeElemIdxStream.boxed().collect(Collectors.toList())
				.stream().map(i -> elements.get(i)).collect(Collectors.toSet());
	}
	
	public List<T> getSortedTreeSubet() {
		List<T> sortedTreeSet = new ArrayList<T>();
		Iterator<T> elemIte = elements.iterator();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (isATreeElement(i))
				sortedTreeSet.add(elemIte.next());
			else elemIte.next();
		}
		return sortedTreeSet;
	}

	@Override
	public T getRoot() {
		return sortedElements.get(0);
	}	
	
	private void validateArgument() throws InvalidTreeException {
		Set<List<Integer>> chains = getMaxChainsIndexesFrom(getRoot());
		Set<T> leaves = getTreeLeaves();
		if (chains.size() != leaves.size())
			throw new InvalidTreeException("Tree.validateArguments() : inconsistency.");
	}

}
