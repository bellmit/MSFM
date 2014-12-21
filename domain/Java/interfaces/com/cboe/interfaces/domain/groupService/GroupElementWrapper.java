//
// -----------------------------------------------------------------------------------
// Source file: GroupElementWrapper.java
//
// PACKAGE: com.cboe.interfaces.domain.groupService
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.groupService;

/**
 * This is just a base interface for the any model object (data holder) used for the
 * caching objects in GroupElementCache.
 */
public interface GroupElementWrapper
{
    public long getElementKey();
    public void setElementKey(long elementKey);

    public short getNodeType();
    public void setNodeType(short nodeType);
    
    public boolean isLeaf();
    public boolean isRoot();
    public boolean isGroup();

    public String getElementName();
    public void setElementName(String elementName);

    public short getElementGroupType();
    public void setElementGroupType(short elementGroupType);

    public short getElementDataType();
    public void setElementDataType(short elementDataType);

    public long getElementDataKey();
    public void setElementDataKey(long elementDatakey);

    public int getVersionId();
    public void setVersionId(int versionId);

}
