package com.cboe.interfaces.domain.groupService;

import com.cboe.idl.groupElement.ElementStruct;

/**
 * Interface type for GroupElement
 */
public interface GroupElement extends GroupElementWrapper
{
    public String getElementName();

    public short getElementGroupType();

    public short getElementDataType();

    public long getElementDataKey();

    public short getNodeType();

    public String getExtensions();

    public int getVersionId();

    public long getCreatedTime();

    public long getLastModifiedTime();

    public void setElementName(String aValue);

    public void setElementGroupType(short aValue);

    public void setElementDataType(short aValue);

    public void setElementDataKey(long aValue);

    public void setNodeType(short aValue);

    public void setExtensions(String aValue);

    public void setVersionId(int aValue);

    public void setCreatedTime(long aValue);

    public void setLastModifiedTime(long aValue);

    public ElementStruct toStruct();

}
