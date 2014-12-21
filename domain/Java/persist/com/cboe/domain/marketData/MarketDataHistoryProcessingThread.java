package com.cboe.domain.marketData;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import com.cboe.domain.marketData.mdhQueueEntry.MDHQueueEntry;
import com.cboe.domain.util.MarketDataHistoryServiceStatisticsManager;
import com.cboe.domain.util.ThreadLocalStringWriter;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryService;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryServiceHome;
import com.cboe.interfaces.internalBusinessServices.HistoryServiceStatisticsManager;
import com.cboe.server.queue.ServerQueue;
import com.cboe.server.queue.ServerQueueException;


class MarketDataHistoryProcessingThread extends Thread
{
	private ThreadLocalStringWriter threadLocalStringWriter = new ThreadLocalStringWriter();

	public int myThreadNumber;

	public BOHome home;

	public ServerQueue<MDHQueueEntry> myServerQueue;

	private MarketDataHistoryServiceHome marketDataHistoryServiceHome;

	private int marketDataHistoryBatchTime;

	private int marketDataHistoryDequeueBatchSize;

	private int marketDataHistoryBatchSize;
	
	private int dbIdBlockSize;

	public MarketDataHistoryProcessingThread(BOHome home, int threadNumber, ServerQueue sQueue, int marketDataHistoryBatchTime,
			int marketDataHistoryDequeueBatchSize, int marketDataHistoryBatchSize, int dbIdBlockSize)
	{
		super("MarketDataHistoryProcessingThread_" + threadNumber);
		this.home = home;
		myThreadNumber = threadNumber;
		myServerQueue = sQueue;
		this.marketDataHistoryBatchTime = marketDataHistoryBatchTime;
		this.marketDataHistoryDequeueBatchSize = marketDataHistoryDequeueBatchSize;
		this.marketDataHistoryBatchSize = marketDataHistoryBatchSize;
		this.dbIdBlockSize = dbIdBlockSize;
	}

	protected MarketDataHistoryServiceHome getMarketDataHistoryServiceHome()
	{
		if (marketDataHistoryServiceHome == null)
		{
			try
			{
				BOHome home = HomeFactory.getInstance().findHome(MarketDataHistoryServiceHome.HOME_NAME);
				marketDataHistoryServiceHome = (MarketDataHistoryServiceHome) home;
			}
			catch (Exception e)
			{
				logException("Could not find home " + MarketDataHistoryServiceHome.HOME_NAME, e);
			}
		}

		return marketDataHistoryServiceHome;
	}

	protected MarketDataHistoryService getMarketDataHistoryServiceObject()
	{
		return this.getMarketDataHistoryServiceHome().getServiceObject();
	}

	protected void refreshMarketDataHistoryServiceObject()
	{
		this.getMarketDataHistoryServiceHome().releaseServiceObject(this.getMarketDataHistoryServiceObject());
	}

	/**
	 * Enqueues an entry into the market data history queue.
	 */
	public void enqueue(MDHQueueEntry newEntry)
	{
		try
		{
			myServerQueue.enqueue(newEntry);
			return;
		}
		catch (Exception e)
		{
			logAlarm("Unable to add market data event to queue in thread " + this.getName() + " - will process from current thread");
		}

		processImmediately(newEntry);
	}

	private void processImmediately(MDHQueueEntry newEntry)
	{
		try
		{
			// Process this entry immediately.
//			Object[] entry = new Object[1];
//			entry[0] = newEntry;

			ArrayList<MDHQueueEntry> myArrayList = new ArrayList<MDHQueueEntry>(1);
			myArrayList.add(0, newEntry);

			doProcessing(myArrayList);
		}
		catch (Exception e)
		{
			logException(null, e);
		}
	}

	private boolean commitTransaction()
	{
		boolean rval = getMarketDataHistoryServiceObject().commitTransaction();

		if (!rval)
		{
			getMarketDataHistoryServiceObject().rollbackTransaction();
		}

		return rval;
	}

	public void getNextQueueEntry(ArrayList<MDHQueueEntry> nextEntry){	
	    try
	    {
	        nextEntry.clear();
	        final int dequeueBatchSize = getMarketDataHistoryDequeueBatchSize();
	        final int batchTime = getMarketDataHistoryBatchTime();

	        myServerQueue.dequeue(nextEntry, dequeueBatchSize, ServerQueue.INFINITE_TIMEOUT, batchTime);
	    }
	    catch (ServerQueueException e)
	    {
	        logException("Received exception on market data queue", e);
	        try
	        {
	            Thread.sleep(100);
	        }
	        catch (Exception ex)
	        {
	        } // avoid spinning wildly on a constant dequeue failure.
	    }
	}

	private int getMarketDataHistoryBatchTime()
	{
		return marketDataHistoryBatchTime;
	}

	private int getMarketDataHistoryDequeueBatchSize()
	{
		return marketDataHistoryDequeueBatchSize;
	}

	/**
	 * Processes events from market data queue.
	 */
	public void run()
	{
		ArrayList<MDHQueueEntry> nextEntry= new ArrayList<MDHQueueEntry>(getMarketDataHistoryBatchSize());
		Exception dequeueEx = null;

		if (myServerQueue != null)
		{
		    myServerQueue.reportDequeueThreadRunning();
		}
		try
		{
			initIdService ();
			// initialize the thread pool for MDH entries. This thread pool is used
			// for persisting entries from current process to the database.
			//
			MarketDataHistoryThreadPool.init(getMarketDataHistoryBatchSize());

			while (true)
			{
				getNextQueueEntry(nextEntry);

				doProcessing(nextEntry);
			}
		}
		catch (Exception e)
		{
			Log.exception(this.getHome(), Thread.currentThread().getName() + " caught exception. exiting.", e);
			
			dequeueEx = e;
		}
		finally
		{
			if (myServerQueue != null)
			{
			    myServerQueue.reportDequeueThreadExited(dequeueEx);
			}
		}
	}
	
	private int getDbIdBlockSize ()
	{
		return this.dbIdBlockSize;
	}

	private void initIdService()
	{
		if (getDbIdBlockSize () > 0)
		{
			long value = getDbIdBlockSize ();
			
			Log.information(getHome(), "Set IdService parameter PerThreadBlockSize to " + value);
			
			FoundationFramework.getInstance().getIdService().setPerThreadBlockSize(value);
		}
	}

	private void doProcessing(ArrayList<MDHQueueEntry> nextEntry)
	{
		if ((nextEntry != null) && (nextEntry.size() > 0) && !processEntries(nextEntry))
		{
			retry(nextEntry);
		}
	}

	private void notifyHomeOfFailure()
	{
		MarketDataHistoryService serviceObject = this.getMarketDataHistoryServiceObject();

		getMarketDataHistoryServiceHome().notifyHomeOfServiceFailure(serviceObject);
	}

	private void retry(ArrayList<MDHQueueEntry> nextEntry)
	{
		notifyHomeOfFailure();

		logInformation("Retrying persisting market data history using history service on "
				+ this.getMarketDataHistoryServiceObject().getServerRouteName());

		/*
		 * Do a switch to start persisting from the current process to the
		 * database if no remote services are available.
		 */
		if (!processEntries(nextEntry))
		{
			incrementDroppedCount(nextEntry.size());

			notifyHomeOfFailure();

			logDroppedEntries(nextEntry);
		}
	}

	private void logDroppedEntries(ArrayList<MDHQueueEntry> entry)
	{
		StringBuffer buf = new StringBuffer(2000);

		String id = this.getMarketDataHistoryServiceHome().getServiceIdentifier();

		buf.append("Dropped Market Data History Entries: Service Id = ").append(id).append(", count = ").append(entry.size())
				.append('\n');

		for (int i = 0; i < entry.size(); i++)
		{
			buf.append("\nEntry: ").append(i + 1).append("\n");
			buf.append(toString(entry.get(i)));
		}

		logAlarm(buf.toString());
	}

	private boolean processEntries(ArrayList<MDHQueueEntry> nextEntry)
	{
		boolean rval = false;

		try
		{
			MDHQueueEntry entry = null;

			startTransaction();

			for (int i = 0; i < nextEntry.size(); i++)
			{
				entry = (MDHQueueEntry) nextEntry.get(i);

				entry.execute(getMarketDataHistoryServiceObject());
			}

			rval = commitTransaction();
		}
		catch (Exception e)
		{
			logException("Caught exception using market data history service on: "
					+ this.getMarketDataHistoryServiceObject().getServerRouteName(), e);

			rollbackTransaction();

			rval = false;
		}

		return rval;
	}

	private int getMarketDataHistoryBatchSize()
	{
		return marketDataHistoryBatchSize;
	}

	private void rollbackTransaction()
	{
		getMarketDataHistoryServiceObject().rollbackTransaction();
	}

	private void startTransaction()
	{
		// In the server side we don't want to incur the overhead of Jgrinder
		// startTransaction as there's no jgrinder work as the call's being
		// forwarded to the history server.
		//
		refreshMarketDataHistoryServiceObject();

		getMarketDataHistoryServiceObject().startTransaction();
	}

	/**
	 * @return Returns the home.
	 */
	public BOHome getHome()
	{
		return home;
	}

	private void clearStringWriterBuffer()
	{
		this.threadLocalStringWriter.clear();
	}

	private StringWriter getStringWriter()
	{
		return this.threadLocalStringWriter.getStringWriter();
	}

	private void incrementDroppedCount(int length)
	{
		HistoryServiceStatisticsManager manager = MarketDataHistoryServiceStatisticsManager.getInstance();

		manager.incrementCount(manager.DROP_COUNT, length);
	}

	private String toString(Object entry)
	{
		StringWriter writer = getStringWriter();
		String rval = "";

		try
		{
			com.cboe.util.ReflectiveObjectWriter.writeObject(entry, "\t\t" + entry.getClass().getName(), writer);

			rval = writer.toString();
		}
		catch (IOException e)
		{
			logException("Caught exception while converting MarketDataHistoryEntry to string", e);
		}
		finally
		{
			clearStringWriterBuffer();
		}

		return rval;
	}

	private void logException(String msg, Exception e)
	{
		String message = Thread.currentThread().getName();

		if (msg != null)
		{
			message += " " + msg;
		}

		Log.exception(this.getHome(), message, e);
	}

	private void logInformation(String msg)
	{
		Log.information(this.getHome(), Thread.currentThread().getName() + " " + msg);
	}

	private void logAlarm(String msg)
	{
		Log.alarm(this.getHome(), Thread.currentThread().getName() + " " + msg);
	}

}
