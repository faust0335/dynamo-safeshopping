package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.DelegationReqResp;

public class SafeShoppingDelegationReqResp implements DelegationReqResp {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//The ID of the CliSeAu unit sending this request / response
	public String sourceID;
	//The ID of the CliSeAu unit meant to be receiving this request / response
	public String destID;

	protected SafeShoppingDelegationReqResp(final String sourceID, final String destID) {
		this.sourceID = sourceID;
		this.destID   = destID;
	}
	
	public String getSourceID(){
		return sourceID;
	}
	
	
	public String getDestID(){
		return destID;
	}	
}
