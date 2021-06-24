package com.tregouet.root_to_leaves.data.impl.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fjab.poset.Poset;

public class OutfittedPoset<T> extends Poset<T> {

	public OutfittedPoset(List<T> elements, int[][] incidenceMatrix) {
		super(elements, incidenceMatrix);
	}
	
	public int compare(T elem1, T elem2) {
		if (elem1.equals(elem2)) {
			return EQUALS;
		}
		else {
			int elem1Idx = sortedElements.indexOf(elem1);
			int elem2Idx = sortedElements.indexOf(elem2);
			if (elem1Idx < elem2Idx) {
				if (transitiveExpansion[elem1Idx][elem2Idx] == 1)
					return UPPER_BOUND;
			}
			else if (transitiveExpansion[elem2Idx][elem1Idx] == 1) {
				return LOWER_BOUND;
			}
		}
		return UNCOMPARABLE;
	}
	
	public Set<T> getLowerSet(T elem) {
		Set<T> lowerSet = new HashSet<T>();
		for (int idx : getLowerSetIndexes(elem))
			lowerSet.add(sortedElements.get(idx));
		return lowerSet;
	}
	
	public Set<Integer> getLowerSetIndexes(T elem){
		int elemIdx = sortedElements.indexOf(elem);
		Set<Integer> lowerSetIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (transitiveExpansion[elemIdx][i] == 1)
				lowerSetIndexes.add(i);
		}
		return lowerSetIndexes;
	}
	
	public Set<List<T>> getMaxChainsFrom(T firstElem) {
		return getMaxChainsFrom(sortedElements.indexOf(firstElem));
	}
	
	public Set<List<T>> getMaxChainsFrom(int rootIdx){
		Set<List<T>> chains = new HashSet<List<T>>();
		for (List<Integer> chainIdxs : getMaxChainsIndexesFrom(rootIdx)) {
			List<T> chain = new ArrayList<T>();
			for (int idx : chainIdxs) {
				chain.add(sortedElements.get(idx));
			}
			chains.add(chain);
		}
		return chains;
	}
	
	public Set<List<Integer>> getMaxChainsIndexesFrom(int rootIdx) {
		Set<List<Integer>> chains = new HashSet<List<Integer>>();
		Set<Integer> successorsIdxes = new HashSet<>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
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
	
	public Set<T> getMinimalElements(){
		Set<T> minimalElements = new HashSet<T>();
		for (int idx : getMinimalElementsIndexes())
			minimalElements.add(sortedElements.get(idx));
		return minimalElements;
	}
	
	public Set<Integer> getMinimalElementsIndexes(){
		Set<Integer> minimalElementsIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			boolean minimal = true;
			int j = 0;
			while (minimal && j < sortedElements.size()) {
				if (j != i && transitiveExpansion[i][j] == 1)
					minimal = false;
				j++;
			}
			if (minimal)
				minimalElementsIndexes.add(i);				
		}
		return minimalElementsIndexes;
	}
	
	public Map<T, Set<T>> getPrecRelationMap() {
		Map<T, Set<T>> prcRelation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			Set<T> predecessors = new HashSet<>();
			for (int j = 0 ; j < sortedElements.size() ; j++) {
				if (j != i) {
					if (transitiveReduction[j][i] == 1)
						predecessors.add(sortedElements.get(j));
				}
			}
			prcRelation.put(sortedElements.get(i), predecessors);
		}
		return prcRelation;
	}
	
	public Set<Integer> getPredecessorsIndexesOf(T elem){
		Set<Integer> predecessorsIndexes = new HashSet<Integer>();
		int elemIdx = sortedElements.indexOf(elem);
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (i != elemIdx && transitiveReduction[i][elemIdx] == 1)
				predecessorsIndexes.add(i);
		}
		return predecessorsIndexes;
	}
	
	public Set<T> getPredecessorsOf(T elem){
		Set<T> predecessors = new HashSet<T>();
		for (int idx : getPredecessorsIndexesOf(elem))
			predecessors.add(sortedElements.get(idx));
		return predecessors;
	}
	
	/**
	 * WARNING : returned relation is non-reflexive
	 * @return a non-reflexive partial order
	 */
	public Map<T, Set<T>> getRelationMap() {
		Map<T, Set<T>> relation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			Set<T> related = new HashSet<>();
			for (int j = 0 ; j < sortedElements.size() ; j++) {
				if (j != i) {
					if (transitiveExpansion[i][j] == 1)
						related.add(sortedElements.get(j));
				}
			}
			relation.put(sortedElements.get(i), related);
		}
		return relation;
	}
	
	public Set<T> getSet(){
		return new HashSet<T>(sortedElements);
	}
	
	public List<T> getSortedElements() {
		return sortedElements;
	}
	
	public Set<T> getStrictLowerBounds(Set<T> elems) {
		Set<T> strictLowerBounds = new HashSet<T>();
		for (int idx : getStrictLowerBoundsIndexes(elems))
			strictLowerBounds.add(sortedElements.get(idx));
		return strictLowerBounds;
	}
	
	public Set<T> getStrictLowerBounds(T elem) {
		Set<T> strictLowerBounds = new HashSet<T>();
		for (int idx : getStrictLowerBoundsIndexes(elem))
			strictLowerBounds.add(sortedElements.get(idx));
		return strictLowerBounds;
	}
	
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
	
	public Set<Integer> getStrictLowerBoundsIndexes(T elem) {
		int elemIdx = sortedElements.indexOf(elem);
		Set<Integer> strictLowerBoundsIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (i != elemIdx && transitiveExpansion[elemIdx][i] == 1)
				strictLowerBoundsIndexes.add(i);
		}
		return strictLowerBoundsIndexes;
	}
	
	public Set<T> getStrictUpperBounds(Set<T> elems) {
		Set<T> strictUpperBounds = new HashSet<T>();
		for (int idx : getStrictUpperBoundsIndexes(elems))
			strictUpperBounds.add(sortedElements.get(idx));
		return strictUpperBounds;
	}
	
	public Set<T> getStrictUpperBounds(T elem){
		Set<T> strictUpperBounds = new HashSet<T>();
		for (int idx : getStrictUpperBoundsIndexes(elem))
			strictUpperBounds.add(sortedElements.get(idx));
		return strictUpperBounds;
	}
	
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
	
	public Set<Integer> getStrictUpperBoundsIndexes(T elem) {
		Set<Integer> strictUpperBoundsIndexes = new HashSet<Integer>();
		int elemIdx = sortedElements.indexOf(elem);
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (i != elemIdx && transitiveExpansion[i][elemIdx] == 1)
				strictUpperBoundsIndexes.add(i);
		}
		return strictUpperBoundsIndexes;
	}	
	
	public Set<Integer> getSuccessorsIndexesOf(T elem) {
		Set<Integer> successorsIndexes = new HashSet<Integer>();
		int elemIdx = sortedElements.indexOf(elem);
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (i != elemIdx && transitiveReduction[elemIdx][i] == 1)
				successorsIndexes.add(i);
		}
		return successorsIndexes;
	}
	
	public Set<T> getSuccessorsOf(T elem){
		Set<T> successors = new HashSet<T>();
		for (int idx : getSuccessorsIndexesOf(elem))
			successors.add(sortedElements.get(idx));
		return successors;
	}	
	
	public Map<T, Set<T>> getSuccRelationMap() {
		Map<T, Set<T>> succRelation = new HashMap<T, Set<T>>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			Set<T> successors = new HashSet<>();
			for (int j = 0 ; j < sortedElements.size() ; j++) {
				if (j != i) {
					if (transitiveReduction[i][j] == 1)
						successors.add(sortedElements.get(j));
				}
			}
			succRelation.put(sortedElements.get(i), successors);
		}
		return succRelation;
	}
	
	public Set<T> getUpperSet(T elem) {
		Set<T> upperSet = new HashSet<T>();
		for (int idx : getUpperSetIndexes(elem))
			upperSet.add(sortedElements.get(idx));
		return upperSet;
	}
	
	public Set<Integer> getUpperSetIndexes(T elem){
		int elemIdx = sortedElements.indexOf(elem);
		Set<Integer> upperSetIndexes = new HashSet<Integer>();
		for (int i = 0 ; i < sortedElements.size() ; i++) {
			if (transitiveExpansion[i][elemIdx] == 1)
				upperSetIndexes.add(i);
		}
		return upperSetIndexes;
	}	

}
