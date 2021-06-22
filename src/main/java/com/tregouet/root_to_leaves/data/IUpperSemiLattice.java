package com.tregouet.root_to_leaves.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUpperSemiLattice<T> {
	
	public static final int UNCOMPARABLE = -2;
	public static final int SUB = -1;
	public static final int EQUALS = 0;
	public static final int SUPER = 1;	
	
	void addAsNewMax(T newMax);

	int compare(T elem1, T elem2);
	
	Set<T> get(T elem);
	
	Set<T> getLowerSet(T elem);
	
	Set<List<T>> getMaxChainsFrom(T root);

	T getMaximum();

	Set<T> getMinimalElements();
	
	Map<T, Set<T>> getPrecRelationMap();

	Set<T> getPredecessorsOf(T elem);
	
	Map<T, Set<T>> getRelationMap();

	IUpperSemiLattice<T> getRestrictionTo(Set<T> subset);

	T getRoot();
	
	Set<T> getSet();

	Set<T> getStrictLowerBounds(Set<T> elems);
	
	Set<T> getStrictLowerBounds(T elem);
	
	Set<T> getStrictUpperBounds(Set<T> elems);
	
	Set<T> getStrictUpperBounds(T elem);
	
	Set<T> getSuccessorsInSpecifiedSubset(T elem, Set<T> subset);
	
	Set<T> getSuccessorsOf(T elem);
	
	Map<T, Set<T>> getSuccRelationMap();
	
	Set<T> getUpperSet(T elem);

}
