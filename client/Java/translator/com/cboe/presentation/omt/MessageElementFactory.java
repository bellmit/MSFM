//
// -----------------------------------------------------------------------------------
// Source file: MessageElementFactory.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.util.ArrayList;
import java.util.List;

import com.cboe.domain.util.RoutingGroupCancelReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportDropCopyContainer;
import com.cboe.domain.util.RoutingGroupFillReportRejectContainer;
import com.cboe.domain.util.RoutingGroupLinkageCancelReportContainer;
import com.cboe.domain.util.RoutingGroupLinkageFillReportContainer;
import com.cboe.domain.util.RoutingGroupManualFillTimeoutContainer;
import com.cboe.domain.util.RoutingGroupManualOrderTimeoutContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelReplaceContainer;
import com.cboe.domain.util.RoutingGroupOrderStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupTradeNotificationContainer;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.order.CancelReplaceRoutingStruct;
import com.cboe.idl.order.CancelReportDropCopyRoutingStruct;
import com.cboe.idl.order.CancelReportRoutingStruct;
import com.cboe.idl.order.CancelRoutingStruct;
import com.cboe.idl.order.FillReportDropCopyRoutingStruct;
import com.cboe.idl.order.FillReportRejectRoutingStruct;
import com.cboe.idl.order.FilledReportRoutingStruct;
import com.cboe.idl.order.LinkageCancelReportRoutingStruct;
import com.cboe.idl.order.LinkageExtensionsStruct;
import com.cboe.idl.order.LinkageFillReportRoutingStruct;
import com.cboe.idl.order.ManualFillRoutingStruct;
import com.cboe.idl.order.ManualFillTimeoutRoutingStruct;
import com.cboe.idl.order.ManualOrderTimeoutRoutingStruct;
import com.cboe.idl.order.OrderRoutingStruct;
import com.cboe.idl.order.TradeNotificationRoutingStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.FillReportDropCopyMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement.MessageType;
import com.cboe.interfaces.presentation.order.OrderDetail;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.presentation.common.formatters.Sources;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.CustomKeys;
import com.cboe.presentation.api.APIHome;

import com.cboe.domain.util.*;
public class MessageElementFactory
{
    private static SessionProduct defaultProduct;

    private MessageElementFactory()
    {
    }

    public static FillReportDropCopyMessageElement createDropCopyNullImpl()
    {
        if(defaultProduct == null)
        {
            defaultProduct = APIHome.findProductQueryAPI().getDefaultSessionProduct();
        }
        RoutingParameterV2Struct routingParameterV2Struct = new RoutingParameterV2Struct((short)0, "", new String[0]);
        RouteReasonStruct routeReasonStruct = new RouteReasonStruct((short)0, "", 0, new DateWrapper().toDateTimeStruct());
        OrderStruct orderStruct = OrderStructBuilder.buildOrderStruct();
        orderStruct.productKey = defaultProduct.getProductKey();
        orderStruct.classKey = defaultProduct.getProductKeysStruct().classKey;
        orderStruct.activeSession = defaultProduct.getTradingSessionName();
        FilledReportStruct filledReportStruct = new FilledReportStruct();
        filledReportStruct.productKey = defaultProduct.getProductKey();

        filledReportStruct.tradeId = new CboeIdStruct();
        filledReportStruct.executingOrGiveUpFirm = new ExchangeFirmStruct("", "");

        filledReportStruct.userId = "";
        filledReportStruct.userAcronym = new ExchangeAcronymStruct("", "");
        filledReportStruct.sessionName = defaultProduct.getTradingSessionName();
        filledReportStruct.price= new PriceStruct(PriceTypes.VALUED, 0, 0);
        filledReportStruct.side = Sides.BUY;
        filledReportStruct.orsId = "";
        filledReportStruct.executingBroker = "";
        filledReportStruct.cmta = new ExchangeFirmStruct("", "");
        filledReportStruct.account = "";
        filledReportStruct.subaccount = "";
        filledReportStruct.originator = new ExchangeAcronymStruct("", "");
        filledReportStruct.optionalData = "";
        filledReportStruct.userAssignedId = "";
        filledReportStruct.extensions = "";
        filledReportStruct.contraParties = new ContraPartyStruct[0];
        filledReportStruct.timeSent = new DateWrapper().toDateTimeStruct();
//        filledReportStruct.positionEffect = ' ';

        FillReportDropCopyMessageElement retVal = new FillReportDropCopyMessageElementImpl(routingParameterV2Struct, routeReasonStruct, orderStruct, filledReportStruct);
        return retVal;
    }

    @SuppressWarnings({"InstanceofInterfaces", "OverlyLongMethod"})
    public static MessageElement[] createMessageElement(Object object)
    {
        MessageElement[] newElements;
        List<MessageElement> allElements;

        if(object instanceof RoutingGroupFillReportRejectContainer)
        {
            RoutingGroupFillReportRejectContainer castedObject = (RoutingGroupFillReportRejectContainer)object;
            FillReportRejectRoutingStruct[] fillReportRejects = castedObject.getFillReportRejects();
            allElements = new ArrayList<MessageElement>(5);

            for(FillReportRejectRoutingStruct rejectStruct : fillReportRejects)
            {
                for(ManualFillRoutingStruct manualFillStruct : rejectStruct.fillReports)
                {
                    allElements.add(new FillReportRejectMessageElementImpl(manualFillStruct,
                                                                           castedObject.getRoutingParameterV2Struct(),
                                                                           rejectStruct.cboeId,
                                                                           rejectStruct.rejectReason));
                }
            }

            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupLinkageCancelReportContainer)
        {
            RoutingGroupLinkageCancelReportContainer castedObject =
                    (RoutingGroupLinkageCancelReportContainer) object;
            LinkageCancelReportRoutingStruct[] linkageStructs = castedObject.getLinkageCancelReportStructs();
            RoutingParameterV2Struct routing = castedObject.getRoutingParameterV2Struct();
            allElements = new ArrayList<MessageElement>(5);

            for(LinkageCancelReportRoutingStruct linkageStruct : linkageStructs)
            {
                LinkageExtensionsStruct extension = linkageStruct.linkageExtensions;
                for(CancelReportRoutingStruct cancelReport : linkageStruct.cancelReports)
                {
                    allElements.add(new LinkageCancelReportMessageElementImpl(cancelReport,
                                                                      extension,
                                                                      routing));
                }
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupLinkageFillReportContainer)
        {
            RoutingGroupLinkageFillReportContainer castedObject =
                    (RoutingGroupLinkageFillReportContainer) object;
            LinkageFillReportRoutingStruct[] linkageStructs = castedObject.getFillReports();
            RoutingParameterV2Struct routing = castedObject.getRoutingParameterV2Struct();
            allElements = new ArrayList<MessageElement>(5);

            for(LinkageFillReportRoutingStruct linkageStruct : linkageStructs)
            {
                LinkageExtensionsStruct extension = linkageStruct.linkageExtensions;
                for(FilledReportRoutingStruct fillReport : linkageStruct.fillReports)
                {
                    allElements.add(new LinkageFillReportMessageElementImpl(fillReport,
                                                                              extension, routing));
                }
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupOrderStructSequenceContainer)
        {
            RoutingGroupOrderStructSequenceContainer castedObject =
                    (RoutingGroupOrderStructSequenceContainer)object;
            OrderRoutingStruct[] structs = castedObject.getOrderStructs();
            newElements = new MessageElement[structs.length];
            for(int i = 0; i < structs.length; i++)
            {
                newElements[i] = new OrderMessageElementImpl(structs[i], castedObject.getRoutingParameterV2Struct());
            }
        }
        else if(object instanceof RoutingGroupOrderCancelContainer)
        {
            RoutingGroupOrderCancelContainer castedObject =
                    (RoutingGroupOrderCancelContainer) object;
            CancelRoutingStruct[] structs = castedObject.getCancelRoutingStructs();
            newElements = new MessageElement[structs.length];
            for(int i = 0; i < structs.length; i++)
            {
                newElements[i] = new OrderCancelMessageElementImpl(structs[i],castedObject.getRoutingParameterV2Struct());
            }
        }
        else if(object instanceof RoutingGroupOrderCancelReplaceContainer)
        {
            RoutingGroupOrderCancelReplaceContainer castedObject =
                    (RoutingGroupOrderCancelReplaceContainer) object;
            CancelReplaceRoutingStruct[] structs = castedObject.getCancelReplaceRoutingStructs();
            newElements = new MessageElement[structs.length];
            for(int i = 0; i < structs.length; i++)
            {
                newElements[i] = new OrderCancelReplaceMessageElementImpl(structs[i],castedObject.getRoutingParameterV2Struct());
            }
        }
        else if(object instanceof RoutingGroupTradeNotificationContainer)
        {
            RoutingGroupTradeNotificationContainer castedObject =
                    (RoutingGroupTradeNotificationContainer) object;
            TradeNotificationRoutingStruct[] structs = castedObject.getTradeNotifications();
            RoutingParameterV2Struct routing = castedObject.getRoutingParameterV2Struct();
            newElements = new MessageElement[structs.length];
            for(int i = 0; i < structs.length; i++)
            {
                newElements[i] = new TradeNotificationMessageElementImpl(structs[i].tradeNotification , 
                                                                         structs[i].routeReasonStruct,
                                                                         routing);
            }
        }
        else if(object instanceof RoutingGroupCancelReportDropCopyContainer)
        {
            RoutingGroupCancelReportDropCopyContainer castedObject =
                    (RoutingGroupCancelReportDropCopyContainer) object;
            CancelReportDropCopyRoutingStruct[] cancelReportDropCopies = castedObject.getCancelRoprtDropCopies();
            allElements = new ArrayList<MessageElement>(5);

            for(CancelReportDropCopyRoutingStruct dropCopyRoutingStruct : cancelReportDropCopies)
            {
                allElements.add(new CancelReportDropCopyMessageElementImpl(
                                     castedObject.getRoutingParameterV2Struct(),
                                     dropCopyRoutingStruct.routeReasonStruct,
                                     dropCopyRoutingStruct.orderStruct,
                                     dropCopyRoutingStruct.cancelReport));
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupFillReportDropCopyContainer)
        {
            RoutingGroupFillReportDropCopyContainer castedObject =
                    (RoutingGroupFillReportDropCopyContainer) object;
            FillReportDropCopyRoutingStruct[] fillReportDropCopies = castedObject.getFillReportDropCopies();
            allElements = new ArrayList<MessageElement>(5);

            for(FillReportDropCopyRoutingStruct struct : fillReportDropCopies)
            {
                allElements.add(new FillReportDropCopyMessageElementImpl(
                                castedObject.getRoutingParameterV2Struct(),
                                struct.routeReasonStruct,
                                struct.orderStruct,
                                struct.fillReport));
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupManualOrderTimeoutContainer)
        {
            RoutingGroupManualOrderTimeoutContainer castedObject =
                    (RoutingGroupManualOrderTimeoutContainer) object;
            ManualOrderTimeoutRoutingStruct[] orderTimeouts = castedObject.getManualOrderTimeouts();
            allElements = new ArrayList<MessageElement>(5);

            for(ManualOrderTimeoutRoutingStruct struct : orderTimeouts)
            {
                allElements.add(new ManualOrderTimeoutMessageElementImpl(
                        castedObject.getRoutingParameterV2Struct(), struct));
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof RoutingGroupManualFillTimeoutContainer)
        {
            RoutingGroupManualFillTimeoutContainer castedObject =
                    (RoutingGroupManualFillTimeoutContainer) object;
            ManualFillTimeoutRoutingStruct[] fillTimeouts = castedObject.getManualFillTimeouts();
            allElements = new ArrayList<MessageElement>(5);

            for(ManualFillTimeoutRoutingStruct struct : fillTimeouts)
            {
                allElements.add(new ManualFillTimeoutMessageElementImpl(
                        castedObject.getRoutingParameterV2Struct(), struct.manualFillRouteMsg,
                        struct.timeoutRequestType));
            }
            newElements = allElements.toArray(new MessageElement[allElements.size()]);
        }
        else if(object instanceof OrderDetail[])
        {
            OrderDetail[] castedArray =(OrderDetail[]) object;
            newElements = new MessageElement[castedArray.length];
            for(int i = 0; i < castedArray.length; i++)
            {
                OrderStruct order = castedArray[i].getStruct().orderStruct;
                MessageType type = order.source == Sources.LIGHT ? MessageType.LIGHT_ORDER : MessageType.ORDER_QUERY;
                newElements[i] = new OrderMessageElementImpl(order, null, type);
            }
        }
        else if(object instanceof OrderStruct)
        {
        	OrderStruct order = (OrderStruct) object;
        	MessageType type = order.source == Sources.LIGHT ? MessageType.LIGHT_ORDER : MessageType.SINGLE_ORDER_QUERY;
        	newElements = new MessageElement[1];
			newElements[0] = new OrderMessageElementImpl(order, null, type);
        }
        else if(object instanceof String)
        {
            newElements = new MessageElement[1];
            newElements[0] = new InfoMessageElementImpl((String) object);
        }
        else
        {
            GUILoggerHome.find().alarm("MessageElementFactory.createMessageElement",
                                       "Unknown element type.");
            newElements = new MessageElement[1];
            newElements[0] = new InfoMessageElementImpl("Unknown Element Type");
        }

        return newElements;
    }
}
