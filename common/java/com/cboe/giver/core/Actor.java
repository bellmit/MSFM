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
 * An actor is an entity that receives messages and responds to them.
 */
public abstract class Actor
{

	private volatile Mailbox box;
	private volatile Metrics metrics;
	private volatile ExceptionHandler handler;

	void setMailbox(Mailbox box)
	{
		this.box = box;
	}

	void setMetrics(Metrics metrics)
	{
		this.metrics = metrics;
	}

	void setExceptionHandler(ExceptionHandler handler)
	{
		this.handler = handler;
	}

	final ExceptionHandler getExceptionHandler()
	{
		if (handler == null)
		{
			return SystemErrExceptionHandler.getInstance();
		}
		return handler;
	}

	/**
	 * All messages should be deposited here. Always returns a value
	 */
	final Mailbox getMailbox()
	{
		if (box == null)
		{
			return NullMailbox.getInstance();
		}
		return box;
	}

	final Metrics getMetrics()
	{
		if (metrics == null)
		{
			return NullMetrics.getInstance();
		}
		return metrics;
	}

	/**
	 * Sends a message to this actor through a mediated channel. Messages are
	 * guaranteed to be delivered but not necessarily within the same thread. It
	 * is possible to query the actor via {@link #isSendInlined()}.
	 */
	public final void send(Message msg)
	{
		final Metrics metrics = getMetrics();
		msg = metrics.preSend(msg);
		getMailbox().send(msg);
		metrics.postSend(msg);
	}

	public final boolean isSendInlined()
	{
		return getMailbox().isSendInlined();
	}

	/* should be called by the mail room */
	protected final void respond(Message msg)
	{
		final Metrics metrics = getMetrics();
		msg = metrics.preAct(msg);
		try
		{
			act(msg);
		} catch (Throwable t)
		{
			getExceptionHandler().handleUncaught(this, t);
		}
		metrics.postAct(msg);
	}

	/**
	 * The actor should respond to the message in a non-blocking way
	 */
	protected abstract void act(Message msg) throws Throwable;

	@Override
	public String toString()
	{
		return "Actor: " + getClass().getSimpleName();
	}

}
