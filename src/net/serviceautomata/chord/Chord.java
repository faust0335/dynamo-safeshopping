/**
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
 */
package net.serviceautomata.chord;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class Chord {

	SortedMap<Integer, CliSeAuNode> nodeMap =
			new TreeMap<Integer, CliSeAuNode>();
	Object[] keyArray;
	
	/**
	 * Create a Chord ring according to the node list
	 * 
	 * @param nodeIDs	The list of the nodes to be added
	 */
	public ArrayList<CliSeAuNode> createRing(Integer[] nodeIDList) {
		ArrayList<CliSeAuNode> nodeList = new ArrayList<CliSeAuNode>();
		/*
		 * Create a new node for the 0-th element in the node identifier list
		 */
		createNode(nodeIDList[0]);
		/*
		 * Take advantage of the first node to create a chord ring
		 * nodeIDList[0].predecessor --> NULL
		 * nodeIDList[0].successor --> nodeIDList[0]
		 */
		getSortedNode(0).createChord();
		/*
		 * nodeIDList[0].predecessor -->nodeIDList[0]
		 * nodeIDList[0].successor --> nodeIDList[0]
		 */
		getSortedNode(0).stabilize();
		
		for (int i = 1; i < nodeIDList.length; i++) {
			/*
			 * Create a new node for the i-th element in the node identifier list
			 */
			createNode(nodeIDList[i]);
			
			CliSeAuNode newNode = getSortedNode(i);
			CliSeAuNode lastNode = getSortedNode(i - 1);
			/*
			 * Joins the Chord ring with the node with the maximum identifier in
			 * the Chord ring.
			 * 
			 * newNode.predecessor --> NULL
			 * newNode.successor --> the node responsible for the identifier of
			 * the newNode
			 */
			newNode.join(lastNode);
			/*
			 * Verifies the last node, and tells the last node about the new node.
			 * newNode.successor --> successor.predecessor.
			 * newNode.successor.predecessor --> newNode
			 */
			newNode.stabilize();
		}
		
		for (int i = 0; i < nodeIDList.length; i++) {
			CliSeAuNode node = getSortedNode(i);
			/*
			 * Fix the fingers in the finger table of every node
			 */
			node.fixFingers();
			nodeList.add(getSortedNode(i)); // Add node in the node list
		}
		return nodeList;
	}
	
	/**
	 * Create a new node with the given node identifier
	 * 
	 * @param nodeID The node identifier
	 */
	public void createNode(Integer nodeID) {
		/*
		 * Instantiate a new node in terms of the node identifier
		 */
		CliSeAuNode node = new CliSeAuNode(nodeID);
		/*
		 * Add the key-value pair (key = node identifier, value = CliSeAu node)
		 * in the node map.
		 */
		nodeMap.put(node.getNodeID(), node);
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