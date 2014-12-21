package com.cboe.interfaces.domain.property;

//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceListener
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

/**
 *  Listener for listening to property changes in the property service.
 */
public interface PropertyServiceListener
{
    /**
     *  Accept events for a property group changing.
     *
     *  @param group The group that changed.
     */
    public void acceptPropertyUpdate(PropertyServicePropertyGroup group);
    /**
     *  Accept events for a property being removed.
     *
     *  @param category The category the group was in.
     *  @param key The key for the group that was removed.
     */
    public void acceptPropertyRemove(String category, String key);
}
