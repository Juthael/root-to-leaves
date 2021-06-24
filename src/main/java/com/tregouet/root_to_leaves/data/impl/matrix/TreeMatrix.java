package com.tregouet.root_to_leaves.data.impl.matrix;

import java.util.List;
import java.util.Set;

import com.tregouet.root_to_leaves.data.ITree;

public class TreeMatrix<T> extends USLMatrix<T> implements ITree<T> {

	public TreeMatrix(List<T> elements, int[][] incidenceMatrix) {
		super(elements, incidenceMatrix);
	}

	@Override
	public Set<T> getLeaves() {
		return getMinimalElements();
	}

}
