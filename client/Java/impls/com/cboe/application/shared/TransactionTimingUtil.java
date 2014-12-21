package com.cboe.application.shared;

import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.instrumentationService.EntityID;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;
import com.cboe.application.shared.TransactionTimingConfiguration;


import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionTimingUtil {
	public static final String SOURCETYPE_STR = "prefixCluster";
	public static final String SOURCETYPE_CAS = "CAS";
	public static final String SOURCETYPE_FIX = "FIXCAS";
	public static final String SOURCENUM_FIX_STR = "prefixFixServerName";
	public static final String HOSTNAME_STR = "HOSTNAME";

    // The following are to take care of differences in naming among various environments.
    public static final String PERF_CAS_PREFIX = "perf";
    public static final String TST_CAS_PREFIX = "tst";
    public static final String DEMO_CAS_PREFIX = "demo";
    public static final String CME_CAS_PREFIX = "cme";
    public static final String DEV_CAS_PREFIX = "dev";
    public static final short PERF_SOURCENUM_BASE = 100;
	public static final short TST_SOURCENUM_BASE = 200;
	public static final short DEMO_SOURCENUM_BASE = 300;
	public static final short CME_SOURCENUM_BASE = 400;
	public static final short DEV_B_SOURCENUM_BASE = 500;

	
	private static final String SOURCETYPE_SACAS = "SACAS";
	private static short sourceType;
	private static short sourceNumber;
	private static TransactionTimer remoteTransactionTimer = null;
	private static TransactionTimer localTransactionTimer = null;
	
	private static AtomicReference<TransactionTimer> transactionTimerHolder = new AtomicReference<TransactionTimer>();
	
	private static final int TRANSID_BASE = 1000;
	private static ThreadLocal<Long> transactionNumber = null; 
	private static AtomicLong transIdBase = new AtomicLong(0L);
	private static long startTimeId = 0L;
	private static final int STARTTIMEID_SHIFT = 24;
	private static volatile boolean initialized = false;
    private static ThreadLocal<Long> curEntityId = new ThreadLocal<Long>()
    {
        @Override public Long initialValue ()
        {
            return 0L;
        }
    };

    private static ThreadLocal<Long> transactionStartTime = new ThreadLocal<Long>()
    {
        @Override public Long initialValue ()
        {
            return 0L;
        }
    };

    static
	{
		try
        {
            localTransactionTimer = FoundationFramework.getInstance().getInstrumentationService().getTransactionTimerFactory().getLocalTransactionTimer();
            remoteTransactionTimer = FoundationFramework.getInstance().getInstrumentationService().getTransactionTimerFactory().getRemoteTransactionTimer();
        }catch (Exception e)
        {
        	Log.exception("Exception getting the TransactionTimer", e);
        }
		initialize();
	}
	
    public static TransactionTimer getTTE()
	{
		return TransactionTimingConfiguration.isLocal() ? localTransactionTimer:remoteTransactionTimer;
	}
	public static TransactionTimer getTT()
	{
		return remoteTransactionTimer;
	}
	public static boolean ifCollectionEnabled()
    {            
		if (getTT()!= null)
		{
			if (TransactionTimingConfiguration.publishTT())
			{
				return true;
			}
		}
        return false;
    }
	

    /** Output data for Transaction Timing Enhanced (TTE), used in tracing the
     * history of an order through CBOEdirect.
     * @param registerId Event category
     * @param eid Entity ID (a/k/a Transaction ID, TID) specific to this order.
     * @param eventType #TransactionTimer.Enter, #TransactionTimer.Leave or #TransactionTimer.LeaveWithException
     */
	public static void generateOrderEvent(long registerId, long eid, int eventType)
	{
		if (getTTE()!= null)
		{
			if (TransactionTimingConfiguration.publishOrderTTE())
			{
				if( 0 != eid )
				{
					try
					{
						getTTE().sendTransactionMethodEvent (registerId, eid, eventType);
						
					}
					catch(Exception e)
					{
						Log.exception("Exception Sending Order TransactionMethodEvent", e);
						
					}
				} 
			}
		}
	}


    /** Output data for Transaction Timing Enhanced (TTE), used in tracing the
     * history of a quote through CBOEdirect.
     * @param registerId Event category
     * @param eid Entity ID (a/k/a Transaction ID) specific to this quote.
     * @param eventType #TransactionTimer.Enter, #TransactionTimer.Leave or #TransactionTimer.LeaveWithException
     */
    public static void generateQuoteEvent(long registerId, long eid, int eventType)
	{
		if (getTTE()!= null)
		{
			if (TransactionTimingConfiguration.publishQuoteTTE())
			{
				if( 0 != eid )
				{
					try
					{
					
						getTTE().sendTransactionMethodEvent (registerId, eid, eventType);								
					
					}
					catch(Exception e)
					{
						Log.exception("Exception Sending Quote TransactionMethodEvent", e);
						
					}
				}	
			}
		}
	}


    /** Generate an ID for use with Transaction Timing (TT) reporting.
     * @param productClass ClassKey of quotes in block.
     * @param session Name of session.
     * @param quoteBlock Number of quotes in block.
     * @return An identifier to use with #sendMethodEvent.
     */
    public  static long generateQuoteMetricId(long productClass, String session, long quoteBlock)
    {
        long id = 0;

        try
        {
            if(ifCollectionEnabled())
            {
                id =  CollectPerformanceMetrics.generateQuoteMetricId(productClass, session, quoteBlock);
            }
        }
        catch(Exception e)
        {
            Log.exception(e);
        }

        return id;
    }
    
    

    public static long generateOrderMetricId(OrderIdStruct orderId, String session)
    {
        long id = 0;
        try
        {
            if(ifCollectionEnabled())
            {
                id =  CollectPerformanceMetrics.generateOrderMetricId(orderId, session);
            }
        }
        catch(Exception e)
        {
            Log.exception(e);
        }

        return id;
    }


    /** Output data for Transaction Timing (TT), used by the Outlier Monitor.
     * @param metricEventType Event category.
     * @param id Identifier created by #generateQuoteMetricId.
     * @param direction 0 if entering the method, 1 if leaving the method.
     */
    public static void sendMethodEvent( long metricEventType, long id, int direction )
    {
        if(!ifCollectionEnabled()) return;

        try
        {
        	getTT().sendTransactionMethodEvent(metricEventType, id, direction);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

	private static boolean initSourceType() throws Exception
	{
		boolean initSourceTypeFlag = false;
		String sType = System.getProperty(SOURCETYPE_STR);
		
		if (SOURCETYPE_CAS.equals(sType))
		{
			sourceType = EntityID.TT_CAS;
			initSourceTypeFlag = true;
		}
		else if (SOURCETYPE_FIX.equals(sType))
		{
			sourceType = EntityID.TT_FIX;
			initSourceTypeFlag = true;
		}
		else if (SOURCETYPE_SACAS.equals(sType))
		{
			sourceType = EntityID.TT_EVENT;
			initSourceTypeFlag = false;
		}
		else
		{
			throw new Exception("Unknown sourceType: " + sType);
		}
		return initSourceTypeFlag;
	}
	
	private static void initCASSourceNumber() throws Exception
	{
//		String sCasNum = System.getProperty(HOSTNAME_STR);	// Use this for doing JUnit Testing
		String sCasNum = System.getenv(HOSTNAME_STR); 

		char chs[] = sCasNum.toCharArray();
		

		int len = 0;
		int endindex = -1;
		for (int i=chs.length-1; i>=0; --i)
		{
			
			if (chs[i] >= '0' && chs[i] <= '9')
			{
				++len;
				if (endindex < 0)
					endindex = i+1;
			}
			else
			{				
				if (len > 0)
				{
					break;
				}
			}
		}
		if (endindex < 0)
		{
			throw new Exception("A valid CAS number could not be extracted from the hostname: " + sCasNum); 
		}		
		String baseCASName = sCasNum.substring(0, endindex-len);
		String sCASNum = sCasNum.substring(endindex-len, endindex);
		try 
		{
			sourceNumber = (short)Integer.parseInt(sCASNum);
		}
		catch (Exception e)
		{
			throw new Exception("A valid CAS number could not be extracted from the hostname: " + sCasNum); 
		}
		if (baseCASName.startsWith(PERF_CAS_PREFIX))
		{
			sourceNumber += PERF_SOURCENUM_BASE;						
		}
		else if (baseCASName.startsWith(TST_CAS_PREFIX))
		{
			sourceNumber += TST_SOURCENUM_BASE;						
		}
		else if (baseCASName.startsWith(DEMO_CAS_PREFIX))
		{
			sourceNumber += DEMO_SOURCENUM_BASE;						
		}
		else if (baseCASName.startsWith(CME_CAS_PREFIX))
		{
			sourceNumber += CME_SOURCENUM_BASE;						
		}
		else if (baseCASName.startsWith(DEV_CAS_PREFIX))
		{
			sourceNumber += DEV_B_SOURCENUM_BASE;						
		}
	}
	
	private static void initFIXSourceNumber() throws Exception
	{
		String sFixNum = System.getProperty(SOURCENUM_FIX_STR);

		if (sFixNum != null)
		{
			String s = sFixNum.substring(sFixNum.indexOf("FIX", 0)+3, sFixNum.length()-1);
			try 
			{
				sourceNumber = (short)Integer.parseInt(s); 
			}
			catch (NumberFormatException nfe)
			{
				throw new Exception("A valid FIXCAS number could not be extracted from : " + sFixNum); 
			}			
		}		
	}
	
	private static void initSourceNumber(short sType) throws Exception
	{
		if (sType == EntityID.TT_CAS)
		{
			initCASSourceNumber();
		}
		else if (sType == EntityID.TT_FIX)
		{
			initFIXSourceNumber();
		}
		else
		{
			throw new Exception("Source Type not supported or invalid. sourceType=" + sType);
		}		
	}


	private static void initTransactionNumber()
	{
		// Get the current hour and  min.
        // starttimeId is calculated as (Hour * 10 + Minutes/10) and this goes in the
        // higher order bits of the transaction Identifier.
        // The max value for the starttimeId is 23 * 10 + 59/10 = 235 and it takes
        // 8 out of the 32 bits.
        Calendar c1 = TimeServiceWrapper.getCalendar();
		c1.setTimeInMillis(System.currentTimeMillis());
		long startmins = c1.get(Calendar.HOUR_OF_DAY) * 10 + c1.get(Calendar.MINUTE) / 10;
		startTimeId = startmins << STARTTIMEID_SHIFT;

        // the transactionNumber is initialized to 0
        transactionNumber = new ThreadLocal<Long>()
		{
			@Override public Long initialValue () 
			{
				return 0L;
            }
		};		
	}
	
	// TODO
	// public signature here is for testing with JUnit.
	public static void initialize() 
	{
		String sType = System.getProperty(SOURCETYPE_STR);
		if (!initialized)
		{
			try 
			{
				if (initSourceType())				
				{
					initSourceNumber(sourceType);
					
					initTransactionNumber();

				}
				initialized = true;
                StringBuilder sb = new StringBuilder(90);
                sb.append("TransactionTiming: sourceType=").append(sourceType)
                  .append(": sourceNumber=").append(sourceNumber)
                  .append(": startTimeId=").append(startTimeId);
                Log.information(sb.toString());
            }
			catch (Exception e)
			{
				Log.exception("Exception initializing TransactionTimingUtil", e);
                //e.printStackTrace();
			}			
		}		
	}

	// transactionIdentifier is 32 bits;
    // high order 8 bits represents the startTime as initialized in initTransactionNumber().
	// remaining 24 bits is enough to represent upto 16,777,215.  If this number is crossed,
    // then it will overflow to the upper 8 bits as well to avoid any duplicates occurring.
    // transactionNumber is a ThreadLocal variable that is incremented each time this
    // is called. Whenever the number reaches a multiple of TRANSID_BASE (set to 1000 now),
    // it gets the next base number from the transIdBase, which is an AtomicLong shared by all threads.
    private static long getNextTransactionNumber()
	{
		long tN = transactionNumber.get();
		if (tN % TRANSID_BASE == 0)
		{
			long base = transIdBase.getAndIncrement();
			tN = base * TRANSID_BASE;
		}
		transactionNumber.set(++tN);		
        return (startTimeId + tN);
    }
	
	public static TransactionTimer getTransactionTimer() 
	{
		if (transactionTimerHolder.get() == null)
		{
			synchronized (transactionTimerHolder) {
				if (transactionTimerHolder.get() == null)
				{
					try 
					{ 
						TransactionTimer tt = (TransactionTimer) FoundationFramework.getInstance().getInstrumentationService().getTransactionTimerFactory().getServiceContextTransactionTimer();
						transactionTimerHolder.set(tt);
					}
					catch (Exception e)
					{
						Log.exception("Exception getting the TransactionTimer", e) ;
					}
				}
			}
		}
		return transactionTimerHolder.get();
	}
	
	private static long getNextEntityID()
	{
		return EntityID.createID(sourceType,sourceNumber,getNextTransactionNumber());
	}

    // Gets the current entityId set in the current thread.
    public static long getEntityID() throws IllegalStateException
	{	
    	// only generate EID for FIX and CAS not SACAS
		if(sourceType == EntityID.TT_EVENT)
		{
			return 0L;
		}
		if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
		return curEntityId.get();
	}

    /*
     * The CAS and FIXCAS call this method to set the entityId before making the server calls
     * that needs to be timed with TransactionTiming.
     */
    public static long setEntityID() throws IllegalStateException
	{		
		if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
		long entityID = getNextEntityID();
		getTransactionTimer().setEntityID(entityID);
		curEntityId.set(entityID);
		return entityID;
	}

    /*
     * The CAS and FIXCAS call this method to set the entityId before making the server calls
     * that needs to be timed with TransactionTiming.
     */
    public static long setTTContext() throws IllegalStateException
	{
		if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
		long entityID = getNextEntityID();
        long currentTime=System.currentTimeMillis();
        getTransactionTimer().setTTContext(entityID,currentTime);
		curEntityId.set(entityID);
        transactionStartTime.set(currentTime);
        return entityID;
	}
    /*
       * The CAS and FIXCAS call this method to set the entityId before making the server calls
       * that needs to be timed with TransactionTiming.
       */
    public static long resetEntityID(long entityId) throws IllegalStateException
    {
        if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
		getTransactionTimer().setEntityID(entityId);
        curEntityId.set(entityId);
		return curEntityId.get();   
    }

    /*
     * Re-sets whatever EntityID was previously set.
     */
    public static long resetEntityID() throws IllegalStateException
	{		
		if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
		getTransactionTimer().setEntityID(curEntityId.get());

        return curEntityId.get();
	}

    /*
     * Re-sets whatever EntityID was previously set.
     */
    public static long resetTTContext() throws IllegalStateException
	{
		if (!initialized)
		{
			throw new IllegalStateException("TransactionTiming module not properly initialized!");
		}
        long startTime =  transactionStartTime.get();
        if(startTime>0)
        {
            getTransactionTimer().setTTContext(curEntityId.get(),startTime);
        }
        else
        {
            getTransactionTimer().setTTContext(curEntityId.get(),System.currentTimeMillis());
        }
        return curEntityId.get();
	}

	// For JUnit testing only. TODO
	public static void cleanup()
	{
		initialized = false;
	}
	
	
}
