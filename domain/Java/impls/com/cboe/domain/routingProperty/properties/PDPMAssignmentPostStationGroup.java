package com.cboe.domain.routingProperty.properties;
//-----------------------------------------------------------------------------------
//Source file: PDPMAssignmentPostStationGroup
//
//PACKAGE: 
//
//Created: Jun 28, 2006 9:46:00 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Comparator;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;
import com.cboe.util.ExceptionBuilder;

/**
* This defines a group of RoutingProperties needed for "Firm Volume Limit"
* routing for the supplied BasePropertyKey.  This contains 1 BaseProperty to
* set the value for volume.
*
* The BasePropertyType for this group is RoutingPropertyTypeImpl.FIRM_VOLUME_LIMIT_POST_STATION.
*/
public class PDPMAssignmentPostStationGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
 public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.PDPM_ASSIGNMENT_POST_STATION;

    public static final String PMM = "PMM";
    private StringListBaseProperty pdpmAcronyms;

    public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey)
 {
     super(basePropertyKey);
 }

 public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
         throws DataValidationException, InvocationTargetException
 {
     super(basePropertyKey, propertyGroup);
 }

 public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
 {
     super(basePropertyKey, versionNumber);
 }

public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
{
    super(basePropertyKey, validators);
}

public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                      List<Validator> validators)
        throws DataValidationException, InvocationTargetException
{
    super(basePropertyKey, propertyGroup, validators);
}

public PDPMAssignmentPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
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

    protected void initializeProperties()
    {
        pdpmAcronyms = new StringListBasePropertyImpl(getPropertyCategoryType(), PMM, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if(name.equals(PMM))
        {
            return pdpmAcronyms;
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
        properties[0] = pdpmAcronyms;

        return properties;
    }


    public String[] getPMM()
    {
        return pdpmAcronyms.getStringListValue();
    }

    public void setPMM(String[] pdpmAcronyms)
    {
        this.pdpmAcronyms.setStringListValue(pdpmAcronyms);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        PDPMAssignmentPostStationGroup newGroup = (PDPMAssignmentPostStationGroup) super.clone();
        newGroup.pdpmAcronyms = (StringListBaseProperty) pdpmAcronyms.clone();

        return newGroup;
    }

    protected Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries =  {PMM};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}