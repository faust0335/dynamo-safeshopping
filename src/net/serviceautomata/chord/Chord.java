/**
 * @author Chen, Yiqun
 * @author Liu, Yi
 * @author Xu, Yinhua
 */
package net.serviceautomata.chord;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class Chord {

	ArrayList<CliSeAuNode> nodeList = new ArrayList<CliSeAuNode>();
	SortedMap<Integer, CliSeAuNode> nodeMap =
			new TreeMap<Integer, CliSeAuNode>();
	Object[] keyArray;
	
	/**
	 * Create a Chord ring according to the node list
	 * 
	 * @param nodeIDs	The list of the nodes to be added
	 */
	public void createRing(Integer[] nodeIDs) {
		for (int i = 0; i < nodeIDs.length; i++) {
			createNode(nodeIDs[i]);
		}
	}
	
	/**
	 * Create a new node with the given node identifier
	 * 
	 * @param nodeID The node identifier
	 */
	public void createNode(Integer nodeID) {
		CliSeAuNode node = new CliSeAuNode(nodeID);
		
		nodeList.add(node);
		nodeMap.put(node.getNodeID(), node);
	}

	/**
	 * Find the i-th node in the CliSeAu node list
	 * 
	 * @param i	The order number of the element to search
	 * @return	The i-th element of the list
	 */
	public CliSeAuNode getNode(int i) {
		return (CliSeAuNode) nodeList.get(i);
	}
	
	/**
	 * Find the i-th node in the <nodeID, CliSeAuNode> map
	 * 
	 * @param i	The order number of the element to search
	 * @return	The i-th element of the map
	 */
	public CliSeAuNode getSortedNode(int i) {
		if (keyArray == null) {
			keyArray = nodeMap.keySet().toArray();
		}
		return (CliSeAuNode) nodeMap.get(keyArray[i]);
	}
}