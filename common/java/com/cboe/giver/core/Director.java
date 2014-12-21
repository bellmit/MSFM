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
package com.cboe.giver.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.giver.core.util.ThroughputMailroom;

/**
 * The main facility to start and stop actors. This object coordinates the lifecycle of actors and
 * connects them with the mailroom.
 */
public final class Director
{
	private final ConcurrentHashMap<Class<? extends Actor>, Mailroom> mailrooms;
	private final ExceptionHandler exceptionHandler;
	private final MetricsFactory metricsFactory;
	private final WeakHashMap<Actor, Actor> isAttached;
	/* o(n) lookup, but it is assumed small number of rooms, should be protected with mutex */
	private final Set<Mailroom> startedMailrooms;

	/**
	 * Use this to make new directors. It allows some reasonable defaults
	 */
	public static class Builder
	{
		private Mailroom mailroom = new ThroughputMailroom();
		private MetricsFactory metrics = NullMetricsFactory.getInstance();
		private ExceptionHandler exceptionHandler = SystemErrExceptionHandler.getInstance();
		private Map<Class<? extends Actor>, Mailroom> overrides = new HashMap<Class<? extends Actor>, Mailroom>();

		public Builder defaultMailroom(Mailroom mailroom)
		{
			if (mailroom == null)
			{
				throw new IllegalArgumentException("Cannot assign null to mailroom");
			}
			this.mailroom = mailroom;
			return this;
		}

		public Builder exceptionHandler(ExceptionHandler eh)
		{
			if (eh == null)
			{
				throw new IllegalArgumentException("Cannot assign a null exception handler");
			}
			this.exceptionHandler = eh;
			return this;
		}

		public Builder metricsFactory(MetricsFactory metricsFactory)
		{
			if (metricsFactory == null)
			{
				throw new IllegalArgumentException("Cannot assign null to metrics factory");
			}
			this.metrics = metricsFactory;
			return this;
		}

		public Builder overrideMailroom(Class<? extends Actor> type, Mailroom room)
		{
			if (room == null)
			{
				throw new IllegalArgumentException("Cannot assign null mailroom for actor type " + type);
			}

			overrides.put(type, room);
			return this;
		}

		public Director build()
		{

			Director d = new Director(this);
			for (Entry<Class<? extends Actor>, Mailroom> entry : overrides.entrySet())
			{
				d.bind(entry.getKey(), entry.getValue());
			}
			return d;
		}
	}

	/**
	 * Use the builder as it can do validation
	 */
	private Director(Builder builder)
	{

		this.exceptionHandler = builder.exceptionHandler;
		this.metricsFactory = builder.metrics;
		this.metricsFactory.start();

		isAttached = new WeakHashMap<Actor, Actor>();
		mailrooms = new ConcurrentHashMap<Class<? extends Actor>, Mailroom>();
		startedMailrooms = new HashSet<Mailroom>();
		bind(Actor.class, builder.mailroom);
	}

	public void shutdown()
	{
		Mailroom[] startedMailroomsCopy;
		synchronized (startedMailrooms)
		{
			startedMailroomsCopy = startedMailrooms.toArray(new Mailroom[startedMailrooms.size()]);
		}

		for (Mailroom room : startedMailroomsCopy)
		{
			room.stop();
		}
		metricsFactory.stop();
	}

	/**
	 * Friend method; injects the exception handler into the actor
	 */
	protected final void applyExceptionHandler(Actor actor, ExceptionHandler handler)
	{
		actor.setExceptionHandler(handler);
	}

	/**
	 * Friend method; the director injects the box into the actor. This method allows subsclasses to
	 * inject a mailbox into a particular actor even if not part of this package
	 */
	protected final void applyMailbox(Actor actor, Mailbox box)
	{
		actor.setMailbox(box);
	}

	/**
	 * Friend method; assigns metrics to the actor
	 */
	protected final void applyMetrics(Actor actor, Metrics metrics)
	{
		actor.setMetrics(metrics);
	}

	/** allow an actor to begin receiving messages */
	public void attach(Actor actor)
	{
		Mailroom mailroom = getMailroom(actor.getClass());
		attach(actor, mailroom);
	}

	/** Allow specifying a specific mailbox to use for the actor */
	public void attach(Actor actor, Mailroom mailroom)
	{
		if (actor == null)
		{
			throw new NullPointerException("Attempting to start null actor");
		}
		if (mailroom == null)
		{
			throw new NullPointerException("Attempting to attach using a null mailroom");
		}
		if (isAttached.containsKey(actor))
		{
			return;
		}
		isAttached.put(actor, actor);

		startIfNotStarted(mailroom);
		applyExceptionHandler(actor, exceptionHandler);
		applyMailbox(actor, mailroom.buildMailbox(actor));
		applyMetrics(actor, metricsFactory.buildMetricsFor(actor));
	}

	/**
	 * starts the mailroom if it hasn't been started here yet
	 * 
	 * @param mailroom
	 */
	private void startIfNotStarted(Mailroom mailroom)
	{
		synchronized (startedMailrooms)
		{
			if (startedMailrooms.contains(mailroom) == false)
			{
				startedMailrooms.add(mailroom);
				mailroom.start();
			}
		}
	}

	/** removes the actor from receiving messages. */
	public void detach(Actor actor)
	{
		if (actor == null)
		{
			throw new NullPointerException("Attempting to stop null actor");
		}
		isAttached.remove(actor);

		applyMetrics(actor, NullMetrics.getInstance());
		applyMailbox(actor, NullMailbox.getInstance());
		applyExceptionHandler(actor, SystemErrExceptionHandler.getInstance());
	}

	/**
	 * Define which mailroom should be associated with an actor when it's created. All actors
	 * started after this call will get your assigned room.
	 */
	protected void bind(Class<? extends Actor> type, Mailroom room)
	{
		mailrooms.put(type, room);
		startIfNotStarted(room);
	}

	@SuppressWarnings("unchecked")
	public Mailroom getMailroom(Class<? extends Actor> type)
	{
		Mailroom result = mailrooms.get(type);
		if (result != null)
		{
			return result;
		}
		return getMailroom((Class<? extends Actor>) type.getSuperclass());
	}

	@Override
	public String toString()
	{
		return "Director";
	}

	protected ExceptionHandler getExceptionHandler()
	{
		return exceptionHandler;
	}

}
