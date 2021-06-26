package com.tregouet.root_to_leaves.data.impl.map;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tregouet.root_to_leaves.data.ITree;

public class Tree<T> extends UpperSemiLattice<T> implements ITree<T> {

	private final Set<T> leaves;
	
	//for test use only
	public Tree(Map<T, Set<T>> treeRelation) {
		super(treeRelation);
		leaves = getMinimalElements();
	}
	
	public Tree(T seed) {
		super(seed);
		leaves = new HashSet<T>();
		leaves.add(seed);
	}
	
	//the specified subtrees must not have any common element
	public Tree(T root, Set<ITree<T>> subTrees) {
		super(root, subTrees);
		leaves = new HashSet<T>();
		for (ITree<T> subtree : subTrees) {
			leaves.addAll(subtree.getTreeLeaves());
		}
	}

	@Override
	public Set<T> getTreeLeaves() {
		return leaves;
	}

}
