package com.cboe.domain.eventInstrumentation;

import java.util.concurrent.atomic.AtomicLong;

import com.cboe.interfaces.domain.eventInstrumentation.EventInstrumentation;

public class EventInstrumentationImpl implements EventInstrumentation {
	private String name;
	private AtomicLong count;
	public EventInstrumentationImpl(String name)
	{
		this.name=name;
		this.count=new AtomicLong(0);
	}
	
	public EventInstrumentationImpl(String name,long count)
	{
	    this(name);
		this.count.getAndAdd(count);
	}
	
	public long getCount() {
		return this.count.get();
	}

	public String getName() {
		return name;
	}

	public synchronized void increment() {
		this.count.getAndIncrement();
	}

}
