package com.cboe.domain.messaging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;


public abstract class AbstractRegisteredMessageConsumer implements RegisteredMessageConsumer
{
	final MessageConsumer consumer;
	private volatile boolean registered = true;
	private final List<MessageTopic> subscriptions = new CopyOnWriteArrayList<MessageTopic>();
	private final ReentrantLock lock = new ReentrantLock();

	protected AbstractRegisteredMessageConsumer(MessageConsumer consumer)
	{
		this.consumer = consumer;
	}

	public MessageConsumer getConsumer()
    {
    	return consumer;
    }

	@Override
	public void subscribe(MessageTopic topic)
	{
		lock.lock();
		try
		{
			checkIsRegistered();
			doSubscribe(topic);
			subscriptions.add(topic);
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public void unsubscribe(MessageTopic topic)
	{
		lock.lock();
		try
		{
			checkIsRegistered();
			doUnsubscribe(topic);
			subscriptions.remove(topic);
		}
		finally
		{
			lock.unlock();
		}
	}

	protected abstract void doSubscribe(MessageTopic topic);

	protected abstract void doUnsubscribe(MessageTopic topic);

	protected abstract void doUnregister();

	protected abstract void doReceive(Message message);

	@Override
	public void receive(Message message)
	{
		checkIsRegistered();
		doReceive(message);
	}

	@Override
	public void unregister()
	{
		lock.lock();
		try
		{
			registered = false;
			for (MessageTopic topic : subscriptions)
			{
				doUnsubscribe(topic);
			}
			doUnregister();
		}
		finally
		{
			lock.unlock();
		}
	}

	void checkIsRegistered()
	{
		if (registered == false)
			throw new IllegalStateException("This consumer has been unregistered.");
	}

}
