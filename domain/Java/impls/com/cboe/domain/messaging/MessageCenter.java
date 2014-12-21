package com.cboe.domain.messaging;

/**
 * Provides a central point for messaging services.
 * 
 * @author morrow
 * 
 */
public class MessageCenter implements MessageService
{
	private static final MessageCenter MESSAGE_CENTER = new MessageCenter();
	private final MessageService messageService;

	protected MessageCenter()
	{
		messageService = createMessageService();
	}

	protected MessageService createMessageService()
	{
		return MessageServiceFactory.create();
	}

	public static MessageCenter get()
	{
		return MESSAGE_CENTER;
	}

	@Override
	public RegisteredMessageConsumer register(MessageConsumer consumer)
	{
		return messageService.register(consumer);
	}

	@Override
	public MessagePublisher getPublisher(MessageTopic topic)
	{
		return messageService.getPublisher(topic);
	}

	@Override
	public void shutdown()
	{
		messageService.shutdown();
	}

	/**
	 * Convenience method that publishes the message for the specified topic.
	 * 
	 * @param topic
	 * @param messageContent
	 * @return a message publisher
	 */
	public void publish(MessageTopic topic, Object messageContent)
	{
		MessagePublisher publisher = getPublisher(topic);
		Message message = publisher.getMessage(messageContent);
		publisher.publish(message);
	}

}
