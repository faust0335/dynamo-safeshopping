/**
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
 */
package net.serviceautomata.chord;

public class FingerTable {
	
	private Finger[] fingers;
	
	public final static int FINGER_NUMBER = 6;
	
	/**
	 * The constructor of the finger table
	 * 
	 * @param node the node to be represented
	 */
	public FingerTable (CliSeAuNode node) {
		this.fingers = new Finger[FINGER_NUMBER];

		for (int i = 0; i < fingers.length; i++) {
			/*
			 * Initialize the finger table of the current node as:
			 * currentID + 2^0						<--> node
			 * currentID + 2^1						<--> node
			 * ...
			 * currentID + 2^(FINGER_NUMBER - 1)	<--> node
			 */
			int key = (int) (node.getNodeID() + Math.pow(2, i)) %
					CliSeAuNode.RING_LENGTH;
			fingers[i] = new Finger(key, node.findSuccessor(key));
		}
	}
	
	/**
	 * @param i	The i-th entry of the finger table
	 * @return	The node of the corresponding entry
	 */
	public Finger getFinger(int i) {
		if (i >= 0 || i < fingers.length) {
			return fingers[i];
		}
		else {
			return null;
		}
	}
}