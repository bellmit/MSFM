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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.cboe.giver.core.Message;

/**
 * Tracks the latency timing information of a message in microseconds Thread
 * safe
 */
public class MessageLatency
{
	private long time = 0;
	private long count = 0;
	private long totalCount = 0;
	private Lock lock = new ReentrantLock();
	private final Class<? extends Message> messageType;
	private final boolean isContained;

	public MessageLatency(Class<? extends Message> messageType,
			boolean isContained)
	{
		this.isContained = true;
		this.messageType = messageType;
	}

	public boolean isContainedInOtherMessage()
	{
		return isContained;
	}

	public Class<? extends Message> getMessageType()
	{
		return messageType;
	}

	public void updateLatencyNanos(long duration)
	{
		try
		{
			lock.lock();
			count++;
			totalCount++;
			time += duration;
		} finally
		{
			lock.unlock();
		}
	}

	public long getMicrosecondsElapsed()
	{
		try
		{
			lock.lock();
			double avg = 0;
			if (count != 0)
			{
				avg = (double) time / count;
			}
			long mics = TimeUnit.MICROSECONDS.convert((long) avg,
					TimeUnit.NANOSECONDS);

			return mics;

		} finally
		{
			lock.unlock();
		}
	}

	public long getTotalCallCount()
	{
		return totalCount;
	}

	public long getCount()
	{
		return count;
	}

	public void reset()
	{
		try
		{
			lock.lock();
			count = 0;
			time = 0;
		} finally
		{
			lock.unlock();
		}
	}

	@Override
	public int hashCode()
	{
		return getMessageType().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MessageLatency == false)
			return false;
		else if (obj.hashCode() == hashCode())
			return true;
		else
			return false;
	}

}
