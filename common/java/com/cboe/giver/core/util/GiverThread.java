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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * More of a convenience than anything, this object helps identifiy threads from
 * this system, and ties into our naming scheme. All threads regardless of the
 * pipeline location get a unique number. They can also get a human readable
 * name.
 */
public class GiverThread extends Thread
{
	private final static AtomicInteger gCount = new AtomicInteger(0);
	private final String name;

	public GiverThread(String shortName)
	{
		this.name = buildNameForId(shortName, gCount.getAndIncrement());
		setName(name);
	}

	public GiverThread(Runnable r, String shortName)
	{
		super(r);
		this.name = buildNameForId(shortName, gCount.getAndIncrement());
	}

	private String buildNameForId(String shortName, int id)
	{
		return String.format("%s:%d", shortName, id);
	}

}
