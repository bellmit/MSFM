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
 * Wraps any message with timing data. Used like a tracer bullet.
 */
public final class MetricMessageWrapper implements ContainerMessage
{

	private final Message original;
	private final long startTimeNS;

	public MetricMessageWrapper(Message original, long startTimeNS)
	{
		this.original = original;
		this.startTimeNS = startTimeNS;
	}

	public long getStartTimeNS()
	{
		return startTimeNS;
	}

	@Override
	public Message getOriginalMessage()
	{
		return original;
	}

}