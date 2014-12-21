//
// -----------------------------------------------------------------------------------
// Source file: RoutingGroupRemoveMessageContainer.java
//
// PACKAGE: com.cboe.domain.util;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.util.RoutingParameterV2Struct;

public class RoutingGroupRemoveMessageContainer extends RoutingGroupV2Container
{
    private long msgId;

    @SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
    public RoutingGroupRemoveMessageContainer(RoutingParameterV2Struct routingParameterV2Struct,
                                              long msgId)
    {
        super(routingParameterV2Struct);
        this.msgId =msgId;
    }

    public long getMsgId()
    {
        return msgId;
    }
}
