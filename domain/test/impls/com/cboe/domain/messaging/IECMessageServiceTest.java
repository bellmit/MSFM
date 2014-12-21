package com.cboe.domain.messaging;

public class IECMessageServiceTest extends MessageServiceTest
{

	@Override
	protected MessageService createMessageService()
	{
		return new IECMessageService();
	}

}
