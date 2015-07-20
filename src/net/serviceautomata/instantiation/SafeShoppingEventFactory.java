package net.serviceautomata.instantiation;

import net.serviceautomata.javatarget.CriticalEventFactory;

/**
 * This class construct a critical event factory which extract the token String
 * and the payerID String from the given httpMessage and instantiate the related
 * critical event
 * 
 * @author Liu, Yi	
 * 
 */
public class SafeShoppingEventFactory implements CriticalEventFactory {
	/**
	 * The method createEvent extracts the token string and payerID string
	 * from the given httpMessage. Then it returns a Event instantiation.
	 * 
	 * @param httpMessage
	 * @return A SafeShoppingEvent instantiation.
	 */
	
	public static SafeShoppingEvent createEvent(String httpMessage){
		String token = getToken(httpMessage);
		String payerID = getPayerID(httpMessage);
		return new SafeShoppingEvent(token, payerID);
	}
	
	/**
	 * getToken method is used to extract the token from the http message.
	 * It is called by the makeEvent method.
	 * 
	 * @param httpMessage
	 * @return The token string.
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
	 * getPayerID method is used to extract the payerID from the http message.
	 * It is called by the makeEvent method.
	 * 
	 * @param httpMessage
	 * @return The payerID string.
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