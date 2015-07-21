package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.LocalPolicyResponse;
/**
 * This class takes the SafeShoppingDelegationReqResp class as parent class
 * and handles the transmission of decision from destination to source CliSeAu node
 * 
 * @author Liu,Yi
 *
 */
public class SafeShoppingDelegationResponse extends SafeShoppingDelegationReqResp{
	
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -5620176864627998625L;
	/**
	 * the decison to be sent
	 */
	public SafeShoppingDecision decision;
	/**
	 * Constructor of this class
	 * @param se           critical event
	 * @param sourceID
	 * @param destID
	 */
	public SafeShoppingDelegationResponse(SafeShoppingDecision decision, String sourceID, String destinationID) {
		super(sourceID, destinationID);
		this.decision = decision;
	}

	public LocalPolicyResponse getDecision() {
		return decision;
	}
}