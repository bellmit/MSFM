package com.cboe.presentation.api;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ExceptionBuilder;
import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.interfaces.presentation.common.exceptionHandling.DefaultExceptionHandler;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import org.omg.CORBA.UserException;

/**
 * This can be extended and used to perform a "get" that appears to be a synchronous method, when it actually listens
 * to and event channel and returns the event's struct.  getEventChannelData() will subscribe to event channel and wait
 * up to TIME_OUT milliseconds for an event to be received on the event channel.  If no event is received, then it throws
 * a CommunicationExcetpion.
 */
public abstract class AbstractEventChannelSnapshot extends AbstractSnapshot implements EventChannelListener
{
    /**
     * Default constructor
     */
    public AbstractEventChannelSnapshot()
    {
        this(0);
    }

    /**
     * Constructor
     */
    public AbstractEventChannelSnapshot(int timeout)
    {
        super(timeout);
    }

    protected void processException()
        throws UserException, CommunicationException, SystemException, AuthorizationException, DataValidationException,
        AlreadyExistsException, AuthenticationException, NotAcceptedException, NotFoundException,
        NotSupportedException, TransactionFailedException, TimedOutException
    {
        // We should not be getting here in this implementation
        throw ExceptionBuilder.systemException(getExceptionMessage(), 0);
    }

    protected void processTimeOut() throws TimedOutException
    {
        throw new TimedOutException(getTimeoutMessage() + "; timeout="+snapshotTimeout +" millisec", this.snapshotTimeout);
    }
}
