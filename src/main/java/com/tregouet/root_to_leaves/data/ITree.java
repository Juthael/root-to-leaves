package com.tregouet.root_to_leaves.data;

import java.util.Set;

public interface ITree<T> extends IUpperSemiLattice<T> {
	
	/**
	 * 
	 * @return the minimal elements of the tree
	 */
	Set<T> getLeaves();

}
