package com.cboe.interfaces.domain.property;

//
// -----------------------------------------------------------------------------------
// Source file: PropertyServicePropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.property.PropertyGroupStruct;

public interface PropertyServicePropertyGroup extends PropertyGroup
{
    /**
     *  Get the struct that represents this property.
     */
    public PropertyGroupStruct getStruct();

    /**
     *  Set the object to the values in this struct.
     */
    public void setStruct(PropertyGroupStruct struct,  boolean loadPropertyDefinitions);
}
