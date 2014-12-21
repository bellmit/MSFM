package com.cboe.domain.messaging;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class GiverThreadFactory implements ThreadFactory
{
	private static final AtomicInteger poolCount = new AtomicInteger(1);
	private final AtomicInteger threadCount = new AtomicInteger(1);

	@Override
	public Thread newThread(Runnable r)
	{
		String name = "mailroom-pool-" + poolCount.getAndIncrement() + "-thread-" + threadCount.getAndIncrement();
		Thread thread = new Thread(r, name);
		thread.setDaemon(false);
		thread.setPriority(Thread.NORM_PRIORITY);
		return thread;
	}
}