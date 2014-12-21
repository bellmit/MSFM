package com.cboe.giver.core;

public class DummyMailroom extends Mailroom
{
	
	public static final DummyMailbox mailbox = new DummyMailbox();
	
	
	@Override
	public void start()
	{

	}

	@Override
	public void stop()
	{

	}

	@Override
	public Mailbox buildMailbox(Actor actor)
	{
		return mailbox;
	}

}
