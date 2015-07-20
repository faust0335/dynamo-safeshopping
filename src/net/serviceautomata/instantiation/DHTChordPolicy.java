package net.serviceautomata.instantiation;

import net.serviceautomata.chord.Chord;
import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationLocPolReturn;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;

import java.util.HashMap;
/**
 * This class construct a policy which can accept the SafeShoppingEvent
 * as request and convert it to a number between 0 and 63. 
 * Then we use the Chord algorithm to find the responsible CliSeAu node.
 * And handle the local request and remote request.
 * Finally gives a corresponding decision back to coordinator.
 * 
 * @author Liu, Yi
 * 	
 */
public class DHTChordPolicy extends LocalPolicy {
	/**
	 * Store the pair of token and payerID for the transaction
	 */
	private HashMap<String, String> transactionMap = new HashMap<String, String>();
	/**
	 *  The bits limitation of identifier.
	 */
	private final static int BITS_OF_IDENTIFIER = 63;
	/**
	 * An instantiation of class Chord
	 */
	private Chord chord = new Chord();
	/**
	 * An Integer array is used to initialize the first CliSeAu node
	 */
	private final Integer[] initIDArray = { 2 };

	/**
	 * Construct a local policy object.
	 * 
	 * @param identifier 
	 * 					The identifier of the unit using the local policy
	 */
	public DHTChordPolicy(final String identifier) {
		super(identifier);
		chord.createRing(initIDArray);
	}

	/**
	 * Getter of the chord object
	 * 
	 * @return a chord instance
	 */
	public Chord getChord() {
		return chord;
	}

	/**
	 * compute identifier with the least 6 bits of the token`s hashcode
	 * 
	 * @param ce the critical event
	 * @return the number between 0 and 63 as event ID
	 */
	protected int makeEventID(CriticalEvent ce) {
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
	 * @param event
	 *            The critical event for which a decision is requested.
	 * @return The response to the coordinator
	 * @exception IllegalArgumentException
	 *                Can be thrown if event is of the wrong sub-type of
	 *                CriticalEvent
	 */
	@Override
	public final LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException {
		// compute hashcode and get the least 6 bits as identifier
		int eventID = makeEventID(ev);
		// change the Id of the CliSeAu into int type
		int policyID = Integer.parseInt(getIdentifier());

		
		/* get the responsible id of CliSeAuNode by calling findSuccessor method
		 * and change it to String
		 */
		int handler = chord.nodeMap.get(policyID).findSuccessor(eventID)
				.getNodeID();
		String responsible = String.valueOf(handler);
		
		//print the Event ID and the responsible CliSeAu ID on the console for debugging
		System.out.println("Event ID: " + eventID
				+ "\nResponsible CliSeAu ID: " + responsible);

		if (getIdentifier().equals(responsible)) {
			// local policy is responsible for deciding
			return makeDecision((SafeShoppingEvent) ev);
		} else {
			// must forward to remote CliSeAu unit
			SafeShoppingDelegationRequest dr = new SafeShoppingDelegationRequest(
					(SafeShoppingEvent) ev, getIdentifier(), responsible);
			return new DelegationLocPolReturn(responsible, dr);
		}
	}
	
	/**
	 * Handles a request/reponse received from a remote CliSeAu unit.
	 *
	 * This method is supposed to be called by the Coordinator for every request
	 * or response received from a remote CliSeAu unit.
	 *  - If a request is received and a decision on the request can be made
	 *    locally, then a delegation response of some subtype of
	 *    DelegationReqResp (encapsulated in a DelegationLocPolReturn object)
	 *    should be returned by this method. If a local decision cannot be made,
	 *    the same or a modified request can be returned (also encapsulated in a
	 *    DelegationLocPolReturn object).
	 *  - If a response is received and is destined for the local CliSeAu unit,
	 *    then an EnforcementDecision for the local enforcer should
	 *    be returned. If the response is not for the local CliSeAu unit,
	 *    then the same or a modified response should be forwarded by this method
	 *    to another CliSeAu unit (encapsulated in a DelegationLocPolReturn
	 *    object).
	 *
	 * This method may update the state of the local policy object during the
	 * process of handling the request.
	 *
	 * @param dr The delegation request/response received from a remote party
	 * @return The response to the coordinator
	 * @exception IllegalArgumentException Can be thrown if dr is of the wrong subtype of DelegationReqResp
	 */
	@Override
	public LocalPolicyResponse remoteRequest(DelegationReqResp dr)
			throws IllegalArgumentException {
		if (dr instanceof SafeShoppingDelegationRequest) {
			SafeShoppingDelegationRequest sReq = (SafeShoppingDelegationRequest) dr;
			if (getIdentifier().equals(sReq.getDestID())) {
				// handle request locally and return DirectDelegationResponse
				SafeShoppingDecision sd = makeDecision((SafeShoppingEvent) sReq
						.getEvent());
				return sd;
			} else {
				// forward request
				// TODO remove after test
				System.out.println("id: " + getIdentifier() + "destId: " + sReq.getDestID());
				return new DelegationLocPolReturn(sReq.getDestID(), sReq);
			}
		} else if (dr instanceof SafeShoppingDelegationResponse) {
			SafeShoppingDelegationResponse sResp = (SafeShoppingDelegationResponse) dr;
			if (getIdentifier().equals(sResp.getDestID())) {
				// response is for local unit
				// and the response already contains the enforcement decision to
				// return
				return sResp.getDecision();
			} else {
				// forward response
				return new DelegationLocPolReturn(sResp.getDestID(), sResp);
			}
		} else {
			throw new IllegalArgumentException(
					"Event for remote request of wrong type");
		}
	}

	/**
	 * According to the current event one can decide whether the current HTTP
	 * message should continue to be forwarded to the web store from the client
	 * and vice versa
	 * 
	 * @param event The critical event for which a decision is requested.
	 * @return The decision made to the coordinator
	 * 
	 * @author Xu,Yinhua
	 * 
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
