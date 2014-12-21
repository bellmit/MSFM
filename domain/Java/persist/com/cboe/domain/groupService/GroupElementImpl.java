package com.cboe.domain.groupService;


import com.cboe.domain.util.DateWrapper;
import com.cboe.idl.constants.ElementNodeTypes;
import com.cboe.idl.groupElement.ElementEntryStruct;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.groupService.GroupElement;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A Domain layer implementation of the GroupElement interface
 *
 * @author Antony Jesuraj
 * @author Cherian Mathew - Refactoring and documentation
 */
public class GroupElementImpl extends PersistentBObject implements GroupElement
{
    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "group_element";

    /**
     * The element name
     */
    private String elementName;

    /**
     * The extensions
     */
    private String extensions;

    /**
     * The group type element
     */
    private short elementGroupType;

    /**
     * The data type of the element
     */
    private short elementDataType;

    /**
     * The data key of the element
     */
    private long elementDataKey;

    /**
     * The node type of the element
     */
    private short nodeType;

    /**
     * The version ID of the element
     */
    private int versionId;

    /**
     * The time of creation
     */
    private long createdTime;

    /**
     * The last modified date
     */
    private long lastModifiedTime;

    /**
     * Fields for JavaGrinder
     */
    private static Field _elementName;
    private static Field _elementGroupType;
    private static Field _elementDataType;
    private static Field _elementDataKey;
    private static Field _nodeType;
    private static Field _extensions;
    private static Field _versionId;
    private static Field _createdTime;
    private static Field _lastModifiedTime;

    /*
	 * JavaGrinder attribute descriptions.
	 */
    private static Vector classDescriptor;

    /**
     * This static block will be regenerated if persistence is regenerated.
     */
    static
    {
        try
        {
            _elementName = GroupElementImpl.class.getDeclaredField("elementName");
            _elementGroupType = GroupElementImpl.class.getDeclaredField("elementGroupType");
            _elementDataType = GroupElementImpl.class.getDeclaredField("elementDataType");
            _elementDataKey = GroupElementImpl.class.getDeclaredField("elementDataKey");
            _nodeType = GroupElementImpl.class.getDeclaredField("nodeType");
            _extensions = GroupElementImpl.class.getDeclaredField("extensions");
            _versionId = GroupElementImpl.class.getDeclaredField("versionId");
            _createdTime = GroupElementImpl.class.getDeclaredField("createdTime");
            _lastModifiedTime = GroupElementImpl.class.getDeclaredField("lastModifiedTime");

            _elementName.setAccessible(true);
            _elementGroupType.setAccessible(true);
            _elementDataType.setAccessible(true);
            _elementDataKey.setAccessible(true);
            _nodeType.setAccessible(true);
            _extensions.setAccessible(true);
            _versionId.setAccessible(true);
            _createdTime.setAccessible(true);
            _lastModifiedTime.setAccessible(true);
        }
        catch (NoSuchFieldException ex)
        {
            Log.exception("Unable to create field defintions for GroupElement", ex);
        }
    }

    /**
     * Creates an instance
     */
    public GroupElementImpl()
    {
        super();

    }

    /**
     * @return Returns the name of the element
     */
    public String getElementName()
    {
        return editor.get(_elementName, elementName);
    }

    /**
     * @return Returns the group type of the element
     */
    public short getElementGroupType()
    {
        return editor.get(_elementGroupType, elementGroupType);
    }

    /**
     * @return Returns the data type of the element
     */
    public short getElementDataType()
    {
        return editor.get(_elementDataType, elementDataType);
    }

    /**
     * @return Returns the data key of the element
     */
    public long getElementDataKey()
    {
        return editor.get(_elementDataKey, elementDataKey);
    }

    /**
     * @return Returns the node type of the element
     */
    public short getNodeType()
    {
        return editor.get(_nodeType, nodeType);
    }

    /**
     * @return Returns the extension of the element
     */
    public String getExtensions()
    {
        return editor.get(_extensions, extensions);
    }

    /**
     * @return Returns the versionID of the element
     */
    public int getVersionId()
    {
        return editor.get(_versionId, versionId);
    }

    /**
     * @return Returns the created time of the element
     */
    public long getCreatedTime()
    {
        return editor.get(_createdTime, createdTime);
    }

    /**
     * @return Returns the last modified time of the element
     */
    public long getLastModifiedTime()
    {
        return editor.get(_lastModifiedTime, lastModifiedTime);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public void initDescriptor()
    {
        if (classDescriptor != null)
        {
            return;
        }

        Vector tempDescriptor = getSuperDescriptor();

        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("elementName", _elementName));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("elementGroupType", _elementGroupType));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("elementDataType", _elementDataType));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("elementDataKey", _elementDataKey));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("nodeType", _nodeType));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("extensions", _extensions));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("versionNumber", _versionId));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("create_time", _createdTime));
        tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("lmod_time", _lastModifiedTime));

        classDescriptor = tempDescriptor;
    }

    /**
     * Needed to define table name and the description of this class.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
        if (classDescriptor == null)
        {
            initDescriptor();
        }
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

    /**
     * Sets the name of the element
     *
     * @param aValue - The name for the element
     */
    public void setElementName(String aValue)
    {
        editor.set(_elementName, aValue, elementName);

    }

    /**
     * Sets the group type of the element
     *
     * @param aValue - The group type
     */
    public void setElementGroupType(short aValue)
    {
        editor.set(_elementGroupType, aValue, elementGroupType);
    }

    /**
     * Sets the data type of the element
     *
     * @param aValue - The data type
     */
    public void setElementDataType(short aValue)
    {
        editor.set(_elementDataType, aValue, elementDataType);
    }

    /**
     * Sets the data key of the element
     *
     * @param aValue - The data key
     */
    public void setElementDataKey(long aValue)
    {
        editor.set(_elementDataKey, aValue, elementDataKey);
    }

    /**
     * Sets the node type of the element
     *
     * @param aValue -- The node type
     */
    public void setNodeType(short aValue)
    {
        editor.set(_nodeType, aValue, nodeType);
    }

    /**
     * Sets the extension of the element
     *
     * @param aValue - The extension
     */
    public void setExtensions(String aValue)
    {
        editor.set(_extensions, aValue, extensions);
    }

    /**
     * Sets the version ID of the element
     *
     * @param aValue - The versionID
     */
    public void setVersionId(int aValue)
    {
        editor.set(_versionId, aValue, versionId);
    }

    /**
     * Sets the time on which the element was created
     *
     * @param aValue - The time of creation
     */
    public void setCreatedTime(long aValue)
    {
        editor.set(_createdTime, aValue, createdTime);
    }

    /**
     * Sets the latest time on which the element was modified
     *
     * @param aValue - The time
     */
    public void setLastModifiedTime(long aValue)
    {
        editor.set(_lastModifiedTime, aValue, lastModifiedTime);
    }

    /**
     * @return Returns the GroupElement as an ElementStruct
     */
    public ElementStruct toStruct()
    {
        ElementStruct elementStruct = new ElementStruct();
        ElementEntryStruct elementEntryStruct = new ElementEntryStruct();
        elementStruct.elementKey = getObjectIdentifierAsLong();
        elementEntryStruct.elementName = getElementName();

        elementEntryStruct.elementGroupType = getElementGroupType();
        elementEntryStruct.elementDataType = getElementDataType();
        elementEntryStruct.elementDataKey = getElementDataKey();
        elementEntryStruct.nodeType = getNodeType();
        elementEntryStruct.extensions = getExtensions();
        elementStruct.entryStruct = elementEntryStruct;

        elementStruct.versionNumber = getVersionId();
        elementStruct.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
        elementStruct.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());

        return elementStruct;
    }

    /**
     * Utility method for logging
     *
     * @return The log
     */
    public String toString()
    {
        return new StringBuilder("elementName ").append(elementName).append(" elementGroupType ")
                .append(elementGroupType).append(" elementDataType ").append(elementDataType)
                .append(" elementDataKey ").append(elementDataKey).append(" nodeType ").append(nodeType)
                .append(" extensions ").append(extensions).append(" versionId ").append(versionId).toString();
    }

    /**
     * @return Returns the key of the element
     */
    public long getElementKey()
    {
        return getObjectIdentifierAsLong();
    }

    /**
     * Set the a new key for the element
     *
     * @param elementKey - The new key
     */
    public void setElementKey(long elementKey)
    {
        setObjectIdentifierFromLong(elementKey);
    }

    /**
     * @return Returns "true" if the element is a leaf
     */
    public boolean isLeaf()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_LEAF);
    }

    /**
     * @return Returns "true" if the element is a root
     */
    public boolean isRoot()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_ROOT);
    }

    /**
     * @return Returns "true" if the element is a group
     */
    public boolean isGroup()
    {
        return (nodeType == ElementNodeTypes.NODE_TYPE_GROUP);
    }

    /**
     * @return Returns the ID of the current thread in execution
     */
    public static String getThreadId()
    {
        return new StringBuilder("Thread ID ::: ").append(Thread.currentThread().getId()).append(" ").toString();
    }
}
