package com.cboe.domain.messaging;

import com.cboe.giver.core.Actor;
import com.cboe.giver.core.ExceptionHandler;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public enum FFLogExceptionHandler implements ExceptionHandler
{
	INSTANCE;

	@Override
	public void handleUncaught(Actor actor, Throwable t)
	{
		Log.exception("An exception was thrown from Actor: " + String.valueOf(actor), new Exception(t));
	}

}
