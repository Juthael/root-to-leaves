package com.tregouet.root_to_leaves.data.impl.matrix;

import java.util.List;

import com.tregouet.root_to_leaves.data.IUpperSemiLattice;

public class USLMatrix<T> extends OutfittedPoset<T> implements IUpperSemiLattice<T> {

	public USLMatrix(List<T> elements, int[][] incidenceMatrix) {
		super(elements, incidenceMatrix);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addAsNewMax(T newMax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getMaximum() {
		return sortedElements.get(0);
	}

	@Override
	public T getRoot() {
		return sortedElements.get(0);
	}

}
