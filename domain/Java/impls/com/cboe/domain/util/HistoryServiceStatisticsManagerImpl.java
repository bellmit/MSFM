package com.cboe.domain.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cboe.interfaces.internalBusinessServices.HistoryServiceStatisticsManager;
import com.cboe.util.HRTime;


/**
 * Implementation of history service stats manager.
 * 
 * @author singh
 *
 */

public class HistoryServiceStatisticsManagerImpl implements HistoryServiceStatisticsManager
{
	class Counter
	{
		private volatile long lastReportTime;

		private volatile long lastReportTimeInterval;

		private volatile long currentCount;

		private volatile long countAtReportTime;

		private volatile long incrementAtReportTime;

		public Counter()
		{
			lastReportTime = HRTime.gethrtime();
		}

		public void increment(int count)
		{
			currentCount += count;
		}

		public void sample()
		{
			long currentTime = HRTime.gethrtime();

			long count = currentCount;

			lastReportTimeInterval = currentTime - lastReportTime;
			incrementAtReportTime = count - countAtReportTime;
			countAtReportTime = count;
			lastReportTime = currentTime;
		}

		public long getCountAtReportTime()
		{
			return countAtReportTime;
		}

		public long getIncrementCount()
		{
			return incrementAtReportTime;
		}

		public double getIncrementRate()
		{
			return (getIncrementCount() * 1000000000.0) / lastReportTimeInterval;
		}
	}

	class ServerCounter
	{
		private String serverName;

		private Counter[] counters;

		public ServerCounter(int totalCounter)
		{
			counters = new Counter[totalCounter];

			for (int i = 0; i < totalCounter; i++)
			{
				counters[i] = new Counter();
			}
		}

		public void setServerName(String serverName2)
		{
			this.serverName = serverName2;
		}

		public String getServerName()
		{
			return serverName;
		}

		public void increment(int code, int count)
		{
			counters[code].increment(count);
		}

		public CounterSample[] sample()
		{
			CounterSample[] counterSample = new CounterSample[counters.length];

			for (int i = 0; i < counters.length; i++)
			{
				counters[i].sample();

				counterSample[i] = new CounterSample(counters[i].getCountAtReportTime(), counters[i].getIncrementCount(),
						counters[i].getIncrementRate());
			}

			return counterSample;
		}

		public long getIncrementCount(int code)
		{
			return counters[code].getIncrementCount();
		}

		public double getIncrementRate(int code)
		{
			return counters[code].getIncrementRate();
		}
	}

	class ThreadStatistics
	{
		private String serviceIdentifier;

		private HashMap serverStatistics = new HashMap();

		private volatile ServerCounter activeServerCounter;

		public void setActiveServer(String serverName)
		{
			synchronized (serverStatistics)
			{
				ServerCounter serverCounter = (ServerCounter) serverStatistics.get(serverName);

				if (serverCounter == null)
				{
					serverCounter = new ServerCounter(TOTAL_NUM_COUNTER);

					serverCounter.setServerName(serverName);

					serverStatistics.put(serverName, serverCounter);

					register(serverCounter);
				}

				activeServerCounter = serverCounter;
			}
		}

		public void setServiceIdentifier(String serviceIdentifier)
		{
			this.serviceIdentifier = serviceIdentifier;
		}

		public String getServiceIdentifier()
		{
			return this.serviceIdentifier;
		}

		public void increment(int code, int count)
		{
			activeServerCounter.increment(code, count);
		}
	}

	class CounterSample
	{
		private long totalCount;

		private long increment;

		private double incrementRate;

		public CounterSample(long totalCount, long increment, double incrementRate)
		{
			this.totalCount = totalCount;
			this.increment = increment;
			this.incrementRate = incrementRate;
		}

		public long getTotalCount()
		{
			return totalCount;
		}

		public long getIncrement()
		{
			return this.increment;
		}

		public double getIncrementRate()
		{
			return this.incrementRate;
		}
	}

	class ServiceCounter
	{
		private long[] totalCount;

		private long[] totalIncrement;

		private int totalThreadCount;

		private double[] totalRateCount;

		public ServiceCounter(int numCounters)
		{
			totalCount = new long[numCounters];
			totalIncrement = new long[numCounters];
			totalRateCount = new double[numCounters];
		}

		public void add(CounterSample[] sample)
		{
			totalThreadCount++;
			for (int i = 0; i < sample.length; i++)
			{
				totalCount[i] += sample[i].getTotalCount();
				totalIncrement[i] += sample[i].getIncrement();
				totalRateCount[i] += sample[i].getIncrementRate();
			}
		}
		
		public String toString ()
		{
			StringBuffer buffer= new StringBuffer (200);
			int numCounters = totalCount.length;
			
			for (int i = 0; i < numCounters; i++)
			{
				buffer.append(getCodeString(i)).append(" [ ");
				buffer.append(totalCount[i]).append(" / ");
				buffer.append(totalIncrement[i]).append(" / ");
				buffer.append(format (totalRateCount[i] / totalThreadCount)).append(" ]; ");
			}

			buffer.append("\n");

			return buffer.toString();
		}
	}

	class HistoryServiceCounter
	{
		private HashMap serverCounts = new HashMap();

		private ServiceCounter grandTotal;

		public void addCounterSamples(String server, CounterSample[] samples)
		{
			ServiceCounter counter = (ServiceCounter) serverCounts.get(server);

			if (counter == null)
			{
				counter = new ServiceCounter(samples.length);

				serverCounts.put(server, counter);
			}

			counter.add(samples);

			addToGrandTotal(samples);
		}

		
		public String toString()
		{
			StringBuffer buffer = new StringBuffer(500);

			Iterator itor = serverCounts.entrySet().iterator();

			while (itor.hasNext())
			{
				Map.Entry entry = (Map.Entry) itor.next();

				buffer.append(entry.getKey().toString()).append(" ").append(entry.getValue().toString()).append("\n");
			}
			
			if (grandTotal != null)
			{
				buffer.append("Total service statistics").append(" ").append(grandTotal.toString());
			}
			else
			{
				buffer.append ("No traffic yet");
			}

			return buffer.toString();
		}
		
		private void addToGrandTotal(CounterSample[] samples)
		{
			if (grandTotal == null)
			{
				grandTotal = new ServiceCounter(samples.length);
			}

			grandTotal.add(samples);
		}
	}

	private HashMap threadRegistry = new HashMap();

	static String[] counterStringValues = { "Input", "Forward", "Persist", "Drop", "Received", "Enqueue" };
	
	private NumberFormat numberFormat = NumberFormat.getInstance(); 
	
	public HistoryServiceStatisticsManagerImpl ()
	{
		numberFormat.setMaximumFractionDigits(3);
		numberFormat.setMinimumFractionDigits(2);
	}
	
	private String format (double value)
	{
		return numberFormat.format(value);
	}

	protected void register(ServerCounter counter)
	{
		synchronized (threadRegistry)
		{
			String key = Thread.currentThread().getName() + ":" + counter.getServerName();

			threadRegistry.put(key, counter);
		}
	}

	private ThreadLocal threadStatistics = new ThreadLocal();

	private ThreadStatistics getThreadStatistics()
	{
		ThreadStatistics stats = (ThreadStatistics) threadStatistics.get();

		if (stats == null)
		{
			stats = new ThreadStatistics();

			threadStatistics.set(stats);
		}

		return stats;
	}

	public void setServiceIdentifier(String serviceIdentifier)
	{
		getThreadStatistics().setServiceIdentifier(serviceIdentifier);
	}

	public void setActiveServer(String serverRouteName)
	{
		getThreadStatistics().setActiveServer(serverRouteName);
	}

	public void incrementCount(int code, int count)
	{
		getThreadStatistics().increment(code, count);
	}

	public String getStats()
	{
		StringBuffer buffer = new StringBuffer(500);

		synchronized (threadRegistry)
		{
			Iterator itor = threadRegistry.entrySet().iterator();

			HistoryServiceCounter serviceCounter = new HistoryServiceCounter();

			buffer.append("Thread statistics:\n==================\n");
			
			while (itor.hasNext())
			{
				Map.Entry entry = (Map.Entry) itor.next();

				ServerCounter serverCounter = (ServerCounter) entry.getValue();

				CounterSample[] samples = serverCounter.sample();

				format(buffer, (String) entry.getKey(), samples);

				serviceCounter.addCounterSamples(serverCounter.getServerName(), samples);
			}

			buffer.append("\n\nService Statistics:\n==================\n");
			buffer.append(serviceCounter.toString()).append("\n");
		}

		return buffer.toString();
	}

	private void format(StringBuffer buffer, String threadId, CounterSample[] samples)
	{
		buffer.append(threadId).append(" ");

		for (int i = 0; i < samples.length; i++)
		{
			buffer.append(getCodeString(i)).append(" [");
			buffer.append(samples[i].getTotalCount()).append(" / ");
			buffer.append(samples[i].getIncrement()).append(" / ");
			buffer.append(format (samples[i].getIncrementRate())).append(" ]; ");
		}

		buffer.append("\n");
	}

	private String getCodeString(int i)
	{
		return counterStringValues[i];
	}
}
