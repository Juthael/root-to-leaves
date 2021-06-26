package com.tregouet.root_to_leaves.data;

import java.util.List;
import java.util.Set;

/*
 * A poset in which any element is either part of the tree, or connected to no other element.
 * 
 */
public interface ITree<T> extends IPoset<T> {
	
	T getRoot();
	
	Set<T> getTreeLeaves();
	
	Set<T> getTreeSubset();
	
	List<T> getSortedTreeSubet();

}
