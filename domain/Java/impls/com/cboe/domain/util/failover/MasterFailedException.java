package com.cboe.domain.util.failover;


/**
 * This exception should be thrown by the fail-over processing in case if the processes
 * running on the master side of the cluster cannot be killed due to for example a 
 * network issue or an operating system issue. 
 * When this exception is caught the operators should be given an option to kill the master side 
 * using other means ( if it is not down already ) and then continue fail-over processing with the
 * indication that the master side should be ignored.
 * @author baranski
 *
 */
public class MasterFailedException extends Exception
{

    public MasterFailedException()
    {
        super();
    }

    public MasterFailedException(String p_message, Throwable p_cause)
    {
        super(p_message, p_cause);
    }

    public MasterFailedException(String p_message)
    {
        super(p_message);
    }

    public MasterFailedException(Throwable p_cause)
    {
        super(p_cause);
    }
    
}
