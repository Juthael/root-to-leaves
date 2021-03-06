package com.tregouet.root_to_leaves;

import java.util.List;

import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;

/**
 * <p>
 * Finds every arborescence in the directed graph generated by the successor relation of an upper semilattice, 
 * such that :
 * </p>
 * <ul>
 * 	<li> the root is the maximum
 * 	<li> every minimal element is a leaf
 * </ul> 
 * <p>
 * The tree doesn't have to be a <i>spanning</i> tree : some elements in the input relation may be left 
 * aside, provided that they are neither a minimal nor the maximum element. 
 * </p>
 * 
 * @author Gael Tregouet
 *
 * @param <T> extends {@link Object}. toString() overriding facilitates visualization.
 */
public interface IRootToLeavesTreeFinder<T> {

	/**
	 * An <i>upper semi-lattice</i> is a partially ordered set which has a greatest lower bound for any nonempty 
	 * subset. 
	 * @param upperSemiLattice an upper semilattice
	 */
	void input(IUpperSemiLattice<T> upperSemiLattice);
	
	/**
	 * @return a list of all trees from the root to the leaves
	 */
	List<ITree<T>> output();

}