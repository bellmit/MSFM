//
// -----------------------------------------------------------------------------------
// Source file: UserInputMonitorFactory.java
//
// PACKAGE: com.cboe.domain.uim
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.uim;

import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyGroup;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.uim.UserInputMonitorEntry;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


@SuppressWarnings({"RawUseOfParameterizedType"})
public class UserInputMonitorFactory
{
    private UserInputMonitorFactory(){} //to prevent instantiation of class having only static methods

    public static UserInputMonitorEntry create(String sessionName, int windowInterval)
    {
        return new UserInputMonitorEntryImpl(sessionName, windowInterval);
    }

    public static UserInputMonitorEntry create(PropertyServiceProperty property) throws DataValidationException
    {
        String sessionName;
        int windowInterval;

        List nameList = property.getNameList();
        List valueList = property.getValueList();

        sessionName = (String)nameList.get(0);
        try
        {
            windowInterval  = Integer.parseInt((String) valueList.get(0));
        }
        catch (NumberFormatException nfe)
        {
            Log.exception("Property does not contain the proper data to make a UIM Entry", nfe);
            throw ExceptionBuilder.dataValidationException("Property does not contain the proper data to make a UIM entry", DataValidationCodes.INVALID_GROUP);
        }

        UserInputMonitorEntry uimEntry = create(sessionName, windowInterval);
        ((UserInputMonitorEntryImpl) uimEntry).setPropertyDefinition(property.getPropertyDefinition());

        return uimEntry;
    }

    public static List<UserInputMonitorEntry> createUserInputMonitorEntryList(PropertyGroup propertyGroup) throws DataValidationException
    {
        List<UserInputMonitorEntry> list = new ArrayList<UserInputMonitorEntry>(50);

        Map properties = propertyGroup.getProperties();
        for(Object o : properties.values())
        {
            PropertyServiceProperty property = (PropertyServiceProperty) o;
            UserInputMonitorEntry uimEntry = create(property);
            list.add(uimEntry);
        }

        return list;
    }

     public static PropertyServicePropertyGroup createPropertyGroup(List<UserInputMonitorEntry> uimEntries, int version, String userId)
    {
        PropertyServicePropertyGroup group;

        String propertyGroupKey = getUserInputMonitorKey(userId);
        group = PropertyFactory.createPropertyGroup(PropertyCategoryTypes.USER_PROPERTIES, propertyGroupKey);
        group.setVersion(version);

        for(UserInputMonitorEntry uimEntry : uimEntries)
        {
            UserInputMonitorEntryImpl localUIMEntry = (UserInputMonitorEntryImpl) uimEntry;
            Property property = localUIMEntry.getProperty();
            group.addProperty(property);
        }

        return group;
    }

    public static String getUserInputMonitorKey(String userId)
    {
        //key consists of user ID and the hardcoded value "UIMInterval"
        return BasicPropertyParser.buildCompoundString(new String[]{userId, "UIMInterval"});
    }


}
