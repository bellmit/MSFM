/*
 * Created on Jul 14, 2004
 *  
 */
package com.cboe.presentation.fix.appia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.javtech.appia.MessageObject;

/**
 * Implementation of Guarded Suspension design pattern for FIX request/response
 * interaction.
 * 
 * Guarded Suspension concurrency design pattern 
 * Intent: Suspend execution of a method call until a precondition is satisfied.
 * 
 * Can handle multiple response messages per request - returns from wait when last
 * response is received.
 * 
 * @author Don Mendelson
 *
 */
public class ResponseSynchronizer {
    
    /**
     * Entry to correlate request with its responses and measure progress
     */
    class RequestEntry {

        /** Collection of response messages */
        private ArrayList responseMessages = new ArrayList();

        /** Used to determine when all responses have been received */
        int expectedResponses;

        /** Used to determine progress */
        int receivedResponses;
        
        /** ID to correlate responses with request */
        String requestID;
        
        /** The request message */
        MessageObject request;

        /** System time of request - used to determine timeout */
        long requestTime;

        /**
         * Creates a new RequestEntry
         * @param requestID unique ID
         * @param request the request message
         */
        RequestEntry(String requestID, MessageObject request) {
            this.requestID = requestID;
            this.request = request;
            requestTime = System.currentTimeMillis();
        }
    }
    
    // default timeout value in milliseconds
    private long defaultTimeout = 5000;
        
    // Synchronized map of request ID to RequestEntry
    private Map requestMap = Collections.synchronizedMap(new HashMap());

    /**
     * Enters a request
     * @param requestID unique ID of the request
     * @param request the request message
     */
    public void enterRequest(String requestID, MessageObject request) {
        RequestEntry entry = new RequestEntry(requestID, request);
        requestMap.put(requestID, entry);
    }
    
    /**
     * Enter the last response to a request and notify waiting requester
     * @param requestID unique ID of the request
     * @param response the response message
     * @return returns the request that was waiting on this response, or <tt>null</tt> 
     * if no request was registered
     */
    public MessageObject enterResponse(String requestID, MessageObject response) {
        return enterResponse(requestID, response, true);
    }
    
    /**
     * Enter a response and notify waiting requester if it is the last response
     * @param requestID unique ID of the request
     * @param response the response message
     * @param isLast tells whether this response is the last one for the request
     * @return returns the request that was waiting on this response, or <tt>null</tt> 
     * if no request was registered
     */
    public MessageObject enterResponse(String requestID, MessageObject response, 
    		boolean isLast) {
        RequestEntry entry = (RequestEntry) requestMap.get(requestID);
        if (entry != null) {
            
            synchronized(entry) {
	            entry.responseMessages.add(response);
	            if (isLast) {
	                entry.notifyAll();
	            }
            }
            return entry.request;
        } else {
            return null;
        }
    }

    /**
     * Enter a response and notify waiting requester if it is the last response
     * @param requestID unique ID of the request
     * @param response the response message
     * @param expectedResponses number of expected responses
     * @param partialResponses are accumulated. If they add to expectedResponses,
     * then this is the last response message.
     * @return returns the request that was waiting on this response, or <tt>null</tt> 
     * if no request was registered
     */
    public MessageObject enterResponse(String requestID, MessageObject response,
            int expectedResponses, int partialResonses) {
        RequestEntry entry = (RequestEntry) requestMap.get(requestID);
        if (entry != null) {
            
            synchronized(entry) {
	            entry.receivedResponses += partialResonses;
	            entry.responseMessages.add(response);
	            if (entry.receivedResponses >= expectedResponses) {
	                entry.notifyAll();
	            }
            }
            return entry.request;
        } else {
            return null;
        }
    }
    
    /**
     * Set default timeout value - the maximum time to wait for a response
     * @param millis timeout in milliseconds
     */
    public void setDefaultTimeout(long millis) {
        defaultTimeout = millis;
    }
    
    /**
     * Waits up to the default timeout period for a response.
     * @param requestID unique ID of the request
     * @return the responses, if received within the timeout period. If times out,
     * returns empty array.
     * @throws InterruptedException is thrown if another thread interrupts the wait
     * for a response
     */
    public MessageObject [] waitForResponse(String requestID)
            throws InterruptedException {
        return waitForResponse(requestID, defaultTimeout);
    }
    
    /**
     * Waits up to the specified timeout period for a response.
     * @param requestID unique ID of the request
     * @param timeout the timeout period in milliseconds
     * @return the responses, if received within the timeout period. If times out,
     * returns empty array.
     * @throws InterruptedException is thrown if another thread interrupts the wait
     * for a response
     */
    public MessageObject [] waitForResponse(String requestID, long timeout)
            throws InterruptedException {
        MessageObject [] responses = new MessageObject[0];
        RequestEntry entry = (RequestEntry) requestMap.get(requestID);
        if (entry != null) {
            synchronized(entry) {
                entry.wait(timeout);
                responses = (MessageObject[]) entry.responseMessages.toArray(responses);
            }
            requestMap.remove(requestID);
        }
        return responses;
    }

}
