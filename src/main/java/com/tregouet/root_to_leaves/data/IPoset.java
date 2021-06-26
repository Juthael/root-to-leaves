package com.tregouet.root_to_leaves.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPoset<T> {

	int UNCOMPARABLE = -2;
	int LOWER_BOUND = -1;
	int EQUALS = 0;
	int UPPER_BOUND = 1;

	int compare(T elem1, T elem2);

	Set<T> getLowerSet(T elem);

	Set<Integer> getLowerSetIndexes(T elem);

	Set<List<T>> getMaxChainsFrom(T firstElem);

	Set<List<T>> getMaxChainsFrom(int rootIdx);

	Set<List<Integer>> getMaxChainsIndexesFrom(int rootIdx);

	Set<List<Integer>> getMaxChainsIndexesFrom(T elem);

	Set<T> getMinimalElements();

	Set<Integer> getMinimalElementsIndexes();

	Map<T, Set<T>> getPrecRelationMap();

	Set<Integer> getPredecessorsIndexesOf(T elem);

	Set<T> getPredecessorsOf(T elem);

	/**
	 * WARNING : returned relation is non-reflexive
	 * @return a strict partial order
	 */
	Map<T, Set<T>> getRelationMap();

	Set<T> getSet();

	List<T> getSortedSet();

	Set<T> getStrictLowerBounds(Set<T> elems);

	Set<T> getStrictLowerBounds(T elem);

	Set<Integer> getStrictLowerBoundsIndexes(Set<T> elems);

	Set<Integer> getStrictLowerBoundsIndexes(T elem);

	Set<T> getStrictUpperBounds(Set<T> elems);

	Set<T> getStrictUpperBounds(T elem);

	Set<Integer> getStrictUpperBoundsIndexes(Set<T> elems);

	Set<Integer> getStrictUpperBoundsIndexes(T elem);

	Set<Integer> getSuccessorsIndexesOf(T elem);

	Set<T> getSuccessorsOf(T elem);

	Map<T, Set<T>> getSuccRelationMap();

	Set<T> getUpperSet(T elem);

	Set<Integer> getUpperSetIndexes(T elem);

}