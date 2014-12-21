//
// -----------------------------------------------------------------------------------
// Source file: ActionThreaderPool.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.threading
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.threading;

/**
 * Contains a pool of ActionThreader's and allows one to obtain the next available ActionThreader
 */
public interface ActionThreaderPool
{
    /**
     * Gets the next available ActionThreader from the pool to be used
     */
    public ActionThreader getNextAvailableActionThreader();

    /**
     * Gets the next available ActionThreader from the pool to be used.
     * @param sharedReference used to either find the same instance of a previous
     */
    public ActionThreader getNextAvailableActionThreader(Object sharedReference);

    public void incrementSharedReferenceCount(Object sharedReference);

    public void decrementSharedReferenceCount(Object sharedReference);

    /**
     * Notifies the pool that the ActionThreader has completed its execution and is available for reuse.
     * @param threader
     */
    public void actionThreaderCompleted(ActionThreader threader);
}