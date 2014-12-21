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
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.DummyActor;
import com.cboe.giver.core.DummyMessage;
import com.cboe.giver.core.Instrumented;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * Define some basic functionality tests for mailrooms. All mailroom subclasses
 * should test against this contract.
 * 
 * @author ryan
 * 
 */
public abstract class WhenSendingMessagesToAMailroom {
	private static final int MESSAGE_COUNT = 5000;

	/** Each test subclass should build a new implementation for testing */
	public abstract Mailroom buildNewMailroom();

	private Mailroom testMailroom;

	@Before
	public void before() {
		testMailroom = buildNewMailroom();
		testMailroom.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void after() {
		testMailroom.stop();
		testMailroom = null;
	}

	@Test
	public void poisonPillEqualityTests() {
		Assert.assertTrue(Mailroom.isPoisonPill(Mailroom.getPoisonPill()));
		Assert.assertFalse(Mailroom.isPoisonPill(null));
	}

	/**
	 * Test to make sure one message makes it all the way through the system
	 */
	@Test
	public void singleMessageShouldAlwaysArriveAtActor() {
		Message message = new DummyMessage();
		CountingActor actor = new CountingActor(message);

		Mailroom mailroom = testMailroom;
		Mailbox box = mailroom.buildMailbox(actor);

		box.send(message);

		Assert.assertTrue(actor.waitForCompletion(5, TimeUnit.SECONDS));

	}

	@Test
	public void multipleMessagesShouldAlwaysArriveAtActor()
			throws InterruptedException {
		List<Message> messages = new ArrayList<Message>();
		for (int i = 0; i < MESSAGE_COUNT; i++) {
			messages.add(new DummyMessage());
		}

		CountingActor actor = new CountingActor(
				messages.toArray(new Message[MESSAGE_COUNT]));

		Mailroom mailroom = testMailroom;
		Mailbox box = mailroom.buildMailbox(actor);

		for (Message msg : messages) {
			box.send(msg);
			Thread.sleep(1);
		}

		Assert.assertTrue(actor.waitForCompletion(5, TimeUnit.SECONDS));
	}

	@Test
	public void spammingMultipleMessagesShouldArriveAtActor() {
		List<Message> messages = new ArrayList<Message>();
		for (int i = 0; i < MESSAGE_COUNT; i++) {
			messages.add(new DummyMessage());
		}

		CountingActor actor = new CountingActor(
				messages.toArray(new Message[MESSAGE_COUNT]));

		Mailroom mailroom = testMailroom;
		Mailbox box = mailroom.buildMailbox(actor);

		for (Message msg : messages) {
			box.send(msg);
		}

		Assert.assertTrue(actor.waitForCompletion(5, TimeUnit.SECONDS));
	}

	/**
	 * All mailbox types should include a test to make sure they return the
	 * correct type. This method enforces the rule, but asks the subclass to
	 * define what type should be done.
	 */
	@Test
	public void sendInlineDescriptionShouldMatchImplementationExpectations() {
		Mailroom mailroom = testMailroom;
		DummyActor actor = new DummyActor();
		Mailbox box = mailroom.buildMailbox(actor);

		Assert.assertEquals(box.isSendInlined(),
				getRequiredInlineDescriptionForMailroom());
	}

	protected abstract boolean getRequiredInlineDescriptionForMailroom();

	/**
	 * This actor allows us to specify how many we expect and block waiting for
	 * completion
	 * 
	 * @author ryan
	 * 
	 */
	public static class CountingActor extends Actor {
		final ConcurrentLinkedQueue<Message> messages;
		final CountDownLatch latch;

		public CountingActor(Message... msgs) {

			this.messages = new ConcurrentLinkedQueue<Message>();
			for (Message msg : msgs) {
				messages.add(msg);
			}
			latch = new CountDownLatch(msgs.length);
		}

		@Override
		protected void act(Message msg) throws Throwable {
			if (messages.remove(msg)) {
				latch.countDown();
			}
		}

		public boolean waitForCompletion(long timeout, TimeUnit unit) {
			try {
				return latch.await(timeout, unit);
			} catch (InterruptedException e) {
			}
			return false;
		}
	}

	@Instrumented
	public static class LatencyActor extends Actor {
		private AtomicLong latency = new AtomicLong();
		private AtomicLong total = new AtomicLong();

		private CountDownLatch latch;
		String var;

		public LatencyActor(int count) {
			latch = new CountDownLatch(count);
		}

		@Override
		protected void act(Message msg) throws Throwable {
			latency.addAndGet(System.nanoTime()
					- ((LatencyMessage) msg).getCreated());
			total.getAndIncrement();
			latch.countDown();
			for (int i = 0; i < 100; i++) {
				var = Math.random() + "" + i;
			}
		}

		public void waitTerm() throws InterruptedException {
			latch.await();
		}

		public double getAvgMics() {
			long mics = TimeUnit.MICROSECONDS.convert(latency.get(),
					TimeUnit.NANOSECONDS);

			return mics / (double) total.get();
		}

	}

	@Instrumented
	public static class LatencyMessage implements Message {

		private final long created;

		public LatencyMessage() {
			created = System.nanoTime();
		}

		public long getCreated() {
			return created;
		}

	}

}
