package com.cboe.domain.messaging;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

public class ThroughputMailroom extends Mailroom
{
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	private ExecutorService executor;
	private final int threadCount;
	private volatile int burstLength = 100;

	public ThroughputMailroom()
	{
		this(AVAILABLE_PROCESSORS);
	}
	
	public ThroughputMailroom(int threadCount)
	{
		this.threadCount = threadCount;
	}

	public void setBurstLength(int burstLength)
	{
		this.burstLength = burstLength;
	}

	@Override
	public void start()
	{
		executor = Executors.newFixedThreadPool(threadCount, new GiverThreadFactory());
	}

	@Override
	public void stop()
	{
		executor.shutdown();
	}

	@Override
	public Mailbox buildMailbox(Actor actor)
	{
		return new MyMailbox(actor);
	}


	private class MyMailbox implements Mailbox, Runnable
	{
		private final Actor actor;
		private final Queue<Message> messages = new ConcurrentLinkedQueue<Message>();
		private final Semaphore semaphore = new Semaphore(AVAILABLE_PROCESSORS);

		private MyMailbox(Actor actor)
		{
			this.actor = actor;
		}

		@Override
		public void send(Message msg)
		{
			messages.offer(msg);

			if (semaphore.tryAcquire())
			{
				executor.execute(this);
			}
		}

		@Override
		public boolean isSendInlined()
		{
			return false;
		}

		@Override
		public void run()
		{
			for (int i = 0; i < burstLength; i++)
			{
				final Message message = messages.poll();
				if (message == null)
				{
					break;
				}
				forwardMessage(actor, message);
			}

			semaphore.release();

			if (messages.isEmpty() == false)
			{
				if (semaphore.tryAcquire())
				{
					executor.execute(this);
				}
			}
		}
	}
}
