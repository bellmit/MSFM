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
package com.cboe.giver.core.util.experimental;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Allow defining signals by integer and listeners for signals. Similar to old
 * event systems.
 * 
 * For now signals 0-N are given preferential treatment. These will go faster
 * and use less garbage than values greater than N, where N is defined in the
 * constructor
 */
public final class EventSystem
{
	private final List<EventActor> actors;

	private final EventMessage[] cachedEventMessages;

	/**
	 * Default event system using 32 precached signals
	 */
	public EventSystem()
	{
		this(32);
	}

	/**
	 * Event system with maxCount defined cached signals. More are possible but
	 * will created extra garbage
	 */
	public EventSystem(int maxCount)
	{
		if (maxCount < 1)
		{
			throw new IllegalArgumentException("Cannot build precache of size zero or negative");
		}
		actors = new CopyOnWriteArrayList<EventActor>();
		cachedEventMessages = new EventMessage[maxCount];
		for (int i = 0; i < maxCount; i++)
		{
			// leaking this during construction is usually bad, however this is
			// a package protected access
			cachedEventMessages[i] = new EventMessage(i, this);
		}
	}

	/**
	 * Sends a signal to all of those that are listening
	 */
	public void signal(int value)
	{
		EventMessage msg = getEvent(value);
		broadcast(msg);
	}

	private final void broadcast(EventMessage msg)
	{
		/*
		 * XXX: optimization, don't use iterator, this creates unnecessary
		 * garbage This class only exists to reduce garbage
		 */
		for (EventActor actor : actors)
		{
			if (actor.isHandled(msg.getValue()))
			{
				actor.send(msg);
			}
		}
	}

	private final EventMessage getEvent(int value)
	{
		if (value < cachedEventMessages.length)
		{
			return cachedEventMessages[value];
		}

		return new EventMessage(value, this);
	}

	public void register(EventActor actor)
	{
		if (actor == null)
		{
			return;
		}
		if (actors.contains(actor))
		{
			return;
		}
		actors.add(actor);
	}
}
