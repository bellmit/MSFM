package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistoryGroupImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 14, 2006 11:21:27 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.internalPresentation.product.GroupModel;
import com.cboe.interfaces.domain.dateTime.DateTime;

public class PSEHistoryGroupImpl extends AbstractProductStateEventHistory
{
    private GroupModel group;

    PSEHistoryGroupImpl(GroupModel group, DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        super(dateTime, status, description);
        this.group = group;
    }

    public String getSubject()
    {
        return group.getGroupName();
    }
}
