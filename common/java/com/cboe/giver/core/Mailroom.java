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

/**
 * Takes messages from the actors mailbox and delivers the message to the actor
 * based on a threading configuration.
 * 
 * This object acts as a builder for mailbox objects which are implementations
 * of the mediator pattern.
 */
public abstract class Mailroom implements Service
{

	private static final Message POISON_PILL = new Message()
	{
	};

	public static final boolean isPoisonPill(Message message)
	{
		return message == POISON_PILL;
	}

	public static final Message getPoisonPill()
	{
		return POISON_PILL;
	}

	/** always returns a valid mailbox for the provided actor */
	public abstract Mailbox buildMailbox(Actor actor);

	/**
	 * Route messages to the actor via private interfaces. this method is here
	 * so we can make use of the package visibility rules in java. Those who
	 * extend Actor will not be able to see our secret back door method. All of
	 * this trickery is to make coding actors easier at the expense of a little
	 * more complexity in the mailroom.
	 */
	protected final void forwardMessage(Actor actor, Message msg)
	{
		actor.respond(msg);
	}

}
