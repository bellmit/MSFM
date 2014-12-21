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

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * An experimental low latency mailroom. This mailroom achieves a reasonable
 * latency response by blocking the caller. This mailroom will show advantages
 * in latency when others would tend to queue. Note that this class will only
 * show advantages when the rate is fast enough that other mail rooms will
 * queue.
 * 
 * Notes: This version seems to do really well under load. It causes the calling
 * thread to back up but generally once a call is made the messages is delivered
 * very rapidly.
 * 
 * This implementation currently does not do very well with pauses when compared
 * to {@link ConcurrentMailroom} which uses the typical queuing style of
 * delivery.
 * 
 * A fairly substantial problem for this and all mailroom's is that they will
 * block and possibily create a deadlock situation if the call other types using
 * the same setup. All of these systems therefore need a deadlock resolution
 * system. In fact, this might be a useful generalized class. This might be a
 * "inline" scenario instead of consume two threads, alternately we have a
 * back-up queuing approach for second order messages.
 */
public class LowLatencyThreadedMailroom extends Mailroom {

	private static final int DEFAULT_THREADCOUNT = Runtime.getRuntime()
			.availableProcessors();

	public static enum ThreadPolicy {
		/** Each thread in the pool is used in turn */
		RoundRobin,

		/** An attempt is made to not wake up threads that are not needed */
		MinimizeActiveThreads,
	};

	private final BlockingDeque<InjectionThread> threads;
	private final int threadCount;
	private final ThreadPolicy myThreadPolicy;

	private ArrayList<InjectionThread> builtThreads = new ArrayList<InjectionThread>();

	public LowLatencyThreadedMailroom() {
		this(DEFAULT_THREADCOUNT, ThreadPolicy.MinimizeActiveThreads);
	}

	public LowLatencyThreadedMailroom(int threadCount, ThreadPolicy threadPolicy) {
		this.threadCount = threadCount;
		threads = new LinkedBlockingDeque<InjectionThread>(threadCount);
		this.myThreadPolicy = threadPolicy;
	}

	@Override
	public Mailbox buildMailbox(Actor actor) {
		return new InjectionMailbox(actor);
	}

	@Override
	public void start() {
		for (int i = 0; i < threadCount; i++) {
			InjectionThread t = new InjectionThread();
			t.start();
			threads.add(t);
			builtThreads.add(t);
		}
	}

	@Override
	public void stop() {
		for (InjectionThread t : builtThreads) {
			t.kill();
		}
		builtThreads.clear();
	}

	/**
	 * Offers the thread so it can be used again
	 */
	private void recycle(InjectionThread injectionThread) {
		assert injectionThread != null;
		/*
		 * No need to test for success since we know there will be enough room.
		 * We are reusing the top of the deque so that we keep the number of
		 * live threads to a minimum. This will increase contention but lower
		 * context switching.
		 */
		if (myThreadPolicy == ThreadPolicy.MinimizeActiveThreads) {
			threads.offerFirst(injectionThread);
		} else {
			threads.offerLast(injectionThread);
		}

	}

	public InjectionThread getFreeThread() {
		/* the loop is to catch spurious wakeup conditions */
		InjectionThread result = null;
		while (result == null) {
			try {
				result = threads.take();
			} catch (InterruptedException e) {
				// we want to keep trying even with a spurious wakeup
			}
		}
		return result;
	}

	/**
	 * Allow direct injection and execution
	 */
	public class InjectionMailbox implements Mailbox {
		private final Actor myActor;

		public InjectionMailbox(Actor actor) {
			this.myActor = actor;
		}

		/*  */
		@Override
		public boolean isSendInlined() {
			return false;
		}

		@Override
		public void send(Message msg) {
			getFreeThread().inject(myActor, msg);
		}

	}

	/**
	 * Inject data into this thread
	 */
	private final class InjectionThread extends GiverThread {

		private AtomicBoolean running = new AtomicBoolean(true);
		private Semaphore sem = new Semaphore(0);
		private AtomicReference<Task> work = new AtomicReference<Task>();

		/** parent just names the thread for us */
		public InjectionThread() {
			super(LowLatencyThreadedMailroom.class.getSimpleName());
		}

		public void inject(Actor actor, Message message) {
			if (actor == null || message == null)
				return;

			work.set(new Task(actor, message));
			sem.release();
		}

		public void kill() {
			running.set(false);
			sem.release();
		}

		@Override
		public void run() {
			while (running.get()) {
				try {
					/*
					 * acquire() will guarantee visibility of the published
					 * variables actor and message because of happens-before
					 * relationship.
					 */
					sem.acquire();
					Task myTask = work.getAndSet(null);
					if (myTask != null) {
						myTask.execute();
					}

				} catch (InterruptedException e) {
					/*
					 * interruption or spurious wakeup. If it is a spurious
					 * wakeup than the running value will still be true we'll
					 * just take another run at the acquire(), otherwise we are
					 * likely being killed.
					 * 
					 * NOOP
					 */
				} finally {
					recycle(this);
				}

			}
		}

	}

	public class Task {
		private final Actor actor;
		private final Message msg;

		public Task(Actor actor, Message msg) {
			this.actor = actor;
			this.msg = msg;
		}

		public void execute() {
			forwardMessage(actor, msg);
		}
	}
}
