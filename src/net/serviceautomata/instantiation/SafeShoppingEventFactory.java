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
	protected static String getToken(String hm){
		if(!hm.isEmpty() && hm.contains("token=") && hm.contains("&PayerID")){
			String tok = hm.substring(hm.indexOf("token="), hm.indexOf("&PayerID"));
			return(tok.substring(6));
		}
		else
			return null;
	}
	
	/*
	 * getPayerID method is used to extract the payerID from the http message
	 */
	protected static String getPayerID(String hm){
		if(!hm.isEmpty() && hm.contains("token=") && hm.contains("&PayerID")){
			String payerid = hm.substring(hm.indexOf("PayerID="), hm.indexOf(" HTTP"));
			return(payerid.substring(8));
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