package net.serviceautomata.instantiation;

import net.serviceautomata.lib.enforcer.PermittingEnforcer;
import net.serviceautomata.lib.enforcer.SuppressingEnforcer;
import net.serviceautomata.javacor.CriticalEvent;
import net.serviceautomata.javacor.EnforcementDecision;
import net.serviceautomata.javatarget.Enforcer;
import net.serviceautomata.javatarget.EnforcerFactory;

/**
 * This class construct an EnforcerFactory which can create a suitable Enforcer
 * according to the given decision.
 * 
 * @author Liu, Yi	
 * 
 */
public class SafeShoppingEnforcerFactory implements EnforcerFactory{

	/**
	 * Create a suitable Enforcer for a given EnforcementDecision.
	 *
	 * @param ed The EnforcementDecision from which the Enforcer is constructed.
	 * @return The constructed Enforcer.
	 */
	public static Enforcer fromDecision(final EnforcementDecision ed) {
		SafeShoppingDecision safeShoppingDecision = (SafeShoppingDecision) ed;
		if (safeShoppingDecision != null) {
			switch (safeShoppingDecision) {
				case PERMIT:
					return new PermittingEnforcer();
				case REJECT:
					return new SuppressingEnforcer();
			}
		}
		return new SuppressingEnforcer(); // be conservative
	}
	
	/**
	 * Create a fallback Enforcer for a given CriticalEvent.
	 *
	 * Here we always conservatively suppress the program event in case of an
	 * error. In principle, however, we could use a more optimistic approach.
	 *
	 * @param	event The CriticalEvent from which the Enforcer is constructed.
	 * @return	The constructed Enforcer.
	 */
	public static Enforcer fallback(final CriticalEvent event) {
		return new SuppressingEnforcer();
	}
}
