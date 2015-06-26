package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;


public class SafeShoppingEventFactory implements CriticalEventFactory {
	/*createEvent method take the httpMessage from SafeShoppingServiceAutomata
	 * as parameter and extract the substring: token from it. The token is
	 *  used to instantiate a SafeShoppingEvent class 
	 */
	
	
	public static SafeShoppingEvent createEvent(String httpMessage){
		String token = getToken(httpMessage);
		String payerID = getPayerID(httpMessage);
		String sessionID = getSessionID(httpMessage);
		boolean paySucceed = getPaySucceed(httpMessage);
		return new SafeShoppingEvent(token, payerID, sessionID, paySucceed );
	}
	
	/*
	 * getToken method is used to extract the token from the http message
	 */
	protected static String getToken(String httpMessage){
		if(!httpMessage.isEmpty() && httpMessage.contains("token=") &&
				httpMessage.contains("&PayerID")){
			String tokenID = httpMessage.substring(httpMessage.indexOf("token="),
					httpMessage.indexOf("&PayerID"));
			return(tokenID.substring(6));
		}
		else
			return null;
	}
	
	/*
	 * getPayerID method is used to extract the payerID from the http message
	 */
	protected static String getPayerID(String httpMessage){
		if(!httpMessage.isEmpty() &&
				httpMessage.contains("token=") &&
				httpMessage.contains("&PayerID")){
			String payerID = httpMessage.substring(httpMessage.indexOf("PayerID="),
					httpMessage.indexOf("HTTP"));
			return(payerID.substring(8));
		}
		else
			return null;
	}
	
	//getSessionID method is used to extract the sessionID from the http message
	protected static String getSessionID(String httpMessage){
		if(!httpMessage.isEmpty() && httpMessage.contains("sessionid")){
			String sessionid = httpMessage.substring(httpMessage.indexOf("sessionid"));
			return(sessionid.substring(9));
		}
		else
			return null;
	}
	
	protected static boolean getPaySucceed(String httpMessage){
		if(!httpMessage.isEmpty() && httpMessage.contains("success")){
			return true;
		}
		else
			return false;
	}
}