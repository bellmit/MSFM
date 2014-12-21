//
// -----------------------------------------------------------------------------------
// Source file: GroupElementModelImpl.java
//
// PACKAGE: com.cboe.internalPresentation.userGroup
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.groups;

import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.constants.ElementDataTypes;
import com.cboe.idl.constants.ElementNodeTypes;

/**
 * Class used as GroupElementMoel implementation.
 * This is the final class used in GUI cache for Group Service.
 */
public class GroupElementModelImpl implements GroupElementModel, Comparable, Cloneable
{

    private long elementKey;
    private String elementName;
    private long elementDataKey;
    private short elementGroupType; // cloud type
    private short elementDataType; // element type
    private short nodeType;
    private DateWrapper lastModifiedTime;
    private int versionId;
    private String extensions;
    /**
     * variable holds the SavedState of the model true or false.
     */
    private boolean isDirty;


    public GroupElementModelImpl()
    {
    }

    public GroupElementModelImpl(ElementStruct elementStruct)
    {
        elementKey = elementStruct.elementKey;

        elementName = elementStruct.entryStruct.elementName;
        elementGroupType = elementStruct.entryStruct.elementGroupType;
        elementDataType = elementStruct.entryStruct.elementDataType;
        elementDataKey = elementStruct.entryStruct.elementDataKey;
        nodeType = elementStruct.entryStruct.nodeType;
        extensions = elementStruct.entryStruct.extensions;

        versionId = elementStruct.versionNumber;
        lastModifiedTime = new DateWrapper(elementStruct.lastModifiedTime);

    }

    public long getElementKey()
    {
        return elementKey;
    }

    public void setElementKey(long elementKey)
    {
        this.elementKey = elementKey;
    }

    public String getElementName()
    {
        return elementName;
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }

    public long getElementDataKey()
    {
        return elementDataKey;
    }

    public void setElementDataKey(long elementDataKey)
    {
        this.elementDataKey = elementDataKey;
    }

    public short getElementGroupType()
    {
        return elementGroupType;
    }

    public void setElementGroupType(short elementGroupType)
    {
        this.elementGroupType = elementGroupType;
    }

    public short getElementDataType()
    {
        return elementDataType;
    }

    public void setElementDataType(short elementDataType)
    {
        this.elementDataType = elementDataType;
    }

    public short getNodeType()
    {
        return nodeType;
    }

    public void setNodeType(short nodeType)
    {
        this.nodeType = nodeType;
    }


    public boolean isLeaf()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_LEAF);
    }

    public boolean isRoot()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_ROOT);
    }

    public boolean isGroup()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_GROUP);
    }

    public DateWrapper getLastModifiedTime()
    {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(DateWrapper lastModifiedTime)
    {
        this.lastModifiedTime = lastModifiedTime;
    }

    public int getVersionId()
    {
        return versionId;
    }

    public void setVersionId(int versionId)
    {
        this.versionId = versionId;
    }

    public String getExtensions()
    {
        return extensions;
    }

    public void setExtensions(String extensions)
    {
        this.extensions = extensions;
    }

    public boolean equals(Object newGEMObj)
    {
        if (this == newGEMObj)
        {
            return true;
        }
        if (newGEMObj == null || getClass() != newGEMObj.getClass())
        {
            return false;
        }
        return (elementKey == ((GroupElementModelImpl) newGEMObj).elementKey);
    }

    public int hashCode()
    {
        return (int) elementKey;
    }

    public int compareTo(Object newGEMObj)
    {
        if (newGEMObj == null || getClass() != newGEMObj.getClass())
        {
            return Integer.MIN_VALUE;
        }
        return (this.elementName.compareTo(((GroupElementModelImpl) newGEMObj).elementName));
    }

    public String toString()
    {
        return elementName;
    }

    public ElementEntryStruct toElementEntryStruct()
    {
        ElementEntryStruct entryStruct = new ElementEntryStruct();
        entryStruct.elementName = elementName;
        entryStruct.elementGroupType = elementGroupType;
        entryStruct.elementDataType = elementDataType;
        entryStruct.elementDataKey = elementDataKey;
        entryStruct.nodeType = nodeType;
        entryStruct.extensions = extensions != null ? extensions : "";
        return entryStruct;
    }

    public ElementStruct toElementStruct()
    {
        ElementEntryStruct entryStruct = toElementEntryStruct();

        ElementStruct elementStruct = new ElementStruct();
        elementStruct.elementKey = elementKey;
        elementStruct.entryStruct = entryStruct;
        elementStruct.versionNumber = versionId;
        elementStruct.lastModifiedTime = lastModifiedTime.toDateTimeStruct();
        elementStruct.createdTime = new DateWrapper(System.currentTimeMillis()).toDateTimeStruct();

        return elementStruct;
    }

    public void copyValues(GroupElementModel groupElementModel)
    {
        this.elementName = ((GroupElementModelImpl)groupElementModel).elementName;
        this.extensions = ((GroupElementModelImpl)groupElementModel).extensions;
        this.versionId = ((GroupElementModelImpl)groupElementModel).versionId;
        this.lastModifiedTime = ((GroupElementModelImpl)groupElementModel).lastModifiedTime;
    }


    public  Object clone() throws CloneNotSupportedException
    {
        super.clone();
        GroupElementModelImpl element = new GroupElementModelImpl();
        element.elementKey = this.elementKey;

        element.elementName = this.elementName;
        element.elementGroupType = this.elementGroupType;
        element.elementDataType = this.elementDataType;
        element.elementDataKey = this.elementDataKey;
        element.nodeType = this.nodeType;
        element.extensions = this.extensions;

        element.versionId = this.versionId;
        element.lastModifiedTime = this.lastModifiedTime;

        return element;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    public void setDirty(boolean isDirty)
    {
        this.isDirty = isDirty;
    }


    public boolean isCluster()
    {
        return elementDataType == ElementDataTypes.DATA_TYPE_CLUSTER;
    }

    public boolean isHost()
    {
        return elementDataType == ElementDataTypes.DATA_TYPE_HOST;
    }
}
