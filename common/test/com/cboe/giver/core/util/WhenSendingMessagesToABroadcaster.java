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

import junit.framework.Assert;

import org.junit.Test;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Director;
import com.cboe.giver.core.Message;

public class WhenSendingMessagesToABroadcaster {

	@Test(expected = NullPointerException.class)
	public void topicsShouldFailForANullName() {
		Broadcaster.get().find(null);
	}

	@Test
	public void eachTopicShouldReceiveTheTransmittedMessage() {
		Director director = new Director.Builder().defaultMailroom(
				new InlineMailroom()).build();

		MyActor actor = new MyActor();
		director.attach(actor);

		Broadcaster.get()
			.find("MyTopic")
			.subscribe(actor);

		Broadcaster.get()
			.find("MyTopic")
			.createTopicMessageFrom(new MyMessage())
			.broadcast();
		
		Assert.assertTrue(actor.wasCalled);
	}

	public class MyActor extends Actor {
		boolean wasCalled = false;

		@Override
		protected void act(Message msg) {
			wasCalled = true;
		}
	}

	public class MyMessage implements Message {

	}
}
