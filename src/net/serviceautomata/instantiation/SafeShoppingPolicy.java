package net.serviceautomata.instantiation;

import java.io.File;
import java.util.HashMap;

import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;

public class SafeShoppingPolicy extends LocalPolicy{
	
	/* Store the pair of token and payerID for the transaction */
	
	private HashMap <String, String> transactionMap =
			new HashMap <String, String> ();
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
	 * some subtype of DelegationReqResp encapsulated in an object of type
	 * DelegationLocPolReturn. The method may update the state of the local
	 * policy object during the process of handling the request.
	 *
	 * @param ev The critical event for which a decision is requested.
	 * @return The response to the coordinator
	 * @exception IllegalArgumentException Can be thrown if ev is of the wrong subtype of CriticalEvent
	 */
	@Override
	public LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException{
				return makeDecision((SafeShoppingEvent)ev);
			}
	
	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr) throws IllegalArgumentException {
		return null;
	}

//do policy here
	protected SafeShoppingDecision makeDecision(SafeShoppingEvent event)
			throws IllegalArgumentException{
		
		String tokenID = event.getToken();
		String payerID = event.getPayerID();
		boolean paySucceed = event.getPaySucceed();
		
		/* If the HTTP message does not contain the information about payerID
		 * or token, the transaction will be permitted */
		if (tokenID == null || payerID == null) {
			return SafeShoppingDecision.PERMIT;
		}
		
		/* If the pair of payerID and token exists in the transaction list,
		 * and the transaction has succeeded, the transaction with the same
		 * information will be rejected */
		if (transactionMap.containsKey(tokenID) && paySucceed) {
			return SafeShoppingDecision.REJECT;
		}
		
		/* If the pair of payerID and token does not exist in the transaction
		 * list, the transaction with such information will be recorded and
		 * permitted */
		if (!transactionMap.containsKey(tokenID)) {
			transactionMap.put(tokenID, payerID);
		}
		
		return SafeShoppingDecision.PERMIT;
	}
}
