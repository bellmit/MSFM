//
// -----------------------------------------------------------------------------------
// Source file: DropCopyBasePropertyImpl.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import java.util.*;

import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.DropCopyBaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.DropCopyList;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class DropCopyBasePropertyImpl extends AbstractBaseProperty
        implements DropCopyBaseProperty
{
    public static final String DROP_COPY_LIST_CHANGE_EVENT = "DropCopyListValue";
    protected DropCopyList destinations;
    private static final DropCopyList emptyDestination = new DropCopyListImpl();

    public DropCopyBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                    BasePropertyType type)
    {
        super(propertyCategory, propertyName, key, type);
    }

    public DropCopyBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                    BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
    }

    public DropCopyBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                    BasePropertyType type, DropCopyList list)
    {
        super(propertyCategory, propertyName, key, type);
        setDropCopyListValue(list);
    }

    public DropCopyBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                    BasePropertyType type, DropCopyList list, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, validators);
        setDropCopyListValue(list);
    }

    public DropCopyList getDropCopyListValue()
    {
        return destinations;
    }

    public void setDropCopyListValue(DropCopyList newDestinations)
    {
        DropCopyList oldValue = this.destinations;
        DropCopyList newValue = new DropCopyListImpl();
        newValue.setDestinations(newDestinations.getDestinations());
        this.destinations = newValue;
        firePropertyChange(DROP_COPY_LIST_CHANGE_EVENT, oldValue, this.destinations);
    }

    public Destination[] getDestinations()
    {
        return destinations.getDestinations();
    }

    public void setDestinations(Destination[] newDestinations)
    {
        DropCopyList oldValue = this.destinations;
        DropCopyList newValue = new DropCopyListImpl();
        for (int i = 0; i < newDestinations.length && i < destinations.getDestinations().length; i++)
        {
            newValue.getDestinations()[i] = newDestinations[i];
        }
        this.destinations = newValue;
        firePropertyChange(DROP_COPY_LIST_CHANGE_EVENT, oldValue, this.destinations);
    }

    /**
     * Used to compare another BaseProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public int compareTo(Object object)
    {
        DropCopyList thierList = ((DropCopyBaseProperty) object).getDropCopyListValue();
        return destinations.compareTo(thierList);
    }

    /**
     * Parses the field values from the passed String
     * @param value to parse
     */
    protected void decodeValue(String value)
    {
        if(value != null && value.length() > 0)
        {
            String[] listValues = BasicPropertyParser.parseArray(value);
            int numDestinations = listValues.length;
            ArrayList<Destination> destList = new ArrayList<Destination>(numDestinations);

            int k = 0;
            for(int i = 0; i < numDestinations; i++)
            {
                try
                {
                    Destination destination = new DestinationImpl(listValues[k]);
                    destList.add(destination);
                    k += 1;
                }
                catch(IllegalArgumentException e)
                {
                    Log.exception(e);
                }
            }
            destinations = new DropCopyListImpl();
            destinations.setDirectRoute(DropCopyList.DIRECT_ROUTE_INDEX < k ?
                destList.get(DropCopyList.DIRECT_ROUTE_INDEX) : new DestinationImpl(DropCopyList.NO_DEST));
            destinations.setFillDropCopy(DropCopyList.FILL_DROP_COPY_INDEX < k ?
                destList.get(DropCopyList.FILL_DROP_COPY_INDEX) : new DestinationImpl(DropCopyList.NO_DEST));
            destinations.setCancelDropCopy(DropCopyList.CANCEL_DROP_COPY_INDEX < k ?
                destList.get(DropCopyList.CANCEL_DROP_COPY_INDEX) : new DestinationImpl(DropCopyList.NO_DEST));
        }
    }

    /**
     * Get all the Property values as a List of Strings
     * @return List of Strings
     */
    protected List getEncodedValuesAsStringList()
    {
        List<String> valueList = new ArrayList<String>(DropCopyList.NUM_DESTINATIONS);
        int k = 0;
        for(int i = 0; i < DropCopyList.NUM_DESTINATIONS; i++)
        {
            valueList.add(k, destinations.getDestinations()[i].getWorkstation());
            k += 1;
        }

        return valueList;
    }


    public Destination getDestinationListValue(int index)
    {
        return destinations.getDestinations()[index];
    }

    public void setDestinationListValue(int index, Destination destination)
    {
        if(index >= DropCopyList.NUM_DESTINATIONS)
        {
            throw new IllegalArgumentException("Number of destinations cannot be greater than " +
                                               DropCopyList.NUM_DESTINATIONS);
        }
        else
        {
            destinations.getDestinations()[index] = destination;

//            firePropertyChange(DROP_COPY_LIST_CHANGE_EVENT, oldValue, this.destinations);
        }
    }

    public Destination getDirectRoute()
    {
        return destinations == null ? emptyDestination.getDirectRoute() : destinations.getDirectRoute();
    }

    public void setDirectRoute(Destination destination)
    {
        destinations.setDirectRoute(destination);
    }

    public Destination getFillDropCopy()
    {
        return destinations == null ? emptyDestination.getFillDropCopy() : destinations.getFillDropCopy();
    }

    public void setFillDropCopy(Destination destination)
    {
        destinations.setFillDropCopy(destination);
    }

    public Destination getCancelDropCopy()
    {
        return destinations == null ? emptyDestination.getCancelDropCopy() : destinations.getCancelDropCopy();
    }

    public void setCancelDropCopy(Destination destination)
    {
        destinations.setCancelDropCopy(destination);
    }

    public void setDirectRouteOptional(boolean optional)
    {
        destinations.setDirectRouteOptional(optional);
    }

    public void setFillDropCopyOptional(boolean optional)
    {
        destinations.setFillDropCopyOptional(optional);
    }

    public void setCancelDropCopyOptional(boolean optional)
    {
        destinations.setCancelDropCopyOptional(optional);
    }

    public boolean isDirectRouteOptional()
    {
        return destinations == null ? emptyDestination.isDirectRouteOptional() : destinations.isDirectRouteOptional();
    }

    public boolean isFillDropCopyOptional()
    {
        return destinations == null ? emptyDestination.isFillDropCopyOptional() : destinations.isFillDropCopyOptional();
    }

    public boolean isCancelDropCopyOptional()
    {
        return destinations == null ? emptyDestination.isCancelDropCopyOptional() : destinations.isCancelDropCopyOptional();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(255);
        buffer.append(getPropertyName()).append("=").append(destinations.toString());
        return buffer.toString();
    }

    @Override
    protected List<Validator> getDefaultValidators()
    {
        return BasePropertyValidationFactoryHome.find().createDropCopyDestinationValidators();
    }
}