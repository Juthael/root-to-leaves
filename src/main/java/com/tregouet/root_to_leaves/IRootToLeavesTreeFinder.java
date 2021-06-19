package com.tregouet.root_to_leaves;

import java.util.Set;

import com.tregouet.root_to_leaves.data.ITree;

public interface IRootToLeavesTreeFinder<T> {

	Set<ITree<T>> getRootToLeavesTrees();

}