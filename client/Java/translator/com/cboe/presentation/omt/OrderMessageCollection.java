//
// -----------------------------------------------------------------------------------
// Source file: OrderMessageCollection.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.order.OrderIdRoutingStruct;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.presentation.omt.MessageCollectionListener;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.omt.OrderCancelReplaceMessageElement;
import com.cboe.interfaces.presentation.omt.OrderMessageElement;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderId;

import com.cboe.util.ChannelKey;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.order.OrderIdFactory;

import com.cboe.domain.util.RoutingGroupOrderCancelContainer;
import com.cboe.domain.util.RoutingGroupOrderCancelReplaceContainer;
import com.cboe.domain.util.RoutingGroupOrderIdStructSequenceContainer;
import com.cboe.domain.util.RoutingGroupOrderStructSequenceContainer;

public class OrderMessageCollection extends IECMessageCollection
{

    public OrderMessageCollection(Object eventProcessingLockObject)
    {
        super(eventProcessingLockObject);
    }

    protected int[] getChannelTypes()
    {
        return new int[]{
                ChannelKey.CB_OMT_ORDER_ACCEPTED,
                ChannelKey.CB_OMT_ORDER_CANCELED,
                ChannelKey.CB_OMT_ORDER_CANCEL_REPLACED,
                ChannelKey.CB_OMT_ORDER_REMOVED};
    }


    protected void processEvent(int channelType, Object eventData)
    {
        switch(channelType){
            case ChannelKey.CB_OMT_ORDER_ACCEPTED:
                RoutingGroupOrderStructSequenceContainer orderContainer =
                        (RoutingGroupOrderStructSequenceContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_ORDER_ACCEPTED", orderContainer.getRoutingParameterV2Struct(),
                           orderContainer.getOrderStructs());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_ORDER_CANCELED:
                RoutingGroupOrderCancelContainer cancelContainer = (RoutingGroupOrderCancelContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_ORDER_CANCELED", cancelContainer.getRoutingParameterV2Struct(),
                           cancelContainer.getCancelRoutingStructs());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_ORDER_CANCEL_REPLACED:
                RoutingGroupOrderCancelReplaceContainer cnclRepContainer = (RoutingGroupOrderCancelReplaceContainer) eventData;
                logMessage(processMethodLogName + "CB_OMT_ORDER_CANCEL_REPLACED", cnclRepContainer.getRoutingParameterV2Struct(),
                           cnclRepContainer.getCancelReplaceRoutingStructs());
                processBasicEvent(eventData);
                break;
            case ChannelKey.CB_OMT_ORDER_REMOVED:
                String orsids = processOrderRemoved(eventData);
                logMessage(processMethodLogName + "CB_OMT_ORDER_REMOVED", "ORSID:" + orsids, "");
                break;
            default:
                break;

        }
    }

    protected void addElement(MessageElement element)
    {
        boolean wasAdded = false;
        boolean wasUpdated = false;
        MessageElement messageElement = element;

        synchronized (eventProcessingLockObject)
        {
            OrderMessageElement orderMessageElement = findOrderElement(((Order)element).getOrderId());
            if (orderMessageElement == null)
            {
                wasAdded = elements.add(messageElement);
                if (element.getType().equals(MessageElement.MessageType.ORDER_ACCEPTED))
                {
                    processOrphanOrders((OrderMessageElement)messageElement);
                    ((OrderMessageElement)messageElement).setAllInfoMessageIndicators();
                }
            }
            else
            {
                switch (element.getType())
                {
                    case ORDER_ACCEPTED:
                        wasUpdated = replaceOrderElement((OrderMessageElement)element);
                        break;
                    case ORDER_CANCELED:
                    case ORDER_CANCEL_REPLACED:
                        wasUpdated = addCanceltoOrder(element, orderMessageElement);
                        messageElement = orderMessageElement;
                        break;
                    default:
                        break;
                }
            }
        }
        if (wasUpdated)
        {
            fireElementUpdated(messageElement);
        }
        else if (wasAdded)
        {
            fireElementAdded(messageElement);

        }
    }
    
    @Override
    protected void fireElementAdded(MessageElement element)
    {
    	super.fireElementAdded(element);
    	OMTMarketabilityWorker.getInstance().queueMessageElement(element);
    }

    /**
     * Update the marketability and the marketStruct of a MessageElement.
     * @param element containing the update marketability and marketStruct.
     */
    public void updateElement(MessageElement element){
    	MessageElement order = findMessageElementByOrsId(element.getOrsId());
    	//still a window to get the order updated while continuing.
    	if (order == element){
    		//The order is still the same so we can update it.
    		super.fireElementUpdated(element);
    	}
    }

    private MessageElement findMessageElementByOrsId(String orsId){
    	synchronized(eventProcessingLockObject){
    		for (MessageElement element : getAllMessageElements()){
    			if (element.getOrsId().equals(orsId)){
    				return element;
    			}
    		}
    	}
    	return null;
    }
    
    
    private String processOrderRemoved(Object eventData)
    {
        StringBuilder sb = new StringBuilder(20);
        RoutingGroupOrderIdStructSequenceContainer orderWrapper =
                (RoutingGroupOrderIdStructSequenceContainer) eventData;
        OrderIdRoutingStruct[] idStructs = orderWrapper.getOrderIdStructs();

        for(OrderIdRoutingStruct idStruct : idStructs)
        {
            //noinspection NonPrivateFieldAccessedInSynchronizedContext
            OrderId idObject = OrderIdFactory.createOrderId(idStruct.orderId);
            for(MessageElement element : elements)
            {
                if(MessageElement.MessageType.ORDER_ACCEPTED.equals(element.getType()))
                {
                    OrderMessageElement castedElement = (OrderMessageElement) element;
                    if(castedElement.getOrderId().equals(idObject))
                    {
                        sb.append(castedElement.getOrder().getDisplayOrsId());
                        removeMessageElement(castedElement);
                        break;
                    }
                }
                else if(MessageElement.MessageType.ORDER_CANCELED.equals(element.getType()))
                {
                    OrderCancelMessageElement castedElement = (OrderCancelMessageElement) element;
                    if(castedElement.getOrderId().equals(idObject))
                    {
                        sb.append(castedElement.getOrder().getDisplayOrsId());
                        removeMessageElement(castedElement);
                        break;
                    }
                }
                else if(MessageElement.MessageType.ORDER_CANCEL_REPLACED.equals(element.getType()))
                {
                    OrderMessageElement castedElement = (OrderMessageElement) element;
                    if(castedElement.getOrderId().equals(idObject))
                    {
                        sb.append(castedElement.getOrder().getDisplayOrsId());
                        removeMessageElement(castedElement);
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }

    private void processOrphanOrders(OrderMessageElement addedOrder)
    {
        List<MessageElement> list = new ArrayList<MessageElement>(1);

        if(addedOrder.getOrderId() != null)
        {
            for(MessageElement messageElement : elements)
            {
                if(messageElement.getType() == MessageElement.MessageType.ORDER_CANCELED ||
                   messageElement.getType() == MessageElement.MessageType.ORDER_CANCEL_REPLACED)
                {
                    if(addedOrder.getOrderId().equals(((Order) messageElement).getOrderId()))
                    {
                        list.add(messageElement);
                        addCanceltoOrder(messageElement, addedOrder);
                    }
                }
            }
            removeMessageElements(list);
        }
    }

    private boolean addCanceltoOrder(MessageElement element, OrderMessageElement orderMessageElement)
    {
        boolean added = false;
        if (element.getType() == MessageElement.MessageType.ORDER_CANCELED)
        {
            orderMessageElement.addCancelRequest((OrderCancelMessageElement)element);
            added = true;
        }
        else if (element.getType() == MessageElement.MessageType.ORDER_CANCEL_REPLACED)
        {
            orderMessageElement.addCancelReplaceRequest((OrderCancelReplaceMessageElement)element);
            added = true;
        }
        return added;
    }

    @SuppressWarnings({"BooleanMethodNameMustStartWithQuestion"})
    private boolean replaceOrderElement(OrderMessageElement orderMessageElement)
    {
        OrderMessageElement replacedElement = null;
        int index = elements.indexOf(orderMessageElement);

        if(index == -1)
        {
            reportElementNotFound(orderMessageElement);
        }
        else
        {
            replacedElement = (OrderMessageElement)elements.set(index, orderMessageElement);

            synchronized (replacedElement.getCancelLockObject())
            {
                orderMessageElement.copyPendingCancelOperations(replacedElement);
            }
            orderMessageElement.setInfoMessageIndicator(replacedElement.getInfoMessageIndicator());
            /*
             * the current market also updates the marketability
             */
            orderMessageElement.setCurrentMarket(replacedElement.getCurrentMarket());
        }
        
        //noinspection ConstantConditions
        return replacedElement != null;
    }

    private void reportElementNotFound(OrderMessageElement replacementElement)
    {
        StringBuilder sb = new StringBuilder("OMT replaceOrderElement - indexOf() is negative\n");
        OrderMessageElement originalOrder = findOrderElement(replacementElement.getOrderId());

        if(originalOrder != null)
        {
            sb.append("Session Name: Original Order = ").append(originalOrder.getSessionName())
                    .append("  Replacement Order = ").append(replacementElement.getSessionName())
                    .append('\n');
            sb.append("Product Key: Original Order = ").append(originalOrder.getProductKeyValue())
                    .append(" Replacment Order = ").append(replacementElement.getSessionName())
                    .append('\n');
            sb.append("Message ID: Original Order = ").append(originalOrder.getMessageId())
                    .append(" Replacment Order = ").append(replacementElement.getMessageId())
                    .append('\n');
        }
        else
        {
            sb.append("In OrderMessageCollection.reportElementNotFound() could not find original order by OrderId");
        }
        GUILoggerHome.find().audit(sb.toString());
    }

    private OrderMessageElement findOrderElement(OrderId orderId)
    {
        OrderMessageElement returnElement = null;
        if (orderId != null)
        {
            synchronized (eventProcessingLockObject)
            {
                for (MessageElement messageElement : elements)
                {
                    if (messageElement.getType() == MessageElement.MessageType.ORDER_ACCEPTED)
                    {
                        OrderMessageElement orderMessageElement = (OrderMessageElement)messageElement;
                        if (orderId.equals(orderMessageElement.getOrderId()))
                        {
                            returnElement = orderMessageElement;
                            break;
                        }
                    }
                }
            }
        }
        return returnElement;
    }

    public void initializeAutoCancel(MessageCollectionListener listener, boolean isAutoCancelOn)
    {
        synchronized (eventProcessingLockObject)
        {
            if (isAutoCancelOn)
            {
                MessageElement[] allElements = getAllMessageElements();
                for (MessageElement element : allElements)
                {
                    if (element.getType().equals(MessageElement.MessageType.ORDER_ACCEPTED))
                    {
                        try
                        {
                            ((OrderMessageElement)element).applyCancels();
                        }
                        catch (UserException e)
                        {
                            GUILoggerHome.find().exception(e);
                        }
                    }
                }
            }
            addListener(listener);
        }
    }


    public boolean removeMessageElement(MessageElement element)
    {
        boolean wasRemoved;

        synchronized (eventProcessingLockObject)
        {
            wasRemoved = super.removeMessageElement(element);
            if (wasRemoved)
            {
                /**
                 * Process any pending cancels and cancel replaces that might have been in the order element.
                 */
                if (element.getType().equals(MessageElement.MessageType.ORDER_ACCEPTED))
                {
                    OrderMessageElement orderElement = (OrderMessageElement)element;
                    if (orderElement.hasAnyPendingCancelOperations())
                    {
                        try
                        {
                            orderElement.applyCancels();
                        }
                        catch (DataValidationException dve)
                        {
                            GUILoggerHome.find().exception(dve);
                        }
                        catch (UserException e)
                        {
                            DefaultExceptionHandlerHome.find().process(e);
                        }
                    }
                }
            }
            return wasRemoved;
        }
    }

    /**
     * An info message has been received or removed at the info message collection. Update info message indicator
     * in corresponding order element if necessary.
     */
    public void infoMessageNotify(MessageElement infoElement)
    {
        //get all orders with same CBOE ID as the info message element passed in
        List<MessageElement> elementList = findElements(infoElement.getCboeId());

        //set info indicators in each order element
        for(MessageElement element : elementList)
        {
            if(((OrderMessageElement) element).setAllInfoMessageIndicators())
            {
                //the indicator in the element changed, so redraw the table.
                fireElementUpdated(element);
            }
        }
    }  

}
