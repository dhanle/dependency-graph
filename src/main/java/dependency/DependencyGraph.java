package dependency;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Main takes a file of dependencies and prints out its dependency graph.
 * 
 * @author dhanle
 */
public class DependencyGraph {
	
	public static void main(String[] args) {
		// Uses default graph.txt if no arguments and only 1 argument is needed as the file path
		String filePath = "";
		if (args.length == 0) {
			System.out.println("No file provided, using default graph.txt as input");
			filePath = "graph.txt";
		} else if (args.length == 1) {
			System.out.println("Using " + args[0] + " as input");
			filePath = args[0];
		} else {
			System.out.println("Too many arguments, please provide just the path of the input file");
			return;
		}
		
		// Generates a list of dependencies from the file, returns if there is a problem reading the file
		List<String> graph = readFile(filePath);
		if (graph == null || graph.isEmpty()) {
			System.out.println("Error reading file " + filePath);
			return;
		}
		
		// To find all the root nodes, we need to know all nodes that exist and all dependencies that exist
		Set<String> allNodes = new HashSet<String>();
		Set<String> allDeps  = new HashSet<String>();
		// From the list of dependencies, create a hash map of node and list of dependencies of that node
		// returns if the file is not formatted correctly such as A->B on each line
		Map<String, List<String>> graphMap = populateMap(graph, allNodes, allDeps);
		if (graphMap == null) {
			System.out.println("File is not formatted correctly, each lines needs to look like this A->B");
			return;
		}
		
		// To find all the root nodes, any node not in the dependency list is a root node 
		allNodes.removeAll(allDeps);
		List<String> rootNodes = new ArrayList<String>(allNodes);
		
		// For each root node print out the graph to standard out
		for (String rootNode : rootNodes) {
			System.out.println(rootNode);
			printGraph(graphMap, rootNode, "", new Stack<String>());
		}
		System.out.println("* denotes a circular dependency");
	}
	
	/**
	 * Prints the graph of the hash map dependencies recursively while keeping track of 
	 * the current dependencies in a stack to watch for circular dependencies.
	 * Each call prints out the prefix for that node if there is one and then the node itself.
	 * 
	 * @param graphMap
	 * @param node
	 * @param prefix
	 * @param currentDeps
	 */
	private static void printGraph(Map<String, List<String>> graphMap, String node, String prefix, Stack<String> currentDeps) {
		// Find all dependencies for the given node, if none just return as terminal case
		List<String> depList = graphMap.get(node);
		if (depList == null) {
			return;
		}
		// Iterate through all the dependencies of the current node
		for (int i = 0; i < depList.size(); i++) {
			String dep = depList.get(i);
			// The prefix carries through if there is one all the spaces and | before the actual node
			System.out.print(prefix);	
			// If final dependency of that node use the \ instead of the | visually
			if (i == depList.size() - 1) {
				System.out.print("\\");
				prefix = prefix + "  ";
			} else {
				System.out.print("|");
				prefix = prefix + "| ";
			}
			System.out.print("_");
			// If the dependency has already been shown in this current thread then add a * and return to prevent an infinite loop
			if (currentDeps.contains(dep)) {
				System.out.println(dep + "*");
				return;
			} else {
				System.out.println(dep);
			}
			// Add the current dependency to the current thread to watch for circular dependencies
			currentDeps.push(dep);
			// Recursive call with the dependency now as the current node
			printGraph(graphMap, dep, prefix, currentDeps);
			// Remove the current dependency since we are moving back up the chain
			currentDeps.pop();
			// Remove the last 2 characters of the prefix as we move back up the chain
			prefix = prefix.substring(0, prefix.length() - 2);
		}
	}
	
	/**
	 * Populates a hash map of node and list of dependencies from the given list of relationships.
	 * Also, keeps track of all the nodes ever seen and all dependencies ever seen, important to find the root nodes.
	 * 
	 * @param graphList
	 * @param allNodes
	 * @param allDeps
	 * @return HashMap of nodes and list of dependencies
	 */
	private static Map<String, List<String>> populateMap(List<String> graphList, Set<String> allNodes, Set<String> allDeps) {
		Map<String, List<String>> graphMap = new HashMap<String, List<String>>();
		// Iterate through the list of relationships given from the file
		for (String graph : graphList) {
			// If the format is not like this A->B exactly then return as file is corrupted
			if (graph.length() == 4 && graph.charAt(1) == '-' && graph.charAt(2) == '>') {
				String node = graph.substring(0, 1);
				String dep = graph.substring(3, 4);
				// Add node and dependency to set of all nodes and dependencies
				allNodes.add(node);
				allDeps.add(dep);
				// If the node already exists in the map then just add the dependency to the list, otherwise add a new pair to the map
				if (graphMap.containsKey(node)) {
					List<String> depList = graphMap.get(node);
					depList.add(dep);
				} else {
					List<String> depList = new ArrayList<String>();
					depList.add(dep);
					graphMap.put(node, depList);
				}
			} else {
				return null;
			}
		}
		
		return graphMap;
	}
	
	/**
	 * Reads the file from the file path and returns null if it cannot read the file.
	 * 
	 * @param filePath
	 * @return Each line from the file as a list of strings
	 */
	private static List<String> readFile(String filePath) { 
		try {
			// File path could be relative to the runtime location or an absolute path
			File f = new File(filePath);
			List<String> lines;
			lines = FileUtils.readLines(f, "UTF-8");
			return lines;
		} catch (IOException e) {
			// Catch the IO exception and return null for better error handling
			e.printStackTrace();
			return null;
		}
	}
 }
