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
import com.cboe.giver.core.Message;
import com.cboe.giver.core.util.Broadcaster;
import com.cboe.giver.core.util.Topic;
import com.cboe.giver.core.util.TopicMessage;

/** TODO: these are not tests yet */
public class WhenDelegatingTopicCalls
{
	static final String FOO_TOPIC = "foo";
	static final String BAR_TOPIC = "bar";

	@Test
	public void shouldDelegateToAppropriateMethodForTopic()
	{
		Director director = new Director.Builder().build();
		DelegateTest actor = new DelegateTest();
		director.attach(actor);
		
		Topic foo = Broadcaster.get().find(FOO_TOPIC);
		Topic bar = Broadcaster.get().find(BAR_TOPIC);

		foo.subscribe(actor);
		bar.subscribe(actor);

		foo.broadcast(new StringMessage("hello"));

		Broadcaster.get()
			.find(BAR_TOPIC)
			.createTopicMessageFrom(new StringMessage("hello"))
			.broadcast();
		
	}

	public static class DelegateTest extends TopicDelegateActor
	{
		@HandleTopic(FOO_TOPIC)
		public void foo(TopicMessage msg)
		{
			System.out.println("Foo method received this: " +msg);
		}

		@HandleTopic(BAR_TOPIC)
		public void bar(TopicMessage msg)
		{
			System.out.println("Bar method received this: " +msg);
		}
	}

	public class StringMessage implements Message
	{
		private String value;

		/** */
		public StringMessage(String value)
		{
			this.value = value;
		}

		/*  */
		@Override
		public String toString()
		{
			return value;
		}
	}
}
