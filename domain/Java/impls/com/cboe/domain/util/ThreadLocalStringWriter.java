package com.cboe.domain.util;

import java.io.StringWriter;

public class ThreadLocalStringWriter extends ThreadLocal
{
	protected Object initialValue()
	{
		return new StringWriter(2000);
	}

	public StringWriter getStringWriter()
	{
		return (StringWriter) super.get();
	}

	public void clear()
	{
		StringBuffer buffer = getStringWriter().getBuffer();

		buffer.delete(0, buffer.length());
	}
}
