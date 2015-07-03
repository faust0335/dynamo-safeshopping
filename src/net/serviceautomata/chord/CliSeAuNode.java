/*
 * This is a simplified revised version of jchord-src-0.1 from
 * https://code.google.com/p/joonion-jchord/downloads/list
 */
package net.serviceautomata.chord;

/**
 * This class simulates the internal and external environment for a CliSeAu node.
 * With regard to the position of the CliSeAu node, we can locate the immediate
 * successor and predecessor, and recursively all the other successors and
 * predecessors on the ring. By taking advantage of such methods, we can also
 * find the real CliSeAu node in charge of an event identifier. On the case of a
 * new joining node, we have to notify the predecessor and successor of the new
 * position with the help of a bootstrapping CliSeAu node, which has already been
 * on the ring, and in the meantime the finger table should be stabilized in
 * related to the current reality.
 * 
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
 */
public class CliSeAuNode{
	
	/**
	 * The position of the CliSeAu node on the Chord ring
	 */
	private int nodeID;
	
	/**
	 * This field points to the immediate predecessor of the current node on the
	 * Chord ring
	 */
	private CliSeAuNode predecessor;
	
	/**
	 * This field points to the immediate successor of the current node on the
	 * Chord ring
	 */
	private CliSeAuNode successor;

	/**
	 * A CliSeAu node holds a finger table to search for the successor of the
	 * given event identifier, sometimes itself or its successor
	 */
	private FingerTable fingerTable;
	
	/**
	 * The length of the identifier ring, which will be increased or decreased
	 * with respect to the changes to the FINGER_NUMBER
	 */
	public final static int RING_LENGTH =
			(int) Math.pow(2, FingerTable.FINGER_NUMBER);
	
	/**
	 * The constructor of a CliSeAu node to be initialized
	 * @param nodeID The node identifier
	 */
	public CliSeAuNode(int nodeID) {
		this.nodeID = nodeID;
		predecessor = null;
		successor = this;
		fingerTable = new FingerTable(this);
	}

	/**
	 * Lookup a successor (handler) of the given event identifier
	 * 
	 * @param eventID	An event identifier to lookup
	 * @return			The successor node of given key
	 */
	public CliSeAuNode findSuccessor(int eventID) {
		
		// The current node is responsible to handle the event
		if (eventID == this.getNodeID()) {
			return this;
		}
		else {
			// eventID is inside the range (this, successor]
			if (isBetween(this.getNodeID(), eventID, successor.getNodeID()) ||
					eventID == successor.getNodeID()) {
				return successor;
			} else {
				// Try to find the node closest to the target in the finger table
				CliSeAuNode node = closestPredecessor(eventID);
				/* 
				 * When such a node is coincidentally the current node, pass
				 * the search task to the successor
				 */
				if (node == this) {
					return successor.findSuccessor(eventID);
				}
				// Search the successor of the event identifier recursively
				return node.findSuccessor(eventID);
			}
		}
	}
	
	/**
	 * Check whether the position of the resource is located between the current
	 * node and the next node
	 * 
	 * @param currentID		The identifier of the current node
	 * @param eventID		The identifier of the event
	 * @param successorID	The identifier of the next node
	 * @return				A positive or negative feedback
	 */
	
	public boolean isBetween (int currentID, int eventID, int successorID) {
		
		currentID %= RING_LENGTH;
		eventID %= RING_LENGTH;
		successorID %= RING_LENGTH;
		
		// When only one node exists in the ring
		if (currentID == successorID) {
			if (eventID == currentID && eventID == successorID) {
				return false;
			// currentID --> eventID --> currentID
			} else {
				return true;
			}
		// Multiple nodes in the ring
		// nodeID = 0 not between currentID and successorID
		} else if (currentID < successorID) {
			// currentID --> eventID --> successorID
			if (eventID > currentID && eventID < successorID) {
				return true;
			} else {
				return false;
			}
		} else {
			// currentID --> eventID --> 0 --> successorID
			if ((eventID > currentID && eventID < RING_LENGTH) ||
			// currentID --> 0 --> eventID --> successorID
					(eventID >= 0 && eventID < successorID)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Search the finger table from the last entry to the first one to find a
	 * predecessor closest to the target event identifier
	 * 
	 * @param eventID	The identifier of the event
	 * @return			The closest predecessor of the event identifier
	 */
	private CliSeAuNode closestPredecessor(int eventID) {
		for (int i = FingerTable.FINGER_NUMBER - 1; i >= 0; i--) {
			Finger finger = fingerTable.getFinger(i);
			int fingerKey = finger.getNode().getNodeID();
			// currentID --> fingerKey --> eventID
			if (isBetween(this.getNodeID(), fingerKey, eventID)) {
				return finger.getNode();
			}
		}
		/* 
		 * When the relationship "currentID --> fingerKey --> (currentID + 1)"
		 * does not exist, the current node must be the predecessor closest to
		 * the event identifier.
		 */
		return this;
	}

	/**
	 * Joins a Chord ring with a node in the Chord ring
	 * 
	 * @param node a bootstrapping node
	 */
	public void join(CliSeAuNode node) {
		predecessor = null;
		successor = node.findSuccessor(this.getNodeID());
	}

	/**
	 * Verifies the successor, and tells the successor about this node.
	 * The function should be called periodically.
	 */
	public void stabilize() {
		CliSeAuNode node = successor.getPredecessor();
		// When the predecessor of the successor exists
		if (node != null) {
			// When the current node is the unique one in the ring
			if (this == successor ||
			/*
			 * Or the identifier of the new node is in the range of
			 * (currentID, successorID]
			 */
					isBetween(this.getNodeID(), node.getNodeID(), successor.getNodeID())) {
				successor = node; // Set the successor to the new node
			}
		}
		// Set the predecessor of the successor to the current node
		successor.notifyPredecessor(this);
	}
	
	/**
	 * Notify the predecessor of the joining of a new node
	 * 
	 * @param node the bootstrapping node to join
	 */
	
	private void notifyPredecessor(CliSeAuNode node) {
		// When the current node is the unique one in the ring
		if (predecessor == null ||
		/* 
		 * Or the identifier of the new node is in the range of
		 * (predecessorID, currentID)
		 */
				isBetween(predecessor.getNodeID(), node.getNodeID(), this.getNodeID())) {
			predecessor = node; // Set the predecessor to the new node
		}
	}

	/**
	 * Refresh finger table entries.
	 */
	public void fixFingers() {
		for (int i = 0; i < FingerTable.FINGER_NUMBER; i++) {
			Finger finger = fingerTable.getFinger(i);
			if (finger != null) {
				int fingerKey = finger.getFingerKey();
				finger.setNode(findSuccessor(fingerKey));
			}
		}
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public CliSeAuNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(CliSeAuNode predecessor) {
		this.predecessor = predecessor;
	}

	public CliSeAuNode getSuccessor() {
		return successor;
	}

	public void setSuccessor(CliSeAuNode successor) {
		this.successor = successor;
	}

	public FingerTable getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}
	
	/**
	 * Compare the identifier of the current node with the one of the given node
	 * to see the current node should be before or after the give node clockwise,
	 * or enjoy the same position with the given node.
	 * 
	 * @param node	The reference CliSeAu node
	 * @return		The identifier of the current node larger	return 1
	 * 				The identifier of the current node smaller	return -1
	 * 				Otherwise									return 0
	 */
	public int compareTo(CliSeAuNode node) {
		if (this.nodeID > node.nodeID) {
			return 1;
		} else if (this.nodeID < node.nodeID) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Convert the information about the CliSeAu node into a string
	 * 
	 * @return A string of the CliSeAu node information
	 */
	public String toString() {
		String nodeString = "Node " + Integer.toString(nodeID) + ":\n\n";
		nodeString += "The Predecessor of Node " + Integer.toString(nodeID) +
				" is " + Integer.toString(predecessor.getNodeID()) + ".\n";
		nodeString += "The Successor of Node " + Integer.toString(nodeID) +
				" is " + Integer.toString(successor.getNodeID()) + ".\n\n";
		nodeString += fingerTable.toString();
		return nodeString;
	}
}