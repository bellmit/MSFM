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

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Mailbox;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * This should be used sparingly, however in some scenarios you don't want to
 * use a thread, but directly call the next actor. If this describes your
 * situation then this is your object.
 * 
 * Some of the problems to consider, if the actor that is being called inline
 * makes another inline call, and this inline call makes a call back to the
 * originator you can have a stack overflow condition. The best candidates for
 * inlining have zero feedback connections in the actor call chain.
 * 
 * You've been warned.
 */
public class InlineMailroom extends Mailroom
{

	@Override
	public Mailbox buildMailbox(Actor actor)
	{
		return new InlineMailbox(actor);
	}

	@Override
	public void start()
	{
	}

	@Override
	public void stop()
	{
	}

	public final class InlineMailbox implements Mailbox
	{
		private final Actor actor;

		public InlineMailbox(Actor actor)
		{
			this.actor = actor;
		}
		
		/*  */
		@Override
		public boolean isSendInlined()
		{
			return true;
		}

		@Override
		public void send(Message msg)
		{
			forwardMessage(actor, msg);
		}
	}

}
