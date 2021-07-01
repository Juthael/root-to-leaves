package com.tregouet.root_to_leaves.data.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.tregouet.root_to_leaves.data.IPoset;

public class OutfittedPosetTest {

	String max = "MAX";
	String a = "A";
	String b = "B";
	String c = "C";
	String ab = "AB";
	String ac = "AC";
	String bc = "BC";
	//for Poset constructor with map argument.
	Map<String, Set<String>> relation = new HashMap<String, Set<String>>();
	IPoset<String> posetBuiltFromMap;
	//for Poset constructor with matrix argument
	List<String> elements = new ArrayList<String>();
	int[][] matrix;
	IPoset<String> posetBuiltFromMatrix;
	
	@Before
	public void setUp() throws Exception {
		//encode relation as a map
		relation.put(max, new HashSet<String>());
		relation.put(a, new HashSet<String>());
		relation.put(b, new HashSet<String>());
		relation.put(c, new HashSet<String>());
		relation.put(ab, new HashSet<String>());
		relation.put(ac, new HashSet<String>());
		relation.put(bc, new HashSet<String>());
		relation.get(max).add(a);
		relation.get(max).add(b);
		relation.get(max).add(c);
		relation.get(max).add(ab);
		relation.get(max).add(ac);
		relation.get(max).add(bc);
		relation.get(a).add(ab);
		relation.get(a).add(ac);
		relation.get(b).add(ab);
		relation.get(b).add(bc);
		relation.get(c).add(ac);
		relation.get(c).add(bc);
		//encode relation as a matrix
		elements.add(max);
		elements.add(a);
		elements.add(b);
		elements.add(c);
		elements.add(ab);
		elements.add(ac);
		elements.add(bc);
		matrix = new int[][] {
			{1, 1, 1, 1, 1, 1, 1},
			{0, 1, 0, 0, 1, 1, 0},
			{0, 0, 1, 0, 1, 0, 1},
			{0, 0, 0, 1, 0, 1, 1},
			{0, 0, 0, 0, 1, 0, 0},
			{0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 0, 0, 0, 1}
		};	
		posetBuiltFromMap = new OutfittedPoset<String>(relation);
		posetBuiltFromMatrix = new OutfittedPoset<String>(elements, matrix);
	}

	@Test
	public void whenEquivalentConstructorArgInDiffFormatThenSamePosetBuilt() {
		System.out.println(posetBuiltFromMap.toString() + System.lineSeparator());
		System.out.println(posetBuiltFromMatrix.toString() + System.lineSeparator());
		assertTrue(posetBuiltFromMap.equals(posetBuiltFromMatrix));
	}

}
