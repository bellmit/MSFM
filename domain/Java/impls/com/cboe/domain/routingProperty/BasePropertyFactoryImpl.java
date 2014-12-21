package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyFactoryImpl
//
// PACKAGE: com.cboe.domain.routingProperty
// 
// Created: Jun 20, 2006 2:32:44 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.UserException;

import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.domain.routingProperty.key.AbstractBasePropertyKey;
import com.cboe.domain.routingProperty.key.RoutingKeyHelper;
import com.cboe.domain.routingProperty.key.SessionClassKey;
import com.cboe.domain.routingProperty.key.SessionPostStationKey;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.constants.PropertyQueryTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.MutableBasePropertyKey;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelListener;

public abstract class BasePropertyFactoryImpl implements BasePropertyFactory
{
    /**
     * Provides for the creation of a new trading property group that did not exist before.
     *
     * @return an appropriate implementation of a new instance of a BasePropertyGroup based on the values passed. The
     *         intention would be that this BasePropertyGroup was not obtained from the host source.
     * @throws com.cboe.exceptions.DataValidationException
     *          should be thrown if firmRoutingPropertyName is not a known type.
     * @throws java.lang.reflect.InvocationTargetException
     *          If any exception occurs from instantiating the appropriate domain wrapper object, it is returned as the
     *          cause in this exception.
     */
    public BasePropertyGroup createNewPropertyGroup(BasePropertyKey key) throws DataValidationException,
            InvocationTargetException
    {
        BasePropertyGroup newBasePropertyGroup;
        BasePropertyClassType classType = findPropertyClassType(key.getPropertyName());

        //we have a known property name and implementation type to try to instantiate
        newBasePropertyGroup = createNewRoutingPropertyGroup(classType, key);
        return newBasePropertyGroup;
    }

    public BasePropertyKey buildKey(String propertyKey) throws InvocationTargetException, DataValidationException
    {
        String propertyName = getPropertyNameFromKey(propertyKey);
        if (propertyName == null)
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + propertyName
                    + ". Could not find class type to handle.", 0);
        }

        BasePropertyClassType classType = findPropertyClassType(propertyName);

        try
        {
        	return buildKey(classType.getPropertyType(), propertyKey);
        }
        catch (InvocationTargetException e)
        {
        	Log.exception("PropertyName:"+propertyName+" classType:"+classType.getClassType().getName()+" ProeprtyType:"+classType.getPropertyType().getFullName(), e);
        	throw e;
        }
    }

    public BasePropertyKey buildKey(BasePropertyType type, String[] arguments) throws InvocationTargetException
    {
        Class classType = type.getKeyType().getClassType();

        if (classType != null)
        {
            Constructor constructor = type.getKeyType().getDefaultKeyConstructor();
            return buildKey(constructor, arguments);
        }

        return null;
    }

    public BasePropertyKey buildKey(com.cboe.interfaces.domain.routingProperty.BasePropertyType type, String propertyKey) throws InvocationTargetException
    {
        Class classType = type.getKeyType().getClassType();

        if (classType != null)
        {
            Constructor constructor = type.getKeyType().getPropertyKeyContructor();
            String[] arguments = {propertyKey};
            return buildKey(constructor, arguments);
        }

        return null;
    }

    public MutableBasePropertyKey buildKey(BasePropertyType type) throws InvocationTargetException
    {
        Class classType = type.getKeyType().getClassType();

        if (classType != null)
        {
            Constructor constructor = type.getKeyType().getPropertyTypeContructor();
            BasePropertyType[] arguments = {type};
            return buildKey(constructor, arguments);
        }

        return null;
    }

    public BasePropertyKey buildKey(String[] arguments)
    {
        return RoutingKeyHelper.create(arguments);
    }

    /**
     * Attempts to find a previously created BasePropertyClassType keyed by the passed propertyName.
     *
     * @param propertyName to find BasePropertyClassType for
     * @return found BasePropertyClassType for passed propertyName
     * @throws com.cboe.exceptions.DataValidationException
     *          will be thrown if BasePropertyClassType could not be found for passed propertyName
     */
    public BasePropertyType findPropertyType(String propertyName) throws DataValidationException
    {
        return findPropertyClassType(propertyName).getPropertyType();
    }

    /**
     * Attempts to find a previously created FirmRoutingPropertyClassType keyed by the passed RoutingPropertyName.
     *
     * @param propertyName to find BasePropertyClassType for
     * @return found FirmRoutingPropertyClassType for passed FirmRoutingPropertyName
     * @throws com.cboe.exceptions.DataValidationException
     *          will be thrown if BasePropertyClassType could not be found for passed RoutingPropertyName
     */
    protected BasePropertyClassType findPropertyClassType(String propertyName)
            throws DataValidationException
    {
        BasePropertyClassType classTypeWrapper = getPropertyMap().get(propertyName);
        if (classTypeWrapper != null)
        {
            return classTypeWrapper;
        }
        else
        {
            Log.alarm("Unknown RoutingPropertyName:" + propertyName + ". Could not find class type to handle.");

            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + propertyName +
                    ". Could not find class type to handle.", 0);
        }
    }

    /**
     * Subscribes the listener to events for the BasePropertyGroup identified.
     *
     * @param basePropertyKey of the BasePropertyGroup to subscribe to
     * @param listener        to subscribe
     */
    public void subscribe(com.cboe.interfaces.domain.routingProperty.BasePropertyKey basePropertyKey, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        String compoundKey = basePropertyKey.getPropertyKey();
        PropertyServiceFacadeHome.find().subscribe(getPropertyCategoryType(), compoundKey, listener);
    }

    public void subscribeForAll(EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        PropertyServiceFacadeHome.find().subscribe(getPropertyCategoryType(), listener);
    }

    /**
     * Unsubscribes a listener to events for the BasePropertyGroup identified.
     *
     * @param basePropertyKey of the BasePropertyGroup to unsubscribe to
     * @param listener        to unsubscribe
     */
    public void unsubscribe(BasePropertyKey basePropertyKey, EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        String compoundKey = basePropertyKey.getPropertyKey();
        PropertyServiceFacadeHome.find().unsubscribe(getPropertyCategoryType(), compoundKey, listener);
    }

    public void unsubscribeForAll(EventChannelListener listener)
            throws SystemException, DataValidationException, CommunicationException, AuthorizationException
    {
        PropertyServiceFacadeHome.find().unsubscribe(getPropertyCategoryType(), listener);
    }

    public BasePropertyGroup[] getAllRoutingPropertyGroupsForKey(String key)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException

    {
        List<BasePropertyGroup> groupList = new ArrayList<BasePropertyGroup>(100);

        PropertyServicePropertyGroup[] groups = PropertyServiceFacadeHome.find().
                getPropertyGroupsForPartialKey(getPropertyCategoryType(), key, PropertyQueryTypes.BEGINS_WITH);

        if (groups != null)
        {
            for (int i = 0; i < groups.length; i++)
            {
                PropertyServicePropertyGroup group = groups[i];

                if (group != null)
                {
                    String propertyName = getPropertyNameFromKey(group.getKey());

                    try
                    {
                        BasePropertyClassType classType = findPropertyClassType(propertyName);

                        BasePropertyKey newKey = buildKey(classType.getPropertyType(), group.getKey());

                        BasePropertyGroup newBasePropertyGroup = createNewRoutingPropertyGroup(classType, newKey);
                        newBasePropertyGroup.setPropertyGroup(group);
                        groupList.add(newBasePropertyGroup);
                    }
                    catch (InvocationTargetException e)
                    {
                        Log.exception("Unable to build BasePropertyGroup for propertyName '" + propertyName +
                                "', key '" + group.getKey() + "'", e);
                    }
                    catch (DataValidationException dve)
                    {
                        Log.exception("Unable to find BasePropertyClassType for propertyName '" + propertyName +
                                "'; cannot create BasePropertyGroup for key '" + group.getKey() + "'", dve);
                    }
                }
            }
        }

        BasePropertyGroup[] newPropertyGroups = new BasePropertyGroup[groupList.size()];
        newPropertyGroups = groupList.toArray(newPropertyGroups);

        return newPropertyGroups;
    }

    public BasePropertyGroup[] copyPropertiesToFirm(BasePropertyGroup[] groups, String firmNumber, String exchange)
            throws CloneNotSupportedException, DataValidationException, InvocationTargetException
    {
        List<BasePropertyGroup> groupList = new ArrayList<BasePropertyGroup>(100);

        for (int i = 0; i < groups.length; i++)
        {
            BasePropertyGroup theGroup = groups[i];

            AbstractBasePropertyKey key = (AbstractBasePropertyKey)theGroup.getPropertyKey().clone();
            key.setFirmNumber(firmNumber);
            key.setExchangeAcronym(exchange);

            BasePropertyGroup newGroup = getPropertyFactoryHome().createNewPropertyGroup(key);
            newGroup.copyProperties(theGroup.getPropertyGroup());

            groupList.add(newGroup);
        }

        BasePropertyGroup[] newPropertyGroups = new BasePropertyGroup[groupList.size()];
        newPropertyGroups = groupList.toArray(newPropertyGroups);

        return newPropertyGroups;
    }

    public void copyFirmProperties(BasePropertyGroup[] fromGroups, String session, ExchangeFirmStruct toFirm) throws UserException, InvocationTargetException, CloneNotSupportedException
    {
        //before copying we have to remove all existing properties for the toFirm
        BasePropertyGroup[] toGroups = getAllPropertyGroupsForFirm(session, toFirm.firmNumber, toFirm.exchange);
        for (BasePropertyGroup fromGroup : fromGroups)
        {
            AbstractBasePropertyKey key = (AbstractBasePropertyKey)fromGroup.getPropertyKey().clone();
            key.setFirmNumber(toFirm.firmNumber);
            key.setExchangeAcronym(toFirm.exchange);

            BasePropertyGroup newGroup = createNewPropertyGroup(key);
            newGroup.copyProperties(fromGroup.getPropertyGroup());

            for (BasePropertyGroup toGroup : toGroups)
            {
//                if (fromGroup.getPropertyName().equals(toGroup.getPropertyName()))
//                {
                    if (toGroup.getPropertyKey().equals(key))
                    {
                        toGroup.delete();
                    }
//                }
            }

            newGroup.save();
        }
    }

    public void copyClassProperties(BasePropertyGroup[] fromGroups, String session, int toClass) throws UserException, InvocationTargetException, CloneNotSupportedException
    {
        //before copying we have to remove all existing properties for the toClass
        BasePropertyGroup[] toGroups = RoutingPropertyFactoryHome.find().getAllPropertyGroupsForClass(session, String.valueOf(toClass));
        for (BasePropertyGroup fromGroup : fromGroups)
        {
            //noinspection InstanceofInterfaces
            if (fromGroup.getPropertyKey() instanceof SessionClassKey)
            {
                SessionClassKey key = (SessionClassKey)fromGroup.getPropertyKey().clone();
                key.setClassKey(toClass);

                BasePropertyGroup newGroup = RoutingPropertyFactoryHome.find().createNewPropertyGroup(key);
                newGroup.copyProperties(fromGroup.getPropertyGroup());

                for (BasePropertyGroup toGroup : toGroups)
                {
                    if (toGroup.getPropertyKey().equals(key))
                    {
                        toGroup.delete();
                    }
                }

                newGroup.save();
            }
        }
    }

    public void copyPostStationProperties(BasePropertyGroup[] fromGroups, String session, int toPost, int toStation) throws UserException, InvocationTargetException, CloneNotSupportedException
    {
        //before copying we have to remove all existing properties for destination
        String toKey = RoutingKeyHelper.createPropertyKey(session, toPost, toStation);
        BasePropertyGroup[] toGroups = RoutingPropertyFactoryHome.find().getAllRoutingPropertyGroupsForKey(toKey);
        for (BasePropertyGroup fromGroup : fromGroups)
        {
            //noinspection InstanceofInterfaces
            if (fromGroup.getPropertyKey() instanceof SessionPostStationKey)
            {
                SessionPostStationKey key = (SessionPostStationKey)fromGroup.getPropertyKey().clone();
                key.setPost(toPost);
                key.setStation(toStation);
                BasePropertyGroup newGroup = RoutingPropertyFactoryHome.find().createNewPropertyGroup(key);
                newGroup.copyProperties(fromGroup.getPropertyGroup());

                for (BasePropertyGroup toGroup : toGroups)
                {
                    if (toGroup.getPropertyKey().equals(key))
                    {
                        toGroup.delete();
                    }
                }

                newGroup.save();
            }

        }
    }


    public BasePropertyGroup[] getAllPropertyGroupsForFirm(String sessionName, String firmAcronym, String exchangeArconym)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException
    {
        String queryKey = RoutingKeyHelper.createBasePropertyKey(sessionName, firmAcronym, exchangeArconym);

        return getAllRoutingPropertyGroupsForKey(queryKey);
    }

    public BasePropertyGroup[] getAllPropertyGroupsForClass(String sessionName, String className)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException
    {
        String queryKey = RoutingKeyHelper.createPropertyKey(sessionName, className);
        return getAllRoutingPropertyGroupsForKey(queryKey);
    }

    /**
     * Returns all RoutingPropertyGroups for the sessionName, which aren't for a specific ExchangeFirm.
     */
    public BasePropertyGroup[] getAllPropertyGroupsForSession(String sessionName)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException
    {
        String queryKey = RoutingKeyHelper.createPropertyKey(sessionName);
        return getAllRoutingPropertyGroupsForKey(queryKey);
    }

    public BasePropertyGroup getPropertyGroup(BasePropertyKey key)
            throws InvocationTargetException, DataValidationException, SystemException, NotFoundException,
            CommunicationException, AuthorizationException, TransactionFailedException
    {
        com.cboe.interfaces.domain.routingProperty.BasePropertyGroup newBasePropertyGroup;
        BasePropertyClassType classType = findPropertyClassType(key.getPropertyName());

        //we have a known property name and implementation type to try to instantiate
        BasePropertyType routingPropertyType = classType.getPropertyType();

        //try to get group from Routing Properties through PropertyService
        PropertyServicePropertyGroup group =
                PropertyServiceFacadeHome.find().getPropertyGroup(routingPropertyType.getPropertyCategory(),
                        key.getPropertyKey());

        if (group != null)
        {
            //got a group and did not throw out any exceptions
            newBasePropertyGroup = createNewRoutingPropertyGroup(classType, key);
            newBasePropertyGroup.setPropertyGroup(group);
        }
        else
        {
            StringBuffer msg = new StringBuffer(100);
            msg.append(getClass().getName());
            msg.append(":PropertyServicePropertyGroup received from PropertyServiceFacadeHome was null:");
            msg.append("category=").append(routingPropertyType.getPropertyCategory());
            msg.append("; Key=").append(key);

            throw ExceptionBuilder.notFoundException(msg.toString(), NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }

        return newBasePropertyGroup;
    }

    protected abstract String getPropertyCategoryType();

    protected abstract Map<String, BasePropertyClassType> getPropertyMap();

    protected abstract BasePropertyFactory getPropertyFactoryHome();

    private MutableBasePropertyKey buildKey(Constructor constructor, Object[] arguments)
            throws InvocationTargetException
    {
        MutableBasePropertyKey key = null;

        try
        {
            if (constructor != null)
            {
                key = (MutableBasePropertyKey)constructor.newInstance(arguments);
            }
        }
        catch (InstantiationException e)
        {
            throw new InvocationTargetException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new InvocationTargetException(e);
        }

        return key;
    }

    /**
     * Creates a new BasePropertyGroup representing the passed attributes. If the new BasePropertyGroup is one of the
     * known three Simple** types, then it will be further initialized so that it knows which BasePropertyType that it
     * represents.
     *
     * @param classType to find the representative implementation of BasePropertyGroup for, for instantiation.
     * @return newly instantiated implementation of a BasePropertyGroup, who's real class implements the type as
     *         represented in the passed classTypeWrapper
     * @throws InvocationTargetException will be thrown if the implementation could not be instantiated.
     */
    private BasePropertyGroup createNewRoutingPropertyGroup(BasePropertyClassType classType, BasePropertyKey key)
            throws InvocationTargetException
    {
        BasePropertyGroup newBasePropertyGroup = null;
        try
        {
            Constructor constructor = classType.getConstructor();
            if (constructor != null)
            {
                //we have a known constructor that accepts (String, int), try to instantiate using
                //this constructor for session name and class key
                Object[] parms = {key};
                newBasePropertyGroup = (BasePropertyGroup)constructor.newInstance(parms);
            }
            else
            {
                // Todo throw exception maybe
            }
        }
        catch (InstantiationException e)
        {
            throw new InvocationTargetException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new InvocationTargetException(e);
        }

        return newBasePropertyGroup;
    }

    /**
     * Returns an array of all known BasePropertyClassType's.
     */
    private BasePropertyClassType[] getAllHandledClassTypes()
    {
        Collection<BasePropertyClassType> values = getPropertyMap().values();
        return values.toArray(new BasePropertyClassType[values.size()]);
    }

    private String getPropertyNameFromKey(String propertyKey)
    {
        return RoutingKeyHelper.getPropertyNameFromKey(propertyKey);
    }
}
