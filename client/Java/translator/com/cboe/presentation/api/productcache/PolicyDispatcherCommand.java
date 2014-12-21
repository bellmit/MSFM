//
// -----------------------------------------------------------------------------------
// Source file: PolicyDispatcherCommand.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

/**
 * Policy dispatcher command that is implemented by all policy dispatchers. 
 * Each policy dispatcher dispatches the cache values into the IEC.
 * 
 * @author Eric Maheo
 *
 */
public interface PolicyDispatcherCommand<T>
{
    /**
     * Method called by all policy dispatcher to dispatch event into the IEC.
     */
    void dispatchEvent(T event);
    /**
     * Start the dispatching of events for a policy.
     */
    void startDispatching();
    /**
     * Stop the dispatching of events for a policy.
     * 
     */
    void stopDispatching();
    
}
