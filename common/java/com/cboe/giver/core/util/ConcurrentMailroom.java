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

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * Very rudimentary implementation. Consider other approaches. This will not
 * necessarily preserve FIFO ordering as it possible that if an executor is
 * already working on a node that two executors would execute against the same
 * node.
 * 
 * If you want FIFO ordering use that implementation instead
 */
public abstract class ConcurrentMailroom extends Mailroom
{

	private static final int DEFAULT_BURST_LENGTH = 100;
	private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime()
			.availableProcessors();
	/**
	 * How many messages will we try and do in a single quantum
	 */
	private volatile int burstLength;
	private final int threadCount;

	/**
	 * Create a mailroom with a specified number for the messages to attempt to
	 * "burst" or send on with a single thread iteration
	 * 
	 */
	public ConcurrentMailroom(int threadCount)
	{
		this.threadCount = threadCount;
		this.burstLength = DEFAULT_BURST_LENGTH;
	}

	public ConcurrentMailroom()
	{
		this(DEFAULT_THREAD_COUNT);
	}

	protected final int getThreadCount()
	{
		return threadCount;
	}

	/**
	 * Intended to be overridden. Provide your own variant. This object will
	 * only farm jobs to the exector, override {@link #start()} and
	 * {@link #stop()} to maintain the lifecycle of the executor
	 * 
	 * @return The executor for this mailroom
	 */
	protected abstract ExecutorService getExecutorService();

	/**
	 * Redefines the number of entries to do per CPU burst
	 */
	public final ConcurrentMailroom setBurstLength(int length)
	{
		this.burstLength = length;
		return this;
	}

	@Override
	public final Mailbox buildMailbox(Actor actor)
	{
		return new MyMailbox(actor);
	}

	/**
	 * This is a very naive implementation of the mailbox
	 */
	private class MyMailbox implements Mailbox, Runnable
	{

		private final Actor myActor;
		private final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();

		public MyMailbox(Actor actor)
		{
			this.myActor = actor;
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
			getExecutorService().execute(this);
		}

		@Override
		public void run()
		{
			int i = 0;
			while (i < burstLength)
			{
				Message msg = messages.poll();
				if (msg == null)
					break;
				forwardMessage(myActor, msg);
				i++;
			}
			if (messages.peek() != null)
			{
				getExecutorService().execute(this);
			}
		}

	}

}
