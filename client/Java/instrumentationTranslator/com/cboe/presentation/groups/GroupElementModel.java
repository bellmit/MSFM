//
// -----------------------------------------------------------------------------------
// Source file: GroupElementModel.java
//
// PACKAGE: com.cboe.interfaces.internalPresentation.userGroup
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.groups;

import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.interfaces.domain.groupService.GroupElementWrapper;

/**
 * This is just a marker interface for the GUI model implementation of
 * GroupElementWrapper used for the caching objects. Also adds more functionality
 * specific to the gui interface.
 */
public interface GroupElementModel extends GroupElementWrapper
{

    public DateWrapper getLastModifiedTime();

    public void setLastModifiedTime(DateWrapper lastModifiedTime);

    public ElementEntryStruct toElementEntryStruct();

    public ElementStruct toElementStruct();

    public void copyValues(GroupElementModel groupElementModel);

    /**
     * method to handle the dirtyness of the element model of this group
     */
    public boolean isDirty();

    public void setDirty(boolean isDirty);

    public boolean isCluster();

    public boolean isHost();
}
