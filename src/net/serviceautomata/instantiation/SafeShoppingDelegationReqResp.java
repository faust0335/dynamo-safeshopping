package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.DelegationReqResp;
/**
 * This class implements the interface DelegationReqResp and acts as a parent
 * class of SafeShoppingDelegationRequest and SafeShoppingDelegationResponse.
 * It handles the sourceID and the destinationID of the delegation Request /
 * Responses. 
 * 
 * @author Liu, Yi
 * 	
 */
public class SafeShoppingDelegationReqResp implements DelegationReqResp {
	/** 
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 5545353919108965145L;
	/**
	 * The ID of the CliSeAu unit sending this request / response
	 */
	public String sourceID;
	/**
	 * The ID of the CliSeAu unit meant to be receiving this request / response
	 */
	public String destID;
	
	/**
	 * The constructor of the interface for both delegation requests and responses
	 * 
	 * @param sourceID	the initiator of the delegation request and the target of
	 * 					the delegation response
	 * @param destID	the initiator of the delegation response and the target of
	 * 					the delegation request
	 */
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