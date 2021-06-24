package com.tregouet.root_to_leaves.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A partially ordered set which has a greatest lower bound for any nonempty subset.
 * 
 * @author Gael Tregouet
 *
 * @param <T>
 */
public interface IUpperSemiLattice<T> {
	
	public static final int UNCOMPARABLE = -2;
	public static final int SUB = -1;
	public static final int EQUALS = 0;
	public static final int SUPER = 1;	
	
	void addAsNewMax(T newMax);

	/**
	 * 
	 * @param elem1 any element
	 * @param elem2 any element
	 * @return <code>-2</code> if <code>elem1</code> and <code>elem2</code> are uncomparable, <code>-1</code> 
	 * if <code>elem1</code> is a strict lower bound of <code>elem2</code>, <code>0</code> if equals, 
	 * <code>1</code> if <code>elem1</code> is a struct upper bound of <code>elem2</code> 
	 */
	int compare(T elem1, T elem2);
	
	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return every lower bound of the specified element (including this element)
	 */
	Set<T> getLowerSet(T elem);
	
	/**
	 * 
	 * @param firstElem any element
	 * @return every chain from the root to a leaf in the graph of the successor relation of the semilattice
	 */
	Set<List<T>> getMaxChainsFrom(T firstElem);

	/**
	 * 
	 * @return the root of the semilattice, i.e. its maximum element
	 */
	T getMaximum();

	/**
	 * 
	 * @return the leaves of the semilattice, i.e. its minimal elements
	 */
	Set<T> getMinimalElements();
	
	/**
	 * 
	 * @return a mapping of every element in the semilattice to the set of its predecessors
	 */
	Map<T, Set<T>> getPrecRelationMap();

	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return the predecessors of the specified element in the semilattice
	 */
	Set<T> getPredecessorsOf(T elem);
	
	/**
	 * If <i>y ∈  Y</i> and <i>x</i> is mapped to <i>Y</i>, then <i>xRy</i> 
	 * (with <i>R</i> as the partially ordered strict relation of the semilattice). 
	 * 
	 * The mapping here is non-reflexive : if <i>xRy</i>, then <i>x ≠ y</i>.
	 * @return the semilattice as a mapping from any element to its strict lower bounds
	 */
	Map<T, Set<T>> getRelationMap();

	/**
	 * 
	 * @return the maximal element in the semilattice
	 */
	T getRoot();
	
	/**
	 * 
	 * @return any element in the semilattice
	 */
	Set<T> getSet();

	/**
	 * 
	 * @param elems any subset in the semilattice
	 * @return the set of elements that are strict lower bounds of every element in the specified subset
	 */
	Set<T> getStrictLowerBounds(Set<T> elems);
	
	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return the specified element's strict lower bounds
	 */
	Set<T> getStrictLowerBounds(T elem);
	
	/**
	 * 
	 * @param elems any subset in the semilattice
	 * @return the set of elements that are strict upper bounds of every element in the specified subset
	 */
	Set<T> getStrictUpperBounds(Set<T> elems);
	
	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return the specified element's strict upper bounds
	 */
	Set<T> getStrictUpperBounds(T elem);
	
	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return the set of successors of the specified element
	 */
	Set<T> getSuccessorsOf(T elem);
	
	/**
	 * 
	 * @return a mapping of every element in the semilattice to its set of successors
	 */
	Map<T, Set<T>> getSuccRelationMap();
	
	/**
	 * 
	 * @param elem any element in the semilattice
	 * @return every upper bound of the specified element (including this element)
	 */
	Set<T> getUpperSet(T elem);

}
