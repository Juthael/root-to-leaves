package com.tregouet.root_to_leaves.utils;

public class CoordAdvancer {

	private CoordAdvancer() {
	}
	
	public static final boolean advanceInSpecifiedArea(int[] coords, int[] dimensions, int constant1, int constant2) {
		for(int i=0;i<coords.length;++i) {
	    	if (i != constant1 && i != constant2) {
	    		if (++coords[i] < dimensions[i])
	    			return true;
	    		else coords[i] = 0;
	    	}
	    }
	    return false;
	}	

}
