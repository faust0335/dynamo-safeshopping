/**
<<<<<<< HEAD
 * @author Chen, Yiqun
 * @author Liu, Yi
 * @author Xu, Yinhua
=======
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
>>>>>>> 359db2d36318fc20604d58ae8fadf4ef4e434ce1
 */
package net.serviceautomata.chord;

public class CliSeAuNode{

	private int nodeID;

	private CliSeAuNode predecessor;

	private CliSeAuNode successor;

	private FingerTable fingerTable;
	
	public final static int RING_LENGTH =
			(int) Math.pow(2, FingerTable.FINGER_NUMBER);

	public CliSeAuNode(int nodeID) {
		this.nodeID = nodeID;
		this.fingerTable = new FingerTable(this);
		this.createChord();
	}

	/**
	 * Lookup a successor of given event identifier
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
	 * Check whether the position of the resource is located between
	 * the current node and the next node
	 * 
	 * @param currentID		The identifier of the current node
	 * @param eventID		The identifier of the event
	 * @param successorID	The identifier of the next node
	 * @return				The judge
	 */
	
	public boolean isBetween (int currentID, int eventID, int successorID) {
		
		currentID %= RING_LENGTH;
		eventID %= RING_LENGTH;
		successorID %= RING_LENGTH;
		
		// When only one node exists in the ring
		if (currentID == successorID) {
			return false;
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
					eventID >= 0 && eventID < successorID) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Search the finger table from the last entry to the first one to find
	 * a predecessor closest to the target event identifier
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
	 * Creates a new Chord ring.
	 */
	public void createChord() {
		predecessor = null;
		successor = this;
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
			 * (currentID, successorID)
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
<<<<<<< HEAD
	 * @param node the newly coming node
=======
	 * @param node the bootstrapping node to join
>>>>>>> 359db2d36318fc20604d58ae8fadf4ef4e434ce1
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
			int fingerKey = finger.getFingerKey();
			finger.setNode(findSuccessor(fingerKey));
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

	public int compareTo(CliSeAuNode node) {
		if (this.nodeID == node.nodeID) {
			return 0;
		} else if (this.nodeID < node.nodeID) {
			return -1;
		} else {
			return 1;
		}
	}
}