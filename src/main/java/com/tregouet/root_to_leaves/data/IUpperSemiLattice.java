package com.tregouet.root_to_leaves.data;

import java.util.Set;

/**
 * A partially ordered set which has a greatest lower bound for any nonempty subset.
 * 
 * @author Gael Tregouet
 *
 * @param <T>
 */
public interface IUpperSemiLattice<T> extends IPoset<T> {
	
	public static final int UNCOMPARABLE = -2;
	public static final int SUB = -1;
	public static final int EQUALS = 0;
	public static final int SUPER = 1;	
	
	void addAsNewMax(T newMax);

	T getMaximum();
	
	/**
	 * 
	 * @param subset
	 * @return the least upper bound
	 */
	T getSupremum(Set<T> subset);

}
