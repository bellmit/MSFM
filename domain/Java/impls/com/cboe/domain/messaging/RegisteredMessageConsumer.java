package com.cboe.domain.messaging;

/**
 * A <tt>MessageConsumer</tt> that is registered with a <tt>MessageService</tt>. A registered
 * message consumer can subscribe to a topic to start receiving messages.
 * 
 * @author morrow
 * 
 */
public interface RegisteredMessageConsumer
{

	/**
	 * Subscribe to start receiving messages for the specified <tt>topic</tt>.
	 * 
	 * @param topic
	 *            the topic for which to receive messages.
	 */
	void subscribe(MessageTopic topic);

	/**
	 * Unsubscribe to no longer receive messages for the specified <tt>topic</tt>.
	 * 
	 * @param topic
	 *            the topic for which to stop receiving messages.
	 */
	void unsubscribe(MessageTopic topic);

	/**
	 * To send a message directly to this consumer.
	 * 
	 * @param message
	 *            the message to send to this consumer.
	 */
	void receive(Message message);

	/**
	 * Unsubscribes this consumer from all topics and unregisters it from the message service. Once
	 * this method is called this consumer can no longer receive messages. Calls to any methods will
	 * result in an {@link IllegalStateException}.
	 */
	void unregister();
}
