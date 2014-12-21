package com.cboe.domain.routingProperty.properties;
// -----------------------------------------------------------------------------------
// Source file: ReasonabilityEditGroup
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.properties
// 
// Created: Jun 28, 2006 9:46:00 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * This defines a group of RoutingProperties needed for "Firm Volume Limit"
 * routing for the supplied BasePropertyKey.  This contains 1 BaseProperty to
 * set the value for volume.
 *
 * The BasePropertyType for this group is RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT.
 */
public class ReasonabilityEditGroup extends AbstractRoutingPropertyGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.REASONABILITY_EDIT;

    public static final String EDIT_GROUP = "Edit Group";

    private ReasonabilityEditParameter reasonabilityEdit;

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                  List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public ReasonabilityEditGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(EDIT_GROUP))
        {
            return reasonabilityEdit;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = reasonabilityEdit;

        return properties;
    }

    protected void initializeProperties()
    {
        reasonabilityEdit = new ReasonabilityEditParameter(getPropertyCategoryType(), EDIT_GROUP, getPropertyKey(), getType());
        try
        {
            reasonabilityEdit.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + EDIT_GROUP + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    /*
    NOTE: the following 2 methods are not needed by the GUI, because ReasonabilityEditParameterBeanInfo describes how
    "editGroup" property can be accessed via ReasonabilityEditParameter.get/setEditGroup methods, with editGroup being
    the FieldName of the PropertyName called ReasonabilityEdit in CSV file.

    *** BUT *** don't remove them because they may be used by the server.
     */

    public String getReasonabilityEdit()
    {
        return reasonabilityEdit.getEditGroup();
    }

    public void setReasonabilityEdit(String value)
    {
        reasonabilityEdit.setEditGroup(value);
        // firePropertyChange();
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries =  {EDIT_GROUP};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public Object clone() throws CloneNotSupportedException
    {
        ReasonabilityEditGroup newGroup = (ReasonabilityEditGroup) super.clone();
        newGroup.reasonabilityEdit = (ReasonabilityEditParameter) reasonabilityEdit.clone();

        return newGroup;
    }
}