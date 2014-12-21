package com.cboe.internalPresentation.product;

import com.cboe.idl.product.GroupStruct;

import com.cboe.interfaces.internalPresentation.product.GroupModel;
import com.cboe.interfaces.internalPresentation.product.GroupTypeModel;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

/**
 * @author torresl@cboe.com
 */
class GroupModelImpl extends AbstractMutableBusinessModel implements GroupModel
{
    public static final int     NEW_GROUP = -1;
    public static final String  NEW_GROUP_NAME = "New Group";
    protected int               groupKey;
    protected String            groupName;
    protected GroupTypeModel         groupType;
    protected GroupStruct       groupStruct;
    public GroupModelImpl(GroupStruct groupStruct)
    {
        super();
        this.groupStruct = groupStruct;
        initialize();
    }
    public GroupModelImpl()
    {
        super();
        this.groupStruct = new GroupStruct();
        groupStruct.groupKey = NEW_GROUP;
        groupStruct.groupName = NEW_GROUP_NAME;
        groupStruct.groupType = GroupTypeModelFactory.createGroupType().getStruct();
        initialize();
    }
    private void initialize()
    {
        groupKey = groupStruct.groupKey;
        groupName = new String(groupStruct.groupName);
        groupType = GroupTypeModelFactory.createGroupType(groupStruct.groupType);
    }

    public int getGroupKey()
    {
        return groupKey;
    }

    public void setGroupKey(int groupKey)
    {
        this.groupKey = groupKey;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public GroupTypeModel getGroupType()
    {
        return groupType;
    }

    public void setGroupType(GroupTypeModel groupType)
    {
        this.groupType = groupType;
    }

    public GroupStruct getStruct()
    {
        GroupStruct struct = new GroupStruct();
        struct.groupKey = getGroupKey();
        struct.groupName = getGroupName();
        struct.groupType = getGroupType().getStruct();
        return struct;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return new GroupModelImpl(getStruct());
    }

    public int hashCode()
    {
        return this.groupKey;
    }

    public String toString()
    {
        return getGroupName();
    }
}
