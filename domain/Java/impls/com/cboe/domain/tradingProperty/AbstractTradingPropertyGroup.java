//
// -----------------------------------------------------------------------------------
// Source file: AbstractTradingPropertyGroup.java
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingProperty;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyGroup;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelListener;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.PropertyServiceFacadeHome;

/**
 * Provides an abstract default implementation for many basic operations from the TradingPropertyGroup interface.
 */
public abstract class AbstractTradingPropertyGroup implements TradingPropertyGroup, Cloneable, PropertyChangeListener
{
    /**
     * Default Comparator used for sorting all TradingProperty's that could be added to this TradingPropertyGroup.
     * This implementation assumes all implementations of TradingProperty
     * that this TradingPropertyGroup can contain, implement the Comparable interface.
     */
    protected static final Comparator DEFAULT_TP_SORT_COMPARATOR = new Comparator()
    {
        public int compare(Object o1, Object o2)
        {
            return ((Comparable) o1).compareTo(o2);
        }
    };

    protected String sessionName;
    protected int classKey;
    protected int versionNumber = 0;

    private Map tradingPropertyMap;

    private PropertyServicePropertyGroup initializedGroup;

    private BeanInfo myBeanInfo;

    protected PropertyChangeSupport propertyChangeSupport;

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AbstractTradingPropertyGroup(String sessionName, int classKey)
    {
        this.sessionName = sessionName;
        this.classKey = classKey;
        myBeanInfo = null;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Constructor that initializes with the immutable trading session name, class key and the
     * PropertyServicePropertyGroup to initialize the sub-classes trading property data with.
     * @param sessionName   that this TradingPropertyGroup is for
     * @param classKey      that this TradingPropertyGroup is for
     * @param propertyGroup to initialize with
     */
    public AbstractTradingPropertyGroup(String sessionName, int classKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        this(sessionName, classKey);
        setPropertyGroup(propertyGroup);
    }

    /**
     * Constructor that initializes with the immutable trading session name and class key. WARNING!! THIS CONSTRUCTOR
     * MUST ALWAYS BE EXPOSED. THE FACTORY WILL USE THIS CONSTRUCTOR TO INSTANTIATE THE OBJECT WITH THE IMMUTABLE
     * SESSION NAME AND CLASS KEY.
     * @param sessionName that this TradingProperty is for
     * @param classKey    that this TradingProperty is for
     * @param versionNumber that this TradingProperty is for
     */
    public AbstractTradingPropertyGroup(String sessionName, int classKey, int versionNumber)
    {
        this(sessionName, classKey);
        this.versionNumber = versionNumber;
    }

    /**
     * Attempts to parse the class key from the name of the passed group
     * @param propertyGroup to get name from that contains a class key
     * @return classKey
     * @throws DataValidationException will be thrown if the groups name did not contain a recognizable class key,
     */
    public static int getClassKeyFromGroupName(PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        String groupKey = propertyGroup.getKey();
        String[] keyElements = BasicPropertyParser.parseArray(groupKey);


        if(keyElements.length >= 3)
        {
            try
            {
                return Integer.parseInt(keyElements[2]);
            }
            catch(NumberFormatException e)
            {
                throw ExceptionBuilder.dataValidationException("Third element of propertyGroup.key could not be " +
                                                               "evaluated as a classKey:" + groupKey,
                                                               DataValidationCodes.INVALID_PRODUCT_CLASS);
            }
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Third element of propertyGroup.key could not be " +
                                                           "evaluated as a classKey:" + groupKey,
                                                           DataValidationCodes.INVALID_PRODUCT_CLASS);
        }
    }

    /**
     * Create a new implementation specific TradingProperty.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @return new TradingProperty
     */
    public abstract TradingProperty createNewTradingProperty(String sessionName, int classKey);

    /**
     * Gets the TradingPropertyType for this group that identifies the type of this group.
     */
    public abstract TradingPropertyType getTradingPropertyType();

    public Object clone() throws CloneNotSupportedException
    {
        AbstractTradingPropertyGroup clonedGroup = (AbstractTradingPropertyGroup) super.clone();

        clonedGroup.tradingPropertyMap = null;

        for(Iterator iterator = getTradingPropertyMap().values().iterator(); iterator.hasNext();)
        {
            TradingProperty tradingProperty = (TradingProperty) iterator.next();
            TradingProperty clonedTradingProperty = (TradingProperty) tradingProperty.clone();
            clonedGroup.addTradingProperty(clonedTradingProperty);
        }

        return clonedGroup;
    }

    /**
     * Provides a text representation by dumping the results of all Property's
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append(getTradingPropertyType().getName());
        buffer.append("--");
        TradingProperty[] tradingProperties = getAllTradingProperties();
        for(int i = 0; i < tradingProperties.length; i++)
        {
            TradingProperty tradingProperty = tradingProperties[i];
            buffer.append(tradingProperty.toString());
        }
        return buffer.toString();
    }

    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);
        if(!isEqual)
        {
            if(otherObject instanceof TradingPropertyGroup)
            {
                TradingPropertyGroup castedTPG = (TradingPropertyGroup) otherObject;

                isEqual = ( getTradingPropertyType().equals(castedTPG.getTradingPropertyType()) &&
                            getSessionName().equals(castedTPG.getSessionName()) &&
                            getClassKey() == castedTPG.getClassKey() );

                if(isEqual)
                {
                    TradingProperty[] myProperties = getAllTradingProperties();
                    TradingProperty[] theirProperties = castedTPG.getAllTradingProperties();
                    isEqual = Arrays.equals(myProperties, theirProperties);
                }
            }
        }

        return isEqual;
    }

    public int hashCode()
    {
        return getTradingPropertyType().hashCode();
    }

    /**
     * Forwards property change events this class receives to registered listeners.
     * @param event
     */
    public void propertyChange(PropertyChangeEvent event)
    {
        firePropertyChange(event);
    }

    /**
     * Provides the maximum number of Trading Properties this particular group implementation may allow.
     * @return This implementation will always return INFINITE_TRADING_PROPERTIES_ALLOWED.
     */
    public int getMaxTradingPropertiesAllowed()
    {
        return INFINITE_TRADING_PROPERTIES_ALLOWED;
    }

    /**
     * Registers a listener to be informed whenever changes to this TradingPropertyGroup or its contained
     * TradingProperty's occur. The PropertyChangeListener will be informed of internal changes, not reflective
     * of an event channel event. Whenever TradingProperty's are added to this group, this group is registered as
     * a listener to the TradingProperty. Any events this group receives are forwarded to this groups registered
     * listeners as well.
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
     * Gets the Class that implements the GUI representation of this group. This implementation will get the BeanInfo
     * and cache it for use during repeated calls to this method.
     * @return May return null if this group does not support its own customizer.
     * @throws IntrospectionException that could be returned from the Introspector
     */
    public Class getCustomizerClass()
            throws IntrospectionException
    {
        if(myBeanInfo == null)
        {
            myBeanInfo = Introspector.getBeanInfo(getClass());
        }
        return myBeanInfo.getBeanDescriptor().getCustomizerClass();
    }

    /**
     * Provides the setter for the conversion of the PropertyServicePropertyGroup, representing a PropertyGroupStruct,
     * to this TradingPropertyGroup. The PropertyServicePropertyGroup, representing a PropertyGroupStruct, is the
     * transport mechanism used to move TradingPropertyGroup's across CORBA, using the PropertyService IDL. This set and
     * the corresponding get, act as the translation mechanism for transport across CORBA.
     * @param propertyGroup to translate the TradingPropertyGroup values from
     * @throws DataValidationException is thrown when the passed PropertyServicePropertyGroup does
     * not reflect the Trading Properties appropriately for the implementing TradingPropertyGroup.
     */
    public void setPropertyGroup(PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException
    {
        PropertyServicePropertyGroup oldGroup = initializedGroup;

        String groupKey = propertyGroup.getKey();
        String[] keyElements = BasicPropertyParser.parseArray(groupKey);

        if(keyElements.length >= 2)
        {
            sessionName = keyElements[1];
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("sessionName could not be determined:" + groupKey,
                                                           DataValidationCodes.INVALID_SESSION);
        }

        classKey = getClassKeyFromGroupName(propertyGroup);

        clearTradingPropertyMap();

        Collection values = propertyGroup.getProperties().values();
        Property[] properties = (Property[]) values.toArray(new Property[values.size()]);
        for(int i = 0; i < properties.length; i++)
        {
            Property property = properties[i];
            TradingProperty newTradingProperty = createNewTradingProperty(getSessionName(), getClassKey());
            newTradingProperty.setProperty(property);
            addTradingProperty(newTradingProperty);
        }

        initializedGroup = propertyGroup;

        firePropertyChange(PROPERTY_GROUP_CHANGE_EVENT, oldGroup, initializedGroup);
    }

    /**
     * Provides the getter for the conversion of this TradingPropertyGroup to a PropertyServicePropertyGroup,
     * representing a PropertyGroupStruct. The PropertyServicePropertyGroup, representing a PropertyGroupStruct, is the
     * transport mechanism used to move TradingPropertyGroup's across CORBA, using the PropertyService IDL. This get and
     * the corresponding set, act as the translation mechanism for transport across CORBA.
     * @return PropertyServicePropertyGroup that represents the value from the implementing TradingPropertyGroup.
     */
    public PropertyServicePropertyGroup getPropertyGroup()
    {
        String compoundKey =
                TradingPropertyFactoryHome.find().buildTradingPropertyKey(getSessionName(), getClassKey(),
                                                                          getTradingPropertyType().getName());
        PropertyServicePropertyGroup newGroup =
                PropertyFactory.createPropertyGroup(getTradingPropertyType().getPropertyCategory(), compoundKey);

        newGroup.setVersion( getVersionNumber() );

        TradingProperty[] allTradingProperties = getAllTradingProperties();
        for(int i = 0; i < allTradingProperties.length; i++)
        {
            TradingProperty tradingProperty = allTradingProperties[i];
            newGroup.addProperty(tradingProperty.getProperty());
        }
        return newGroup;
    }

    /**
     * Returns the class key that this TradingProperty is for
     */
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * returns version number that this TradingProperty is for
     */
    public int getVersionNumber()
    {
        if(initializedGroup != null)
        {
            versionNumber = initializedGroup.getVersion();
        }
        return versionNumber;
    }

    /**
     * Returns the Trading Session Name that this TradingProperty is for
     */
    public String getSessionName()
    {
        return sessionName;
    }

    /**
     * Adds a TradingProperty to this group. Implementations are responsible for cardinality and equality to existing
     * TradingProperty's. This default just calls updateTradingProperty(TradingProperty).
     * @param tradingProperty to add
     */
    public void addTradingProperty(TradingProperty tradingProperty)
    {
        updateTradingProperty(tradingProperty);
    }

    /**
     * Updates a TradingProperty in this group. Implementations are responsible for cardinality and equality to existing
     * TradingProperty's. This default implementation adds the TradingProperty to the Map returned from
     * getTradingPropertyMap(), using the TradingProperty as both the key and the value.
     * @param tradingProperty to update with
     * @return TradingProperty that was replaced with the updated one.
     */
    public TradingProperty updateTradingProperty(TradingProperty tradingProperty)
    {
        //clean out any TP's that were keyed the same.
        TradingProperty oldValue =
                (TradingProperty) getTradingPropertyMap().remove(getMapKeyForTradingProperty(tradingProperty));
        if(oldValue != null)
        {
            oldValue.removePropertyChangeListener(this);
        }

        //clean out the same instance of TP where it was keyed differently when added.
        for(Iterator iterator = getTradingPropertyMap().entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            if(entry.getValue() == tradingProperty)
            {
                iterator.remove();
                tradingProperty.removePropertyChangeListener(this);
            }
        }

        getTradingPropertyMap().put(getMapKeyForTradingProperty(tradingProperty), tradingProperty);
        tradingProperty.addPropertyChangeListener(this);
        firePropertyChange(TRADING_PROPERTY_CHANGE_EVENT, oldValue, tradingProperty);
        return oldValue;
    }

    /**
     * Removes a TradingProperty from this group. Implementations are responsible for cardinality and equality to
     * existing TradingProperty's. This default implementation removes the TradingProperty from the Map returned from
     * getTradingPropertyMap(), using the TradingProperty as the key.
     * @param tradingProperty to remove
     * @return TradingProperty removed
     */
    public TradingProperty removeTradingProperty(TradingProperty tradingProperty)
    {
        //clean out any TP's that were keyed the same.
        TradingProperty oldValue =
                (TradingProperty) getTradingPropertyMap().remove(getMapKeyForTradingProperty(tradingProperty));
        if(oldValue != null)
        {
            oldValue.removePropertyChangeListener(this);
        }

        //clean out the same instance of TP where it was keyed differently when added.
        for(Iterator iterator = getTradingPropertyMap().entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            if(entry.getValue() == tradingProperty)
            {
                iterator.remove();
                tradingProperty.removePropertyChangeListener(this);
            }
        }

        firePropertyChange(TRADING_PROPERTY_CHANGE_EVENT, oldValue, null);
        return oldValue;
    }

    /**
     * Gets all the implementation specific TradingProperty's for this group.
     * If a Comparator is returned from getTradingPropertySortComparator(), then the Array instance is sorted using
     * that Comparator.
     * @return an array of all contained TradingProperty's declared as just an array of type TradingProperty
     */
    public TradingProperty[] getAllTradingProperties()
    {
        Collection values = getTradingPropertyMap().values();

        TradingProperty[] allTPs = new TradingProperty[values.size()];
        allTPs = (TradingProperty[]) values.toArray(allTPs);

        allTPs = sortTradingPropertyArray(allTPs);

        return allTPs;
    }

    /**
     * Saves this TradingPropertyGroup to the persistence.
     * @throws SystemException forwarded from the save API
     * @throws CommunicationException forwarded from the save API
     * @throws AuthorizationException forwarded from the save API
     * @throws DataValidationException The group that is returned from the save is set on this object. This exception
     * is forwarded from the setPropertyGroup.
     */
    public void save()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException,
                   TransactionFailedException, NotFoundException
    {
        PropertyServicePropertyGroup group = getPropertyGroup();
        PropertyServicePropertyGroup savedGroup = PropertyServiceFacadeHome.find().savePropertyGroup(group);
        if(savedGroup != null)
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
        String compoundKey =
                TradingPropertyFactoryHome.find().buildTradingPropertyKey(getSessionName(), getClassKey(),
                                                                          getTradingPropertyType().getName());
        PropertyServiceFacadeHome.find().removePropertyGroup(getTradingPropertyType().getPropertyCategory(), compoundKey);
    }

    /**
     * Subscribe an EventChannelListener to update/remove events on this TradingPropertyGroup
     */
    public void subscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradingPropertyFactoryHome.find().subscribe(getSessionName(), getClassKey(), getTradingPropertyType().getName(),
                                                    listener);
    }

    /**
     * Unsubscribe an EventChannelListener to update/remove events on this TradingPropertyGroup
     */
    public void unsubscribe(EventChannelListener listener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        TradingPropertyFactoryHome.find().unsubscribe(getSessionName(), getClassKey(),
                                                      getTradingPropertyType().getName(), listener);
    }

    /**
     * Clears the trading property map returned by getTradingPropertyMap.
     */
    protected void clearTradingPropertyMap()
    {
        getTradingPropertyMap().clear();
    }

    /**
     * Lazily create the map to contain the trading properties for this group
     */
    protected Map getTradingPropertyMap()
    {
        if(tradingPropertyMap == null)
        {
            tradingPropertyMap = new HashMap(10);
        }
        return tradingPropertyMap;
    }

    /**
     * Returns the comparator that this TradingPropertyGroup will use for general sorting and within Collections
     * of TradingProperty's. This implementation will return a comparator that assumes each TradingProperty implements
     * the Comparable interface.
     * @return a comparator to use within Collections and for sorting TradingProperty's. The Comparator implementation
     * must be able to compare all the implementations of TradingProperty that this TradingPropertyGroup can
     * contain. This implementation returns a Comparator that assumes all implementations of TradingProperty
     * that this TradingPropertyGroup can contain, implement the Comparable interface.
     */
    protected Comparator getTradingPropertySortComparator()
    {
        return DEFAULT_TP_SORT_COMPARATOR;
    }

    /**
     * This method is used to get an Object to be used as the key for adding, obtaining and removing the
     * TradingProperty's from the underlying Map collection returned from getTradingPropertyMap(). This default
     * implementation just returns the passed tradingProperty. It is intended to be overriden by sub-classes wishing
     * to enforce a specific cardinality by using another key.
     * @param tradingProperty to get key Object for
     * @return object to use as the key for reference in a Map collection of TradingProperty's.
     */
    protected Object getMapKeyForTradingProperty(TradingProperty tradingProperty)
    {
        return tradingProperty;
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
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected TradingProperty[] sortTradingPropertyArray(TradingProperty[] tradingProperties)
    {
        Comparator defaultComparator = getTradingPropertySortComparator();
        if(defaultComparator != null)
        {
            Arrays.sort(tradingProperties, defaultComparator);
        }

        return tradingProperties;
    }
}
