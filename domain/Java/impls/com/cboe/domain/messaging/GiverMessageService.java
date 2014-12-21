package com.cboe.domain.messaging;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Director;
import com.cboe.giver.core.util.Broadcaster;

public class GiverMessageService implements MessageService
{
	private final Director director;
	private static final Broadcaster broadcaster = Broadcaster.get();

	public GiverMessageService()
	{
		this(new Director.Builder().build());
	}

	public GiverMessageService(Director director)
	{
		this.director = director;
	}

	protected Director getDirector()
	{
		return director;
	}

	protected Broadcaster getBroadcaster()
	{
		return broadcaster;
	}

	@Override
	public RegisteredMessageConsumer register(MessageConsumer consumer)
	{
		return new MyRegisteredConsumer(consumer);
	}

	@Override
	public MessagePublisher getPublisher(MessageTopic topic)
	{
		return new MyMessagePublisher(topic);
	}

	@Override
	public void shutdown()
	{
		getDirector().shutdown();
	}

	protected final static String toTopicString(MessageTopic topic)
	{
		return topic.getClass().getName() + '.' + topic.toString() + topic.hashCode();
	}

	private final class MyMessagePublisher implements MessagePublisher
	{

		private final MessageTopic topic;
		private final com.cboe.giver.core.util.Topic giverTopic;

		private MyMessagePublisher(MessageTopic topic)
		{
			this.topic = topic;
			this.giverTopic = broadcaster.find(toTopicString(topic));
		}

		@Override
		public void publish(Message message)
		{
			giverTopic.broadcast(message);
		}

		@Override
		public MessageTopic getTopic()
		{
			return topic;
		}

		@Override
		public Message getMessage(Object content)
		{
			return new TopicMessage(topic, content);
		}

	}

	private final class MyRegisteredConsumer extends AbstractRegisteredMessageConsumer
	{
		private final Actor actor = new MyActor();

		public MyRegisteredConsumer(MessageConsumer consumer)
		{
			super(consumer);
			getDirector().attach(actor);
		}

		@Override
		protected void doSubscribe(MessageTopic topic)
		{
			broadcaster.find(toTopicString(topic)).subscribe(actor);
		}

		@Override
		protected void doUnsubscribe(MessageTopic topic)
		{
			broadcaster.find(toTopicString(topic)).unsubscribe(actor);
		}

		@Override
		protected void doReceive(Message message)
		{
			actor.send(message);
		}

		@Override
		protected void doUnregister()
		{
			getDirector().detach(actor);
		}

		private final class MyActor extends Actor
		{
			@Override
			protected void act(com.cboe.giver.core.Message message) throws Throwable
			{
				consumer.onMessage((Message) message);
			}

			@Override
            public String toString()
            {
	            return consumer.toString();
            }
		}
	}
}
