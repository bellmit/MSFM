package com.cboe.interfaces.domain.routingProperty;

// -----------------------------------------------------------------------------------
// Source file: BasePropertyType
//
// PACKAGE: com.cboe.domain.firmRoutingProperty2.test2
// 
// Created: Jul 21, 2006 1:27:41 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public interface BasePropertyType
{
    String PROPERTY_TYPE_NOT_DEFINED = "PROPERTY_TYPE_NOT_DEFINED";

    /**
     * Returns the programmatic name
     */
    String getName();

    /**
     * Gets the property category that will contain this type.
     */
    String getPropertyCategory();

    /**
     * Returns the English formatted name
     */
    String getFullName();

    /**
     * Return the Class Type of the Property Key used to store and retrieve this Property Type
     */
    BasePropertyKeyType getKeyType();
    
    
    /**
     * Returns array of masks
     */
    public int[][] getMasks();    
    
}
