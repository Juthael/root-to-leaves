package com.tregouet.root_to_leaves.data.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tregouet.root_to_leaves.data.IPoset;

import fjab.poset.Poset;
import fjab.poset.Util;

public class OutfittedPoset<T> extends Poset<T> implements IPoset<T> {

	public OutfittedPoset(List<T> elements, int[][] incidenceMatrix) {
		super(elements, incidenceMatrix);
	}
	
	public OutfittedPoset(List<T> sortedElements, int[][] transitiveReduction, boolean skipChecks, 
			boolean skipSorting) {
		super(sortedElements, transitiveReduction, skipChecks, skipSorting);
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
	public Set<List<Integer>> getMaxChainsIndexesFrom(int rootIdx) {
		Set<List<Integer>> chains = new HashSet<List<Integer>>();
		Set<Integer> successorsIdxes = new HashSet<>();
		for (int i = 0 ; i < elements.size() ; i++) {
			if (i != rootIdx && transitiveReduction[rootIdx][i] == 1)
				successorsIdxes.add(i);
		}
		if (successorsIdxes.isEmpty()) {
			List<Integer> chain = new ArrayList<Integer>();
			chain.add(rootIdx);
		}
		else {
			for (int succIdx : successorsIdxes) {
				for (List<Integer> chain : getMaxChainsIndexesFrom(succIdx)) {
					chain.add(0, rootIdx);
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
				if (j != i) {
					if (transitiveReduction[j][i] == 1)
						predecessors.add(elements.get(j));
				}
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
			if (i != elemIdx && transitiveReduction[i][elemIdx] == 1)
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
			if (i != elemIdx && transitiveExpansion[elemIdx][i] == 1)
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

}
