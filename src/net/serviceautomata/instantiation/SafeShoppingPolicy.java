package net.serviceautomata.instantiation;

import java.io.File;

import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.EnforcementDecision;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;
import java.util.HashMap;

public class SafeShoppingPolicy extends LocalPolicy{
	
	/* Store the pair of token and payerID for the transaction
	 * to the corresponding sessionID
	 */
	private HashMap <String[], String> transactionMap =
			new HashMap <String[], String> ();
	
	/*
	 * Construct a local policy object.
	 * @param identifier The identifier of the unit using the local policy
	 */
	public SafeShoppingPolicy(final String identifier, String lockUsername, File lockFilename) {
		super(identifier);
	}
	
	/*
	 * Handles a request from the local interceptor component.
	 *
	 * This method is supposed to be called by the Coordinator for every local
	 * request that it receives from the local interceptor. If the policy can
	 * make a local decision, then it should return an object of type
	 * EnforcementDecision. Otherwise it should return a delegation request of
	 * some sub-type of DelegationReqResp encapsulated in an object of type
	 * DelegationLocPolReturn. The method may update the state of the local
	 * policy object during the process of handling the request.
	 *
	 * @param event	The critical event for which a decision is requested.
	 * @return		The response to the coordinator
	 * @exception	IllegalArgumentException Can be thrown if event is
	 * 				of the wrong sub-type of CriticalEvent
	 */
	@Override
	public LocalPolicyResponse localRequest(CriticalEvent event)
			throws IllegalArgumentException{
				return makeDecision((SafeShoppingEvent) event);
			}
	
	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr) throws IllegalArgumentException {
		return null;
	}

	/* 
	 * According to the current event one can decide whether the current HTTP message
	 * should continue to be forwarded to the web store from the client and vice versa
	 * 
	 * @param event	The critical event for which a decision is requested.
	 * @return		The decision made to the coordinator
	 */
	protected SafeShoppingDecision makeDecision(SafeShoppingEvent event)
			throws IllegalArgumentException{
		
		String sessionID = event.getSessionID();
		String token = event.getToken();
		String payerID = event.getPayerID();
		boolean paySucceed = event.getPaySucceed();
		
		/* 
		 * If the HTTP message does not contain the information about payerID
		 * and token, the transaction will be permitted
		 */
		if (token == null && payerID == null) {
			return SafeShoppingDecision.PERMIT;
		}
		/* 
		 * If the HTTP message does not contain one of payerID and token,
		 * the transaction will be considered invalid and rejected
		 */
		else if ((token != null && payerID == null) ||
				 (token == null && payerID != null)) {
			return SafeShoppingDecision.REJECT;
		}
		else {
			String[] pair = new String[2];
			pair[0] = token;
			pair[1] = payerID;
			
			/* 
			 * If the pair of payerID and token for the current session exists
			 * in the transaction map, and the transaction has succeeded,
			 * the transaction with the same information will be rejected
			 */
			if (transactionMap.containsKey(pair) &&
					transactionMap.get(pair).equals(sessionID) &&
					paySucceed) {
				return SafeShoppingDecision.REJECT;
			}
		
			/* 
			 * If the pair of payerID and token for the current session does not
			 * exist in the transaction list, the transaction with such information
			 * will be recorded and permitted
			 */
			if (!transactionMap.containsKey(pair)) {
				transactionMap.put(pair, sessionID);
			}
		}
		return SafeShoppingDecision.PERMIT;
	}
}