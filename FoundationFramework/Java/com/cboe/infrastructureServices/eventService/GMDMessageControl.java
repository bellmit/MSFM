package com.cboe.infrastructureServices.eventService;
/**
 * A handle to a GMD Message so the application can control
 * receipt acknowledgement.
 * 
 * @author Dave Hoag
 * @version 1.1
 */
public interface GMDMessageControl
{
	void acknowledgeReceipt() throws MessageTimedOut;
    void postponeAcknowledgement() throws MessageTimedOut;
}
