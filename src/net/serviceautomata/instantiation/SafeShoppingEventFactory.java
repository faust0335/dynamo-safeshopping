package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;


public class SafeShoppingEventFactory implements CriticalEventFactory {
	/*createEvent method take the httpMessage from SafeShoppingServiceAutomata
	 * as parameter and extract the substring: token from it. The token is
	 *  used to instantiate a SafeShoppingEvent class 
	 */
	
	
	protected static SafeShoppingEvent createEvent(String httpMessage){
		String token = getToken(httpMessage);
		String payerID = getPayerID(httpMessage);
		String sessionID = getSessionID(httpMessage);
		boolean paySucceed = getPaySucceed(httpMessage);
		return new SafeShoppingEvent(token, payerID, sessionID, paySucceed );
	}
	
	/*
	 * getToken method is used to extract the token from the http message
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
	 * getPayerID method is used to extract the payerID from the http message
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
	
	//getSessionID method is used to extract the sessionID from the http message
	protected static String getSessionID(String hm){
		if(!hm.isEmpty() && hm.contains("sessionid")){
			String sessionid = hm.substring(hm.indexOf("sessionid"));
			return(sessionid.substring(9));
		}
		else
			return null;
	}
	
	protected static boolean getPaySucceed(String hm){
		if(!hm.isEmpty() && hm.contains("success")){
			return true;
		}
		else
			return false;
	}
}