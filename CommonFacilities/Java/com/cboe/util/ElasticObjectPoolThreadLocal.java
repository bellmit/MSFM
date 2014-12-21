package com.cboe.util;

import java.util.concurrent.Semaphore;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Thread-local oriented thread pool algorithm
 * <br> 
 * After several iterations, this implementation has ended up looking a lot 
 * like ObjectPoolImpl.  The same basic algorithm is implemented.  One potentially
 * significant difference is that this implementation does not allow for an individual
 * pool to resize. 
 */
public class ElasticObjectPoolThreadLocal<T extends Copyable> implements ObjectPool<T>
{
    public static enum ExtraObjectPolicy { NEW_SEGMENT, NEW_LARGER_SEGMENT, SINGLE_OBJECT };

    /** (used to avoid calling ThreadLocal.set() more than once per public method call) */
    private static final class SimpleStackHolder<T2 extends Copyable>
    {
        SimpleStack<T2> stack;

        public SimpleStackHolder(final SimpleStack<T2> p_stack)
        {
            stack = p_stack;
        }
    }
    
    static boolean logOverflow = System.getProperty("ObjectPool.logPoolOverflow", "false").equals("true");
    static boolean traceMode = System.getProperty("ObjectPool.traceMode", "false").equals("true");
    
	private final String objTypeName;
	private final int segmentSize;
	private int maxNumSegments;
    private final T objectTemplate;
	private final Class objectClass;
	private SimpleStack<SimpleStack<T>> extraFullSegs;  // "global" stack of full stacks
	private SimpleStack<SimpleStack<T>> extraEmptySegs;  // "global" stack of empty stacks
	private final Semaphore globalLock = new Semaphore(1); // semaphore rather than synch for speed.
	private final ExtraObjectPolicy extraObjectPolicy;
	private final ThreadLocal<SimpleStackHolder<T>> poolPerThread;
	private int numObjects;
	
    private volatile int numOfCheckIns;
    private volatile int numOfExchangeFullSegmentForEmpty;
    private volatile int numOfExchangeEmptySegmentForFull;
    private volatile int numOfCheckOuts;
    private volatile int numOfOverflowInvocations;
    private volatile int lowWaterMark;
    private final int poolSizeWithOverflow;
	private volatile boolean logStackTraceOnCheckin = true;

	/**
	 * The value for p_initialCapacity should be minimally 
	 * <blockquote><pre>
	 *     p_segmentSize + [ max(p_segmentSize,numberOfObjectPerPool) * numberOfThreadsUsingPool ]
	 * </pre></blockquote>
	 * 
	 * @param p_objectTemplate - the source of new objects 
	 *     (pool is populated via multiple calls to p_objectTemplate.copy())
	 * @param p_initialCapacity - the number of objects to pre-create in the pool.
	 * @param p_segmentSize - the number of objects per segment.  This should be the
	 *     optimal number of 
	 *  @param p_capacityOverFlowFactor - This is the factor by which we will increase the stack size,
	 *  to account for the overflow, but will not populate the "increased-stack" 
	 */
	public ElasticObjectPoolThreadLocal(T p_objectTemplate, int p_initialCapacity, int p_segmentSize,
	        ExtraObjectPolicy p_extraObjPolicy, int p_capacityOverFlowFactor) 
	{
		this.poolPerThread = new ThreadLocal<SimpleStackHolder<T>>()
		{
		    @Override
		    protected SimpleStackHolder<T> initialValue()
            {
                return new SimpleStackHolder<T>(exchangeEmptySegmentForFull(null, true));
            }
		};
		
		if(p_initialCapacity == 0)
		{
		    this.maxNumSegments = 0;
		}
		else
		{
		    this.maxNumSegments = Math.max(2, (int)Math.ceil(p_initialCapacity/(double)p_segmentSize));
		}
		
		this.objectTemplate = p_objectTemplate;
		this.objTypeName = p_objectTemplate.getClass().getSimpleName();
		
		this.segmentSize = p_segmentSize;
		this.poolSizeWithOverflow = maxNumSegments + (p_capacityOverFlowFactor * maxNumSegments);
		this.extraFullSegs = new SimpleStack<SimpleStack<T>>(poolSizeWithOverflow);
		this.extraEmptySegs = new SimpleStack<SimpleStack<T>>(poolSizeWithOverflow);
		this.extraObjectPolicy = p_extraObjPolicy;
		this.objectClass = p_objectTemplate.getClass();
		// create the initial pool:
		for ( int i = 0; i < maxNumSegments; i++ ) 
		{
			this.extraFullSegs.push( newFullSegment() );
			doAccountingForNewFullSegment();
		}
        lowWaterMark = extraFullSegs.size;
		
		log("Initialized (traceMode="+traceMode+")");
	}

	public void checkIn(final T p_returnedObject) 
	{
	    if(this.maxNumSegments == 0)  //No Object Pooling when maxNumSegments is set to 0.
	    {
	        return;
	    }
	    
	    if (p_returnedObject != null) 
	    {
	        if(p_returnedObject.getAcquiringThreadId() == 0)
	        {
	            if (traceMode || logStackTraceOnCheckin)
	            {
	                log("Same object is returned more than Once.");
	                logStackTraceOnCheckin = false;
	            }
	            return;
	        }
	        
	        if(p_returnedObject.getClass() != objectClass)
	        {
	            log("Returned Object to the Pool is not of the same type as Object Pool Type", true);
	            return;
	        }
	        
	        p_returnedObject.clear();
            p_returnedObject.setAcquiringThreadId(0);
            
	        final SimpleStackHolder<T> stackHolder = this.poolPerThread.get();
	        SimpleStack<T> stack = stackHolder.stack;
	        if (stack.size == segmentSize)
	        {
	            stackHolder.stack = stack = exchangeFullSegmentForEmpty(stackHolder.stack);
	        }
	        stack.push( p_returnedObject );
	        numOfCheckIns++;
	    }
	}

	public T checkOut() 
	{
	    if(this.maxNumSegments == 0)
	    {
	        return newObject();
	    }
	    
	    final SimpleStackHolder<T> stackHolder = this.poolPerThread.get();
	    SimpleStack<T> stack = stackHolder.stack;

	    if ( stack.size == 0 ) 
	    {
	        final SimpleStack<T> newStack;
	        if (extraObjectPolicy == ExtraObjectPolicy.SINGLE_OBJECT)
	        {
	            newStack = exchangeEmptySegmentForFull(stackHolder.stack, false);
	            if (newStack == null) 
	            {
	                if (traceMode) log("Creating new object (since the global pool's exhausted)");
	                numObjects++;
	                T newObject = newObject();
	                newObject.setAcquiringThreadId(Thread.currentThread().getId());
	                return newObject;
	            }
	        }
	        else
	        {
	            newStack = exchangeEmptySegmentForFull(stackHolder.stack, true);
	           
	        }
	        stackHolder.stack = stack = newStack;
	    }
	    
	    T checkedOutObject = stack.pop();
	    numOfCheckOuts++;
	    checkedOutObject.setAcquiringThreadId(Thread.currentThread().getId());
	    return checkedOutObject; // (guaranteed to succeed: stack must now be non-empty.)
	}
	
	/**
	 * @param p_fullSegment - full segment to return
	 * @return An empty segment (never null)
	 */
	private SimpleStack<T> exchangeFullSegmentForEmpty(final SimpleStack<T> p_fullSegment)
	{
	    synchronized(globalLock)
	    {
	        numOfExchangeFullSegmentForEmpty++;
	       
	        if (poolSizeWithOverflow > extraFullSegs.size)
	        {
	            extraFullSegs.push(p_fullSegment);
	            if (traceMode) log("Returned a segment to the pool");
	        }
	        
	        if (extraEmptySegs.size != 0)
	        {
	            if (traceMode) log("Borrowing empty segment");
	            return extraEmptySegs.pop();
	        }
	    }

	    if (traceMode) 
	    {
	        log("Created extra empty segment");
	     }
	    return new SimpleStack<T>(segmentSize); // allocate empty stack outside the lock.
	}

	/**
	 * Return an empty segment to the global pool, and acquire a new, full segment.
	 * If the pool is exhausted and no new segment is available, then:
	 * <blockquote>
	 * If p_forceExchange is false, the method returns null and the empty segment is not recycled.  
	 * <br>If p_forceExchange is true, however, then a new segment is guaranteed to be returned.
	 * </blockquote>
	 * A non-null p_emptySegmentToReturn is recycled if and only if the method returns a non-null value.
	 * @param p_emptySegmentToReturn - the empty segment to recycle.  If null, then ignored.
	 * @param p_forceExchange - if true, then <em>a new segment is guaranteed to be returned</em>.  
	 * @return A full segment, if one can be found or allocated.  Otherwise, null.
  	 */
    private SimpleStack<T> exchangeEmptySegmentForFull(final SimpleStack<T> p_emptySegmentToReturn, final boolean p_forceExchange)
    {
        final boolean returnNewSeg;
        synchronized(globalLock)
        {
            numOfExchangeEmptySegmentForFull++;
            
            if(lowWaterMark > extraFullSegs.size)
            {
                lowWaterMark = Math.min(extraFullSegs.size,lowWaterMark);
            }
            
            if (extraFullSegs.size!=0)
            {
                returnEmptySegment(p_emptySegmentToReturn);
                if (traceMode) log("Borrowing full segment");
                return extraFullSegs.pop(); // Return with a new seg!  Yay! Return immediately.
            }
            if (p_forceExchange)
            {
                returnEmptySegment(p_emptySegmentToReturn);
                doAccountingForNewFullSegment(); // (accounting must be within the lock)
                // (forced to create: the new segment will be created below, outside the lock) 
            }
            else
            {
                return null; // We're not requested to force a segment to be created: abort!
            }
            numOfOverflowInvocations++;
        }
        
        // At this point, we know we need to build a new segment.
        // This work is done outside of the lock:
        if (traceMode || logOverflow)
        {
            log("CREATING AN ADDITIONAL FULL SEGMENT!!"); // (log this even if not in trace mode: it's a probable indication of poor pool sizing)
        }
        return newFullSegment();
	}
	
	@SuppressWarnings("unchecked")
	private T newObject()
	{
	    return (T)this.objectTemplate.copy();
	}
	
	/**
	 * Return en empty segment to the global pools.
	 * @param p_segment - empty segment to return (<em>if null, then method does nothing</em>)
	 */
	private void returnEmptySegment(final SimpleStack<T> p_segment)
	{
	    // NOTE: no lock acquired since it's called from within a lock in exchangeEmpty().
	    if (p_segment == null)
	    {
	        return;
	    }
        if (p_segment.size!=0)
        {
            // log despite traceMode setting: this is a probable error condition.
            log("Return of extra empty segment - NOT EMPTY!! (discarding it).  Size of supposedly empty segment is "+p_segment.size);
            return;
        }
        
        if (poolSizeWithOverflow > extraEmptySegs.size)
        {
            extraEmptySegs.push(p_segment);
            if (traceMode) log("Returned empty segment");
        }
	}
	
	   
    private SimpleStack<T> newFullSegment() 
    {
        final SimpleStack<T> seg = new SimpleStack<T>(segmentSize);
        final Object[] segAry = seg.array;

        // "cheat" and fill the array directly (for the sake of speed)
        for (int j = 0; j < segmentSize; j++) 
        {
            segAry[j] = newObject();
        }
        seg.size = segmentSize;
        return seg;
    }
    
    private void doAccountingForNewFullSegment()
    {
        numObjects += segmentSize;
    }

	private void acquireLock(final Semaphore p_lock)
	{
	    while (true)
	    {
	        try
	        {
	            p_lock.acquire();
	            return;
	        }
	        catch (InterruptedException e)
	        {
	            // this is probably over-engineering the problem, but hey...  why not
	            try
	            {
	                log("Lock acquisition interrupted! Nap and try again. ("+e+")");
	                Thread.currentThread().sleep(10);
	            }
	            catch (InterruptedException e2)
	            {
	            }
	        }
	    }
	}
	
	public String toString()
	{
	    return "objType="+this.objTypeName+", segSize="+segmentSize+", maxSegs="+maxNumSegments+", numFullSeg="+extraFullSegs.size
	        +", numEmptySegs="+this.extraEmptySegs.size+", numObjs="+numObjects + ", globalPoolObjs=" + ((extraFullSegs.size)*segmentSize)
	        +", numOfCheckOuts="+numOfCheckOuts + ", numOfcheckIns="+numOfCheckIns + ", numOfOverflowInvocations= "+ numOfOverflowInvocations +", numOfExchangeFullSegmentForEmpty="+numOfExchangeFullSegmentForEmpty
                +", numOfExchangeEmptySegmentForFull=" + numOfExchangeEmptySegmentForFull + ", lowWaterMark= " + lowWaterMark +"\n";
	}

	private void log(final String msg) 
	{
	    log(msg, false);
	}
	
	/**
	 * 
	 * @param msg
	 * @param alarm
	 */
	private void log(final String msg, boolean alarm) 
    {
	    // Example output: 
	    //   ObjectPool-TL [WorkerThread#4] "A Message Text" [POOL: objType=QuoteUpdatCommand segSize=.... ]
	    //
	    
		String fullMsg = "ObjectPool-TL [" + Thread.currentThread().getName() + "] \"" + msg + "\" [POOL: "+toString()+"]";
		if (alarm)
		{
		    Log.alarm(fullMsg);
		}
		else
		{
		    Log.information(fullMsg);
		}
	}
	
	public boolean isLogOverflow()
    {
        return logOverflow;
    }

    public void setLogOverflow(boolean p_logOverflow)
    {
        logOverflow = p_logOverflow;
    }

    public boolean isTraceMode()
    {
        return traceMode;
    }

    public void setTraceMode(boolean p_traceMode)
    {
        traceMode = p_traceMode;
    }
    
    public int getMaxNumSegments()
    {
        return maxNumSegments;
    }

    public void setMaxNumSegments(int p_maxNumSegments)
    {
        maxNumSegments = p_maxNumSegments;
    }
    
    public void addMoreSegments(StringBuffer returnValue, int numberOfSegmentsToBeAdded)
    {
        synchronized(globalLock)
        {
            int originalCapacity = this.extraFullSegs.array.length;
            SimpleStack<SimpleStack<T>> newExtraFullSegment = new SimpleStack<SimpleStack<T>>(numberOfSegmentsToBeAdded + originalCapacity);
            SimpleStack<SimpleStack<T>> newExtraEmptySegment = new SimpleStack<SimpleStack<T>>(numberOfSegmentsToBeAdded + originalCapacity);
            for(int i=0; i < numberOfSegmentsToBeAdded; i++)
            {
                newExtraFullSegment.push(newFullSegment());
                newExtraEmptySegment.push(new SimpleStack<T>(segmentSize));
            }

            returnValue.append("Original number of segments: ").append(originalCapacity);
            while(this.extraFullSegs.size != 0)
            {
                newExtraFullSegment.push(extraFullSegs.pop());
            }
            while(this.extraEmptySegs.size != 0)
            {
                newExtraEmptySegment.push(extraEmptySegs.pop());
            }
            this.extraFullSegs = newExtraFullSegment;
            this.extraEmptySegs = newExtraEmptySegment;
        }
        returnValue.append("Successfully added: ").append(numberOfSegmentsToBeAdded).append(" For object pool type ").append(objTypeName).append("\n");
    }
    
    public void undoObjectPooling(StringBuffer returnValue)
    {
        int numberOfObjectsPopped = 0;
        synchronized(globalLock)
        {
            this.setMaxNumSegments(0);

            while(this.extraFullSegs.size!= 0)
            {
                this.extraFullSegs.pop();
                numberOfObjectsPopped++;
            }
        }
        returnValue.append("Successfully cleared: " ).append(numberOfObjectsPopped).append(" For object pool type ").append(objTypeName);
    }
}
