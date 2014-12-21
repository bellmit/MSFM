package com.cboe.instrumentationService.impls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines a shared and common method for formatting time values.
 * 
 * This formatter makes use of caching to improve CPU performance.
 */
final class InstrumentorTimeFormatter
{

	/** number of seconds we keep around the cache. The value of 40 here is arbitrary.*/
	private static final int DEFAULT_CACHE_DURATION = 40;

	/**********************************************
	 * IMPORTANT:
	 * 
	 * The key to this caches efficiency is this string. We cache only based on a per second
	 * internal if this value is to change we will not be able to cache as efficiently.
	 * 
	 * Consider removing the cache if this is to change.
	 * 
	 * *********************************************
	 */
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

	/**
	 * Defines a common date formatter for each thread that calls. We do this shared version because
	 * building the formatter is very very expensive, and each formatter is not thread safe.
	 */
	private static ThreadLocal<SimpleDateFormat> sharedFormatter = new ThreadLocal<SimpleDateFormat>()
	{
		protected SimpleDateFormat initialValue()
		{
			return new SimpleDateFormat(DATE_FORMAT);
		}
	};

	/**
	 * Used to cache common repeated time values. This cache is a least recently used cache of time
	 * values
	 */
	private static ThreadLocal<Map<Long, String>> tlTimeCache = new ThreadLocal<Map<Long, String>>()
	{
		protected Map<Long, String> initialValue()
		{
			/*
			 * using an LRU so we keep only a few items around.
			 */
			return new LinkedHashMap<Long, String>()
			{
				protected boolean removeEldestEntry(Map.Entry<Long, String> eldest)
				{
					return size() > DEFAULT_CACHE_DURATION;
				}
			};
		}
	};

	/**
	 * Formats the value from a value since epoch to a standard format to be used by all
	 * instrumentors.
	 * 
	 * @param value
	 *            A long value in milliseconds since epoch.
	 * @return A date formatted string
	 */
	public final static String format(long value)
	{
		/*
		 * This can be called from numerous threads, but all of the caching logic works on a thread
		 * local variable so the map shouldn't get corrupted. This does mean that we will duplicate
		 * values per thread cache.
		 */
		Map<Long, String> cache = tlTimeCache.get();
		
		/*we can get away with this because of our formatting rule is persecond */
		Long key = value / 1000;  

		String result = cache.get(key);
		if (result == null)
		{
			result = sharedFormatter.get().format(new Date(value));
			cache.put(key, result);
		}

		return result;
	}
}
