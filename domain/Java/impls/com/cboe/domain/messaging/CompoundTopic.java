package com.cboe.domain.messaging;


public class CompoundTopic implements MessageTopic
{
	private final Topic topic;
	private final Object subTopic;
	private int hashCode = -1;

	public CompoundTopic(Topic topic, Object subTopic)
	{
		this.topic = topic;
		this.subTopic = subTopic;
	}

	public Topic getTopic()
    {
    	return topic;
    }

	public Object getSubTopic()
    {
    	return subTopic;
    }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else
		{
			if (obj instanceof CompoundTopic)
			{
				CompoundTopic o = (CompoundTopic) obj;
				return (topic == null ? o.topic == null : topic.equals(o.topic)) && (subTopic == null ? o.subTopic == null : subTopic.equals(o.subTopic));
			}
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		if (hashCode == -1)
		{
			int result = 17;
			result = 31 * result + (topic == null ? 0 : topic.hashCode());
			result = 31 * result + (subTopic == null ? 0 : subTopic.hashCode());
			hashCode = result;
		}
		return hashCode;
	}

	@Override
    public String toString()
    {
	    return "Topic:" + topic + " Id:" + subTopic;
    }

}
