/**
 * 
 */
package com.cboe.domain.util;

/**
 * @author Gijo Joseph
 *
 */
public class BlockedOrderStatusContainer {
	private com.cboe.idl.order.BlockedOrderStatus[] statusMsgs;

	public BlockedOrderStatusContainer(com.cboe.idl.order.BlockedOrderStatus[] statusMsgs)		
	{
		this.statusMsgs = statusMsgs;
	}
	
	public com.cboe.idl.order.BlockedOrderStatus[] getBlockedOrderStatus()
	{
		return statusMsgs;
	}
}
