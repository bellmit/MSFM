/*
Copyright (c) 2010 Ryan Eccles

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package com.cboe.giver.core.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * A concurrent implementation of a Mailroom, but with a strong enforcement of
 * message order and thread interaction. This implementation will only allow
 * messages to be processed in FIFO ordering.
 * 
 * A sideeffect of this is that it implies only one thread will access an actor
 * at a time.
 * 
 */
public class FIFOMailroom extends Mailroom
{

	private static final int DEFAULT_BURST_LENGTH = 100;
	private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	/**
	 * How many messages will we try and do in a single quantum
	 */
	private volatile int burstLength;
	private final int threadCount;
	private ExecutorService executor;
	private ThreadFactory threadFactory = new GiverThreadFactory(FIFOMailroom.class.getSimpleName());

	/**
	 * Create a mailroom with a specified number for the messages to attempt to
	 * "burst" or send on with a single thread iteration
	 * 
	 */
	private FIFOMailroom(int threadCount)
	{
		this.threadCount = threadCount;
		this.burstLength = DEFAULT_BURST_LENGTH;
	}

	public FIFOMailroom()
	{
		this(DEFAULT_THREAD_COUNT);
	}

	/**
	 * Redefines the number of entries to do per CPU burst
	 */
	public FIFOMailroom setBurstLength(int length)
	{
		this.burstLength = length;
		return this;
	}

	@Override
	public Mailbox buildMailbox(Actor actor)
	{
		return new MyMailbox(actor);
	}

	@Override
	public void start()
	{
		executor = Executors.newFixedThreadPool(threadCount, threadFactory);

	}

	@Override
	public void stop()
	{
		executor.shutdown();
	}

	/**
	 * This is similar to the simple mailroom, except we use a signal so that we
	 * guarantee only one message gets in at a time.
	 * 
	 */
	private final class MyMailbox implements Mailbox, Runnable
	{

		private final Actor actor;
		private final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
		private final Lock lock = new ReentrantLock();
		private boolean isRunning = false;

		public MyMailbox(Actor actor)
		{
			this.actor = actor;
		}

		/*  */
		@Override
		public boolean isSendInlined()
		{
			return false;
		}

		@Override
		public void send(final Message msg)
		{
			messages.add(msg);

			/*
			 * ensure the thread is running if it isn't already
			 */
			try
			{
				lock.lock();
				if (isRunning == false)
				{
					isRunning = true;
					executor.execute(this);
				}
			} finally
			{
				lock.unlock();
			}
		}

		@Override
		public void run()
		{
			/*
			 * complete a full burst (if there is work to do)
			 */
			for (int i = 0; i < burstLength; i++)
			{
				Message message = messages.poll();
				if (message == null)
				{
					break;
				}

				forwardMessage(actor, message);
			}
			/*
			 * Now we may not have finished
			 */
			try
			{
				lock.lock();

				if (messages.peek() != null)
				{
					executor.execute(this);
				} else
				{
					isRunning = false;
				}

			} finally
			{
				lock.unlock();
			}
		}

	}

}
