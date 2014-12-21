package com.cboe.domain.util;

import com.cboe.idl.product.GroupStruct;

public class GroupContainer extends Object
{
    private GroupStruct group;
    private String groupName;

    /**
      * Sets the internal fields to the passed values
      */
    public GroupContainer(GroupStruct group)
    {
		this.group = group;
		this.groupName = group.groupName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public GroupStruct getStruct()
    {
        return group;
    }

    public int hashCode()
    {
        return groupName.hashCode();
    }

    public boolean equals(Object obj)
    {
        boolean result = false;
        if ((obj != null) && (obj instanceof GroupContainer))
        {
            GroupContainer otherObj = (GroupContainer) obj;
            if (this.groupName.equals(otherObj.getGroupName()))
                result = true;
        }
        return result;
    }

    public String toString()
    {
		return getGroupName();
    }
}
