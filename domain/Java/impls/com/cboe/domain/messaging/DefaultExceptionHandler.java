package com.cboe.domain.messaging;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.ExceptionHandler;

public enum DefaultExceptionHandler implements ExceptionHandler
{
	INSTANCE;

	@Override
	public void handleUncaught(Actor actor, Throwable t)
	{
		StringWriter stringWriter = new StringWriter(1000);
		stringWriter.write("An exception was thrown from Actor: " + String.valueOf(actor));
		t.printStackTrace(new PrintWriter(stringWriter));
		System.err.println(stringWriter.toString());
	}
}
