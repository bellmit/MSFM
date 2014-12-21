//
// -----------------------------------------------------------------------------------
// Source file: GroupRelationship.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

public interface GroupRelationship
{
    public int getGroupElementKey();

    public int getChildElementKey();

    public void setGroupElementKey(int aValue);

    public void setChildElementKey(int aValue);
}
