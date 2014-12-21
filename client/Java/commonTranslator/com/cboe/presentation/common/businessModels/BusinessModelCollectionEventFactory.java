// -----------------------------------------------------------------------------------
// Source file: BusinessModelCollectionEventFactory.java
//
// PACKAGE: com.cboe.presentation.common.businessModels;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.businessModels;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollection;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollectionEvent;

/**
 *  Factory for creating BusinessModelCollectionEvents
 */
public class BusinessModelCollectionEventFactory
{
    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private BusinessModelCollectionEventFactory()
    {}

    /**
     * Creates an instance of a BusinessModelCollectionEvent for a BusinessModelCollection.
     * @param source collection for the event
     * @param collection's element associated with the event
     * @return BusinessModelCollectionEvent
     */
    public static BusinessModelCollectionEvent create(BusinessModelCollection collection, BusinessModel model)
    {
        if (collection == null)
        {
            throw new IllegalArgumentException("BusinessModelCollection can not be NULL");
        }
        if (model == null)
        {
            throw new IllegalArgumentException("BusinessModel can not be NULL");
        }
        BusinessModelCollectionEvent event = new BusinessModelCollectionEventImpl(collection, model);

        return event;
    }

}
