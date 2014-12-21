// -----------------------------------------------------------------------------------
// Source file: GUILoggerProperty.java
//
// PACKAGE: com.cboe.presentation.common.logging;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerProperty;

/**
 *  This class provides getters and setteers for GUILogger property attributes.
 *
 *  @author     Alex Brazhnichenko
 *  @created    July 17, 2001
 */
public abstract class GUILoggerProperty implements IGUILoggerProperty
{
    private int key;
    private String name;

   /**
    *  Constructor
    */
    private GUILoggerProperty()
    {
        super();
    }

   /**
    *  Constructor
    */
    protected GUILoggerProperty(int key, String name)
    {
        this();
        setKey(key);
        setName(name);
    }

    private void setKey(int key)
    {
        this.key = key;
    }

    private void setName(String name)
    {
        if (name != null)
        {
            this.name = name;
        }
        else
        {
            throw new IllegalArgumentException("Name can not be NULL.");
        }
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public int hashCode()
    {
        return key;
    }

    public boolean equals(Object object)
    {
        boolean isEqual = false;
        if ( object != null )
        {
            if ( this.getClass() == object.getClass() )
            {
                IGUILoggerProperty anotherProperty = (IGUILoggerProperty)object;
                isEqual = (hashCode() == anotherProperty.hashCode());
            }
        }
        return isEqual;
    }
}