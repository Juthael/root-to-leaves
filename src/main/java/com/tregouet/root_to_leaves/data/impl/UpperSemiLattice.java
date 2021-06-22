package com.tregouet.root_to_leaves.data.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;

public class UpperSemiLattice<T> implements IUpperSemiLattice<T> {
	
	//elements sorted by the decreasing size of their lowerset
	private final List<T> set;
	private T root = null;
	//non-reflexive
	private final Map<T, Set<T>> relation;
	private final Map<T, Set<T>> succRelation = new HashMap<T, Set<T>>();
	private final Map<T, Set<T>> precRelation = new HashMap<T, Set<T>>();
	
	public UpperSemiLattice(Map<T, Set<T>> relation) {
		this.relation = relation;
		set = new ArrayList<T>(relation.keySet());
		set.sort(Comparator.comparing(n -> -(this.relation.get(n).size())));
		root = set.get(0);
		setSuccRelation();
		setPrecRelation();
	}
	
	protected UpperSemiLattice(T seed) {
		this.root = seed;
		set = new ArrayList<T>();
		set.add(seed);
		relation = new HashMap<T, Set<T>>();
		relation.put(seed, new HashSet<T>());
		succRelation.put(seed, new HashSet<T>());
		precRelation.put(seed, new HashSet<T>());
	}
	
	protected UpperSemiLattice(T root, Set<ITree<T>> subTrees) {
		this.root = root;
		relation = new HashMap<T, Set<T>>();
		Set<T> nonRoots = new HashSet<T>();
		Set<T> subRoots = new HashSet<T>();
		for (ITree<T> subTree : subTrees) {
			nonRoots.addAll(subTree.getSet());
			subRoots.add(subTree.getRoot());
			relation.putAll(subTree.getRelationMap());
			succRelation.putAll(subTree.getSuccRelationMap());
			precRelation.putAll(subTree.getPrecRelationMap());
		}
		relation.put(root, nonRoots);
		succRelation.put(root, subRoots);
		for (T subRoot : subRoots)
			precRelation.get(subRoot).add(root);
		precRelation.put(root, new HashSet<T>());
		set = new ArrayList<T>(relation.keySet());
		set.sort(Comparator.comparing(n -> -(this.relation.get(n).size())));
	}	
	
	@Override
	public void addAsNewMax(T newMax) {
		Set<T> related = getSet();
		T previousMax = getMaximum();
		Set<T> newMaxSucc = new HashSet<T>();
		newMaxSucc.add(previousMax);
		relation.put(newMax, related);
		succRelation.put(newMax, newMaxSucc);
		precRelation.get(previousMax).add(newMax);
		precRelation.put(newMax, new HashSet<T>());
		set.add(0, newMax);
		root = newMax;
	}
	
	@Override
	public int compare(T elem1, T elem2) {
		if (relation.get(elem1).contains(elem2))
			return SUPER;
		else if (relation.get(elem2).contains(elem1))
			return SUB;
		else if (elem1.equals(elem2))
			return EQUALS;
		return UNCOMPARABLE;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpperSemiLattice<?> other = (UpperSemiLattice<?>) obj;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}
	
	@Override
	public Set<T> get(T elem) {
		return new HashSet<>(relation.get(elem));
	}
	
	@Override
	public Set<T> getLowerSet(T elem) {
		Set<T> lowerSet = getStrictLowerBounds(elem);
		lowerSet.add(elem);
		return lowerSet;
	}
	
	@Override
	public Set<List<T>> getMaxChainsFrom(T root){
		Set<List<T>> chains = new HashSet<List<T>>();
		if (!succRelation.get(root).isEmpty()) {
			Set<List<T>> subchains = new HashSet<List<T>>();
			for (T succ : succRelation.get(root)) {
				subchains.addAll(getMaxChainsFrom(succ));
			}
			for (List<T> chain : subchains) {
				chain.add(0, root);
				chains.add(chain);
			}
		}
		else {
			List<T> chain = new ArrayList<T>();
			chain.add(root);
			chains.add(chain);
		}
		return chains;
	}
	
	@Override
	public T getMaximum() {
		return root;
	}	
	
	@Override
	public Set<T> getMinimalElements() {
		Set<T> minElem = set.stream()
							.filter(n -> relation.get(n).isEmpty())
							.collect(Collectors.toSet());
		return minElem;
	}
	
	@Override
	public Map<T, Set<T>> getPrecRelationMap() {
		Map<T, Set<T>> precRelationMap = new HashMap<>();
		for (Entry<T, Set<T>> entry : precRelation.entrySet()) {
			precRelationMap.put(entry.getKey(), new HashSet<T>(entry.getValue()));
		}
		return precRelationMap;
	}
	
	@Override
	public Set<T> getPredecessorsOf(T elem){
		return new HashSet<T>(precRelation.get(elem));
	}	
	
	@Override
	public Map<T, Set<T>> getRelationMap() {
		Map<T, Set<T>> relationMap = new HashMap<>();
		for (Entry<T, Set<T>> entry : relation.entrySet()) {
			relationMap.put(entry.getKey(), new HashSet<T>(entry.getValue()));
		}
		return relationMap;
	}
	
	@Override
	public IUpperSemiLattice<T> getRestrictionTo(Set<T> subset) {
		Map<T, Set<T>> restrictionMap = new HashMap<T, Set<T>>();
		for (T key : relation.keySet()) {
			if (subset.contains(key)) {
				Set<T> value = new HashSet<T>(relation.get(key));
				value.retainAll(subset);
				restrictionMap.put(key, value);
			}
		}
		return new UpperSemiLattice<T>(restrictionMap);
	}

	@Override
	public T getRoot() {
		return root;
	}

	@Override
	public Set<T> getSet(){
		return new HashSet<T>(set);
	}

	@Override
	public Set<T> getStrictLowerBounds(Set<T> elems){
		Set<T> lowerBounds = new HashSet<T>();
		List<T> elemList = new ArrayList<T>(elems);
		for (int i = 0 ; i < elems.size() ; i++) {
			if (i == 0)
				lowerBounds.addAll(getStrictLowerBounds(elemList.get(i)));
			else lowerBounds.retainAll(getStrictLowerBounds(elemList.get(i)));
		}
		return lowerBounds;
	}

	@Override
	public Set<T> getStrictLowerBounds(T elem){
		return new HashSet<T>(relation.get(elem));
	}

	@Override
	public Set<T> getStrictUpperBounds(Set<T> elems) {
		Set<T> upperBounds = new HashSet<T>();
		List<T> elemList = new ArrayList<T>(elems);
		for (int i = 0 ; i < elems.size() ; i++) {
			if (i == 0)
				upperBounds.addAll(getStrictUpperBounds(elemList.get(i)));
			else upperBounds.retainAll(getStrictUpperBounds(elemList.get(i)));
		}
		return upperBounds;
	}
	
	@Override
	public Set<T> getStrictUpperBounds(T elem){
		Set<T> upperBounds = new HashSet<T>();
		for (T e : set) {
			if (relation.get(e).contains(elem))
			upperBounds.add(e);
		}
		return upperBounds;
	}

	@Override
	public Set<T> getSuccessorsInSpecifiedSubset(T elem, Set<T> subset) {
		Set<T> succInSubset = new HashSet<>(succRelation.get(elem));
		succInSubset.retainAll(subset);
		return succInSubset;
	}

	@Override
	public Set<T> getSuccessorsOf(T elem) {
		return new HashSet<T>(succRelation.get(elem));
	}

	@Override
	public Map<T, Set<T>> getSuccRelationMap() {
		Map<T, Set<T>> succRelationMap = new HashMap<>();
		for (Entry<T, Set<T>> entry : succRelation.entrySet()) {
			succRelationMap.put(entry.getKey(), new HashSet<T>(entry.getValue()));
		}
		return succRelationMap;
	}

	@Override
	public Set<T> getUpperSet(T elem) {
		Set<T> upperSet = getStrictUpperBounds(elem);
		upperSet.add(elem);
		return upperSet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	@Override 
	public String toString() {
		return toString(root, 0);
	}

	//successor relation must be set beforehand
	private void setPrecRelation() {
		for (T elem : set) {
			precRelation.put(elem, new HashSet<T>());
		}
		for (T predecessor : set) {
			for (T successor : succRelation.get(predecessor)) {
				precRelation.get(successor).add(predecessor);
			}
		}
	}
	
	private void setSuccRelation() {
		for (T key : set) {
			succRelation.put(key, new HashSet<T>(relation.get(key)));
		}
		Iterator<T> upperIte = set.iterator();
		while (upperIte.hasNext()) {
			T upper = upperIte.next();
			Iterator<T> lowerIte = set.iterator();
			T lower = lowerIte.next();
			while (lower != upper) {
				if (succRelation.get(lower).contains(upper))
					succRelation.get(lower).removeAll(relation.get(upper));
				lower = lowerIte.next();
			}
		}
	}
	
	private String toString(T root, int rankFromStart) {
		StringBuilder sB = new StringBuilder();
		for (int i = 0 ; i < rankFromStart ; i++)
			sB.append("   ");
		sB.append(root.toString() + System.lineSeparator());
		for (T subT : getSuccRelationMap().get(root)) {
			sB.append(toString(subT, rankFromStart + 1));
		}
		return sB.toString();
	}	

}
