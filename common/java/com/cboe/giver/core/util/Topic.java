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
import com.cboe.giver.core.Message;

/**
 * Represents a set of actors subscribed for a single item. All subscribed
 * actors will receive a wrapped TopicMessage for each send operation
 */
public interface Topic
{
	/** adds a new actor to receive TopicMessage messages */
	public void subscribe(Actor actor);

	/** Remove a previously added actor */
	public void unsubscribe(Actor actor);

	/** transmits the message to subscribers */
	public void broadcast(Message message);
	
	/** Creates a wrapper that contains the topic information */
	public TopicMessage createTopicMessageFrom(Message message);
	
	/** the name of the topic */
	public String getName();

}
