package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;

public class SafeShoppingDelegationRequest extends SafeShoppingDelegationReqResp {
	
	private static final long serialVersionUID = 1L;
	
	public SafeShoppingEvent se;

	public SafeShoppingDelegationRequest(final SafeShoppingEvent se, final String sourceID, final String destID) {
		super(sourceID, destID);
		this.se = se;
	}
	
	public CriticalEvent getEvent()
	{
		return se;
	}
}
