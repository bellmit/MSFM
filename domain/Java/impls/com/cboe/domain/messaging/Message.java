package com.cboe.domain.messaging;

/**
 * The root interface for passing information to a <tt>MessageConsumer</tt>.
 * 
 * @author morrow
 * 
 */
public interface Message extends com.cboe.giver.core.Message
{
	MessageTopic getTopic();

	<T> T getContent();
}
