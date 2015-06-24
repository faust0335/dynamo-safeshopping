package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;

public class SafeShoppingEventFactory implements CriticalEventFactory {
	/*	createEvent method takes the HTTP Message from SafeShoppingServiceAutomata
	 *	as parameter and extracts the substring: token from it. The token is
	 *  used to instantiate a SafeShoppingEvent class 
	 */
	protected static SafeShoppingEvent createEvent(String httpMessage){
		String token = getToken(httpMessage);
		String payerID = getPayerID(httpMessage); 
		return new SafeShoppingEvent(token, payerID, true);
	}
	
	/*
	 * getToken method is used to extract the token from the HTTP message
	 */
	protected static String getToken(String httpMessage){
		if(!httpMessage.isEmpty() && httpMessage.contains("token = ") && httpMessage.contains("&PayerID")){
			String tokenID = httpMessage.substring(httpMessage.indexOf("token = "), httpMessage.indexOf("&PayerID"));
			return(tokenID.substring(6));
		}
		else
			return null;
	}
	
	/*
	 * getPayerID method is used to extract the payerID from the HTTP message
	 */
	protected static String getPayerID(String httpMessage){
		if(!httpMessage.isEmpty() && httpMessage.contains("token = ") && httpMessage.contains("&PayerID")){
			String payerid = httpMessage.substring(httpMessage.indexOf("PayerID = "), httpMessage.indexOf(" HTTP"));
			return(payerid.substring(8));
		}
		else
			return null;
	}
}