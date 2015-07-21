package net.serviceautomata.instantiation;

import net.serviceautomata.javacor.CriticalEvent;
/**
 * This class takes the SafeShoppingDelegationReqResp class as parent class
 * and handles the transmission of critical event from source to destination CliSeAu node
 * 
 * @author Liu,Yi
 *
 */
public class SafeShoppingDelegationRequest extends SafeShoppingDelegationReqResp {
	
	private static final long serialVersionUID = 1L;
	/**
	 * the critical event to be sent
	 */
	public SafeShoppingEvent se;
	/**
	 * Constructor of this class
	 * @param se           critical event
	 * @param sourceID
	 * @param destID
	 */
	public SafeShoppingDelegationRequest(final SafeShoppingEvent se, final String sourceID, final String destID) {
		super(sourceID, destID);
		this.se = se;
	}
	
	public CriticalEvent getEvent()
	{
		return se;
	}
}
