/*
 * This is a simplified revised version of jchord-src-0.1 from
 * https://code.google.com/p/joonion-jchord/downloads/list
 */
package net.serviceautomata.chord;

/**
 * The class Finger concerns about only a single entry of the finger table.
 * This class will be instantiated in class FingerTable as the components of
 * the finger table.
 * 
 * @author Xu, Yinhua	
 */
public class Finger {
	
	/**
	 * The key of the entry, on the left column of the finger table, can be
	 * represented as "nodeID + 2^i"
	 */
	private int fingerKey;
	
	/**
	 * The value of the entry, on the right column of the finger table, which
	 * takes charge of the event with the identifier the same as fingerKey
	 */
	private CliSeAuNode node;
	
	/**
	 * The constructor of Finger
	 * 
	 * @param fingerKey	The key of the finger to be initialized
	 * @param node		The corresponding handler of the event identifier
	 * 					which equals to fingerKey
	 */
	public Finger(int fingerKey, CliSeAuNode node) {
		this.fingerKey = fingerKey;
		this.node = node;
	}

	public int getFingerKey() {
		return fingerKey;
	}

	public void setFingerKey(int fingerKey) {
		this.fingerKey = fingerKey;
	}

	public CliSeAuNode getNode() {
		return node;
	}

	public void setNode(CliSeAuNode node) {
		this.node = node;
	}
	
	/**
	 * Convert the finger information into a string
	 * 
	 * @return A string for the finger
	 */
	public String toString() {
		String fingerString = "Key: " + String.valueOf(fingerKey) +
				"\tCliSeAu Node: " + String.valueOf(node.getNodeID()) +
				"\n";
		return fingerString;
	}
}