package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: AbstractBasePropertyGroup
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jun 20, 2006 2:40:11 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.util.event.EventChannelListener;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.domain.routingProperty.validation.ValidationSupport;

abstract public class AbstractBasePropertyGroup implements BasePropertyGroup, PropertyChangeListener
{
    protected int versionNumber = 0;
    protected PropertyChangeSupport propertyChangeSupport;

    private BasePropertyKey basePropertyKey;

    private BeanInfo myBeanInfo;
    private PropertyDescriptor[] myPropertyDescriptors;

    private PropertyServicePropertyGroup initializedGroup;

    private ValidationSupport validationSupport;

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey)
    {
        this(basePropertyKey, (ArrayList <Validator>) null);
    }

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        this(basePropertyKey);
        setPropertyGroup(propertyGroup);
    }

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        this(basePropertyKey);
        this.versionNumber = versionNumber;
    }

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        this.basePropertyKey       = basePropertyKey;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.validationSupport     = new ValidationSupport();

        initializeProperties();

        setValidators(validators);
    }

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                     List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        this(basePropertyKey, validators);
        setPropertyGroup(propertyGroup);
    }

    public AbstractBasePropertyGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        this(basePropertyKey, validators);
        this.versionNumber = versionNumber;
    }

    public abstract BasePropertyType getType();

    public abstract BaseProperty getProperty(String name) throws DataValidationException;

    public abstract BaseProperty[] getAllProperties();

    public String getFirmNumber()
    {
        return getPropertyKey().getFirmNumber();
    }

    public String getExchangeAcronym()
    {
        return getPropertyKey().getExchangeAcronym();
    }

    public ExchangeFirmStruct getExchangeFirmStruct()
    {
        return getPropertyKey().getExchangeFirmStruct();
    }

    public String getSessionName()
    {
        return getPropertyKey().getSessionName();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(255);
        BaseProperty[] baseProperties = getAllProperties();
        
        int length = baseProperties.length;
        for (int i = 0; i < length; i++)
        {
            BaseProperty baseProperty = baseProperties[i];
            buffer.append(baseProperty.toString());
            if (i < length-1) { buffer.append(","); }
        }
        
        return buffer.toString();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setPropertyGroup(PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
                                                                                    InvocationTargetException
    {
        PropertyServicePropertyGroup oldGroup = initializedGroup;

        BasePropertyKey basePropertyKey = getPropertyFactory().buildKey(propertyGroup.getKey());
        if (basePropertyKey != null)
        {
            this.basePropertyKey = basePropertyKey;

            Collection values = propertyGroup.getProperties().values();
            Property[] properties = (Property[]) values.toArray(new Property[values.size()]);
            for (int i = 0; i < properties.length; i++)
            {
                Property property = properties[i];

                BaseProperty updateProperty = getProperty(property.getName());
                if (updateProperty != null)
                {
                    updateProperty.setProperty(property);
                }
            }

            initializedGroup = propertyGroup;

            firePropertyChange(getPropertyGroupChangeEvent(), oldGroup, initializedGroup);
        }
    }

    public void copyProperties(PropertyServicePropertyGroup propertyGroup) throws DataValidationException
    {
        Collection values = propertyGroup.getProperties().values();
        Property[] properties = (Property[]) values.toArray(new Property[values.size()]);
        for (int i = 0; i < properties.length; i++)
        {
            Property property = properties[i];

            BaseProperty updateProperty = getProperty(property.getName());
            if (updateProperty != null)
            {
                updateProperty.setProperty(property);
            }
        }
        
        initializedGroup = null;
        firePropertyChange();
    }
    
    public PropertyServicePropertyGroup getPropertyGroup()
    {
        String compoundKey = getPropertyKey().getPropertyKey();

        PropertyServicePropertyGroup newGroup =
                PropertyFactory.createPropertyGroup(getPropertyCategoryType(), compoundKey);

        newGroup.setVersion(getVersionNumber());

        BaseProperty[] allProperties = getAllProperties();
        for (int i = 0; i < allProperties.length; i++)
        {
            BaseProperty baseProperty = allProperties[i];
            newGroup.addProperty(baseProperty.getProperty());
        }

        return newGroup;
    }

    public String getPropertyName()
    {
        return getType().getName();
    }

    /**
     * returns version number that this FirmFirmRoutingProperty is for
     */
    public int getVersionNumber()
    {
        if (initializedGroup != null)
        {
            versionNumber = initializedGroup.getVersion();
        }
        return versionNumber;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AbstractBasePropertyGroup group = (AbstractBasePropertyGroup) super.clone();
        group.basePropertyKey = (BasePropertyKey) basePropertyKey.clone();
        // propertyChangeSupport.clone() ??
        group.validationSupport = validationSupport.clone();

        return group;
    }

    /**
     * Saves this TradingPropertyGroup to the persistence.
     * @throws SystemException forwarded from the save API
     * @throws CommunicationException forwarded from the save API
     * @throws AuthorizationException forwarded from the save API
     * @throws DataValidationException The group that is returned from the save is set on this object. This exception is
     * forwarded from the setPropertyGroup.
     */
    public void save()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException, InvocationTargetException
    {
        PropertyServicePropertyGroup group = getPropertyGroup();
        PropertyServicePropertyGroup savedGroup = PropertyServiceFacadeHome.find().savePropertyGroup(group);
        if (savedGroup != null)
        {
            setPropertyGroup(savedGroup);
        }
    }

    /**
     * Deletes this TradingPropertyGroup from the persistence.
     * @throws SystemException forwarded from the remove API
     * @throws CommunicationException forwarded from the remove API
     * @throws AuthorizationException forwarded from the remove API
     * @throws DataValidationException forwarded from the remove API
     * @throws TransactionFailedException forwarded from the remove API
     */
    public void delete()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException
    {
        PropertyServiceFacadeHome.find().removePropertyGroup(getPropertyCategoryType(),
                                                             getPropertyKey().getPropertyKey());
    }

    /**
     * Subscribe an EventChannelListener to update/remove events on this TradingPropertyGroup
     */
    public void subscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getPropertyFactory().subscribe(getPropertyKey(), listener);
    }

    /**
     * Unsubscribe an EventChannelListener to update/remove events on this TradingPropertyGroup
     */
    public void unsubscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getPropertyFactory().unsubscribe(getPropertyKey(), listener);
    }

    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);
        if (!isEqual)
        {
            if (otherObject instanceof BasePropertyGroup)
            {
                BasePropertyGroup casted = (BasePropertyGroup) otherObject;
                isEqual = getPropertyKey().equals(casted.getPropertyKey()) && getType().equals(casted.getType()) ;
            }
        }

        return isEqual;
    }

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        firePropertyChange(evt);
    }

    public BasePropertyKey getPropertyKey()
    {
        return basePropertyKey;
    }

    /**
     * Gets the Class that implements the GUI representation of this group. This implementation will get the BeanInfo
     * and cache it for use during repeated calls to this method.
     * @return May return null if this group does not support its own customizer.
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public Class getCustomizerClass()
            throws IntrospectionException
    {
        if (myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }

        return myBeanInfo.getBeanDescriptor().getCustomizerClass();
    }

    /**
     * Gets the actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    public Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException
    {
        Object returnValue = null;
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null)
        {
            try
            {
                returnValue = readMethod.invoke(this, null);
            }
            catch (IllegalArgumentException e)
            { // if method is not for on this Object the try the BasePropertyKey
                returnValue = readMethod.invoke(getPropertyKey(), null);
            }
        }

        return returnValue;
    }

    /**
     * Gets the PropertyDescriptor's for this BasePropertyGroup.
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws java.beans.IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor[] getPropertyDescriptors() throws IntrospectionException
    {
        if (myPropertyDescriptors == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();

            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if (descriptors != null)
            {
                myPropertyDescriptors = new PropertyDescriptor[descriptors.length];
                System.arraycopy(descriptors, 0, myPropertyDescriptors, 0, descriptors.length);
                Arrays.sort(myPropertyDescriptors, getPropertyDescriptorSortComparator());
            }
            else
            {
                myPropertyDescriptors = new PropertyDescriptor[0];
            }
        }
        PropertyDescriptor[] newArray = new PropertyDescriptor[myPropertyDescriptors.length];
        System.arraycopy(myPropertyDescriptors, 0, newArray, 0, myPropertyDescriptors.length);
        return newArray;
    }

    public boolean isValid(StringBuffer validationReport)
    {
        return validationSupport.isValid(this, validationReport);
    }

    // to have no validators, pass in empty array, not null
    public void setValidators(List<Validator> newValidators)
    {
        if (newValidators == null)
        {
            newValidators = getDefaultValidators();
        }
        validationSupport.setValidators(newValidators);

        for (BaseProperty baseProperty : getAllProperties())
        {
            List<Validator> validators = baseProperty.getValidators();
            addValidators(validators);
        }
    }

    public void addValidators(List<Validator> validators)
    {
        validationSupport.addValidators(validators);
    }

    public void addValidator(Validator validator)
    {
        validationSupport.addValidator(validator);
    }

    protected abstract void initializeProperties();

    protected abstract String getPropertyCategoryType();

    protected abstract BasePropertyFactory getPropertyFactory();

    protected abstract String getPropertyGroupChangeEvent();

    protected abstract String getPropertyChangeEvent();

    /**
     * Fires property change events to all listeners registered.
     */
    protected void firePropertyChange(PropertyChangeEvent evt)
    {
        propertyChangeSupport.firePropertyChange(evt);
    }

    /**
     * Fires property change events to all listeners registered.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange()
    {
        firePropertyChange(getPropertyChangeEvent(), null, this);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by property name.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        if(getPropertyKey() instanceof MutableBasePropertyKey)
        {
            return ((MutableBasePropertyKey) getPropertyKey()).getPropertyDescriptorSortComparator();
        }
        else
        {
            return new ForcedPropertyDescriptorComparator(null);
        }
    }

    protected CloneNotSupportedException createCloneException(Exception e)
    {
        CloneNotSupportedException exception = new CloneNotSupportedException();
        exception.initCause(e);
        return exception;
    }

    protected List<Validator> getDefaultValidators()
    {
         return BasePropertyValidationFactoryHome.find().createBaseGroupValidators();
    }

    /**
     * Finds this class implementations BeanInfo and caches it.
     */
    private BeanInfo getMyBeanInfo() throws IntrospectionException
    {
        if (myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }
        return myBeanInfo;
    }
}