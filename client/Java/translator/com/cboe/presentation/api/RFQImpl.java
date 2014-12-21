package com.cboe.presentation.api;

import java.util.*;
import java.lang.reflect.*;
import javax.swing.*;

import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.time.TimeSyncWrapper;

import com.cboe.interfaces.presentation.rfq.RFQ;
import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.domain.util.DateWrapper;

/**
  A "wrapper class" used to wrap RFQStruct instances and manage them
  within the cache.
  @author Will McNabb
 */
public class RFQImpl extends Observable implements RFQ
{
///////////////////////////////////////////////////////////////////////////////
// ATTRIBUTES

    private int rfqState;
    private long entryTime;
    private long greenStateTime;
    private long yellowStateTime;
    private long expireTime;
    private Integer productKey;
    private Integer productType;
    private Integer classKey;
    private Integer quantity;
    private RFQStruct rfqStruct;
    private RFQStruct latestStruct;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTION

    public RFQImpl(RFQStruct struct)
    {
        this.rfqState = DELETED;
        this.rfqStruct = struct;
        this.productKey = new Integer(rfqStruct.productKeys.productKey);
        this.productType = new Integer(rfqStruct.productKeys.productType);
        this.classKey = new Integer(rfqStruct.productKeys.classKey);
        this.quantity = new Integer(rfqStruct.quantity);

        resetEntryExpiration(this.rfqStruct);

        //Set changed for the initial update
        setChanged();
    }

///////////////////////////////////////////////////////////////////////////////
// INTERFACE IMPLEMENTATION

    /**
      Implements InstructionTarget
    */
    public boolean doUpdate()
    {
        //Get on the worker thread and update using the latest rfq struct
        WorkerUpdate updateProc = new WorkerUpdate();

        if(!SwingUtilities.isEventDispatchThread())
        {
            //Wait for the update to happen before continuing
            try
            {
                SwingUtilities.invokeAndWait(updateProc);
            }
            catch (InvocationTargetException e)
            {
                GUILoggerHome.find().exception(e);
            }
            catch (InterruptedException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }
        else
        {
            updateProc.run();
        }

        //Return true if the rfq state has changed or if the overall object
        //has changed.
        boolean retVal = hasChanged();
        clearChanged();
        return retVal;
    }

///////////////////////////////////////////////////////////////////////////////
// RFQ IMPLEMENTATION

    public RFQStruct getRFQStruct()
    {
        return this.rfqStruct;
    }

    /**
      Gets the product key from the wrapped RFQStruct.
      @return Integer
    */
    public Integer getProductKey()
    {
        return this.productKey;
    }
    /**
      Gets the class key from the wrapped RFQStruct.
      @return Integer
    */
    public Integer getClassKey()
    {
        return this.classKey;
    }
    /**
      Gets the product type from the wrapped RFQStruct.
      @return Integer
    */
    public Integer getProductType()
    {
        return this.productType;
    }
    /**
      Gets the session name from the wrapped RFQStruct.
      @return int
    */
    public String getSessionName()
    {
        return this.rfqStruct.sessionName;
    }
    /**
      Sets the given RFQStruct as the core for this wrapper.
      @param RFQStruct rfqStruct
      @throws DataValidationException
    */
    public void update(RFQStruct rfqStruct) throws DataValidationException
    {
        this.latestStruct = rfqStruct;
    }

    /**
      Gets the time to live value for this RFQ
      @return long
     */
    public long getTimeToLive()
    {
        return this.rfqStruct.timeToLive;
    }
    /**
      Gets the quantity value for this RFQ
      @return int
     */
    public Integer getQuantity()
    {
        return this.quantity;
    }
    /**
      Gets the type of this rfq (System or Manual)
    */
    public short getType()
    {
        return this.rfqStruct.rfqType;
    }
    /**
      Gets the state of this this RFQ
      @return int
      @see RFQImpl.STATE_1
      @see RFQImpl.STATE_2
      @see RFQImpl.DELETED
    */
    public int getState()
    {
        return this.rfqState;
    }
    /**
      Get the entry time of this RFQ
      @return long
     */
    public long getEntryTime()
    {
        return this.entryTime;
    }
    /**
      Get the unadjusted expiration time of this RFQ
      @return long
    */
    public long getExpireTime()
    {
        return this.expireTime;
    }
    /**
      Force the deletion of this RFQ
    */
    public void forceDelete()
    {
        if (this.rfqState != DELETED)
        {
            this.rfqState = DELETED;
            setChanged();
            notifyObservers(this);
        }
    }
    /**
      Gets the scheduled execution times for state changes to the given RFQStruct.
      @return long[] consisting of {entryTime, greenStateTime, yellowStateTime, expireTime}
      @param RFQStruct struct
    */
    public static long[] getScheduledTimes(RFQStruct struct)
    {
        long entryTime = DateWrapper.convertToMillis(struct.entryTime);
//  long correctedTime = TimeSyncWrapper.getCorrectedTimeMillis();
    //If the entry time is in the future, we will adjust it to be the
    //current time. Critical changed made 9/25/01.
// Removed 12/10/01 by nmd, per Tom's request...
//-----------------
//  if (entryTime > correctedTime)
//  {
//    entryTime = correctedTime;
//  }
//-----------------
//        long entryTime = System.currentTimeMillis();
        long expireTime = entryTime + (struct.timeToLive * 1000);

        long thirdTime = Math.round( (expireTime - entryTime) / 3 );
        long yellowStateTime = thirdTime + entryTime;
        long greenStateTime = (thirdTime * 2) + entryTime;

        long[] retVal = {entryTime, greenStateTime, yellowStateTime, expireTime};
        return retVal;
    }

    public boolean equals(Object other)
    {
        boolean retVal = false;
        try
        {
            RFQ otherRFQ = (RFQ)other;
            retVal = otherRFQ.getProductKey().equals(getProductKey());
        }
        catch (ClassCastException e)
        {
            GUILoggerHome.find().debug("RFQImpl equals() expected RFQ and got " + other.getClass().getName(), GUILoggerBusinessProperty.RFQ);
        }
        return retVal;
    }

////////////////////////////////////////////////////////////////////////////////
// COMPARABLE IMPLEMENTATION

    public int compareTo(Object other)
    {
        int retVal = -1;
        RFQ otherRFQ = (RFQ) other;

        if (otherRFQ.equals(this))
        {
            retVal = 0;
        }
        else
        {
            long otherExpireTime = otherRFQ.getExpireTime();
            long thisExpireTime = getExpireTime();

            if (otherExpireTime < thisExpireTime)
            {
                retVal = 1;
            }
            else if (otherExpireTime == thisExpireTime)
            {
                retVal = 0;
            }
        }
        return retVal;
    }

////////////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS

    /**
      Updates the state of this RFQ based upon the amount of time that this RFQ has
      left to live.
      @return boolean - true if the update resulted in a change to the state of this
        RFQ.
    */
    protected boolean updateRFQState()
    {
        boolean stateChange = false;
        int existingState = this.rfqState;
//        long timeLeft = this.expireTime - System.currentTimeMillis();
        long timeLeft = getExpireTime() - TimeSyncWrapper.getCorrectedTimeMillis();

        if (timeLeft > 0)
        {
            if (timeLeft > this.greenStateTime)
            {
                this.rfqState = GREEN_STATE;
            }
            else if (timeLeft > this.yellowStateTime)
            {
                this.rfqState = YELLOW_STATE;
            }
            else
            {
                this.rfqState = RED_STATE;
            }
        }
        else
        {
            this.rfqState = DELETED;
        }

        if (this.rfqState != existingState)
        {
            setChanged();
            stateChange = true;
        }

        return stateChange;
    }

    /**
      Sets the entry times, state times, and expiration time of this RFQ to those
      calculated from the given struct.
      @param RFQStruct struct
    */
    protected void resetEntryExpiration(RFQStruct struct)
    {
        long[] times = getScheduledTimes(struct);

        this.entryTime = DateWrapper.convertToMillis(struct.entryTime);
        this.greenStateTime = times[1] - times[0];
        this.yellowStateTime = times[2] - times[0];
        this.expireTime = times[3];
    }

    /**
      This is an override to allow access from the WorkerUpdate runnable.
     */
    protected void setChanged()
    {
        super.setChanged();
    }

    /**
      Internal Runnable used to perform all updates to this RFQ on the Swing Worker Thread.
    */
    class WorkerUpdate implements Runnable
    {
        public void run()
        {
            try
            {
                //If the new rfqStruct has an updated quantity or a new entry time,
                //we need to set the state of this Observable to "changed"
                long newEntryTime = DateWrapper.convertToMillis(RFQImpl.this.latestStruct.entryTime);

                if (RFQImpl.this.productKey.intValue() == RFQImpl.this.latestStruct.productKeys.productKey)
                {
                    if ( (RFQImpl.this.rfqStruct.quantity != RFQImpl.this.latestStruct.quantity)
                        || (newEntryTime != RFQImpl.this.entryTime) )
                    {
                        RFQImpl.this.setChanged();
                    }
                    RFQImpl.this.rfqStruct = RFQImpl.this.latestStruct;
                    RFQImpl.this.quantity = new Integer(RFQImpl.this.rfqStruct.quantity);

                    //We must re-instantiate the state just in case the timer has changed
                    //or has been renewed
                    resetEntryExpiration(RFQImpl.this.rfqStruct);

                    boolean stateChange = RFQImpl.this.updateRFQState();
                    //Notify observers of the state change
                    if (RFQImpl.this.getState() == RFQImpl.DELETED)
                    {
                        RFQImpl.this.notifyObservers(RFQImpl.this);
                        //Do not clearChanged() here. The InstructionTarget impl will do this.
                    }
                }
                else
                {
                    DataValidationException dve = new DataValidationException();
                    dve.details = new ExceptionDetails();
                    dve.details.message = "Cannot update RFQ with productKey = " + RFQImpl.this.productKey.intValue()
                        + " with RFQStruct having productKey = " + RFQImpl.this.latestStruct.productKeys.productKey;
                    throw dve;
                }
            }
            catch (Exception ex)
            {
               GUILoggerHome.find().exception(TraderAPI.TRANSLATOR_NAME + ": RFQImpl.WorkerUpdate.run", ex);
            }
        }
    }
}


