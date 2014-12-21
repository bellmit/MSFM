//
// -----------------------------------------------------------------------------------
// Source file: DropCopyMessageCollection.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.util.ChannelKey;

import com.cboe.domain.util.RoutingGroupCancelReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportDropCopyContainer;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;

import java.util.List;
import java.util.ArrayList;

public class DropCopyMessageCollection extends IECMessageCollection
{
    public DropCopyMessageCollection(Object processEventLockObject)
    {
        super(processEventLockObject);
    }
    
    protected int[] getChannelTypes()
    {
        return new int[]{
                ChannelKey.CB_OMT_FILL_REPORT_DROP_COPY,
                ChannelKey.CB_OMT_CANCEL_REPORT_DROP_COPY,
                ChannelKey.CB_OMT_REMOVE_MESSAGE};
    }

    protected void processEvent(int channelType, Object eventData)
    {
        switch(channelType){
            case ChannelKey.CB_OMT_FILL_REPORT_DROP_COPY:
                RoutingGroupFillReportDropCopyContainer fillContainer =
                        (RoutingGroupFillReportDropCopyContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_FILL_REPORT_DROP_COPY",
                        getLoggingStructs(fillContainer));
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_CANCEL_REPORT_DROP_COPY:
                RoutingGroupCancelReportDropCopyContainer cancelContainer =
                        (RoutingGroupCancelReportDropCopyContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_CANCEL_REPORT_DROP_COPY",
                           cancelContainer.getRoutingParameterV2Struct(),
                           cancelContainer.getCancelRoprtDropCopies());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_REMOVE_MESSAGE:
                Long msgId = (Long) eventData;
                logMessage(processMethodLogName + "CB_OMT_REMOVE_MESSAGE", msgId, "");
                processMessageRemoved(eventData);
                break;
        }
    }

    private Object[] getLoggingStructs(RoutingGroupFillReportDropCopyContainer fillContainer)
    {
        FillReportDropCopyRoutingStruct[] dropCopies = fillContainer.getFillReportDropCopies();
        List<Object> retVal = new ArrayList<Object>(1 + dropCopies.length*2);
        retVal.add(fillContainer.getRoutingParameterV2Struct());
        for(FillReportDropCopyRoutingStruct s : dropCopies)
        {
            retVal.add(s.routeReasonStruct);
            retVal.add(s.fillReport);
        }
        return retVal.toArray(new Object[retVal.size()]);
    }
}
