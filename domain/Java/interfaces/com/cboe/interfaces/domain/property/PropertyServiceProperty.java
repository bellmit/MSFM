package com.cboe.interfaces.domain.property;


//
// -----------------------------------------------------------------------------------
// Source file: PropertyServiceProperty
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.property.PropertyStruct;

/**
 *  A extension of the Property that is specialized for the Property Service.
 */
public interface PropertyServiceProperty extends Property
{
    /**
     *  Get the struct that represents this property.
     */
    public PropertyStruct getStruct();

    /**
     *  Set the object to the values in this struct.
     */
    public void setStruct(PropertyStruct struct);

}
