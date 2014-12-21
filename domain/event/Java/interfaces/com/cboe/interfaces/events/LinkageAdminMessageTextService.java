package com.cboe.interfaces.events;

import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * This is the common interface for the Current Market Home
 * @author John Wickberg
 */
public interface LinkageAdminMessageTextService 
{

    /**
     * This starts the thread if it needed. 
     *
     */
    public void startLinkageAdminTextMessageProcessing();
    
    /**
     * Accept the Linkage Reject Message and enqueues to internal transient queue.
     * @param text
     */
    public void acceptLinkageRejectMessage(String text, DateTimeStruct msgTime);

    /**
     * Accept the Linkage Status Message and enqueues to internal transient queue.
     * @param text
     */
    public void acceptLinkageStatusMessage(String text, DateTimeStruct msgTime);

    /**
     * Accept the Linkage Text Message and enqueues to internal transient queue.
     * @param text
     */
    public void acceptLinkageTextMessage(String text, DateTimeStruct msgTime);
    
    
    public void acceptLinkageReportErrorMessage(String subject, String text, DateTimeStruct msgTime);
    
}

