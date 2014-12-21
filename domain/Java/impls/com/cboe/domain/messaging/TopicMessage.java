package com.cboe.domain.messaging;

public final class TopicMessage implements Message
{

	private final MessageTopic topic;
	private final Object content;

	public TopicMessage(Object content)
	{
		this(null, content);
	}

	public TopicMessage(MessageTopic topic, Object content)
	{
		this.topic = topic;
		this.content = content;
	}

	@Override
	public MessageTopic getTopic()
	{
		return topic;
	}

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getContent()
    {
	    return (T) content;
    }

}
