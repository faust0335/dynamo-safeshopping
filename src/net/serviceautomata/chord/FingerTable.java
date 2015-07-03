/*
 * This is a simplified revised version of jchord-src-0.1 from
 * https://code.google.com/p/joonion-jchord/downloads/list
 */
package net.serviceautomata.chord;

/**
 * The class FingerTable maintains a finger table for a single CliSeAu node.
 * An object of FingerTable will be instantiated in class CliSeAuNode as a
 * reference field. Meanwhile, class Finger is instantiated in this class as an
 * array, which stores all the necessary Finger elements.
 * 
 * @author Xu, Yinhua	(Main)
 * @author Chen, Yiqun	(Cooperator)
 * @author Liu, Yi		(Cooperator)
 */
public class FingerTable {
	
	/**
	 * A finger table is composed of several Finger objects as entries
	 */
	private Finger[] fingers;
	
	/**
	 * The number of event identifier bits is now 6, in other words, the number
	 * of the identifier identifiers is 2^6 = 64. Both of them could be changed
	 * according to the change to the definition of FINGER_NUMBER.
	 */
	public final static int FINGER_NUMBER = 6;
	
	/**
	 * Search the i-the entry (or finger) of the current finger table.
	 * 
	 * @param i	The i-th entry of the finger table
	 * @return	The node of the corresponding entry
	 */
	public Finger getFinger(int i) {
		/*
		 * The search must be executed inside the range of table
		 */
		if (i >= 0 || i < fingers.length) {
			return fingers[i];
		} else {
			return null;
		}
	}

	/**
	 * The constructor of the finger table, in which every entries of the finger
	 * table will be initialized
	 * 
	 * @param node the node to be represented
	 */
	public FingerTable (CliSeAuNode node) {
		this.fingers = new Finger[FINGER_NUMBER];

		for (int i = 0; i < fingers.length; i++) {
			/*
			 * Initialize the finger table of the current node as:
			 * currentID + 2^0						<--> key 0's successor
			 * currentID + 2^1						<--> key 1's successor
			 * ...
			 * currentID + 2^(FINGER_NUMBER - 1)	<--> key MAXIMUM's successor
			 */
			int key = (int) (node.getNodeID() + Math.pow(2, i)) %
					CliSeAuNode.RING_LENGTH;
			/*
			 * Enter the pair (key, handler of key) into the i-th entry
			 */
			fingers[i] = new Finger(key, node.findSuccessor(key));
		}
	}
	
	/**
	 * Convert the information of the finger table into a string
	 * 
	 * @return A string array for the finger table
	 */
	public String toString() {
		String tableString = "The Finger Table:\n\n";
		for (int i = 0; i < fingers.length; i++) {
			tableString += "Table Entry " + Integer.toString(i + 1) +
					": " + fingers[i].toString();
		}
		return tableString;
	}
}