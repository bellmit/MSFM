package com.cboe.domain.messaging;

import com.cboe.giver.core.ExceptionHandler;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;

public class IECMessageService implements MessageService
{
	private final ChannelAdapter iec;
	private final ExceptionHandler exceptionHandler;

	public IECMessageService()
	{
		this(new EventChannelAdapter());
	}

	public IECMessageService(ChannelAdapter iec)
	{
		this(iec, DefaultExceptionHandler.INSTANCE);
	}

	public IECMessageService(ChannelAdapter iec, ExceptionHandler exceptionHandler)
	{
		this.iec = iec;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public RegisteredMessageConsumer register(MessageConsumer consumer)
	{
		return new IECRegisteredConsumer(consumer);
	}

	@Override
	public MessagePublisher getPublisher(MessageTopic topic)
	{
		return new MyMessagePublisher(topic);
	}

	@Override
	public void shutdown()
	{
		iec.stopChannelAdapter();
	}

	private final class MyMessagePublisher implements MessagePublisher
	{
		private final MessageTopic topic;

		private MyMessagePublisher(MessageTopic topic)
		{
			this.topic = topic;
		}

		@Override
		public void publish(Message message)
		{
			final ChannelEvent event = iec.getChannelEvent(this, topic, message);
			iec.dispatch(event);
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

	private final class IECRegisteredConsumer extends AbstractRegisteredMessageConsumer implements InstrumentedEventChannelListener
	{

		private IECRegisteredConsumer(MessageConsumer consumer)
		{
			super(consumer);
		}

		@Override
		protected void doSubscribe(MessageTopic topic)
		{
			iec.addChannelListener(IECMessageService.this, this, topic);
		}

		@Override
		protected void doUnsubscribe(MessageTopic topic)
		{
			iec.removeChannelListener(IECMessageService.this, this, topic);
		}

		@Override
		protected void doUnregister()
		{
			// do nothing
		}

		@Override
		protected void doReceive(Message message)
		{
			try
			{
				getConsumer().onMessage(message);
			}
			catch (Throwable e)
			{
				exceptionHandler.handleUncaught(null, e);
			}
		}

		@Override
		public void channelUpdate(ChannelEvent event)
		{
			try
			{
				consumer.onMessage((Message) event.getEventData());
			}
			catch (Throwable e)
			{
				exceptionHandler.handleUncaught(null, e);
			}
		}

		@Override
		public void queueInstrumentationInitiated()
		{
		}

		@Override
		public String getName()
		{
			return toString();
		}
	}

}
