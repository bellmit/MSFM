//
// -----------------------------------------------------------------------------------
// Source file: EnablementImpl.java
//
// PACKAGE: com.cboe.internalPresentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.util.ArrayList;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.PropertyDefinitionCache;

import com.cboe.interfaces.internalPresentation.user.Enablement;
import com.cboe.interfaces.presentation.product.SessionProductClass;

import com.cboe.internalPresentation.common.formatters.OperationTypes;
import com.cboe.internalPresentation.common.formatters.PropertyCategoryTypes;

public class EnablementImpl implements Enablement
{
    private SessionProductClass sessionProductClass;
    private short operationType;
    private boolean isEnabled;

    // Used for the property implementation of the Enablement
    private PropertyDefinition propertyDefinition;

    public EnablementImpl(SessionProductClass sessionProductClass, short operationType, boolean enabled)
    {
        if(sessionProductClass == null)
        {
            throw new IllegalArgumentException("SessionProductClass may not be null.");
        }

        validateOperationType(operationType);

        this.isEnabled = enabled;
        this.operationType = operationType;
        this.sessionProductClass = sessionProductClass;
        this.propertyDefinition = PropertyDefinitionCache.getInstance().getPropertyDefinition(PropertyCategoryTypes.USER_ENABLEMENT,PropertyCategoryTypes.USER_ENABLEMENT);
    }

    public EnablementImpl(SessionProductClass sessionProductClass, short operationType)
    {
        this(sessionProductClass, operationType, true);
    }

    public boolean equals(Object o)
    {
        if( this == o )
        {
            return true;
        }
        if( !(o instanceof Enablement) )
        {
            return false;
        }

        final Enablement enablement = ( Enablement ) o;

        if( isEnabled() != enablement.isEnabled() )
        {
            return false;
        }
        if( getOperationType() != enablement.getOperationType() )
        {
            return false;
        }
        if( !getSessionProductClass().equals(enablement.getSessionProductClass()) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = sessionProductClass.hashCode();
        result = result + operationType;
        return result;
    }

    public short getOperationType()
    {
        return operationType;
    }

    public SessionProductClass getSessionProductClass()
    {
        return sessionProductClass;
    }

    public boolean isEnabled()
    {
        return isEnabled;
    }

    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    public Property getProperty()
    {
        Property property;

        ArrayList nameList = new ArrayList();
        ArrayList valueList = new ArrayList();
        // Namelist is session Name, classKey, operation type all as strings
        nameList.add(sessionProductClass.getTradingSessionName());
        nameList.add(Integer.toString(sessionProductClass.getClassKey()));
        nameList.add(Integer.toString(operationType));

        valueList.add(Boolean.toString(isEnabled));

        property = PropertyFactory.createProperty(nameList,valueList,propertyDefinition);

        return property;
    }

    public void setPropertyDefinition(PropertyDefinition propertyDefinition)
    {
        this.propertyDefinition = propertyDefinition;
    }

    private void validateOperationType(short operationType)
    {
        boolean isValid = OperationTypes.validateOperationType(operationType);
        if(!isValid)
        {
            throw new IllegalArgumentException("Invalid operationType:" + operationType);
        }
    }
}
