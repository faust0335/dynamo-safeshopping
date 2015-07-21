package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.EnforcementDecision;
/**
 * This class defined the two decisions: PERMIT and REJECT
 * 
 * @author Liu, Yi	
 * 
 */
public enum SafeShoppingDecision implements EnforcementDecision {
	PERMIT,
	REJECT
}