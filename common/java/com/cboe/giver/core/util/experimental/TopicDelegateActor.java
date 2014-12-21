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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Message;
import com.cboe.giver.core.util.TopicMessage;

/**
 * Responsible for using annotations to assign topics to methods.
 * 
 * Issues to still test for: - inheritence - polymorphism - Performance - method signature
 * adaptation
 */
public class TopicDelegateActor extends Actor
{
	private Map<String, Method> delegateMap = new HashMap<String, Method>();

	public TopicDelegateActor()
	{
		for (Method m : getClass().getMethods())
		{
			if (m.isAnnotationPresent(HandleTopic.class))
			{
				HandleTopic topicDelegate = m.getAnnotation(HandleTopic.class);
				// TODO: could either take this as a literal, take a comma separated set, or could
				// do a regex
				delegateMap.put(topicDelegate.value(), m);
			}
		}
	}

	@Override
	protected void act(Message msg) throws Throwable
	{
		if (msg instanceof TopicMessage)
		{
			TopicMessage message = (TopicMessage) msg;
			Method m = delegateMap.get(message.getTopic().getName());
			if (m != null)
			{
				m.invoke(this, message);
			}
		}
	}

}
