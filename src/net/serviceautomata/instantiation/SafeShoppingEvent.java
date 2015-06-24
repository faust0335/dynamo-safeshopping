package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;

public class SafeShoppingEvent implements CriticalEvent{
	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = 6367746522189849621L;
	//token in the check out HTTP message is the critical event
	private String token;
	private String payerID;
	private String sessionID;
    private boolean paySucceed;
	
	/*
	 * Construct a new critical event with the following parameters
	 * @param sessionID		The unique identification for the transaction
	 * @param token			The serial number distributed for the transaction
	 * @param payerID		The serial number distributed for the PayPal account
	 * @param paySuccess	Determine whether the transaction has been successfully
	 * 						completed 
	 * @return				The new event extracted from the HTTP message
	 */
	public SafeShoppingEvent(String sessionID, String token, String payerID, boolean paySucceed){
		this.sessionID = sessionID;
		this.token = token;
		this.payerID = payerID;
		this.paySucceed = paySucceed;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public String getPayerID(){
		return this.payerID;
	}
	
	public String getSessionID(){
		return this.sessionID;
	}

	public boolean getPaySucceed(){
		return this.paySucceed;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setPayerID(String payerID) {
		this.payerID = payerID;
	}
	
	public void setSessionID(String sessionID){
		this.sessionID = sessionID;
	}
	public void setPaySucceed(boolean paySucceed) {
		this.paySucceed = paySucceed;
	}
}
