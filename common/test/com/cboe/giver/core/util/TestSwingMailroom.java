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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import junit.framework.Assert;

import org.junit.Test;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Director;
import com.cboe.giver.core.DummyMessage;
import com.cboe.giver.core.Mailroom;
import com.cboe.giver.core.Message;

/**
 * Make sure that we correcly interact with the AWT thread
 * 
 * @author ryan
 * 
 */
public class TestSwingMailroom extends WhenSendingMessagesToAMailroom
{

	@Override
	public Mailroom buildNewMailroom()
	{
		return new SwingMailroom();
	}

	@Test
	public void awtThreadShouldBeUsedForAllDispatches() throws InterruptedException, InvocationTargetException
	{
		final AWTTestActor actor = new AWTTestActor();
		Director director = new Director.Builder().defaultMailroom(new SwingMailroom()).build();

		director.attach(actor);

		SwingUtilities.invokeAndWait(new Runnable()
		{
			@Override
			public void run()
			{
				actor.send(new DummyMessage());
			}
		});

		/* it shouldn't take longer than 5 seconds to finish */
		actor.count.await(1, TimeUnit.SECONDS);
		Assert.assertTrue("AWT thread did not do the dispatch", actor.isAwtThread);

	}

	public static class AWTTestActor extends Actor
	{
		boolean isAwtThread = false;
		CountDownLatch count = new CountDownLatch(1);

		@Override
		protected void act(Message msg)
		{
			isAwtThread = SwingUtilities.isEventDispatchThread();
			count.countDown();
		}
	}

	/*  */
	@Override
	protected boolean getRequiredInlineDescriptionForMailroom()
	{
		return false;
	}
}
