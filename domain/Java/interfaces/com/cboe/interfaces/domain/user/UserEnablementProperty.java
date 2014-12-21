package com.cboe.interfaces.domain.user;

import com.cboe.interfaces.domain.property.PropertyServiceProperty;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/interfaces/domain/user/UserEnablementProperty.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

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

/**
 * Describes an element of user enablement.  
 *
 * @author Keith A. Korecky
 */
public interface UserEnablementProperty extends PropertyServiceProperty
{
    public String getSessionName();

    public int getClassKey();

    public int getOperationType();

    public void setSessionName(String sessionName);

    public void setClassKey(int classKey);

    public void setOperationType(int operationType);
}

