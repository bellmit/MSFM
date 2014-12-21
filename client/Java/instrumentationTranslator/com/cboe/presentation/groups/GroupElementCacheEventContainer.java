package com.cboe.presentation.groups;

public interface GroupElementCacheEventContainer
{
    GroupElementModel getParentGroupElementModel();

    GroupElementModel getChildGroupElementModel();

    void setParentGroupElementModel(GroupElementModel parentGroupElementModel);

    void setChildGroupElementModel(GroupElementModel childGroupElementModel);
}
