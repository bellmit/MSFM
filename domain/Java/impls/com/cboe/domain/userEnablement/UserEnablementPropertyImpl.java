//
// -----------------------------------------------------------------------------------
// Source file: UserEnablementPropertyImpl.java
//
// PACKAGE: com.cboe.domain.userEnablement
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

// ***************************************************************************************
// ***************************************************************************************
// KAK - 09/2005
//
//  NOTE:
//      This impl is not currently used at this time.  It was a hope that it could be
//      useful in a refactoring effort but refactoring seemed to be a bit overkill at
//      the time.
//
//      This impl and its interface may be useful at a later time.
//
// ***************************************************************************************
// ***************************************************************************************
package com.cboe.domain.userEnablement;

import java.util.*;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.user.UserEnablementProperty;

import com.cboe.domain.property.PropertyServicePropertyImpl;


public class UserEnablementPropertyImpl extends PropertyServicePropertyImpl implements UserEnablementProperty
{

    public UserEnablementPropertyImpl(String sessionName, int classKey, int operationType)
    {
        super();

        ArrayList nameList = new ArrayList();
        ArrayList valueList = new ArrayList();

        // Namelist is session Name, classKey, operationType
        nameList.add(UserEnablementHelper.SESSION_NAME_INDEX, sessionName);
        nameList.add(UserEnablementHelper.CLASSKEY_INDEX, Integer.toString(classKey));
        nameList.add(UserEnablementHelper.OPERATIONTYPE_INDEX, Integer.toString(operationType));

        valueList.add(UserEnablementHelper.OPERATION_ENABLED_INDEX, Boolean.valueOf(true).toString());

        setNameList(nameList);
        setValueList(valueList);
        setPropertyDefinition(null);

    }

    public UserEnablementPropertyImpl(Property property)
        throws NumberFormatException
    {
        super(property.getNameList(), property.getValueList(), property.getPropertyDefinition());
    }

    public String getSessionName()
    {
        return (String) getNameList().get(UserEnablementHelper.SESSION_NAME_INDEX);
    }

    public int getClassKey()
    {
        return Integer.parseInt((String) getNameList().get(UserEnablementHelper.CLASSKEY_INDEX));
    }

    public int getOperationType()
    {
        return Integer.parseInt((String) getNameList().get(UserEnablementHelper.OPERATIONTYPE_INDEX));
    }

    public void setSessionName(String sessionName)
    {
        nameList.remove(UserEnablementHelper.SESSION_NAME_INDEX);
        nameList.add(UserEnablementHelper.SESSION_NAME_INDEX, sessionName);
    }

    public void setClassKey(int classKey)
    {
        nameList.remove(UserEnablementHelper.CLASSKEY_INDEX);
        nameList.add(UserEnablementHelper.CLASSKEY_INDEX, Integer.toString(classKey));
    }

    public void setOperationType(int operationType)
    {
        nameList.remove(UserEnablementHelper.OPERATIONTYPE_INDEX);
        nameList.add(UserEnablementHelper.OPERATIONTYPE_INDEX, Integer.toString(operationType));
    }

}
