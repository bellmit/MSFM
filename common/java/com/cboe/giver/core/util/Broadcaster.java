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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.Message;

/**
 * A subscription/topic utility that works with actors and messages
 */
public class Broadcaster
{

	private static final Broadcaster singleton = new Broadcaster();

	public static final Broadcaster get()
	{
		return singleton;
	}

	private final ConcurrentHashMap<String, Topic> topics;

	private Broadcaster()
	{
		topics = new ConcurrentHashMap<String, Topic>();
	}

	/**
	 * Finds a topic for the topic name. Always returns a topic implementation
	 */
	public Topic find(String topicName)
	{

		if (topicName == null)
		{
			throw new NullPointerException("Cannot get empty topic");
		}

		Topic topic = topics.get(topicName);
		if (topic != null)
		{
			return topic;
		}
		/*
		 * assume many actors creating topics at the same time
		 */
		Topic newTopic = new BroadcasterTopic(topicName);
		Topic previousTopic = topics.putIfAbsent(topicName, newTopic);
		if (previousTopic == null)
		{
			return newTopic;
		}
		else
		{
			return previousTopic;
		}
	}

	/**
	 * Topics are broadcast upon
	 * 
	 */
	private static class BroadcasterTopic implements Topic
	{
		private final CopyOnWriteArrayList<Actor> listeners = new CopyOnWriteArrayList<Actor>();
		private final String topicName;

		BroadcasterTopic(String topicName)
		{
			assert topicName != null;
			this.topicName = topicName;
		}

		/**
		 * Will receive any message sent to the topic, wrapped in a "topic" message
		 */
		public void subscribe(Actor actor)
		{
			if (actor == null)
				return;
			listeners.add(actor);
		}

		public void unsubscribe(Actor actor)
		{
			if (actor == null)
				return;
			listeners.remove(actor);
		}

		/**
		 * For now just do it inline
		 */
		@Override
		public void broadcast(Message msg)
		{
			/* exit early if no listeners are present */
			if (listeners.isEmpty())
			{
				return;
			}

			for (Actor actor : listeners)
			{
				actor.send(msg);
			}
		}

		@Override
		public TopicMessage createTopicMessageFrom(Message message)
		{
			return new TopicMessage(this, message);
		}

		@Override
		public String toString()
		{
			return topicName;
		}

		@Override
		public String getName()
		{
			return topicName;
		}

	}

}
