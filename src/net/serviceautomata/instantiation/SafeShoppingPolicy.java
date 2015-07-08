package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;

import java.util.HashMap;

public class SafeShoppingPolicy extends LocalPolicy {

	/*
	 * Store the pair of token and payerID for the transaction to the
	 * corresponding sessionID
	 */
	private HashMap<String, String> transactionMap = new HashMap<String, String>();

	/*
	 * Construct a local policy object.
	 * 
	 * @param identifier The identifier of the unit using the local policy
	 */
	public SafeShoppingPolicy(final String identifier) {
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
	 * @param event The critical event for which a decision is requested.
	 * 
	 * @return The response to the coordinator
	 * 
	 * @exception IllegalArgumentException Can be thrown if event is of the
	 * wrong sub-type of CriticalEvent
	 */
	@Override
	public LocalPolicyResponse localRequest(CriticalEvent event)
			throws IllegalArgumentException {
		return makeDecision((SafeShoppingEvent) event);
	}

	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr)
			throws IllegalArgumentException {
		return null;
	}

	/*
	 * According to the current event one can decide whether the current HTTP
	 * message should continue to be forwarded to the web store from the client
	 * and vice versa
	 * 
	 * @param event The critical event for which a decision is requested.
	 * 
	 * @return The decision made to the coordinator
	 */
	protected SafeShoppingDecision makeDecision(SafeShoppingEvent event)
			throws IllegalArgumentException {

		// event.getSessionID();
		String token = event.getToken();
		String payerID = event.getPayerID();

		/*
		 * If the HTTP message does not contain the information about payerID
		 * and token, the transaction will be permitted
		 */
		if (token == null && payerID == null) {
			return SafeShoppingDecision.PERMIT;

		} else {

			/*
			 * If the pair of payerID and token for the current session exists
			 * in the transaction map, and the transaction has succeeded, the
			 * transaction with the same information will be rejected
			 */

			if (transactionMap.containsKey(token)) {
				return SafeShoppingDecision.REJECT;
			} else {
				transactionMap.put(token, payerID);
				return SafeShoppingDecision.PERMIT;

			}
		}
	}
}