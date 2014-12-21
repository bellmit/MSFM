package com.cboe.domain.messaging;

/**
 * Provides message delivery services. A {@link MessageConsumer} must register with this service
 * before it can begin receiving messages. The message service handles the routing of all messages
 * delivered to the message consumers. Messages may be delivered synchronously or asynchronously on
 * a single thread or with multiple threads depending on the implementation.
 * 
 * @author morrow
 * 
 */
public interface MessageService
{
	/**
	 * Registers the specified consumer with this message service which allows the consumer to
	 * subscribe to topics and begin receiving messages.
	 * 
	 * @param consumer
	 *            the consumer to register
	 * @return the registered consumer
	 */
	RegisteredMessageConsumer register(MessageConsumer consumer);

	/**
	 * Returns a message publisher for the specified topic.
	 * 
	 * @param topic
	 * @return a message publisher
	 */
	MessagePublisher getPublisher(MessageTopic topic);

	/**
	 * Initiates an orderly shutdown.
	 */
	void shutdown();
}
