//
// -----------------------------------------------------------------------------------
// Source file: Group.java
//
// PACKAGE: com.cboe.interfaces.presentation.productConfiguration
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.productConfiguration;

import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public interface Group extends Comparable
{
    public int getGroupKey();
    public String getGroupName();
    public GroupStruct getGroupStruct();
    public GroupTypeStruct getGroupTypeStruct();

    /**
     * Returns true if this Group represents a Post.
     */
    public boolean isPostGroup();

    /**
     * If this Group is a Post node, this will return the post number.  Otherwise it will return -1.
     */
    public int getPostNumber();

    /**
     * Returns true if this Group represents a Station.
     */
    public boolean isStationGroup();

    /**
     * If this Group is a Station node, this will return the station number.  Otherwise it will return -1.
     */
    public int getStationNumber();

    public Group[] getSuperGroups()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public Group[] getSubGroups()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}