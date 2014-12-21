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

import static com.cboe.giver.core.ActorAnnotationHelper.isInstrumented;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.ContainerMessage;
import com.cboe.giver.core.Message;
import com.cboe.giver.core.Metrics;
import com.cboe.giver.core.MetricsFactory;

/**
 * Build metric implementations that track both Message latency and actor method
 * timings. This tool will print the results to standard out.
 */
public class SysOutMetricsFactory implements MetricsFactory, Runnable
{

	/* using weak in case the object is no longer referenced elsewhere */
	private ConcurrentLinkedQueue<WeakReference<DualMetricsRecorder>> metrics = new ConcurrentLinkedQueue<WeakReference<DualMetricsRecorder>>();
	private ConcurrentHashMap<Class<?>, MessageLatency> messageTiming = new ConcurrentHashMap<Class<?>, MessageLatency>();
	private ScheduledExecutorService executors;

	/**
	 * Safe method to access the message latency
	 */
	protected final Collection<MessageLatency> getMessageTimingData()
	{
		return messageTiming.values();
	}

	@Override
	public void start()
	{

		executors = Executors.newScheduledThreadPool(1);
		executors.scheduleAtFixedRate(this, 0, 5, TimeUnit.SECONDS);

	}

	@Override
	public void stop()
	{
		executors.shutdown();
	}

	@Override
	public Metrics buildMetricsFor(Actor actor)
	{
		if (isInstrumented(actor))
		{

			DualMetricsRecorder sm = new DualMetricsRecorder(actor);
			metrics.add(new WeakReference<DualMetricsRecorder>(sm));
			return sm;

		} else
		{
			return new MessageOnlyRecorder();
		}
	}

	@Override
	public void run()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("==========================\n");
		builder.append("--------------------------\n");
		builder.append("Actors\n");

		builder.append("Name\t\tDuration (usec)\t\tCount\n");
		for (WeakReference<DualMetricsRecorder> wr : metrics)
		{
			DualMetricsRecorder metric = wr.get();
			builder.append(metric + "\n");
		}
		builder.append("end actors\n");
		builder.append("--------------------------\n");
		builder.append("Messages\n");
		builder.append("Class\t\tlatency(usec)\t\tCount\n");
		/* regular messages first */
		for (MessageLatency latency : getMessageTimingData())
		{

			long elapsed = latency.getMicrosecondsElapsed();
			long count = latency.getCount();
			latency.reset();
			builder.append(String.format("%s\t%d\t%d\n", latency
					.getMessageType(), elapsed, count));
		}
		builder.append("end Messages\n");

		System.out.println(builder.toString());
	}

	/**
	 * return calls if instrumented
	 * 
	 * @param msg
	 * @return
	 */
	private MessageLatency getCounterFor(Message msg)
	{
		boolean isContainer = false;

		if (msg instanceof ContainerMessage)
		{
			msg = ((ContainerMessage) msg).getOriginalMessage();
			isContainer = true;
		}

		MessageLatency latency = messageTiming.get(msg);
		if (latency != null)
		{
			return latency;
		}

		/*
		 * we only build one if not instrumented. This implies an overhead for
		 * non instrumented messages as well
		 */
		MessageLatency c = new MessageLatency(msg.getClass(), isContainer);
		MessageLatency previous = messageTiming.putIfAbsent(msg.getClass(), c);
		return (previous == null) ? c : previous;
	}

	private class MessageOnlyRecorder implements Metrics
	{

		@Override
		public Message preSend(Message msg)
		{
			if (isInstrumented(msg))
			{
				msg = new MetricMessageWrapper(msg, System.nanoTime());
			}

			return msg;
		}

		@Override
		public Message preAct(Message msg)
		{
			if (msg instanceof MetricMessageWrapper)
			{
				MetricMessageWrapper wrapper = (MetricMessageWrapper) msg;
				// unpack first to just be sure we don't loose the message
				msg = wrapper.getOriginalMessage();

				MessageLatency c = getCounterFor(msg);
				if (c != null)
				{
					c.updateLatencyNanos(System.nanoTime()
							- wrapper.getStartTimeNS());
				}
			}

			return msg;
		}

		@Override
		public void postAct(Message msg)
		{

		}

		@Override
		public void postSend(Message msg)
		{

		}

	}

	/* Records both the provided actor and messages */
	private class DualMetricsRecorder extends MessageOnlyRecorder
	{

		private final Actor actor;

		/* the actual amount of time (avg) that the call takes */
		private TimeCounter actualTime = new TimeCounter();
		private ThreadLocal<Long> preAct = new ThreadLocal<Long>();

		public DualMetricsRecorder(Actor actor)
		{
			if (actor == null)
			{
				throw new NullPointerException("Cannot assign null to actor");
			}
			this.actor = actor;
		}

		@Override
		public Message preAct(Message msg)
		{
			msg = super.preAct(msg);
			preAct.set(System.nanoTime());
			return msg;
		}

		@Override
		public synchronized void postAct(Message msg)
		{
			actualTime.updateNanoseconds(System.nanoTime() - preAct.get());
		}

		@Override
		public synchronized String toString()
		{
			TimeCounter call = actualTime;
			long callDuration = call.getMicrosecondsElapsed();
			long callCount = call.getCount();
			call.reset();

			String result = String.format("%s\t\t%d\t\t%d", actor,
					callDuration, callCount);

			return result;
		}
	}

}
