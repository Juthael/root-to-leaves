package com.tregouet.root_to_leaves.utils;

import org.api.hyperdrive.NArray;

public class NArrayBool extends NArray<Boolean> {

	private final Boolean[] data;
	
	public NArrayBool(int[] dimensions) {
		super(dimensions);
		this.data = new Boolean[super.size()];
	}

	@Override
	public final Boolean get(int idx) {
		return data[idx];
	}

	@Override
	public final void set(int idx, Boolean value) {
		data[idx] = value;
	}
	
	public final Boolean getBoolean(int idx) {
		return data[idx];
	}
	
	public final void setBoolean(int idx, Boolean value) {
		data[idx] = value;
	}
	
	public final Boolean getBoolean(int[] coords) {
		return data[super.indexOf(coords)];
	}
	
	public final void setBoolean(int[] coords, Boolean value) {
		data[super.indexOf(coords)] = value;
	}

}
