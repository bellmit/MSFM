//
// -----------------------------------------------------------------------------------
// Source file: GroupElementRelationship.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
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

/**
 * Interface type for GroupElementRelationship
 */
public interface GroupElementRelationship
{
    public long getGroupElementKey();

    public long getChildElementKey();

    public void setGroupElementKey(long aValue);

    public void setChildElementKey(long aValue);

}
