package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.LocalPolicyResponse;

public class SafeShoppingDelegationResponse extends SafeShoppingDelegationReqResp{
	
	private static final long serialVersionUID = 1L;

	public SafeShoppingDecision decision;

	public SafeShoppingDelegationResponse(SafeShoppingDecision decision, String sourceID, String destinationID) {
		super(sourceID, destinationID);
		this.decision = decision;
	}

	public LocalPolicyResponse getDecision() {
		return decision;
	}
}
