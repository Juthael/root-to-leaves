package com.tregouet.root_to_leaves.data.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tregouet.root_to_leaves.data.IPoset;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.error.InvalidSemiLatticeExeption;

public class UpperSemiLattice<T> extends OutfittedPoset<T> implements IUpperSemiLattice<T> {

	//UNSAFE : no validation of arguments
	public UpperSemiLattice(List<T> sortedElements, int[][] incidenceMatrix) {
		super(sortedElements, incidenceMatrix);
	}
	
	//SAFE
	public UpperSemiLattice(Map<T, Set<T>> relation) throws InvalidSemiLatticeExeption {
		super(relation);
		validateArgument();
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
	
	private void validateArgument() throws InvalidSemiLatticeExeption {
		for (int i = 0 ; i < elements.size() ; i++) {
			for (int j = i + 1 ; j < elements.size() ; j++) {
				Set<T> pair = new HashSet<T>();
				pair.add(elements.get(i));
				pair.add(elements.get(j));
				Set<T> upperBounds = getUpperSet(pair);
				T alledgedSupremum = getSupremum(pair);
				if (upperBounds.contains(alledgedSupremum)) {
					for (T upperBound : upperBounds) {
						int comparisonResult = compare(alledgedSupremum, upperBound);
						if (comparisonResult != IPoset.LOWER_BOUND && comparisonResult != IPoset.EQUALS)
							throw new InvalidSemiLatticeExeption("UpperSemiLattice.validateArguments() : "
									+ "inconsistency.");
					}
				}
				else throw new InvalidSemiLatticeExeption("UpperSemiLattice.validateArguments() : "
						+ "inconsistency.");
			}
		}
	}

	@Override
	public T getSupremum(Set<T> subset) {
		int supremumIdxInSortedSet = -1;
		int upperBoundIdxInSortedSet;
		for (T upperBound : getUpperSet(subset)) {
			upperBoundIdxInSortedSet = sortedElements.indexOf(upperBound);
			if (upperBoundIdxInSortedSet > supremumIdxInSortedSet)
				supremumIdxInSortedSet = upperBoundIdxInSortedSet;
		}
		return sortedElements.get(supremumIdxInSortedSet);
	}

}
