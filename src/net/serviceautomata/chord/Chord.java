/*
 * This is a simplified revised version of jchord-src-0.1 from
 * https://code.google.com/p/joonion-jchord/downloads/list
 */
package net.serviceautomata.chord;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main class of the routing on a Chord ring, which manages and monitors the
 * status of the Chord ring. With this class we can have an overview of the Chord
 * ring and may create more CliSeAu nodes to join into the ring.
 * 
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
 */
public class Chord {

	/**
	 * A node hash map which stores the CliSeAu nodes (as values)
	 * corresponding to their identifiers (as keys)
	 */
	private HashMap<Integer, CliSeAuNode> nodeMap =
			new HashMap<Integer, CliSeAuNode>();
	
	/**
	 * Insert a new node with a given identifier into a Chord ring
	 * 
	 * @param nodeID	The identifier of the node to be inserted into the Chord
	 * 					ring
	 * @return			
	 */
	public CliSeAuNode insertNode(Integer nodeID) {
		
		nodeID = Integer.valueOf(nodeID.intValue() % CliSeAuNode.RING_LENGTH);
		
		// The Chord ring is empty
		if (nodeMap.size() == 0) {
			/*
			 * Create a new node for the given node identifier
			 */
			createNode(nodeID);
			
			/*
			 * nodeIDList[0].predecessor -->nodeIDList[0]
			 * nodeIDList[0].successor --> nodeIDList[0]
			 */
			nodeMap.get(nodeID).stabilize();
		} else {
			// The node with the identifier has already existed
			if (!nodeMap.keySet().contains(nodeID)) {
				// The Chord ring is not full
				if (nodeMap.size() < CliSeAuNode.RING_LENGTH) {
					/*
					 * Find the bootstrapping node nearest to the new coming node
					 */
					CliSeAuNode bootstrap = nearestPredecessor(nodeID);
					
					createNode(nodeID);
					
					/*
					 * Joins the Chord ring with the start node in the Chord ring.
					 * 
					 * newNode.predecessor --> NULL
					 * newNode.successor --> the node responsible for the identifier of
					 * the newNode
					 */
					CliSeAuNode newNode = nodeMap.get(nodeID);
					
					newNode.join(bootstrap);
					
					CliSeAuNode preceding =
							newNode.getSuccessor().getPredecessor();
					
					newNode.stabilize();
					
					if (preceding == null) {
						newNode.getSuccessor().stabilize();
					} else {
						preceding.stabilize();
					}
					
					for (Integer key: nodeMap.keySet()) {
					/*
					 * Fix the fingers in the finger table of every node
					 */
						nodeMap.get(key).fixFingers();
					}
				}
			}
		}
		return nodeMap.get(nodeID);
	}
	
	/**
	 * Search for the predecessor nearest to the node with the given identifier
	 * 
	 * @param nodeID	The new node with the identifier
	 * @return			The nearest predecessor of the new node
	 */
	public CliSeAuNode nearestPredecessor(Integer nodeID) {
		
		ArrayList<Integer> members = new ArrayList<Integer>();
		
		if (nodeID == null || nodeMap.size() == 0) {
			return null;
		}
		
		for (Integer key: nodeMap.keySet()) {
			members.add(key);
		}
		
		if (members.contains(nodeID)) {
			return nodeMap.get(nodeID);
		} else {
			int difference = CliSeAuNode.RING_LENGTH;
			int predecessor = nodeID.intValue();
			for (Integer member: members) {
				int differ = (nodeID.intValue() - member.intValue())
						% CliSeAuNode.RING_LENGTH;
				if (differ < difference) {
					difference = differ;
					predecessor = member;
				}
			}
			return nodeMap.get(Integer.valueOf(predecessor));
		}
	}
	
	/**
	 * Create a Chord ring according to the node list
	 * 
	 * @param nodeIDs	The list of the nodes identifiers representing the nodes
	 * 					to be added
	 * @return			The list of the sorted CliSeAu nodes
	 */
	public ArrayList<CliSeAuNode> createRing(Integer[] nodeIDList) {
		
		if (nodeIDList == null) {
			return null;
		}
		
		nodeMap.clear();
		
		ArrayList<CliSeAuNode> nodeList = new ArrayList<CliSeAuNode>();
		
		/*
		 * Create the new nodes for all the elements in the node identifier list
		 */
		for (int i = 0; i < nodeIDList.length; i++) {
			Integer nodeID = nodeIDList[i];
			insertNode(nodeID);
			nodeList.add(nodeMap.get(nodeID));
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
}