package com.cboe.domain.messaging;

/**
 * Publishes messages to subscribed consumers.
 * 
 * @author morrow
 *
 */
public interface MessagePublisher
{
	/**
	 * Returns the topic this publisher is for.
	 * 
	 * @return a topic
	 */
	MessageTopic getTopic();

	/**
	 * Returns a message with the passed in content.
	 * 
	 * @return a message
	 */
	Message getMessage(Object content);

	/**
	 * Publishes the passed message to all consumers subscribed for this publisher's topic.
	 * 
	 * @param message the message to publish
	 */
	void publish(Message message);
}
