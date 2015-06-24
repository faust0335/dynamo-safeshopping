package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;

public class SafeShoppingEvent implements CriticalEvent{
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 8337703243371428866L;
	//token in the check out http message is the critical event
	private String token;
	private String payerID;
	private boolean paySucceed;
	
	public SafeShoppingEvent(String token, String payerID, boolean paySucceed){
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
	
	public boolean getPaySucceed(){
		return this.paySucceed;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setPayerID(String payerID) {
		this.payerID = payerID;
	}
	
	public void setPaySucceed(boolean paySucceed) {
		this.paySucceed = paySucceed;
	}
}
