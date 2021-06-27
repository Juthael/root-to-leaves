package com.tregouet.root_to_leaves.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IPoset<T> {

	int UNCOMPARABLE = -2;
	int LOWER_BOUND = -1;
	int EQUALS = 0;
	int UPPER_BOUND = 1;

	/**
	 * 
	 * @param elem1 any element
	 * @param elem2 any element
	 * @return <code>-2</code> if <code>elem1</code> and <code>elem2</code> are incomparable, <code>-1</code> 
	 * if <code>elem1</code> is a strict lower bound of <code>elem2</code>, <code>0</code> if equals, 
	 * <code>1</code> if <code>elem1</code> is a strict upper bound of <code>elem2</code> 
	 */
	int compare(T elem1, T elem2);

	/**
	 * 
	 * @param elem any element in the poset
	 * @return every lower bound of the specified element (including this element)
	 */
	Set<T> getLowerSet(T elem);
	
	/**
	 * 
	 * @param elem any subset
	 * @return every lower bound of the specified subset
	 */
	Set<T> getLowerSet(Set<T> subset);

	/**
	 * 
	 * @param elem any element in the poset
	 * @return the indexes of every lower bound of the specified element (including this element)
	 */
	Set<Integer> getLowerSetIndexes(T elem);
	
	/**
	 * 
	 * @param elem any subset
	 * @return the indexes of every lower bound of the specified subset
	 */
	Set<Integer> getLowerSetIndexes(Set<T> subset);	

	/**
	 * 
	 * @param firstElem any element
	 * @return every chain from the specified element to a minimal element
	 */
	Set<List<T>> getMaxChainsFrom(T firstElem);

	/**
	 * 
	 * @param firstElemIdx the index of any element
	 * @return every chain from the specified element to a minimal element
	 */
	Set<List<T>> getMaxChainsFrom(int firstElemIdx);

	Set<List<Integer>> getMaxChainsIndexesFrom(int rootIdx);

	/**
	 * chain elements are indicated by their indexes
	 * @param firstElem any elem
	 * @return every chain from the root to a leaf in the graph of the successor relation of the poset
	 */
	Set<List<Integer>> getMaxChainsIndexesFrom(T elem);

	Set<T> getMinimalElements();

	Set<Integer> getMinimalElementsIndexes();

	Map<T, Set<T>> getPrecRelationMap();

	Set<Integer> getPredecessorsIndexesOf(T elem);

	Set<T> getPredecessorsOf(T elem);

	/**
	 * WARNING : returned relation is non-reflexive. <br>
	 * 
	 * If <i>y ∈  Y</i> and <i>x</i> is mapped to <i>Y</i>, then <i>xRy</i> 
	 * (with <i>R</i> as the partially ordered strict relation of the poset). 
	 * 
	 * The mapping here is non-reflexive : if <i>xRy</i>, then <i>x ≠ y</i>.
	 * @return a strict partial order
	 */
	Map<T, Set<T>> getRelationMap();

	Set<T> getSet();

	/**
	 * 
	 * @return the topologically sorted set
	 */
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

	/**
	 * 
	 * @return a mapping of every element in the poset to its set of successors
	 */
	Map<T, Set<T>> getSuccRelationMap();

	/**
	 * 
	 * @param elem any element in the poset
	 * @return every upper bound of the specified element (including this element)
	 */
	Set<T> getUpperSet(T elem);
	
	/**
	 * 
	 * @param elem any subset
	 * @return every upper bound of the specified subset
	 */
	Set<T> getUpperSet(Set<T> elem);	
	
	/**
	 * 
	 * @param elem any subset
	 * @return indexes of every upper bound of the specified subset
	 */
	Set<Integer> getUpperSetIndexes(Set<T> subset);		
		

	/**
	 * 
	 * @param elem any element in the poset
	 * @return the indexes of every upper bound of the specified element (including this element)
	 */
	Set<Integer> getUpperSetIndexes(T elem);

}