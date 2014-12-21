// -----------------------------------------------------------------------------------
// Source file: BusinessModelCollectionListener.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.businessModels;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.businessModels;

import java.util.*;

/**
 * Provides a callback interface to listeners for events from a BusinessModelCollection.
 */
public interface BusinessModelCollectionListener extends EventListener
{
    /**
     * Method is called when element added to this collection.
     */
    public void elementAdded(BusinessModelCollectionEvent event);

    /**
     * Method is called when element is updated.
     */
    public void elementUpdated(BusinessModelCollectionEvent event);

    /**
     * Method is called when element is removed from this collection
     */
    public void elementRemoved(BusinessModelCollectionEvent event);
}