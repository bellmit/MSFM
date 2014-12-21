//
// -----------------------------------------------------------------------------------
// Source file: InfoMessageCollection.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.interfaces.presentation.omt.MessageElement;

import com.cboe.util.ChannelKey;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

import com.cboe.domain.util.RoutingGroupFillReportRejectContainer;
import com.cboe.domain.util.RoutingGroupManualOrderTimeoutContainer;
import com.cboe.domain.util.RoutingGroupManualFillTimeoutContainer;

public class InfoMessageCollection extends IECMessageCollection
{
    public InfoMessageCollection(Object processEventLockObject)
    {
        super(processEventLockObject);
    }
    
    protected int[] getChannelTypes()
    {
        return new int[]{ChannelKey.CB_OMT_FILL_REPORT_REJECT,
                         ChannelKey.CB_OMT_LINKAGE_CANCEL_REPORT,
                         ChannelKey.CB_OMT_LINKAGE_FILL_REPORT,
                         ChannelKey.CB_OMT_REMOVE_MESSAGE,
                         ChannelKey.CB_OMT_TRADE_NOTIFICATION,
                         ChannelKey.CB_OMT_MANUAL_ORDER_TIMEOUT,
                         ChannelKey.CB_OMT_MANUAL_FILL_TIMEOUT};
    }

    

    protected void processEvent(int channelType, Object eventData)
    {
        switch(channelType){
            case ChannelKey.CB_OMT_FILL_REPORT_REJECT:
                RoutingGroupFillReportRejectContainer fillRejectContainer = (RoutingGroupFillReportRejectContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_FILL_REPORT_REJECT", fillRejectContainer.getRoutingParameterV2Struct(),
                           fillRejectContainer.getFillReportRejects());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_LINKAGE_CANCEL_REPORT:
                GUILoggerHome.find().debug(processMethodLogName + "CB_OMT_LINKAGE_CANCEL_REPORT",
                                           GUILoggerBusinessProperty.OMT, "");
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_LINKAGE_FILL_REPORT:
                GUILoggerHome.find().debug(processMethodLogName + "CB_OMT_LINKAGE_FILL_REPORT",
                                           GUILoggerBusinessProperty.OMT, "");
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_REMOVE_MESSAGE:
                GUILoggerHome.find().debug(processMethodLogName + "CB_OMT_REMOVE_MESSAGE",
                                           GUILoggerBusinessProperty.OMT, "");
                processMessageRemoved(eventData);
                break;
            case ChannelKey.CB_OMT_TRADE_NOTIFICATION:
                GUILoggerHome.find().debug(processMethodLogName + "CB_OMT_TRADE_NOTIFICATION",
                                           GUILoggerBusinessProperty.OMT, "");
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_MANUAL_ORDER_TIMEOUT:
                RoutingGroupManualOrderTimeoutContainer orderTimeoutContainer = (RoutingGroupManualOrderTimeoutContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_MANUAL_ORDER_TIMEOUT",
                           orderTimeoutContainer.getRoutingParameterV2Struct(),
                           orderTimeoutContainer.getManualOrderTimeouts());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_MANUAL_FILL_TIMEOUT:
                RoutingGroupManualFillTimeoutContainer fillTimeoutContainer = (RoutingGroupManualFillTimeoutContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_MANUAL_FILL_TIMEOUT", fillTimeoutContainer.getRoutingParameterV2Struct(),
                           fillTimeoutContainer.getManualFillTimeouts());
                processBasicEvent(eventData);
                break;
            default:
                break;
        }
    }

    public boolean removeMessageElement(MessageElement element){
        boolean result = super.removeMessageElement(element);
        ((OrderMessageCollection) MessageCollectionFactory.getOrderMessagesCollection())
                .infoMessageNotify(element);
        return result;
    }

    protected void addElement(MessageElement element)
    {
        super.addElement(element);
        ((OrderMessageCollection) MessageCollectionFactory.getOrderMessagesCollection()).infoMessageNotify(element);
    }

}
