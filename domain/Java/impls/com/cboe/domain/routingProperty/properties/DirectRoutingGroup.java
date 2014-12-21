//
// -----------------------------------------------------------------------------------
// Source file: DirectRoutingGroup.java
//
// PACKAGE: com.cboe.domain.routingProperty.properties
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

//import java.lang.reflect.InvocationTargetException;
//import java.util.*;
//
//import com.cboe.exceptions.DataValidationException;
//
//import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
//import com.cboe.interfaces.domain.routingProperty.BaseProperty;
//import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
//import com.cboe.interfaces.domain.routingProperty.Validator;
//import com.cboe.interfaces.domain.routingProperty.common.Destination;
//import com.cboe.interfaces.domain.routingProperty.common.DestinationBaseProperty;
//
//import com.cboe.util.ExceptionBuilder;
//
//import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
//import com.cboe.domain.routingProperty.common.DestinationBasePropertyImpl;

// TODO remove later
@Deprecated
public abstract class DirectRoutingGroup // extends AbstractRoutingPropertyGroup
{
//    public static final String DESTINATION = "Destination";
//
//    private DestinationBaseProperty destination;
//
//    public DirectRoutingGroup(BasePropertyKey basePropertyKey)
//    {
//        super(basePropertyKey);
//    }
//
//    public DirectRoutingGroup(BasePropertyKey basePropertyKey, int versionNumber)
//    {
//        super(basePropertyKey, versionNumber);
//    }
//
//    public DirectRoutingGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
//            throws DataValidationException, InvocationTargetException
//    {
//        super(basePropertyKey, propertyGroup);
//    }
//
//    public BaseProperty[] getAllProperties()
//    {
//        return new BaseProperty[]{ destination };
//    }
//
//    public BaseProperty getProperty(String name)
//            throws DataValidationException
//    {
//        if(name.equals(DESTINATION))
//        {
//            return destination;
//        }
//        else
//        {
//            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
//                                                           ". Could not find class type to handle.", 0);
//        }
//    }
//
//    protected void initializeProperties()
//    {
//        destination = new DestinationBasePropertyImpl(getPropertyCategoryType(), DESTINATION, getPropertyKey(), getType());
//    }
//
//    public Destination getDestination()
//    {
//        return destination.getDestination();
//    }
//
//    public void setDestination(Destination dest)
//    {
//        destination.setDestination(dest);
//        firePropertyChange();
//    }
//
//    public Object clone() throws CloneNotSupportedException
//    {
//        DirectRoutingGroup newGroup = (DirectRoutingGroup) super.clone();
//        newGroup.destination = (DestinationBaseProperty) destination.clone();
//
//        return newGroup;
//    }
//
//    @Override
//    public List<Validator> getValidators()
//    {
//        List<Validator> validators = super.getValidators();
//        validators.addAll(destination.getValidators());
//        return validators;
//    }
}
