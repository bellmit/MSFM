package com.cboe.domain.messaging;

import com.cboe.util.property.PropertyHelper;

public class MessageServiceFactory
{

	public static MessageService create()
	{
		MessageService service = null;
		InternalMessageService type = PropertyHelper.getEnum(InternalMessageService.Giver);
		switch (type)
		{
			case IEC:
				service = new IECMessageService();
				break;

			case Giver:
				service = new GiverMessageService();
				break;
		}
		return service;
	}

	private enum InternalMessageService
	{
		IEC,
		Giver
	}
}
