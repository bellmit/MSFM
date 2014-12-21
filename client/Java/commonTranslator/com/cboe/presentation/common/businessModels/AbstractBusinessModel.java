//
// -----------------------------------------------------------------------------------
// Source file: AbstractBusinessModel.java
//
// PACKAGE: com.cboe.presentation.common.businessModels;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.businessModels;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Implements BusinessModel interface, providing a little behaviour
 */
public abstract class AbstractBusinessModel implements BusinessModel
{
    /**
     * Returns the key Object to be used as a key in collection.
     * This implementation wraps hashCode() value into Integer object.
     * @return Object
     */
    public Object getKey()
    {
        return new Integer(hashCode());
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    protected void checkState(Object obj)
    {
        if (obj == null)
        {
            //cannot do this when obj is null
//            throw new IllegalStateException(obj.getClass().getName() + " can not be NULL");
            throw new IllegalStateException("Object cannot be NULL");
        }
    }
    protected void checkParam(Object param, String paramName)
    {
        if (param == null)
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Argument ").append(paramName);
            buffer.append(" cannot be NULL");
            throw new IllegalArgumentException(buffer.toString());
        }
    }
}
