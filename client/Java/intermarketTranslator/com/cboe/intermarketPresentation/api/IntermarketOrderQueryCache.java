package com.cboe.intermarketPresentation.api;


import java.util.*;

import com.cboe.domain.util.*;
import com.cboe.util.event.*;
import com.cboe.util.channel.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.interfaces.domain.*;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;

/**
 *  This class caches order information for use by SBT client
 *  The user of this class will have to call publishAllOrders
 *
 *
 *  @author Jing Chen
 */

public class IntermarketOrderQueryCache implements EventChannelListener {
    private Map heldOrdersByIdMap;
    private Map heldOrdersBySessionClassMap;
    private Map heldOrdersByIdCrossReferenceMap;

    private HeldOrderDetailStruct[] emptyHeldOrderSequence;
    private final String Category = this.getClass().getName();

/**
 * OrderQueryCache constructor.
 */
public IntermarketOrderQueryCache() {
    super();
    initialize();
}
/**
 * Adds/Updates cache maps by orders passed in
 * @param orders com.cboe.idl.cmiOrder.OrderDetailStruct[]
 */
private void addOrders(HeldOrderDetailStruct[] heldOrders) {
    for ( int i = 0; i < heldOrders.length; i++ )
    {
        addUpdateOrder(heldOrders[i]);
    }
}

/**
 * Adds/Updates cache maps with the OrderDetailStruct
 *
 * @param orderStruct com.cboe.idl.cmiOrder.OrderDetailStruct
 */
private synchronized void addUpdateOrder(HeldOrderDetailStruct heldOrderDetailStruct) {
    //Add/Update
    // Only add has to be syncronized, so we are using synchronized update methods for each map
    updateOrderById(heldOrderDetailStruct.heldOrder.order.orderId, heldOrderDetailStruct);

    CompleteOrderIdStructContainer orderIdContainer = getValidOrderIdStructContainer(heldOrderDetailStruct.heldOrder.order.orderId);

    updateOrderBySessionClass(heldOrderDetailStruct.heldOrder.order.activeSession,heldOrderDetailStruct.heldOrder.order.classKey,orderIdContainer,heldOrderDetailStruct);
}

/**
 * Updates orderByIdMap with the passed in order
 * @param orderId com.cboe.application.order.OrderIdStructContainer
 * @param orderDetail com.cboe.idl.cmiOrder.OrderDetailStruct
 */
private synchronized void updateOrderById(OrderIdStruct orderIdStruct, HeldOrderDetailStruct orderDetail) {
    CompleteOrderIdStructContainer  orderId = OrderIdStructContainerFactory.createCompleteOrderIdStructContainer(orderIdStruct);
    CmiOrderIdStructContainer       cmiId   = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(orderIdStruct);
    CboeOrderIdStructContainer      cboeId  = OrderIdStructContainerFactory.createCboeOrderIdStructContainer(orderIdStruct);

    heldOrdersByIdMap.put(orderId, orderDetail);
    // create xref keys...
    heldOrdersByIdCrossReferenceMap.put(cmiId, orderId);
    heldOrdersByIdCrossReferenceMap.put(cboeId, orderId);
}

private synchronized void updateOrderBySessionClass(String sessionName, int classKey, CompleteOrderIdStructContainer orderId, HeldOrderDetailStruct heldOrderDetail)
{
    SessionKeyContainer sessionClass = new SessionKeyContainer(sessionName, classKey);
    Map heldOrdersBySessionClassById = (Map) heldOrdersBySessionClassMap.get(sessionClass);

    //If ordersByProductTypeMap does not have a map for this class, create it and add it into the map
    if (heldOrdersBySessionClassById == null)
    {
        heldOrdersBySessionClassById = new HashMap();
        heldOrdersBySessionClassMap.put(sessionClass, heldOrdersBySessionClassById);
    }
    heldOrdersBySessionClassById.put(orderId, heldOrderDetail);
}

/**
 * This method updates the Order Cache with the given ChannelEvent.
 * @param event EventChannelEvent
 */
public void channelUpdate(ChannelEvent event)
{
    int channelType = ((ChannelKey) event.getChannel()).channelType;
    Object eventData = event.getEventData();
    if (channelType == ChannelType.CB_HELD_ORDERS)
    {
        addOrders((HeldOrderDetailStruct[])eventData);
    }
    else
    {
        GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.ORDER_QUERY, "Unexpected ChannelType = " + channelType);
    }
}

public HeldOrderDetailStruct[] getOrdersForSessionClass(String sessionName, int classKey)
{
    SessionKeyContainer sessionClass = new SessionKeyContainer(sessionName, classKey);
    synchronized(heldOrdersBySessionClassMap)
    {
        Map heldOrders = (Map)heldOrdersBySessionClassMap.get(sessionClass);
        if ( heldOrders == null || heldOrders.isEmpty() )
        {
            return getEmptyOrderSequence();
        }
        return (HeldOrderDetailStruct[])heldOrders.values().toArray(getEmptyOrderSequence());
    }

}

/**
 * This method was created in VisualAge.
 * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
 */
private HeldOrderDetailStruct[] getEmptyOrderSequence() {
    if ( emptyHeldOrderSequence == null )
    {
        emptyHeldOrderSequence = new HeldOrderDetailStruct[0];
    }
    return emptyHeldOrderSequence;
}

public void addOrder(HeldOrderDetailStruct order) {
    addUpdateOrder(order);
}

/**
 * Retrieves orderDetailStruct from Order Cache.
 * @return com.cboe.idl.cmiOrder.OrderDetailStruct
 * @param orderId OrderIdStruct
 */
public HeldOrderDetailStruct getHeldOrderById(OrderIdStruct orderId) throws NotFoundException
{
    HeldOrderDetailStruct retStruct;
    CompleteOrderIdStructContainer idStructContainer = getValidOrderIdStructContainer(orderId);
    retStruct = (HeldOrderDetailStruct)heldOrdersByIdMap.get(idStructContainer);

    return retStruct;
}

/**
 * Initializes the internal data structures
 */
private void initialize() {
    heldOrdersByIdMap = new HashMap();
    heldOrdersBySessionClassMap = new HashMap();
    heldOrdersByIdCrossReferenceMap = new HashMap();
    emptyHeldOrderSequence = new HeldOrderDetailStruct[0];
}
/**
 * Updates cache maps with the passed in Order.
 *
 * @param orderStruct com.cboe.idl.cmiOrder.OrderDetailStruct
 */
private void updateOrder(HeldOrderDetailStruct heldOrderDetailStruct) {
    CompleteOrderIdStructContainer orderIdContainer = getValidOrderIdStructContainer(heldOrderDetailStruct.heldOrder.order.orderId);

    updateOrder(orderIdContainer, heldOrderDetailStruct);
}
/**
 * Updates cache maps with the passed in Order.
 * @param orderId com.cboe.util.OrderIdStructContainer
 * @param orderDetailStruct com.cboe.idl.cmiOrder.OrderDetailStruct
 */
private synchronized void updateOrder(CompleteOrderIdStructContainer orderIdContainer, HeldOrderDetailStruct heldOrderDetailStruct) {
    // Update all 4 hashtables
    // 1) Update ordersByIdMap and ordersByIdCrossReferenceMap

    heldOrdersByIdMap.put(orderIdContainer, heldOrderDetailStruct);

    SessionKeyContainer sessionClass = new SessionKeyContainer(heldOrderDetailStruct.heldOrder.order.activeSession, heldOrderDetailStruct.heldOrder.order.classKey);
    Map heldOrdersBySessionById = (Map)heldOrdersBySessionClassMap.get(sessionClass);

     if ( heldOrdersBySessionById == null )
    {
        heldOrdersBySessionById = new HashMap();
        heldOrdersBySessionClassMap.put(sessionClass, heldOrdersBySessionById);
    }
    heldOrdersBySessionById.put(orderIdContainer, heldOrderDetailStruct);
}

/**
 * This method creates a valid base OrderIdStructContainer ( Cmi or Cboe ) for use
 * to lookup against the x-ref hashtable and retrieve a CompleteOrderIdStructContainer
 */
private CompleteOrderIdStructContainer getValidOrderIdStructContainer( OrderIdStruct   orderId )
{
    CompleteOrderIdStructContainer  aOrderId        = null;
    // get the "most" valid xref hashtable key
    BaseOrderIdStructContainer      baseOrderIndex  = OrderIdStructContainerFactory.createValidOrderIdStructContainer( orderId );

    aOrderId = (CompleteOrderIdStructContainer)heldOrdersByIdCrossReferenceMap.get( baseOrderIndex );
    return aOrderId;
}


}

