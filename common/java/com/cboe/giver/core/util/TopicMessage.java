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

import com.cboe.giver.core.ContainerMessage;
import com.cboe.giver.core.Message;

/**
 * Sent via Topic objects
 */
public class TopicMessage implements ContainerMessage
{

	private final Topic topic;
	private final Message originalMessage;

	public TopicMessage(Topic topic, Message originalMessage)
	{
		this.topic = topic;
		this.originalMessage = originalMessage;
	}

	public Topic getTopic()
	{
		return topic;
	}

	public Message getOriginalMessage()
	{
		return originalMessage;
	}

	@Override
	public String toString()
	{
		return originalMessage.toString();
	}

	/**
	 * Helper method to broadcast the information in this object
	 */
	public final void broadcast()
	{
		topic.broadcast(originalMessage);
	}
}
