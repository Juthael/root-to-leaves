package com.tregouet.root_to_leaves.error;

public class InvalidSemiLatticeExeption extends Exception {

	private static final long serialVersionUID = -5464382551763696265L;

	public InvalidSemiLatticeExeption() {
	}

	public InvalidSemiLatticeExeption(String message) {
		super(message);
	}

	public InvalidSemiLatticeExeption(Throwable cause) {
		super(cause);
	}

	public InvalidSemiLatticeExeption(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidSemiLatticeExeption(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
