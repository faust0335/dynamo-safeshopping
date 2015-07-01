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

public class Finger {

	private int fingerKey;

	private CliSeAuNode node;

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
}