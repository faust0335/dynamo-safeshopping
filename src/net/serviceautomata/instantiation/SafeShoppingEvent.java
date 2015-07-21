package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;

/**
 * This class construct a critical event according to the HTTP message
 * with two fields: token and payerID
 * 
 * @author Liu, Yi
 *
 */
public class SafeShoppingEvent implements CriticalEvent{
	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = 6367746522189849621L;
	/**
	 * token in the check out HTTP message is the critical event
	 */
	private String token;
	/**
	 * the payer identifier is bundled with the token above
	 */
	private String payerID;
	
	/**
	 * Construct a new critical event with the following parameters
	 * @param token			The serial number distributed for the transaction
	 * @param payerID		The serial number distributed for the PayPal account
	 * @return				The new event extracted from the HTTP message
	 */
	public SafeShoppingEvent(String token, String payerID){
		this.token = token;
		this.payerID = payerID;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public String getPayerID(){
		return this.payerID;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setPayerID(String payerID) {
		this.payerID = payerID;
	}
}