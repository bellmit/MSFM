//
// -----------------------------------------------------------------------------------
// Source file: AbstractTradingProperty.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.constants.PropertyCategoryTypes;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyDefinition;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyDefinitionCache;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Provides an abstract default implementation for many basic operations from the TradingProperty interface.
 */
public abstract class AbstractTradingProperty extends TradingPropertyElements implements TradingProperty, Cloneable
{
    private String sessionName;
    private int classKey;

    private String propertyName;

    private Map propertyDefinitionMap;
    private boolean definitionsModified;

    private BeanInfo myBeanInfo;
    private PropertyDescriptor[] myPropertyDescriptors;
    private PropertyDescriptor defaultFieldPropertyDescriptor;

    protected PropertyChangeSupport propertyChangeSupport;

    /**
     * Constructor that initializes with the immutable trading session name and class key.
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AbstractTradingProperty(String propertyName, String sessionName, int classKey)
    {
        this.propertyName = propertyName;
        this.sessionName = sessionName;
        this.classKey = classKey;
        initialize();
        myBeanInfo = null;
        myPropertyDescriptors = null;
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the Property to initialize
     * the sub-classes trading property data with.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public AbstractTradingProperty(String sessionName, int classKey, Property property)
            throws DataValidationException
    {
        this(property.getName(), sessionName, classKey);
        setProperty(property);
    }

    /**
     * Constructs with all fields initialized
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param integer1 field value
     * @param integer2 field value
     * @param integer3 field value
     * @param double1 field value
     * @param double2 field value
     * @param double3 field value
     * @param sequenceNumber field value
     */
    public AbstractTradingProperty(String propertyName, String sessionName, int classKey,
                                   int integer1, int integer2, int integer3,
                                   double double1, double double2, double double3,
                                   int sequenceNumber)
    {
        super(integer1, integer2, integer3, double1, double2, double3, sequenceNumber);
        this.propertyName = propertyName;
        this.sessionName = sessionName;
        this.classKey = classKey;
        initialize();
    }

    /**
     * Constructs with the field values being parsed from the passed value
     * @param propertyName that this TradingProperty is for
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param value String to parse field values from
     */
    public AbstractTradingProperty(String propertyName, String sessionName, int classKey, String value)
    {
        super(value);
        this.propertyName = propertyName;
        this.sessionName = sessionName;
        this.classKey = classKey;
        initialize();
    }

    /**
     * Gets the Type for this TradingProperty
     */
    public abstract TradingPropertyType getTradingPropertyType();

    /**
     * Used to compare another TradingProperty to this one.
     * @param object to compare this with.
     * @return as defined in java.lang.Comparable
     */
    public abstract int compareTo(Object object);

    public Object clone() throws CloneNotSupportedException
    {
        AbstractTradingProperty clonedTradingProperty = (AbstractTradingProperty) super.clone();
        clonedTradingProperty.myPropertyDescriptors = null;
        clonedTradingProperty.defaultFieldPropertyDescriptor = null;
        clonedTradingProperty.propertyDefinitionMap.clear();

        return clonedTradingProperty;
    }

    /**
     * Provides a text representation by dumping the results of getProperty().
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append(getPropertyName()).append(':');
        buffer.append(getProperty().toString());

        return buffer.toString();
    }

    /**
     * Overridden to check names, class key and session as well
     */
    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);
        if(isEqual)
        {
            if(otherObject instanceof TradingProperty)
            {
                TradingProperty castedTP = (TradingProperty) otherObject;

                isEqual = ( getTradingPropertyType().equals(castedTP.getTradingPropertyType()) &&
                            getPropertyName().equals(castedTP.getPropertyName()) &&
                            getSessionName().equals(castedTP.getSessionName()) &&
                            getClassKey() == castedTP.getClassKey() );
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Overridden to return the hashCode of the property name.
     */
    public int hashCode()
    {
        return getPropertyName().hashCode();
    }

    /**
     * Registers a listener to be informed whenever changes to this TradingProperty occur. The PropertyChangeListener
     * will be informed of internal changes, not reflective of an event channel event.
     * @param listener to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a previous registration.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Sets all field values to their default value. This default will be obtained from the property definition, if
     * one is defined.
     */
    public void initializeDefaultValues()
            throws IntrospectionException
    {
        PropertyDescriptor[] descriptors = getPropertyDescriptors();
        for(int i = 0; i < descriptors.length; i++)
        {
            PropertyDescriptor descriptor = descriptors[i];
            try
            {
                PropertyDefinition definition = getPropertyDefinition(descriptor.getName());
                if(definition != null)
                {
                    Object defaultValue = definition.getDefaultValueAsDataType();
                    setFieldValue(descriptor, defaultValue);
                }
            }
            catch(NotFoundException e)
            {
                //normal flow, just don't set default value
            }
            catch(InstantiationException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
            catch(IllegalAccessException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
            catch(InvocationTargetException e)
            {
                Log.exception("Could not set default value for:" + descriptor.getName(), e);
            }
        }
    }

    /**
     * Gets the Class that implements the GUI representation of this group. This implementation will get the BeanInfo
     * and cache it for use during repeated calls to this method.
     * @return May return null if this group does not support its own customizer.
     * @throws IntrospectionException that could be returned from the Introspector
     */
    public Class getCustomizerClass()
            throws IntrospectionException
    {
        BeanInfo myBeanInfo = getMyBeanInfo();
        return myBeanInfo.getBeanDescriptor().getCustomizerClass();
    }

    /**
     * Gets the PropertyDescriptor's for this TradingProperty. This implementation will cache the PropertyDescriptor's
     * after the first call for repeated calls
     * @return The PropertyDescriptor's that are returned from the BeanInfo
     * @throws IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor[] getPropertyDescriptors()
            throws IntrospectionException
    {
        if(myPropertyDescriptors == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();

            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if(descriptors != null)
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

    /**
     * Gets the default property descriptor that represents the key field for this TradingProperty.
     * @return default field property descriptor. May return null if one is not defined.
     * @exception IntrospectionException that could be returned from the Introspector
     */
    public PropertyDescriptor getDefaultPropertyDescriptor()
            throws IntrospectionException
    {
        if(defaultFieldPropertyDescriptor == null)
        {
            BeanInfo myBeanInfo = getMyBeanInfo();
            int defaultPropIndex = myBeanInfo.getDefaultPropertyIndex();
            PropertyDescriptor[] descriptors = myBeanInfo.getPropertyDescriptors();
            if(defaultPropIndex > -1 && descriptors != null && descriptors.length > defaultPropIndex)
            {
                defaultFieldPropertyDescriptor = descriptors[defaultPropIndex];
            }
        }

        return defaultFieldPropertyDescriptor;
    }

    /**
     * Gets that actual value for the propertyDescriptor from this Object
     * @param propertyDescriptor for field in this Object
     * @return value returned from read method in propertyDescriptor from this Object
     */
    public Object getFieldValue(PropertyDescriptor propertyDescriptor)
            throws IllegalAccessException, InvocationTargetException
    {
        Object returnValue;
        Method readMethod = propertyDescriptor.getReadMethod();
        returnValue = readMethod.invoke(this, null);
        return returnValue;
    }

    /**
     * Sets the actual value for the propertyDescriptor to this Object
     * @param propertyDescriptor for field in this Object
     * @param value Object value to set with
     */
    public void setFieldValue(PropertyDescriptor propertyDescriptor, Object value)
            throws IllegalAccessException, InvocationTargetException
    {
        Method writeMethod = propertyDescriptor.getWriteMethod();
        Object[] values = {value};
        writeMethod.invoke(this, values);
    }

    /**
     * Returns a Property containing the property key from getPropertyName() as the name and all the field values
     * encoded in the value. The order of the encoded value elements is integer1, integer2, integer3, double1,
     * double2, double3, sequenceNumber.
     */
    public Property getProperty()
    {
        List nameList = new ArrayList(1);
        nameList.add(getTradingPropertyType().getName());
        nameList.add(getPropertyName());

        List valueList = getEncodedValuesAsStringList();

        Property newProperty = PropertyFactory.createProperty(nameList, valueList, null);

        return newProperty;
    }

    /**
     * Parses the value into the appropriate fields. The order of the fields is assumed to be integer1, integer2,
     * integer3, double1, double2, double3, sequenceNumber.
     * @param property to set values from
     * @throws DataValidationException is thrown if a field element could not be parsed as expected.
     */
    public void setProperty(Property property)
            throws DataValidationException
    {
        if(property != null)
        {
            String stringValue = property.getValue();
            if(stringValue != null && stringValue.length() > 0)
            {
                try
                {
                    decodeValue(stringValue);
                }
                catch(NumberFormatException e)
                {
                    throw ExceptionBuilder.dataValidationException("Invalid value for property:" + property.getValue(),
                                                                   0);
                }
            }
            else
            {
                throw ExceptionBuilder.dataValidationException("Invalid value for property:" + property.getValue(), 0);
            }
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Property may not be null.", 0);
        }
    }

    /**
     * Returns the class key that this TradingProperty is for
     */
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * Returns the Trading Session Name that this TradingProperty is for
     * @return
     */
    public String getSessionName()
    {
        return sessionName;
    }

    /**
     * Gets the name of the Property that defined this TradingProperty. Should be unique among all of the same
     * implementations of this interface.
     */
    public String getPropertyName()
    {
        if(propertyName != null)
        {
            return propertyName;
        }
        else
        {
            return getTradingPropertyType().getName();
        }
    }

    /**
     * Gets the PropertyDefinition for the passed fieldName that defines what is allowed for that field.
     * @param fieldName to get PropertyDefinition for. This fieldName will be as defined by the JavaBeans
     * specification.
     * @return defines the allowed values, display values, etc. for the passed fieldName.
     */
    public PropertyDefinition getPropertyDefinition(String fieldName)
            throws NotFoundException
    {
        Object[] elements = {getTradingPropertyType().getName(), fieldName};
        String name = BasicPropertyParser.buildCompoundString(elements);
        PropertyDefinition definition = (PropertyDefinition) propertyDefinitionMap.get(name);
        if(definition == null)
        {
            definition =
                PropertyDefinitionCache.getInstance().getPropertyDefinition(PropertyCategoryTypes.TRADING_PROPERTIES,
                                                                            name);

            if(definition != null)
            {
                propertyDefinitionMap.put(name, definition);
                return definition;
            }
            else
            {
                throw ExceptionBuilder.notFoundException("Property Definition does not exist for: " + fieldName,
                                                         NotFoundCodes.RESOURCE_DOESNT_EXIST);
            }
        }
        else
        {
            return definition;
        }
    }

    /**
     * Sets the PropertyDefinition for the passed fieldName that defines what is allowed for that field.
     * @param fieldName to set PropertyDefinition for. This fieldName will be as defined by the JavaBeans
     * specification.
     * @param newDefinition to set.
     */
    public void setPropertyDefinition(String fieldName, PropertyDefinition newDefinition)
    {
        if(newDefinition == null)
        {
            throw new IllegalArgumentException("PropertyDefinition may not be null.");
        }
        if(fieldName == null || fieldName.length() == 0)
        {
            throw new IllegalArgumentException("fieldName may not be null or empty.");
        }

        PropertyDefinition oldValue = (PropertyDefinition) propertyDefinitionMap.get(fieldName);

        propertyDefinitionMap.put(fieldName, newDefinition);
        definitionsModified = true;

        firePropertyChange(PROPERTY_DEFINITION_CHANGE_EVENT, oldValue, newDefinition);
    }

    /**
     * Gets all the PropertyDefinition's regardless of fieldName
     */
    public PropertyDefinition[] getAllPropertyDefinitions()
    {
        Field[] fields = getClass().getFields();
        List definitionList = new ArrayList(fields.length);
        for(int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            String fieldName = field.getName();
            try
            {
                PropertyDefinition definition = getPropertyDefinition(fieldName);
                definitionList.add(definition);
            }
            catch(NotFoundException e)
            {
                Log.information("Could not find PropertyDefinition for field: " + fieldName);
            }
        }
        return (PropertyDefinition[]) definitionList.toArray(new PropertyDefinition[definitionList.size()]);
    }

    /**
     * Can be used to determine if this TradingProperty had its PropertyDefinition's modified
     * @return True if modified, false otherwise.
     */
    public boolean isPropertyDefinitionsModified()
    {
        return definitionsModified;
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setDouble1(double double1)
    {
        double oldValue = getDouble1();
        super.setDouble1(double1);
        firePropertyChange(DOUBLE1_CHANGE_EVENT, oldValue, double1);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setDouble2(double double2)
    {
        double oldValue = getDouble2();
        super.setDouble2(double2);
        firePropertyChange(DOUBLE2_CHANGE_EVENT, oldValue, double2);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setDouble3(double double3)
    {
        double oldValue = getDouble3();
        super.setDouble3(double3);
        firePropertyChange(DOUBLE3_CHANGE_EVENT, oldValue, double3);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setInteger1(int integer1)
    {
        int oldValue = getInteger1();
        super.setInteger1(integer1);
        firePropertyChange(INTEGER1_CHANGE_EVENT, oldValue, integer1);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setInteger2(int integer2)
    {
        int oldValue = getInteger2();
        super.setInteger2(integer2);
        firePropertyChange(INTEGER2_CHANGE_EVENT, oldValue, integer2);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setInteger3(int integer3)
    {
        int oldValue = getInteger3();
        super.setInteger3(integer3);
        firePropertyChange(INTEGER3_CHANGE_EVENT, oldValue, integer3);
    }

    /**
     * Provides setter access to the super class. After the setter is called, a property change event is fired.
     */
    public void setSequenceNumber(int sequenceNumber)
    {
        int oldValue = getSequenceNumber();
        super.setSequenceNumber(sequenceNumber);
        firePropertyChange(SEQUENCE_NUMBER_CHANGE_EVENT, oldValue, sequenceNumber);
    }

    /**
     * Allows the Trading Property to determine the order of the PropertyDescriptor's. This implementation will just
     * return a comparator that will sort by property name.
     * @return comparator to use for sorting the returned PropertyDescriptor's from getPropertyDescriptors().
     */
    protected Comparator getPropertyDescriptorSortComparator()
    {
        return new ForcedPropertyDescriptorComparator(null);
    }

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
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fires property change events to all listeners registered.
     */
    protected void firePropertyChange(String propertyName, int oldValue, int newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fires property change events to all listeners registered.
     */
    protected void firePropertyChange(String propertyName, double oldValue, double newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, new Double(oldValue), new Double(newValue));
    }

    /**
     * Fires property change events to all listeners registered.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Initializes attributes.
     */
    private void initialize()
    {
        propertyDefinitionMap = new HashMap(10);
        definitionsModified = false;

        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Finds this class implementations BeanInfo and caches it.
     */ 
    private BeanInfo getMyBeanInfo()
            throws IntrospectionException
    {
        if(myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }
        return myBeanInfo;
    }
}
