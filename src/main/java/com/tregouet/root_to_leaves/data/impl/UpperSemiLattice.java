package com.tregouet.root_to_leaves.data.impl;

import java.util.List;

import com.tregouet.root_to_leaves.data.IUpperSemiLattice;

public class UpperSemiLattice<T> extends OutfittedPoset<T> implements IUpperSemiLattice<T> {

	public UpperSemiLattice(List<T> elements, int[][] incidenceMatrix) {
		super(elements, incidenceMatrix);
	}

	@Override
	public void addAsNewMax(T newMax) {
		int nbOfElements = elements.size() + 1;
		int[][] newExpMatrix = new int[nbOfElements][nbOfElements];
		for (int i = 0 ; i < nbOfElements ; i++){
			newExpMatrix[0][i] = 1;
		}
		int[][] newRedMatrix = new int[nbOfElements+1][nbOfElements+1];
		newRedMatrix[0][0] = 1;
		newRedMatrix[0][1] = 1;
		for (int i = 0 ; i < elements.size() ; i++) {
			for (int j = 0 ; j < elements.size() ; j++) {
				newExpMatrix[i+1][j+1] = transitiveExpansion[i][j];
				newRedMatrix[i+1][j+1] = transitiveReduction[i][j];
			}
		}
		elements.add(0, newMax);
		transitiveExpansion = newExpMatrix;
		transitiveReduction = newRedMatrix;
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
