// -----------------------------------------------------------------------------------
// Source file: AbstractSnapshot.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.presentation.api.TimedOutException;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

public abstract class AbstractSnapshot
{
    public static final long DEFAULT_SLEEP_TIME = 200;
    public static final int DEFAULT_TIME_OUT = 1000;
    protected long startTime;
    protected int snapshotTimeout;

    protected boolean isTimedOut;
    protected boolean isChannelUpdated;
    protected boolean isExceptionThrown;

    protected Object eventChannelData;
    protected Exception exception;

    public AbstractSnapshot()
    {
        super();
        isTimedOut = false;
        isChannelUpdated = false;
        isExceptionThrown = false;
    }

    public AbstractSnapshot(int timeout)
    {
        this();
        setTimeout(timeout);
    }

    protected abstract void subscribeEventChannel();

    protected abstract void unsubscribeEventChannel();

    protected abstract String getExceptionMessage();

    protected abstract String getTimeoutMessage();

    protected abstract void processTimeOut()
            throws TimedOutException;

    /**
     * Subscribe to the event channel and wait for an event.  If one is received within TIME_OUT milliseconds, return
     * the ChannelEvent.getEventData().  If an event isn't received, throw an Exception.
     */
    public Object getEventChannelData()
            throws UserException, CommunicationException, SystemException, AuthorizationException, DataValidationException,
                   AlreadyExistsException, AuthenticationException, NotAcceptedException, NotFoundException,
                   NotSupportedException, TransactionFailedException, TimedOutException
    {
        subscribeEventChannel();

        // if already got an event, don't bother with this code
        if(!isChannelUpdated())
        {
            setStartTime(System.currentTimeMillis());

            while(!isChannelUpdated() && !isTimedOut() && !isExceptionThrown())
            {
                try
                {
                    Thread.currentThread().sleep(AbstractSnapshot.DEFAULT_SLEEP_TIME);
                    calculateIsTimedOut();
                }
                catch(InterruptedException e)
                {
                    // Do nothing for now. We do not really expect any other threads interrupting this thread.
                    // But ....
                    DefaultExceptionHandlerHome.find().process(e);
                }
            }
        }

        unsubscribeEventChannel();

        if(isExceptionThrown())
        {
            processException();
        }
        else if(isTimedOut())
        {
            processTimeOut();
        }
        else if(isChannelUpdated())
        {
            eventChannelData = getEventData();
        }

        return eventChannelData;
    }

    protected void processException()
            throws UserException, CommunicationException, SystemException, AuthorizationException, DataValidationException,
                   AlreadyExistsException, AuthenticationException, NotAcceptedException, NotFoundException,
                   NotSupportedException, TransactionFailedException, TimedOutException
    {
        if(exception != null)
        {
            if(exception instanceof SystemException)
            {
                throw (SystemException) this.exception;
            }
            else if(exception instanceof CommunicationException)
            {
                throw (CommunicationException) this.exception;
            }
            else if(exception instanceof AuthorizationException)
            {
                throw (AuthorizationException) this.exception;
            }
            else if(exception instanceof DataValidationException)
            {
                throw (DataValidationException) this.exception;
            }
            else if(exception instanceof NotFoundException)
            {
                throw (NotFoundException) this.exception;
            }
            else if(exception instanceof AlreadyExistsException)
            {
                throw (AlreadyExistsException) this.exception;
            }
            else if(exception instanceof AuthenticationException)
            {
                throw (AuthenticationException) this.exception;
            }
            else if(exception instanceof NotAcceptedException)
            {
                throw (NotAcceptedException) this.exception;
            }
            else if(exception instanceof NotSupportedException)
            {
                throw (NotSupportedException) this.exception;
            }
            else if(exception instanceof TransactionFailedException)
            {
                throw (TransactionFailedException) this.exception;
            }
            else if(exception instanceof UserException)
            {
                throw (UserException) this.exception;
            }
            else
            {
                throw (RuntimeException) this.exception;
            }
        }
    }

    protected Object getEventData()
    {
        return eventChannelData;
    }

    protected void setTimeout(int timeout)
    {
        if (timeout > 0)
        {
            snapshotTimeout = timeout;
        }
        else
        {
            snapshotTimeout = DEFAULT_TIME_OUT;
        }
    }

    protected void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public int getTimeout()
    {
        return snapshotTimeout;
    }

    public long getStartTime()
    {
        return startTime;
    }

    protected boolean isChannelUpdated()
    {
        return isChannelUpdated;
    }

    protected boolean isTimedOut()
    {
        return isTimedOut;
    }

    protected boolean isExceptionThrown()
    {
        return isExceptionThrown;
    }

    protected void calculateIsTimedOut()
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime < getStartTime() + getTimeout())
        {
            isTimedOut = false;
        }
        else
        {
            isTimedOut = true;
        }
    }
}
