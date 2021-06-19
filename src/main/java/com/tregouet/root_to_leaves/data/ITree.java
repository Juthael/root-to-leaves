package com.tregouet.root_to_leaves.data;

import java.util.Set;

public interface ITree<T> extends IUpperSemiLattice<T> {
	
	Set<T> getLeaves();

}
