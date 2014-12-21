package com.cboe.domain.messaging;

/**
 * A <tt>MessageConsumer</tt> is used to receive messages. It may receive messages synchronously or
 * asynchronously depending on the threading strategy of the message service.
 * 
 * @author morrow
 * 
 */
public interface MessageConsumer
{
	/**
	 * Receive a message.
	 * 
	 * @param message
	 * @throws Throwable
	 */
	void onMessage(Message message) throws Throwable;
}
