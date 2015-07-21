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
 * @author Xu, Yinhua	
 */
public class Chord {

	/**
	 * A node hash map which stores the CliSeAu nodes (as values)
	 * corresponding to their identifiers (as keys)
	 */
	public HashMap<Integer, CliSeAuNode> nodeMap =
			new HashMap<Integer, CliSeAuNode>();
	
	/**
	 * Insert a new node with a given identifier into a Chord ring
	 * 
	 * @param nodeID	The identifier of the node to be inserted into the Chord
	 * 					ring
	 */
	public void insertNode(Integer nodeID) {
		
		nodeID = Integer.valueOf(nodeID.intValue() % CliSeAuNode.RING_LENGTH);
		
		// The Chord ring is empty
		if (nodeMap.isEmpty()) {
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
			if (!nodeMap.containsKey(nodeID)) {
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
	}
	
	/**
	 * Remove the node with the given identifier on the ring (also remove the
	 * mapping of the given identifier)
	 * 
	 * @param nodeID the node identifier
	 */
	public void removeNode (Integer nodeID) {
		
		if (nodeMap.containsKey(nodeID)) {
			/*
			 * targetPredecessor <--> target <--> targetSuccessor
			 */
			CliSeAuNode target = nodeMap.get(nodeID);
			CliSeAuNode targetPredecessor = target.getPredecessor();
			CliSeAuNode targetSuccessor = target.getSuccessor();
			
			/*
			 * targetPredecessor.successor --> targetSuccessor
			 * targetSuccessor.predecessor --> targetPredecessor
			 * 
			 * and still
			 * 
			 * target.predecessor --> targetPredecessor
			 * target.successor --> targetSuccessor
			 */
			targetPredecessor.setSuccessor(targetSuccessor);
			targetSuccessor.setPredecessor(targetPredecessor);
			/*
			 * Delete the mapping nodeID --> target from the node map
			 */
			nodeMap.remove(nodeID);
			
			for (Integer key: nodeMap.keySet()) {
				nodeMap.get(key).fixFingers();
			}
			
			System.out.println("NODE " + nodeID.intValue() + " IS REMOVED\n");
		}
	}
	
	/**
	 * Search for the predecessor nearest to the node with the given identifier
	 * 
	 * @param nodeID	The new node with the identifier
	 * @return			The nearest predecessor of the new node
	 */
	public CliSeAuNode nearestPredecessor(Integer nodeID) {
		
		ArrayList<Integer> members = new ArrayList<Integer>();
		
		if (nodeID == null || nodeMap.isEmpty()) {
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
				/*
				 * Compute the distance between the current node and every existing
				 * node on Chord ring
				 */
				int differ = (nodeID.intValue() - member.intValue())
						% CliSeAuNode.RING_LENGTH;
				/*
				 * Find the minimum distance and its corresponding node existing
				 * on the Chord ring
				 */
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
	public void createRing(Integer[] nodeIDList) {
		
		if (nodeIDList != null) {
			/*
			 * Delete all mappings in the node map
			 */
			nodeMap.clear();
			
			/*
			 * Create the new nodes for all the elements in the node identifier list
			 */
			for (int i = 0; i < nodeIDList.length; i++) {
				Integer nodeID = nodeIDList[i];
				insertNode(nodeID);
			}
		}
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
	 * Convert the information of all CliSeAu nodes on the Chord ring
	 * 
	 * @return The information of the Chord ring
	 */
	public String toString() {
		String chordString = "";
		
		if (nodeMap.isEmpty()) {
			chordString = "EMPTY RING\n";
		} else {
			for (Integer key: nodeMap.keySet()) {
				chordString += nodeMap.get(key).toString() + "\n";
			}
		}
		
		return chordString;
	}
}