package net.serviceautomata.instantiation;

import net.serviceautomata.chord.Chord;
import net.serviceautomata.chord.CliSeAuNode;
import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationLocPolReturn;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;

import java.util.HashMap;

public class DHTChordPolicy extends LocalPolicy {
	/* Store the pair of token and payerID for the transaction
	 * to the corresponding sessionID
	 */
	private HashMap<String, String> transactionMap = new HashMap<String, String>();
	
	private final static int BITS_OF_IDENTIFIER = 63;
	
	private Chord chord = new Chord();

	/**
	 * Construct a local policy object.
	 * @param identifier The identifier of the unit using the local policy
	 */
	public DHTChordPolicy(final String identifier) {
		super(identifier);
	}
	
	/**
	 * compute identifier with the least 6 bits of the sessionID`s hashcode 
	 * @param ce
	 * @return
	 */
	protected int makeEventID(CriticalEvent ce){
		SafeShoppingEvent se = (SafeShoppingEvent) ce;
		String token = se.getToken();
		return token.hashCode() & BITS_OF_IDENTIFIER;
	}
	
	/**
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
	public final LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException {
		//compute hashcode and get the least 6 bits as identifier
		int eventID = makeEventID(ev);
		
		int policyID = Integer.parseInt(getIdentifier());
		chord.insertNode(policyID);
		//change the Id of the CliSeAu into int type and use it to instantiate CliSeAuNode
		// CliSeAuNode cNode = new CliSeAuNode(Integer.parseInt(getIdentifier()));
		//get the responsible id of CliSeAuNode and change it to String
		int handler = chord.nodeMap.get(policyID).findSuccessor(eventID).getNodeID();
		String responsible = String.valueOf(handler);
		
		if (getIdentifier().equals(responsible)) {
			// local policy is responsible for deciding
			return makeDecision((SafeShoppingEvent)ev);
		} else {
			// must forward to remote CliSeAu unit
			SafeShoppingDelegationRequest dr = new SafeShoppingDelegationRequest((SafeShoppingEvent)ev, getIdentifier(), responsible);
			return new DelegationLocPolReturn(responsible, dr);
		}
	}
	
	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr) throws IllegalArgumentException {
		if (dr instanceof SafeShoppingDelegationRequest) {
			SafeShoppingDelegationRequest sReq = (SafeShoppingDelegationRequest)dr;
			if (getIdentifier().equals(sReq.getDestID())) {
				// handle request locally and return DirectDelegationResponse
				SafeShoppingDecision sd = makeDecision((SafeShoppingEvent)sReq.getEvent());
				if (getIdentifier().equals(sReq.getSourceID())) {
					// the local unit originated the request --> deliver locally
					return sd;
				} else {
					// the request originated remotely --> send response back
					return new DelegationLocPolReturn(sReq.getSourceID(),
							new SafeShoppingDelegationResponse(sd, getIdentifier(), sReq.getSourceID()));
				}
			} else {
				// forward request
				return new DelegationLocPolReturn(sReq.getDestID(), sReq);
			}
		} else if (dr instanceof SafeShoppingDelegationResponse) {
			SafeShoppingDelegationResponse sResp = (SafeShoppingDelegationResponse)dr;
			if (getIdentifier().equals(sResp.getDestID())) {
				// response is for local unit
				// and the response already contains the enforcement decision to return
				return sResp.getDecision();
			} else {
				// forward response
				return new DelegationLocPolReturn(sResp.getDestID(), sResp);
			}
		} else {
			throw new IllegalArgumentException("Event for remote request of wrong type");
		}
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

		//event.getSessionID();
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
