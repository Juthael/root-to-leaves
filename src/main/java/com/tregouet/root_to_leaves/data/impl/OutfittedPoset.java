package com.tregouet.root_to_leaves.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tregouet.root_to_leaves.data.IPoset;

import fjab.poset.Poset;
import fjab.poset.PosetUtil;
import fjab.poset.Util;

public class OutfittedPoset<T> extends Poset<T> implements IPoset<T> {

	//Safe
	public OutfittedPoset(List<T> sortedElements, int[][] incidenceMatrix) {
		super(sortedElements, incidenceMatrix);
		sortMatrixes();
	}
	
	//UNSAFE : no validation of arguments
	protected OutfittedPoset(List<T> sortedElements, int[][] transitiveReduction, boolean skipChecks, 
			boolean skipSorting) {
		super(sortedElements, transitiveReduction, skipChecks, skipSorting);
	}
	
	//Safe
	public OutfittedPoset(Map<T, Set<T>> relation) {
		super(relation);
		sortMatrixes();
	}
	
	@Override
	public int compare(T elem1, T elem2) {
		if (elem1.equals(elem2)) {
			return EQUALS;
		}
		else {
			int elem1Idx = elements.indexOf(elem1);
			int elem2Idx = elements.indexOf(elem2);
			if (transitiveExpansion[elem1Idx][elem2Idx] == 1)
				return UPPER_BOUND;
			else if (transitiveExpansion[elem2Idx][elem1Idx] == 1) {
				return LOWER_BOUND;
			}
		}
		return UNCOMPARABLE;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) 
			return false;
		@SuppressWarnings("unchecked")
		OutfittedPoset<T> other = (OutfittedPoset<T>) o;
		if (alignMatrixes(other))
			return super.equals(other);
		return false;
	}	
	
	@Override
	public Set<T> getLowerSet(T elem) {
		Set<T> lowerSet = new HashSet<T>();
		for (int idx : getLowerSetIndexes(elem))
			lowerSet.add(elements.get(idx));
		return lowerSet;
	}
	
	@Override
	public Set<Integer> getLowerSetIndexes(T elem){
		int elemIdx = elements.indexOf(elem);
		Set<Integer> lowerSetIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (transitiveExpansion[elemIdx][i] == 1)
				lowerSetIndexes.add(i);
		}
		return lowerSetIndexes;
	}
	
	@Override
	public Set<Integer> getLowerSetIndexes(Set<T> subset) {
		Set<Integer> lowerSetIdxes = new HashSet<Integer>();
		Iterator<T> subsetIte = subset.iterator();
		if (subsetIte.hasNext())
			lowerSetIdxes.addAll(getLowerSetIndexes(subsetIte.next()));
		while (subsetIte.hasNext())
			lowerSetIdxes.retainAll(getLowerSetIndexes(subsetIte.next()));
		return lowerSetIdxes;
	}	
	
	@Override
	public Set<List<T>> getMaxChainsFrom(T firstElem) {
		return getMaxChainsFrom(elements.indexOf(firstElem));
	}
	
	@Override
	public Set<List<T>> getMaxChainsFrom(int rootIdx){
		Set<List<T>> chains = new HashSet<List<T>>();
		for (List<Integer> chainIdxs : getMaxChainsIndexesFrom(rootIdx)) {
			List<T> chain = new ArrayList<T>();
			for (int idx : chainIdxs) {
				chain.add(elements.get(idx));
			}
			chains.add(chain);
		}
		return chains;
	}
	
	@Override
	public Set<List<Integer>> getMaxChainsIndexesFrom(int firstElemIdx) {
		Set<List<Integer>> chains = new HashSet<List<Integer>>();
		Set<Integer> successorsIdxes = new HashSet<>();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (i != firstElemIdx && transitiveReduction[firstElemIdx][i] == 1)
				successorsIdxes.add(i);
		}
		if (successorsIdxes.isEmpty()) {
			List<Integer> chain = new ArrayList<Integer>();
			chain.add(firstElemIdx);
			chains.add(chain);
		}
		else {
			for (int succIdx : successorsIdxes) {
				for (List<Integer> chain : getMaxChainsIndexesFrom(succIdx)) {
					chain.add(0, firstElemIdx);
					chains.add(chain);
				}
			}
		}
		return chains;
	}
	
	@Override
	public Set<List<Integer>> getMaxChainsIndexesFrom(T elem){
		return getMaxChainsIndexesFrom(elements.indexOf(elem));
	}
	
	@Override
	public Set<T> getMinimalElements(){
		Set<T> minimalElements = new HashSet<T>();
		for (int idx : getMinimalElementsIndexes())
			minimalElements.add(elements.get(idx));
		return minimalElements;
	}
	
	@Override
	public Set<Integer> getMinimalElementsIndexes(){
		Set<Integer> minimalElementsIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < elements.size() ; i++) {
			boolean minimal = true;
			int j = 0;
			while (minimal && j < elements.size()) {
				if (j != i && transitiveExpansion[i][j] == 1)
					minimal = false;
				j++;
			}
			if (minimal)
				minimalElementsIndexes.add(i);				
		}
		return minimalElementsIndexes;
	}
	
	@Override
	public Map<T, Set<T>> getPrecRelationMap() {
		Map<T, Set<T>> prcRelation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < elements.size() ; i++) {
			Set<T> predecessors = new HashSet<>();
			for (int j = 0 ; j < elements.size() ; j++) {
				if (transitiveReduction[j][i] == 1 && j != i)
					predecessors.add(elements.get(j));
			}
			prcRelation.put(elements.get(i), predecessors);
		}
		return prcRelation;
	}
	
	@Override
	public Set<Integer> getPredecessorsIndexesOf(T elem){
		Set<Integer> predecessorsIndexes = new HashSet<Integer>();
		int elemIdx = elements.indexOf(elem);
		for (int i = 0 ; i < elements.size() ; i++) {
			if (transitiveReduction[i][elemIdx] == 1 && i != elemIdx)
				predecessorsIndexes.add(i);
		}
		return predecessorsIndexes;
	}
	
	@Override
	public Set<T> getPredecessorsOf(T elem){
		Set<T> predecessors = new HashSet<T>();
		for (int idx : getPredecessorsIndexesOf(elem))
			predecessors.add(elements.get(idx));
		return predecessors;
	}
	
	@Override
	public Map<T, Set<T>> getRelationMap() {
		Map<T, Set<T>> relation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < elements.size() ; i++) {
			Set<T> related = new HashSet<>();
			for (int j = 0 ; j < elements.size() ; j++) {
				if (transitiveExpansion[i][j] == 1 && j != i) {
					related.add(elements.get(j));
				}
			}
			relation.put(elements.get(i), related);
		}
		return relation;
	}
	
	@Override
	public Set<T> getSet(){
		return new HashSet<T>(elements);
	}
	
	@Override
	public List<T> getSortedSet() {
		if (sortedElements == null) {
			sortedElements = 
					IntStream.of(Util.sort(transitiveExpansion)).mapToObj(elements::get).collect(Collectors.toList());
		}
		return sortedElements;
	}
	
	@Override
	public Set<T> getStrictLowerBounds(Set<T> elems) {
		Set<T> strictLowerBounds = new HashSet<T>();
		for (int idx : getStrictLowerBoundsIndexes(elems))
			strictLowerBounds.add(elements.get(idx));
		return strictLowerBounds;
	}
	
	@Override
	public Set<T> getStrictLowerBounds(T elem) {
		Set<T> strictLowerBounds = new HashSet<T>();
		for (int idx : getStrictLowerBoundsIndexes(elem))
			strictLowerBounds.add(elements.get(idx));
		return strictLowerBounds;
	}
	
	@Override
	public Set<Integer> getStrictLowerBoundsIndexes(Set<T> elems) {
		Set<Integer> strictLowerBoundsIndexes = new HashSet<Integer>();
		List<T> elemList = new ArrayList<T>(elems);
		for (int i = 0  ; i < elemList.size() ; i++) {
			if (i == 0) {
				strictLowerBoundsIndexes.addAll(getStrictLowerBoundsIndexes(elemList.get(i)));
			}
			else strictLowerBoundsIndexes.retainAll(getStrictLowerBoundsIndexes(elemList.get(i)));
		}
		return strictLowerBoundsIndexes;
	}
	
	@Override
	public Set<Integer> getStrictLowerBoundsIndexes(T elem) {
		int elemIdx = elements.indexOf(elem);
		Set<Integer> strictLowerBoundsIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (transitiveExpansion[elemIdx][i] == 1 && i != elemIdx)
				strictLowerBoundsIndexes.add(i);
		}
		return strictLowerBoundsIndexes;
	}
	
	@Override
	public Set<T> getStrictUpperBounds(Set<T> elems) {
		Set<T> strictUpperBounds = new HashSet<T>();
		for (int idx : getStrictUpperBoundsIndexes(elems))
			strictUpperBounds.add(elements.get(idx));
		return strictUpperBounds;
	}
	
	@Override
	public Set<T> getStrictUpperBounds(T elem){
		Set<T> strictUpperBounds = new HashSet<T>();
		for (int idx : getStrictUpperBoundsIndexes(elem))
			strictUpperBounds.add(elements.get(idx));
		return strictUpperBounds;
	}
	
	@Override
	public Set<Integer> getStrictUpperBoundsIndexes(Set<T> elems){
		Set<Integer> strictUpperBoundsIndexes = new HashSet<Integer>();
		List<T> elemList = new ArrayList<T>(elems);
		for (int i = 0  ; i < elemList.size() ; i++) {
			if (i == 0) {
				strictUpperBoundsIndexes.addAll(getStrictUpperBoundsIndexes(elemList.get(i)));
			}
			else strictUpperBoundsIndexes.retainAll(getStrictUpperBoundsIndexes(elemList.get(i)));
		}
		return strictUpperBoundsIndexes;
	}
	
	@Override
	public Set<Integer> getStrictUpperBoundsIndexes(T elem) {
		Set<Integer> strictUpperBoundsIndexes = new HashSet<Integer>();
		int elemIdx = elements.indexOf(elem);
		for (int i = 0 ; i < elements.size() ; i++) {
			if (i != elemIdx && transitiveExpansion[i][elemIdx] == 1)
				strictUpperBoundsIndexes.add(i);
		}
		return strictUpperBoundsIndexes;
	}	
	
	@Override
	public Set<Integer> getSuccessorsIndexesOf(T elem) {
		Set<Integer> successorsIndexes = new HashSet<Integer>();
		int elemIdx = elements.indexOf(elem);
		for (int i = 0 ; i < elements.size() ; i++) {
			if (i != elemIdx && transitiveReduction[elemIdx][i] == 1)
				successorsIndexes.add(i);
		}
		return successorsIndexes;
	}
	
	@Override
	public Set<T> getSuccessorsOf(T elem){
		Set<T> successors = new HashSet<T>();
		for (int idx : getSuccessorsIndexesOf(elem))
			successors.add(elements.get(idx));
		return successors;
	}	
	
	@Override
	public Map<T, Set<T>> getSuccRelationMap() {
		Map<T, Set<T>> succRelation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < elements.size() ; i++) {
			Set<T> successors = new HashSet<>();
			for (int j = 0 ; j < elements.size() ; j++) {
				if (j != i) {
					if (transitiveReduction[i][j] == 1)
						successors.add(elements.get(j));
				}
			}
			succRelation.put(elements.get(i), successors);
		}
		return succRelation;
	}
	
	@Override
	public Set<T> getUpperSet(T elem) {
		Set<T> upperSet = new HashSet<T>();
		for (int idx : getUpperSetIndexes(elem))
			upperSet.add(elements.get(idx));
		return upperSet;
	}
	
	@Override
	public Set<Integer> getUpperSetIndexes(T elem){
		int elemIdx = elements.indexOf(elem);
		Set<Integer> upperSetIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (transitiveExpansion[i][elemIdx] == 1)
				upperSetIndexes.add(i);
		}
		return upperSetIndexes;
	}

	@Override
	public Set<T> getUpperSet(Set<T> subset) {
		Set<T> upperSet = getUpperSetIndexes(subset)
				.stream()
				.map(n -> elements.get(n))
				.collect(Collectors.toSet());
		return upperSet;
	}

	@Override
	public Set<T> getLowerSet(Set<T> subset) {
		Set<T> lowerSet = getLowerSetIndexes(subset)
				.stream()
				.map(n -> elements.get(n))
				.collect(Collectors.toSet());
		return lowerSet;
	}

	@Override
	public Set<Integer> getUpperSetIndexes(Set<T> subset) {
		Set<Integer> upperSetIdxes = new HashSet<Integer>();
		Iterator<T> subsetIte = subset.iterator();
		if (subsetIte.hasNext())
			upperSetIdxes.addAll(getUpperSetIndexes(subsetIte.next()));
		while (subsetIte.hasNext())
			upperSetIdxes.retainAll(getUpperSetIndexes(subsetIte.next()));
		return upperSetIdxes;
	}

	private void sortMatrixes() {
		if (!elements.equals(sortedElements)) {
			int[][] sortedTransRed = new int[elements.size()][elements.size()];
			for (int i = 0 ; i < sortedElements.size() ; i++) {
				sortedTransRed[i][i] = 1;
				for (T succ : getSuccessorsOf(sortedElements.get(i)))
					sortedTransRed[i][sortedElements.indexOf(succ)] = 1;
			}
			elements = sortedElements;
			transitiveReduction = sortedTransRed;
			transitiveExpansion = PosetUtil.transitiveExpansion(sortedTransRed);
		}
	}
	
	/**
	 * If two poset have the same set of elements, then align their matrixes so that they map to the same 
	 * sorted list of elements. Makes matrixes comparable. 
	 * @param other
	 * @return false of the specified poset's set of elements is not equal to this
	 */
	public boolean alignMatrixes(IPoset<T> other) {
		List<T> alignedSorting = other.getSortedSet();
		if (!alignedSorting.equals(sortedElements)) {
			if (!this.getSet().equals(other.getSet()))
				return false;
			int[][] alignedTransRed = new int[elements.size()][elements.size()];
			for (int i = 0 ; i < alignedSorting.size() ; i++) {
				alignedTransRed[i][i] = 1;
				for (T succ : getSuccessorsOf(alignedSorting.get(i)))
					alignedTransRed[i][alignedSorting.indexOf(succ)] = 1;
			}
			sortedElements = alignedSorting;
			elements = alignedSorting;
			transitiveReduction = alignedTransRed;
			transitiveExpansion = PosetUtil.transitiveExpansion(alignedTransRed);
		}
		return true;
	}

}
