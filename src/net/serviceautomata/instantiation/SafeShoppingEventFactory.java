package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;


public class SafeShoppingEventFactory implements CriticalEventFactory {
	/*	createEvent method takes the HTTP Message from SafeShoppingServiceAutomata
	 *	as parameter and extracts the substring: token from it. The token is
	 *  used to instantiate a SafeShoppingEvent class 
	 */
	
	private static String session = Integer.toString(0); 
	
	protected static SafeShoppingEvent createEvent(String sHTTPMessage){
		String sessionID = session;  
		// String sessionID = getSessionID(sHTTPMessage);
		String token = getToken(sHTTPMessage);
		String payerID = getPayerID(sHTTPMessage);
		// boolean paySuccess = getPaySuccess(sHTTPMessage);
		return new SafeShoppingEvent(sessionID, token, payerID, true);
	}
	
	/*
	 * getToken method is used to extract the token from the HTTP message
	 */
	protected static String getToken(String sHTTPMessage){
		if(!sHTTPMessage.isEmpty() && sHTTPMessage.contains("token=") &&
				sHTTPMessage.contains("&PayerID")){
			String tokenID = sHTTPMessage.substring(sHTTPMessage.indexOf("token="),
					sHTTPMessage.indexOf("&PayerID"));
			return(tokenID.substring(6));
		}
		else
			return null;
	}
	
	/*
	 * getPayerID method is used to extract the payerID from the HTTP message
	 */
	protected static String getPayerID(String sHTTPMessage){
		if(!sHTTPMessage.isEmpty() &&
				sHTTPMessage.contains("token=") &&
				sHTTPMessage.contains("&PayerID")){
			String payerID = sHTTPMessage.substring(sHTTPMessage.indexOf("PayerID="),
					sHTTPMessage.indexOf("HTTP"));
			return(payerID.substring(8));
		}
		else
			return null;
	}
}