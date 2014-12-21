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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * An implementation where there is one thread per Actor
 * 
 * TODO: Not tested
 */
public class SingleThreadMailroom extends Mailroom
{

	/** Time in millis that we await for data */
	private int RUN_PAUSE = 5000;
	
	/** will keep growing as needed */
	private ExecutorService executor;

	private volatile boolean isRunning = true;

	private ThreadFactory threadFactory = new GiverThreadFactory(SingleThreadMailroom.class.getSimpleName());

	@Override
	public Mailbox buildMailbox(Actor actor)
	{
		MyMailbox mb = new MyMailbox(actor);
		executor.execute(mb);
		return mb;
	}

	@Override
	public void start()
	{
		executor = Executors.newCachedThreadPool(threadFactory);
	}

	@Override
	public void stop()
	{
		isRunning = false;
		
		executor.shutdown();
	}

	private class MyMailbox implements Mailbox, Runnable
	{

		private final Actor actor;
		private ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();

		private final AtomicLong depth;
		private final Lock lock;
		private final Condition hasWork;

		public MyMailbox(Actor actor)
		{
			this.actor = actor;
			depth = new AtomicLong();
			lock = new ReentrantLock(false);
			hasWork = lock.newCondition();
		}

		/*  */
		@Override
		public boolean isSendInlined()
		{
			return false;
		}

		@Override
		public void send(Message msg)
		{
			messages.offer(msg);
			if (depth.getAndIncrement() == 0)
			{
				try
				{
					lock.lock();
					hasWork.signal();

				} finally
				{
					lock.unlock();
				}

			}
		}

		@Override
		public void run()
		{
			while (isRunning)
			{

				/*
				 * we may have run out of tasks. IN which case wait for a signal
				 */
				lock.lock();
				try
				{
					while (depth.get() == 0)
					{
						hasWork.await(RUN_PAUSE, TimeUnit.MILLISECONDS);
					}
				} catch (InterruptedException e)
				{
					// TODO: ?
				} finally
				{
					lock.unlock();
				}

				// pull all of the messages out. keep the thread alive as
				// long as possible
				while (depth.get() > 0)
				{

					depth.decrementAndGet();
					Message msg = messages.poll();
					if (msg != null)
					{
						forwardMessage(actor, msg);
					}

				}
			}
		}

	}
}
