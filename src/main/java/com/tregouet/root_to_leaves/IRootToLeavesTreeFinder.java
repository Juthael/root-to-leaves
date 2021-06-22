package com.tregouet.root_to_leaves;

import java.util.Set;

import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;

public interface IRootToLeavesTreeFinder<T> {

	void input(IUpperSemiLattice<T> upperSemiLattice);
	
	Set<ITree<T>> output();

}