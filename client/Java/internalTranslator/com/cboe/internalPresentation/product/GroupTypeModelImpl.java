package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.GroupTypeModel;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.idl.product.GroupTypeStruct;

/**
 * @author torresl@cboe.com
 */
class GroupTypeModelImpl extends AbstractMutableBusinessModel implements GroupTypeModel
{
    public static final int     NEW_GROUP_TYPE = -1;
    public static final String  NEW_GROUP_TYPE_DESCRIPTION = "New Group Type";
    protected int               groupType;
    protected String            groupTypeDescription;
    protected boolean           exclusiveMembership;
    protected GroupTypeStruct   groupTypeStruct;
    public GroupTypeModelImpl(GroupTypeStruct groupTypeStruct)
    {
        this.groupTypeStruct = groupTypeStruct;
        initialize();
    }
    public GroupTypeModelImpl()
    {
        this.groupTypeStruct = new GroupTypeStruct();
        groupTypeStruct.exclusiveMembership = false;
        groupTypeStruct.groupType = NEW_GROUP_TYPE;
        groupTypeStruct.groupTypeDescription = NEW_GROUP_TYPE_DESCRIPTION;
        initialize();
    }

    private void initialize()
    {
        groupType = groupTypeStruct.groupType;
        exclusiveMembership = groupTypeStruct.exclusiveMembership;
        groupTypeDescription = new String(groupTypeStruct.groupTypeDescription);
    }

    public int getGroupType()
    {
        return groupType;
    }

    public void setGroupType(int groupType)
    {
        int oldType = this.groupType;
        this.groupType = groupType;
        firePropertyChange(TYPE_PROPERTY, oldType, this.groupType);
    }

    public String getGroupTypeDescription()
    {
        return groupTypeDescription;
    }

    public void setGroupTypeDescription(String groupTypeDescription)
    {
        String oldGroupTypeDescription = this.groupTypeDescription;
        this.groupTypeDescription = groupTypeDescription;
        firePropertyChange(DESCRIPTION_PROPERTY, oldGroupTypeDescription, this.groupTypeDescription);
    }

    public boolean isExclusiveMembership()
    {
        return exclusiveMembership;
    }

    public void setExclusiveMembership(boolean exclusiveMembership)
    {
        boolean oldExclusiveMembership = this.exclusiveMembership;
        this.exclusiveMembership = exclusiveMembership;
        firePropertyChange(EXCLUSIVE_MEMBERSHIP_PROPERTY, oldExclusiveMembership, this.exclusiveMembership);
    }

    public GroupTypeStruct getStruct()
    {
        GroupTypeStruct struct = new GroupTypeStruct();
        struct.exclusiveMembership = isExclusiveMembership();
        struct.groupType = getGroupType();
        struct.groupTypeDescription = new String(getGroupTypeDescription());
        return struct;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return new GroupTypeModelImpl(getStruct());
    }

    public int hashCode()
    {
        return this.groupType;
    }
}
