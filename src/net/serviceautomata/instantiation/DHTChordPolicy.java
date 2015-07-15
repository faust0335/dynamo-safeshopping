package net.serviceautomata.instantiation;

import net.serviceautomata.chord.Chord;
import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.DelegationLocPolReturn;
import net.serviceautomata.javacor.DelegationReqResp;
import net.serviceautomata.javacor.LocalPolicy;
import net.serviceautomata.javacor.LocalPolicyResponse;

import java.util.HashMap;
/**
 * The class DHTChordPolicy extends its abstract parent class LocalPolicy to
 * execute more detailed policies in each CliSeAu node. In this class we utilize
 * a hash map to store all pairs of token and payer ID in the succeeded
 * transactions to avoid repeated usage of them. Besides that we also establish
 * an initial array to appoint the first CliSeAu node(s) to be started in the
 * process of service initialization. If the event ID does not match the ID of
 * local policy, in other words, the ID of the CliSeAu node, it will send the
 * delegation request of handling the event to another one after the thread starts
 * it, and wait for its response in some time. When a local or remote decision is
 * made, the enforcer will take the measures, permitting or suppressing.
 * 
 * @author Liu, Yi (Main)
 * @author Xu, Yinhua (Coordinator)
 *
 */
public class DHTChordPolicy extends LocalPolicy {
	/**
	 * Store the pair of token and payerID for the transaction to the
	 * corresponding transaction
	 */
	private HashMap<String, String> transactionMap = new HashMap<String, String>();
	/**
	 * When a hash code h & BITS_OF_IDENTIFIER, the last 6 bits will be obtained
	 */
	private final static int BITS_OF_IDENTIFIER = 63;
	/**
	 * A Chord ring to record the topology of all the CliSeAu nodes
	 */
	private Chord chord = new Chord();
	
	/**
	 * The identifier(s) of the initial CliSeAu node(s)
	 */
	private final Integer[] initIDArray = { 2 };

	/**
	 * Construct a local policy object.
	 * 
	 * @param identifier
	 *            The identifier of the unit using the local policy
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
	 * compute identifier with the least 6 bits of the sessionID`s hashcode
	 * 
	 * @param ce An input critical event
	 * @return The last 6 bits of the hash code of the token ID as the event ID
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
	 * @param ev The critical event for which a decision is requested.
	 * @return The response to the coordinator
	 */
	@Override
	public final LocalPolicyResponse localRequest(CriticalEvent ev)
			throws IllegalArgumentException {
		// compute hashcode and get the least 6 bits as identifier
		int eventID = makeEventID(ev);

		int policyID = Integer.parseInt(getIdentifier());

		// get the responsible ID of CliSeAuNode and change it to String
		int handler = chord.nodeMap.get(policyID).findSuccessor(eventID)
				.getNodeID();
		String responsible = String.valueOf(handler);
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
				System.out.println("ID: " + getIdentifier() + "Destination ID: " + sReq.getDestID());
				return new DelegationLocPolReturn(sReq.getDestID(), sReq);
			}
		} else if (dr instanceof SafeShoppingDelegationResponse) {
			SafeShoppingDelegationResponse sResp = (SafeShoppingDelegationResponse) dr;
			if (getIdentifier().equals(sResp.getDestID())) {
				/* 
				 * response is for local unit and the response already contains
				 * the enforcement decision to return
				 */
				return sResp.getDecision();
			} else {
				// forward response
				return new DelegationLocPolReturn(sResp.getDestID(), sResp);
			}
		} else {
			throw new IllegalArgumentException(
					"Event for Remote Request of Wrong Type");
		}
	}

	/**
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
