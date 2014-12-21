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

import org.junit.Test;

import com.cboe.giver.core.Director;
import com.cboe.giver.core.util.FIFOMailroom;

/**
 * 
 */
public class TestEventSystem
{

	@Test
	public void simpleSignal()
	{
		TestEventObject actor = new TestEventObject();
		Director director = new Director.Builder()
			.defaultMailroom(new FIFOMailroom())
			.build();
		director.attach(actor);

		EventSystem system = new EventSystem();
		system.register(actor);

		for (int i = 0; i < 100; i++)
		{
			system.signal(i);
		}
	}

	private static class TestEventObject extends EventActor
	{
		@Override
		public boolean isHandled(int value)
		{
			return true;
		}
		
		@Override
		protected void handle(int value, EventSystem system) throws Throwable
		{
			// TODO: Should signal a latch on receive events
		}

	}
}
