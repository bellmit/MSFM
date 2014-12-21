//
// -----------------------------------------------------------------------------------
// Source file: UserEnablementHelper.java
//
// PACKAGE: com.cboe.domain.userEnablement
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.userEnablement;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.cboe.idl.constants.OperationTypesOperations;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.user.MarketMakerClassAssignmentStruct;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;

import com.cboe.util.ReflectiveObjectWriter;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;


public class UserEnablementHelper
{
    public static final int SESSION_NAME_INDEX = 0;
    public static final int CLASSKEY_INDEX = 1;
    public static final int OPERATIONTYPE_INDEX = 2;

    public static final int OPERATION_ENABLED_INDEX = 0;

    public static int[] classAssignmentOperationTypes = {   OperationTypesOperations.QUOTE_QUOTEENTRY
                                                        };

    public static final int DEFAULT_CLASS_KEY = 0;

    private UserEnablementHelper()
    {
    }

    // return String "key" for lookup use against property service for user enablements
    //
    public static String getUserEnablementKey(String userId, String exchange, String acronym)
    {
        if ( Log.isDebugOn() )
        {
            Log.debug("UserEnablementHelper::getUserEnablementKey for userId:exchange:acronym - " + userId + ":" + exchange + ":" + acronym);
        }
        String enablementKey = BasicPropertyParser.buildCompoundString(new String[]{exchange, acronym});
        return enablementKey;
    }

    // return PropertyGroupStruct (user enablement) with proper session/classKey(s) enabled
    //	NOTE: empty classAssignments sequence implies NO enablements
    //
    //  Also: Intentionally avoided using certain PropertyFactory calls as they envoke the Property Facade implementation
    //          which ultimately calls back to the PropertyService.  Did not want this domain item to do that.
    //
    public static PropertyGroupStruct enableUserAssignedClasses(PropertyGroupStruct userEnablements, String sessionName, MarketMakerClassAssignmentStruct[] classAssignments)
    {
        PropertyGroupStruct newUserEnablements = null;

        if(Log.isDebugOn())
        {
            Log.debug("UserEnablementHelper::enableUserAssignedClasses for sessionName:userEnablements - " + sessionName + ":" + userEnablements);
            try
            {
                Writer stringWriter = new StringWriter();
                ReflectiveObjectWriter.writeObject( userEnablements, "userEnablements", stringWriter);
                Log.debug("UserEnablementHelper::userEnablements - " + stringWriter.toString() );
            }
            catch( IOException ioe )
            {
            }
        }
        PropertyServicePropertyGroup propertyServicePropertyGroup = null;

        // create a "lightweight" service property group to use for add/remove processing
        // of only those items indicated by the sessionName
        propertyServicePropertyGroup = PropertyFactory.createPropertyGroup(userEnablements.category, userEnablements.propertyKey);
        propertyServicePropertyGroup.setVersion(userEnablements.versionNumber);

        // remove (by adding the keepers) all enablements for given sessionName and operationType
        for ( int i = 0; i < userEnablements.preferenceSequence.length; i++)
        {
            PropertyServiceProperty property = PropertyFactory.createProperty(userEnablements.preferenceSequence[i].name, userEnablements.preferenceSequence[i].value);

            if((getSessionName(property).equalsIgnoreCase(sessionName))
               && (isAssignedClassOperationType(getOperationType(property)))
            )
            {
                // do nothing as we don't want this one.
                if(Log.isDebugOn())
                {
                    Log.debug("UserEnablementHelper::enableUserAssignedClasses removing class assignment for property - " + property.getName());
                }
            }
            else
            {
                // add the keepers - don't apply to the assignment request
                if(Log.isDebugOn())
                {
                    Log.debug("UserEnablementHelper::enableUserAssignedClasses adding 'other' property - " + property);
                }
                propertyServicePropertyGroup.addProperty(property);

            }
        }

        // now, add all applicable class assignments based on inbound parms.
        for(int i = 0; i < classAssignments.length; i++)
        {
            if(classAssignments[i].sessionName.equalsIgnoreCase(sessionName))
            {
                for(int j = 0; j < classAssignmentOperationTypes.length; j++)
                {
                    Property property = createClassAssignmentEnablementProperty(classAssignments[i].sessionName, classAssignments[i].classKey, classAssignmentOperationTypes[j]);
                    if(Log.isDebugOn())
                    {
                        Log.debug("UserEnablementHelper::enableUserAssignedClasses adding class assignment for property - " + property);
                    }
                    propertyServicePropertyGroup.addProperty(property);
                }
            }
        }

        newUserEnablements = propertyServicePropertyGroup.getStruct();
        if(Log.isDebugOn())
        {
            try
            {
                Writer stringWriter = new StringWriter();
                ReflectiveObjectWriter.writeObject(newUserEnablements, "newUserEnablements", stringWriter);
                Log.debug("UserEnablementHelper::newUserEnablements - " + stringWriter.toString());
            }
            catch(IOException ioe)
            {
            }
        }

        return newUserEnablements;
    }

    private static Property createClassAssignmentEnablementProperty(String sessionName, int classKey, int operationType)
    {
        String enablementName = BasicPropertyParser.buildCompoundString(new String[]{sessionName,Integer.toString(classKey),Integer.toString(operationType)});
        String enablementValue = BasicPropertyParser.buildCompoundString(new String[]{Boolean.toString(true)});
        return PropertyFactory.createProperty(enablementName, enablementValue);
    }

    private static boolean isAssignedClassOperationType(int operationType)
    {
        boolean isOperationType = false;

        for (int i = 0 ; i < classAssignmentOperationTypes.length; i++)
        {
            if (classAssignmentOperationTypes[ i ] == operationType)
            {
                return true;
            }
        }

        return isOperationType;
    }

    // return PropertyGroupStruct (user enablement) with the "default" set of session/classKey(s) enabled
    //
    public static PropertyGroupStruct createDefaultUserEnablement(String userId, String exchange, String acronym)
    {
        PropertyServicePropertyGroup propertyServicePropertyGroup = null;
        PropertyGroupStruct propertyGroupStruct = null;

        propertyServicePropertyGroup = PropertyFactory.createPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT, getUserEnablementKey(userId, exchange, acronym));
        propertyServicePropertyGroup.setVersion(0);
        propertyGroupStruct = propertyServicePropertyGroup.getStruct();

        return propertyGroupStruct;
    }

    private static String getSessionName(PropertyServiceProperty property)
    {
        return (String) property.getNameList().get(SESSION_NAME_INDEX);
    }

    private static int getClassKey(PropertyServiceProperty property)
    {
        return Integer.parseInt((String) property.getNameList().get(CLASSKEY_INDEX));
    }

    private static int getOperationType(PropertyServiceProperty property)
    {
        return Integer.parseInt((String) property.getNameList().get(OPERATIONTYPE_INDEX));
    }

    /**
     * resetQuoteEnablesBySessionForUser
     * @param userEnablements
     * @param mySessionName
     * @return  PropertyGroupStruct (user enablement) with proper session/classKey(s) enabled
     */
    public static PropertyGroupStruct resetEnablesOfClassAssignmentsBySessionForUser(PropertyGroupStruct userEnablements, String mySessionName)
    {
        PropertyServicePropertyGroup propertyServicePropertyGroup = loadNonClassAssignmentEnablementEntriesOnly(userEnablements, mySessionName);

        // now, add all applicable class assignments based on inbound parms.
        for(int j = 0; j < classAssignmentOperationTypes.length; j++)
        {
            Property property = createClassAssignmentEnablementProperty(mySessionName, DEFAULT_CLASS_KEY, classAssignmentOperationTypes[j]);
            if(Log.isDebugOn())
            {
                Log.debug("UserEnablementHelper::resetEnablesOfClassAssignmentsBySessionForUser:: adding userEnablements of class assignments for property - " + property);
            }
            propertyServicePropertyGroup.addProperty(property);
        }
        PropertyGroupStruct newUserEnablements = null;
        newUserEnablements = propertyServicePropertyGroup.getStruct();

        return newUserEnablements;
    }

    private static PropertyServicePropertyGroup loadNonClassAssignmentEnablementEntriesOnly(PropertyGroupStruct userEnablements, String mySessionName)
    {
        PropertyServicePropertyGroup propertyServicePropertyGroup = null;

        // create a "lightweight" service property group to use for add/remove processing
        // of only those items indicated by the sessionName
        propertyServicePropertyGroup = PropertyFactory.createPropertyGroup(userEnablements.category, userEnablements.propertyKey);
        propertyServicePropertyGroup.setVersion(userEnablements.versionNumber);

        // remove (by adding the keepers) all enablements for given sessionName and operationType
        for ( int i = 0; i < userEnablements.preferenceSequence.length; i++)
        {
            PropertyServiceProperty property = PropertyFactory.createProperty(userEnablements.preferenceSequence[i].name, userEnablements.preferenceSequence[i].value);

            if((getSessionName(property).equalsIgnoreCase(mySessionName))
               && (isAssignedClassOperationType(getOperationType(property)))
            )
            {
                // do nothing as we don't want this one.
                if(Log.isDebugOn())
                {
                    Log.debug("UserEnablementHelper:: removing userEnablements of class assignments for property - " + property);
                }
            }
            else
            {
                // add the keepers - don't apply to the assignment request
                if(Log.isDebugOn())
                {
                    Log.debug("UserEnablementHelper:: adding 'other' property - " + property);
                }
                propertyServicePropertyGroup.addProperty(property);
            }
        }
        return propertyServicePropertyGroup;
    }

}
