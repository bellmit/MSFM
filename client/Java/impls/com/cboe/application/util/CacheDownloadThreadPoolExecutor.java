/**
 * 
 */
package com.cboe.application.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author josephg
 *
 */
public class CacheDownloadThreadPoolExecutor 
{
	private ThreadPoolExecutor tpr;
	private static CacheDownloadThreadPoolExecutor instance = new CacheDownloadThreadPoolExecutor();
	private CacheDownloadThreadPoolExecutor()
	{
		tpr = new ThreadPoolExecutor(10,200,180,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>() );
		tpr.prestartAllCoreThreads();
	}
	
	public static CacheDownloadThreadPoolExecutor getInstance()
	{
		return instance;
	}
	
	public void execute(Runnable task)
	{
		tpr.execute(task);
	}
}
