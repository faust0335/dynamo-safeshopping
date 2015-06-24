package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;

public class SafeShoppingEvent implements CriticalEvent{
	//token in the check out http message is the critical event
	private String token;
	private String payerID;
	private String sessionID;
    	private boolean paySucceed;
	
	public SafeShoppingEvent(String token, String payerID, String sessionID, boolean paySucceed){
		this.token = token;
		this.payerID = payerID;
		this.sessionID = sessionID;
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