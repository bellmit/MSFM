package com.cboe.domain.messaging;

import com.cboe.giver.core.Director;

public class GiverMessageServiceTest extends MessageServiceTest
{

	@Override
	protected MessageService createMessageService()
	{
		Director director = new Director.Builder().build();
		return new GiverMessageService(director);
	}

}
