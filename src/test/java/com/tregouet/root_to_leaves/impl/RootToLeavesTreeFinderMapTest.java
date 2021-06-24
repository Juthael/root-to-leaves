package com.tregouet.root_to_leaves.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.junit.Test;

import com.tregouet.root_to_leaves.IRootToLeavesTreeFinder;
import com.tregouet.root_to_leaves.data.ITree;
import com.tregouet.root_to_leaves.data.IUpperSemiLattice;
import com.tregouet.root_to_leaves.data.impl.map.Tree;
import com.tregouet.root_to_leaves.data.impl.map.UpperSemiLattice;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class RootToLeavesTreeFinderMapTest {

	// whenTreesRequestedThenExpectedReturned()
	String max = "MAX";
	String a = "A";
	String b = "B";
	String c = "C";
	String ab = "AB";
	String ac = "AC";
	String bc = "BC";
	Map<String, Set<String>> mIaIIabIIacIbIIbc;
	Map<String, Set<String>> mIaIIabIIacIcIIbc;
	Map<String, Set<String>> mIaIIacIbIIabIIbc;
	Map<String, Set<String>> mIbIIabIIbcIcIIac;
	Map<String, Set<String>> mIaIIabIcIIacIIbc;
	Map<String, Set<String>> mIbIIabIcIIacIIbc;
	Map<String, Set<String>> mIaIIabIbIIbcIcIIac;
	Map<String, Set<String>> mIaIIacIbIIabIcIIbc;
	Map<String, Set<String>> relation = new HashMap<String, Set<String>>();
	IUpperSemiLattice<String> uSL;
	Set<ITree<String>> expectedTrees = new HashSet<ITree<String>>();	
	
	@Test
	public void whenAdvanceInSpecifiedAreaThenTraversalAsExpected() {
		// members
		int[] coords = new int[4];
		int[] limits = new int[4];
		int constant1Idx = 1;
		int constant2Idx = 3;
		int[][] expected;
		//setUp
		coords[1] = 2;
		coords[3] = 4;
		limits[0] = 3;
		limits[1] = 3;
		limits[2] = 3;
		limits[3] = 5;
		expected = new int[9][];
		expected[0] = new int[] {0,2,0,4};
		expected[1] = new int[] {1,2,0,4};
		expected[2] = new int[] {2,2,0,4};
		expected[3] = new int[] {0,2,1,4};
		expected[4] = new int[] {1,2,1,4};
		expected[5] = new int[] {2,2,1,4};
		expected[6] = new int[] {0,2,2,4};
		expected[7] = new int[] {1,2,2,4};
		expected[8] = new int[] {2,2,2,4};
		//test
		boolean asExpected = true;
		int expIdx = 0;
		do {
			//System.out.println(Arrays.toString(coords));
			asExpected = (Arrays.equals(coords, expected[expIdx]));
			expIdx++;
		}
		while (RootToLeavesTreeFinderMap.advanceInSpecifiedArea(coords, limits, constant1Idx, constant2Idx) && asExpected == true);
		assertTrue(asExpected);
	}

	//FAILS
	@Test
	public void whenLargeInputThenOutputStillReturned() throws IOException {
		Map<Set<Integer>, Set<Set<Integer>>> relationMap = buildLargeUpperSemiLattice();
		IUpperSemiLattice<Set<Integer>> semiLattice = new UpperSemiLattice<Set<Integer>>(relationMap);
		//visualize lattice
		buildGraph(semiLattice);
		//build Trees
		IRootToLeavesTreeFinder<Set<Integer>> tF = new RootToLeavesTreeFinderMap<Set<Integer>>();
		tF.input(semiLattice);
		Set<ITree<Set<Integer>>> trees = tF.output();
		System.out.println(trees.size() + " trees have been returned.");
		assertTrue(!trees.isEmpty());
	}
	
	@Test
	public void whenTreesRequestedThenExpectedReturned() {
		//set up
		relation.put(max, new HashSet<String>());
		relation.put(a, new HashSet<String>());
		relation.put(b, new HashSet<String>());
		relation.put(c, new HashSet<String>());
		relation.put(ab, new HashSet<String>());
		relation.put(ac, new HashSet<String>());
		relation.put(bc, new HashSet<String>());
		setRelations();
		uSL = new UpperSemiLattice<String>(relation);
		expectedTrees.add(new Tree<String>(mIaIIabIIacIbIIbc));
		expectedTrees.add(new Tree<String>(mIaIIabIIacIcIIbc));
		expectedTrees.add(new Tree<String>(mIaIIacIbIIabIIbc));
		expectedTrees.add(new Tree<String>(mIbIIabIIbcIcIIac));
		expectedTrees.add(new Tree<String>(mIaIIabIcIIacIIbc));
		expectedTrees.add(new Tree<String>(mIbIIabIcIIacIIbc));
		expectedTrees.add(new Tree<String>(mIaIIabIbIIbcIcIIac));
		expectedTrees.add(new Tree<String>(mIaIIacIbIIabIcIIbc));
		//test
		Set<ITree<String>> returnedTrees;
		IRootToLeavesTreeFinder<String> tF = new RootToLeavesTreeFinderMap<>();
		System.out.println(uSL.toString() + System.lineSeparator());
		tF.input(uSL);
		returnedTrees = tF.output();
		for (ITree<String> tree : returnedTrees)
			System.out.println(tree.toString() + System.lineSeparator());
		assertTrue(returnedTrees.equals(expectedTrees));
	}
	
	private void buildGraph(IUpperSemiLattice<Set<Integer>> semiLattice) throws IOException {
		//Get file name and path
		String fileName;
		String graphPath;
		System.out.print("Enter the name of the graph file :");
		Scanner sc = new Scanner(System.in);
		fileName = sc.nextLine();
		System.out.println(System.lineSeparator() + "Enter a location for the graph file : ");
		System.out.println("Ex : D:\\ProjetDocs\\essais_viz\\");
		graphPath = sc.nextLine();
		sc.close();
		System.out.println(System.lineSeparator() + "Got it.");
		//buildGraph
		Graph<String, DefaultEdge> graph = GraphTypeBuilder
				.<String, DefaultEdge> directed().allowingMultipleEdges(false).allowingSelfLoops(false)
				.edgeClass(DefaultEdge.class).weighted(false).buildGraph();
		for (Set<Integer> elem : semiLattice.getSet()) {
			graph.addVertex(elem.toString());
		}
		for (Entry<Set<Integer>, Set<Set<Integer>>> entry : semiLattice.getSuccRelationMap().entrySet()) {
			for (Set<Integer> subSet : entry.getValue()) {
				graph.addEdge(entry.getKey().toString(), subSet.toString());
			}
		}
		//convert in DOT format
		DOTExporter<String,DefaultEdge> exporter = new DOTExporter<>();
		exporter.setVertexAttributeProvider((v) -> {
			Map<String, Attribute> map = new LinkedHashMap<>();
			map.put("label", DefaultAttribute.createAttribute(v));
			return map;
		});
		Writer writer = new StringWriter();
		exporter.exportGraph(graph, writer);
		String stringDOT = writer.toString();
		/* TO SEE DOT FILE : 
		 * System.out.println(writer.toString);
		 */
		MutableGraph dotGraph = new Parser().read(stringDOT);
		Graphviz.fromGraph(dotGraph).width(semiLattice.getSet().size()*100)
			.render(Format.PNG).toFile(new File(graphPath + fileName));
	}
	
	private Map<Set<Integer>, Set<Set<Integer>>> buildLargeUpperSemiLattice(){
		return buildUSLFromPowerSetOfNElements(5);
	}
	
	private Map<Set<Integer>, Set<Set<Integer>>> buildUSLFromPowerSetOfNElements(int n){
		Map<Set<Integer>, Set<Set<Integer>>> relationMap = new HashMap<Set<Integer>, Set<Set<Integer>>>();
		int[] atoms = new int[n];
		for (int i = 0 ; i < n ; i++) {
			atoms[i] = i;
		}
		List<Set<Integer>> powerSet = new ArrayList<Set<Integer>>();
		for (int i = 0 ; i < (1 << n) ; i++) {
			Set<Integer> subset = new HashSet<Integer>();
			for (int j = 0 ; j < n ; j++) {
				if (((1 << j) & i) > 0)
					subset.add(atoms[j]);
			}
			powerSet.add(subset);
		}
		for (Set<Integer> subset : powerSet) {
			if (!subset.isEmpty())
				relationMap.put(subset, new HashSet<Set<Integer>>());
		}
		for (int i = 0 ; i < powerSet.size() - 1 ; i++) {
			for (int j = i+1 ; j < powerSet.size() ; j++) {
				if (powerSet.get(i).containsAll(powerSet.get(j)) && !powerSet.get(j).isEmpty())
					relationMap.get(powerSet.get(i)).add(powerSet.get(j));
				else if (powerSet.get(j).containsAll(powerSet.get(i)) && !powerSet.get(i).isEmpty())
					relationMap.get(powerSet.get(j)).add(powerSet.get(i));
			}
		}
		return relationMap;
	}
	
	private void setmIaIIabIbIIbcIcIIac() {
		mIaIIabIbIIbcIcIIac = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIabIbIIbcIcIIac.put(key, new HashSet<String>());
		}
		mIaIIabIbIIbcIcIIac.get(max).add(a);
		mIaIIabIbIIbcIcIIac.get(max).add(b);
		mIaIIabIbIIbcIcIIac.get(max).add(c);
		mIaIIabIbIIbcIcIIac.get(max).add(ab);
		mIaIIabIbIIbcIcIIac.get(max).add(ac);
		mIaIIabIbIIbcIcIIac.get(max).add(bc);
		mIaIIabIbIIbcIcIIac.get(a).add(ab);
		mIaIIabIbIIbcIcIIac.get(b).add(bc);
		mIaIIabIbIIbcIcIIac.get(c).add(ac);
	}
	
	private void setmIaIIabIcIIacIIbc() {
		mIaIIabIcIIacIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIabIcIIacIIbc.put(key, new HashSet<String>());
		}
		mIaIIabIcIIacIIbc.remove(b);
		mIaIIabIcIIacIIbc.get(max).add(a);
		mIaIIabIcIIacIIbc.get(max).add(c);
		mIaIIabIcIIacIIbc.get(max).add(ab);
		mIaIIabIcIIacIIbc.get(max).add(ac);
		mIaIIabIcIIacIIbc.get(max).add(bc);
		mIaIIabIcIIacIIbc.get(c).add(ac);
		mIaIIabIcIIacIIbc.get(c).add(bc);
		mIaIIabIcIIacIIbc.get(a).add(ab);	
	}
	
	private void setmIaIIabIIacIbIIbc() {
		mIaIIabIIacIbIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIabIIacIbIIbc.put(key, new HashSet<String>());
		}
		mIaIIabIIacIbIIbc.remove(c);
		mIaIIabIIacIbIIbc.get(max).add(a);
		mIaIIabIIacIbIIbc.get(max).add(b);
		mIaIIabIIacIbIIbc.get(max).add(ab);
		mIaIIabIIacIbIIbc.get(max).add(ac);
		mIaIIabIIacIbIIbc.get(max).add(bc);
		mIaIIabIIacIbIIbc.get(a).add(ab);
		mIaIIabIIacIbIIbc.get(a).add(ac);
		mIaIIabIIacIbIIbc.get(b).add(bc);	
	}
	
	private void setmIaIIabIIacIcIIbc() {
		mIaIIabIIacIcIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIabIIacIcIIbc.put(key, new HashSet<String>());
		}	
		mIaIIabIIacIcIIbc.remove(b);
		mIaIIabIIacIcIIbc.get(max).add(a);
		mIaIIabIIacIcIIbc.get(max).add(c);
		mIaIIabIIacIcIIbc.get(max).add(ab);
		mIaIIabIIacIcIIbc.get(max).add(ac);
		mIaIIabIIacIcIIbc.get(max).add(bc);
		mIaIIabIIacIcIIbc.get(a).add(ab);
		mIaIIabIIacIcIIbc.get(a).add(ac);
		mIaIIabIIacIcIIbc.get(c).add(bc);
	}
	
	private void setmIaIIacIbIIabIcIIbc() {
		mIaIIacIbIIabIcIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIacIbIIabIcIIbc.put(key, new HashSet<>());
		}
		mIaIIacIbIIabIcIIbc.get(max).add(a);
		mIaIIacIbIIabIcIIbc.get(max).add(b);
		mIaIIacIbIIabIcIIbc.get(max).add(c);
		mIaIIacIbIIabIcIIbc.get(max).add(ab);
		mIaIIacIbIIabIcIIbc.get(max).add(ac);
		mIaIIacIbIIabIcIIbc.get(max).add(bc);
		mIaIIacIbIIabIcIIbc.get(a).add(ac);
		mIaIIacIbIIabIcIIbc.get(b).add(ab);
		mIaIIacIbIIabIcIIbc.get(c).add(bc);
	}
	
	private void setmIaIIacIbIIabIIbc() {
		mIaIIacIbIIabIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIaIIacIbIIabIIbc.put(key, new HashSet<String>());
		}
		mIaIIacIbIIabIIbc.remove(c);
		mIaIIacIbIIabIIbc.get(max).add(a);
		mIaIIacIbIIabIIbc.get(max).add(b);
		mIaIIacIbIIabIIbc.get(max).add(ab);
		mIaIIacIbIIabIIbc.get(max).add(ac);
		mIaIIacIbIIabIIbc.get(max).add(bc);
		mIaIIacIbIIabIIbc.get(b).add(ab);
		mIaIIacIbIIabIIbc.get(b).add(bc);
		mIaIIacIbIIabIIbc.get(a).add(ac);	
	}
	
	private void setmIbIIabIcIIacIIbc() {
		mIbIIabIcIIacIIbc = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIbIIabIcIIacIIbc.put(key, new HashSet<String>());
		}	
		mIbIIabIcIIacIIbc.remove(a);
		mIbIIabIcIIacIIbc.get(max).add(b);
		mIbIIabIcIIacIIbc.get(max).add(c);
		mIbIIabIcIIacIIbc.get(max).add(ab);
		mIbIIabIcIIacIIbc.get(max).add(ac);
		mIbIIabIcIIacIIbc.get(max).add(bc);
		mIbIIabIcIIacIIbc.get(c).add(ac);
		mIbIIabIcIIacIIbc.get(c).add(bc);
		mIbIIabIcIIacIIbc.get(b).add(ab);
	}
	
	private void setmIbIIabIIbcIcIIac() {
		mIbIIabIIbcIcIIac = new HashMap<String, Set<String>>();
		for (String key : relation.keySet()) {
			mIbIIabIIbcIcIIac.put(key, new HashSet<String>());
		}	
		mIbIIabIIbcIcIIac.remove(a);
		mIbIIabIIbcIcIIac.get(max).add(b);
		mIbIIabIIbcIcIIac.get(max).add(c);
		mIbIIabIIbcIcIIac.get(max).add(ab);
		mIbIIabIIbcIcIIac.get(max).add(ac);
		mIbIIabIIbcIcIIac.get(max).add(bc);
		mIbIIabIIbcIcIIac.get(b).add(ab);
		mIbIIabIIbcIcIIac.get(b).add(bc);
		mIbIIabIIbcIcIIac.get(c).add(ac);	
	}
	
	private void setRelation() {
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
	}
	
	private void setRelations() {
		setmIaIIabIIacIbIIbc();
		setmIaIIabIIacIcIIbc();
		setmIaIIacIbIIabIIbc();
		setmIbIIabIIbcIcIIac();
		setmIaIIabIcIIacIIbc();
		setmIbIIabIcIIacIIbc();
		setmIaIIabIbIIbcIcIIac();
		setmIaIIacIbIIabIcIIbc();
		setRelation();
	}

}
