package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.EnforcementDecision;

/**
 * This class is an enumeration class contains only two elements PERMIT (0) and
 * REJECT (1), which are though to be the only two decisions.
 * 
 * @author Liu, Yi
 *
 */
public enum SafeShoppingDecision implements EnforcementDecision {
	/**
	 * Permission for the execution of the event
	 */
	PERMIT,
	/**
	 * Rejection for the execution of the event
	 */
	REJECT
}