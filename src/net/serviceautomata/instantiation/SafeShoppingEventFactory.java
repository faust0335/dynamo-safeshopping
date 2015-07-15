package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;


public class SafeShoppingEventFactory implements CriticalEventFactory {
	
	/**
	 * createEvent method take the httpMessage from SafeShoppingServiceAutomata
	 * as parameter and extract the substring: token from it. The token is
	 * used to instantiate a SafeShoppingEvent class 
	 *
	 * @param httpMessage	HTTP message in the type of string
	 * @return				The new SafeShoppingEvent object
	 */
	 
	public static SafeShoppingEvent createEvent(String httpMessage){
		String token = getToken(httpMessage);
		String payerID = getPayerID(httpMessage);
		return new SafeShoppingEvent(token, payerID);
	}
	
	/**
	 * getToken method is used to extract the token from the HTTP message
	 * 
	 * @param httpMessage HTTP message in the type of string
	 * @return the string of the token
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
	
	/**
	 * getPayerID method is used to extract the payerID from the HTTP message
	 * 
	 * @param httpMessage	HTTP message in the type of string
	 * @return 				The string of the payer identifier
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
}