package com.cboe.domain.messaging;

import java.util.concurrent.atomic.AtomicLong;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Message;
import com.cboe.giver.core.Metrics;
import com.cboe.giver.core.MetricsFactory;
import com.cboe.instrumentationService.InstrumentorHome;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.QueueInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.QueueInstrumentor;

/**
 * Metrics factory that records instrumentation.
 * 
 * @author morrow
 * 
 */
public class InstrumentedMetricsFactory implements MetricsFactory
{
	private final QueueInstrumentorFactory qiFactory;
	private final MethodInstrumentorFactory miFactory;

	public InstrumentedMetricsFactory()
	{
		this(InstrumentorHome.findQueueInstrumentorFactory(), InstrumentorHome.findMethodInstrumentorFactory());
	}

	public InstrumentedMetricsFactory(QueueInstrumentorFactory qiFactory, MethodInstrumentorFactory miFactory)
	{
		this.qiFactory = qiFactory;
		this.miFactory = miFactory;
	}

	@Override
	public void start()
	{
	}

	@Override
	public void stop()
	{
	}

	@Override
	public Metrics buildMetricsFor(Actor actor)
	{
		return new MyMetrics(actor);
	}

	private final class MyMetrics implements Metrics
	{
		private final QueueInstrumentor queue;
		private final MethodInstrumentor methodSend;
		private final MethodInstrumentor methodAct;
		private final AtomicLong size = new AtomicLong(0);

		public MyMetrics(Actor actor)
		{
			String name = (actor == null || actor.toString() == null) ? "null" : actor.toString();
			queue = qiFactory.getInstance(name, null);
			methodSend = miFactory.getInstance(name + "_send", null);
			methodAct = miFactory.getInstance(name + "_act", null);
		}

		@Override
		public Message preSend(Message m)
		{
			// On preSend we are about to hand the message off to the mailbox which will deliver it
			// to the actor. We increment the queue size here since the mailbox will most likely add
			// the message to a queue before delivering it to the actor.
			size.incrementAndGet();
			queue.incEnqueued(1);
			queue.setCurrentSize(size.get());
			return new MessageWrapper(m, System.nanoTime());
		}

		@Override
		public void postSend(Message m)
		{
		}

		@Override
		public Message preAct(Message m)
		{
			MessageWrapper msg = (MessageWrapper) m;
			methodSend.incMethodTime(System.nanoTime() - msg.preSend);
			methodSend.incCalls(1);

			size.decrementAndGet();
			queue.incDequeued(1);
			queue.setCurrentSize(size.get());

			methodAct.beforeMethodCall();
			return msg.m;
		}

		@Override
		public void postAct(Message m)
		{
			methodAct.afterMethodCall();
			methodAct.incCalls(1);
		}
	}

	private static final class MessageWrapper implements Message
	{
		private final Message m;
		private final long preSend;

		public MessageWrapper(Message m, long preSend)
		{
			this.m = m;
			this.preSend = preSend;
		}
	}

}
