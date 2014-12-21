package com.cboe.presentation.api;

//------------------------------------------------------------------------------------------------------------------
// FILE:    OrderQueryCacheProxy.java
//
// PACKAGE:    com.cboe.presentation.api
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------

// Imports
// java packages

import java.util.*;

import com.cboe.domain.util.CboeOrderIdStructContainer;
import com.cboe.domain.util.CmiOrderIdStructContainer;
import com.cboe.domain.util.OrderIdStructContainerFactory;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.common.comparators.DateTimeComparator;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;

/**
 * This class caches order information for use by SBT client The user of this class will have to
 * call publishAllOrders
 * 
 * 
 * @author Alex Brazhnichenko
 * @version 06/29/1999
 */

public class OrderQueryCacheProxy implements EventChannelListener
{
    private Map<CboeOrderIdStructContainer, OrderDetailStruct> ordersByIdMap;
    private Map<CmiOrderIdStructContainer, CboeOrderIdStructContainer> ordersByIdCrossReferenceMap;
    private Map<Integer, Map<CboeOrderIdStructContainer, OrderDetailStruct>> ordersByClassMap;
    private Map<Integer, Map<CboeOrderIdStructContainer, OrderDetailStruct>> ordersByProductMap;
    private Map<Short, Map<CboeOrderIdStructContainer, OrderDetailStruct>> ordersByProductTypeMap;
    private Map<String, Map<CboeOrderIdStructContainer, OrderDetailStruct>> ordersBySession;

    private OrderDetailStruct[] emptyOrderSequence;
    private final String Category = this.getClass().getName();

    private OrdersByTimeComparator ordersByTimeComp = new OrdersByTimeComparator();

    /**
     * OrderQueryCacheProxy constructor.
     */
    public OrderQueryCacheProxy()
    {
        initialize();
    }

    /**
     * Adds/Updates cache maps by orders passed in
     * 
     * @param orders
     *            com.cboe.idl.cmiOrder.OrderDetailStruct[]
     */
    private void addOrders(OrderDetailStruct[] orders)
    {
        for (int i = 0; i < orders.length; i++)
        {
            addUpdateOrder(orders[i]);
        }
    }

    /**
     * Adds/Updates cache maps with the OrderDetailStruct
     * 
     * @param orderStruct
     *            com.cboe.idl.cmiOrder.OrderDetailStruct
     */
    private synchronized void addUpdateOrder(OrderDetailStruct orderDetailStruct)
    {
        // Add/Update
        // Only add has to be synchronized, so we are using synchronized update methods for each map
        updateOrderById(orderDetailStruct.orderStruct.orderId, orderDetailStruct);

        CboeOrderIdStructContainer orderIdContainer = getValidOrderIdStructContainer(orderDetailStruct.orderStruct.orderId);

        updateOrderByClass(orderDetailStruct.orderStruct.classKey, orderIdContainer, orderDetailStruct);

        updateOrderByProduct(orderDetailStruct.orderStruct.productKey, orderIdContainer, orderDetailStruct);

        updateOrderByProductType(orderDetailStruct.orderStruct.productType, orderIdContainer, orderDetailStruct);

        updateOrderBySession(orderDetailStruct.orderStruct.activeSession, orderIdContainer, orderDetailStruct);

    }

    /**
     * This method updates the Order Cache with the given ChannelEvent.
     * 
     * @param event
     *            EventChannelEvent
     */
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch (channelType)
        {
            case ChannelType.CB_ALL_ORDERS:
                addOrders((OrderDetailStruct[]) eventData);
                break;
            default:
                GUILoggerHome.find().debug(Category + ".channelUpdate()", GUILoggerBusinessProperty.ORDER_QUERY,
                        "Unexpected ChannelType = " + channelType);
        }
    }

    /**
     * This method gets all available orders from the order cache
     * 
     * @return OrderDetailStruct[]
     */
    public OrderDetailStruct[] getAllOrders()
    {
        synchronized (ordersByIdMap)
        {
            return ordersByIdMap.values().toArray(getEmptyOrderSequence());
        }
    }

    public List<OrderDetailStruct> getOrders(SessionProduct product, int quantity, Price price, char side, int millis)
    {
        List<OrderDetailStruct> retVal = new ArrayList<OrderDetailStruct>();
        long now = new Date().getTime();
        // get all orders for the product
        OrderDetailStruct[] orders = getOrdersForProduct(product.getProductKey());
        if (orders != null && orders.length > 0)
        {
            // sort the orders most recent -> oldest
            Arrays.sort(orders, ordersByTimeComp);
            int i = 0;
            // iterate through the orders until we reach one that's older than the time range
            while (i < orders.length &&
                    now - DateTimeFactory.getDateTime(orders[i].orderStruct.receivedTime).getTimeInMillis() <= millis)
            {
                int orderQty = orders[i].orderStruct.originalQuantity;
                Price orderPrice = DisplayPriceFactory.create(orders[i].orderStruct.price);
                char orderSide = orders[i].orderStruct.side;

                // check if the order's qty, price and side are equal to the params
                if (orderQty == quantity && orderPrice.equals(price) && orderSide == side)
                {
                    retVal.add(orders[i]);
                }
                i++;
            }
        }
        return retVal;
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
     * @param productType
     *            short
     */
    public OrderDetailStruct[] getAllOrdersForType(short productType)
    {
        synchronized (ordersByProductTypeMap)
        {
            Map<CboeOrderIdStructContainer, OrderDetailStruct> orders = ordersByProductTypeMap.get(productType);
            if (orders == null || orders.isEmpty())
            {
                return getEmptyOrderSequence();
            }
            return orders.values().toArray(getEmptyOrderSequence());
        }
    }

    public OrderDetailStruct[] getOrdersForSession(String sessionName)
    {
        synchronized (ordersBySession)
        {
            Map<CboeOrderIdStructContainer, OrderDetailStruct> orders = ordersBySession.get(sessionName);
            if (orders == null || orders.isEmpty())
            {
                return getEmptyOrderSequence();
            }
            return orders.values().toArray(getEmptyOrderSequence());
        }
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
     */
    private OrderDetailStruct[] getEmptyOrderSequence()
    {
        if (emptyOrderSequence == null)
        {
            emptyOrderSequence = new OrderDetailStruct[0];
        }
        return emptyOrderSequence;
    }

    public void addOrder(OrderDetailStruct order)
    {
        addUpdateOrder(order);
    }

    /**
     * Retrieves orderDetailStruct from Order Cache.
     * 
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct
     * @param orderId
     *            OrderIdStruct
     */
    public OrderDetailStruct getOrderById(OrderIdStruct orderId) throws NotFoundException
    {
        OrderDetailStruct retStruct;
        CboeOrderIdStructContainer idStructContainer = getValidOrderIdStructContainer(orderId);
        synchronized (ordersByIdMap)
        {
            retStruct = ordersByIdMap.get(idStructContainer);
        }

        return retStruct;
    }

    /**
     * Returns orders for a given class.
     * 
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
     * @param classKey
     *            int
     */
    public OrderDetailStruct[] getOrdersByClass(int classKey)
    {
        synchronized (ordersByClassMap)
        {
            Map<CboeOrderIdStructContainer, OrderDetailStruct> orders = ordersByClassMap.get(new Integer(classKey));
            if (orders == null || orders.isEmpty())
            {
                return getEmptyOrderSequence();
            }
            return orders.values().toArray(getEmptyOrderSequence());
        }
    }

    /**
     * Returns orders for a given product.
     * 
     * @return com.cboe.idl.cmiOrder.OrderDetailStruct[]
     * @param productKey
     *            int
     */
    public OrderDetailStruct[] getOrdersForProduct(int productKey)
    {
        synchronized (ordersByProductMap)
        {
            Map<CboeOrderIdStructContainer, OrderDetailStruct> orders = ordersByProductMap.get(new Integer(productKey));
            if (orders == null || orders.isEmpty())
            {
                return getEmptyOrderSequence();
            }
            return orders.values().toArray(getEmptyOrderSequence());
        }
    }

    /**
     * Initializes the internal data structures
     */
    private void initialize()
    {
        ordersByIdMap = new HashMap<CboeOrderIdStructContainer, OrderDetailStruct>();
        ordersByIdCrossReferenceMap = new HashMap<CmiOrderIdStructContainer, CboeOrderIdStructContainer>();
        ordersByClassMap = new HashMap<Integer, Map<CboeOrderIdStructContainer, OrderDetailStruct>>();
        ordersByProductMap = new HashMap<Integer, Map<CboeOrderIdStructContainer, OrderDetailStruct>>();
        ordersByProductTypeMap = new HashMap<Short, Map<CboeOrderIdStructContainer, OrderDetailStruct>>();
        ordersBySession = new HashMap<String, Map<CboeOrderIdStructContainer, OrderDetailStruct>>();

        emptyOrderSequence = new OrderDetailStruct[0];
    }

    /**
     * Updates ordersByClassMap with the passed in order.
     * 
     * @param classKey
     *            java.lang.Integer
     * @param orderId
     *            com.cboe.application.order.OrderIdStructContainer
     * @param orderDetail
     *            com.cboe.idl.cmiOrder.OrderDetailStruct
     */
    private synchronized void updateOrderByClass(Integer classKey, CboeOrderIdStructContainer orderId, OrderDetailStruct orderDetail)
    {
        Map<CboeOrderIdStructContainer, OrderDetailStruct> ordersByClassById = ordersByClassMap.get(classKey);
        if (ordersByClassById == null)
        {
            ordersByClassById = new HashMap<CboeOrderIdStructContainer, OrderDetailStruct>();
            ordersByClassMap.put(classKey, ordersByClassById);
        }
        ordersByClassById.put(orderId, orderDetail);
    }

    /**
     * Updates orderByIdMap with the passed in order
     * 
     * @param orderId
     *            com.cboe.application.order.OrderIdStructContainer
     * @param orderDetail
     *            com.cboe.idl.cmiOrder.OrderDetailStruct
     */
    private synchronized void updateOrderById(OrderIdStruct orderIdStruct, OrderDetailStruct orderDetail)
    {
        CboeOrderIdStructContainer cboeId = OrderIdStructContainerFactory.createCboeOrderIdStructContainer(orderIdStruct);
        CmiOrderIdStructContainer cmiId = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(orderIdStruct);

        ordersByIdMap.put(cboeId, orderDetail);

        // create xref keys...
        ordersByIdCrossReferenceMap.put(cmiId, cboeId);
    }

    /**
     * Updates orderByProductMap with the passed in order.
     * 
     * @param productKey
     *            java.lang.Integer
     * @param orderId
     *            com.cboe.application.order.OrderIdStructContainer
     * @param orderDetail
     *            com.cboe.idl.cmiOrder.OrderDetailStruct
     */
    private synchronized void updateOrderByProduct(Integer productKey, CboeOrderIdStructContainer orderId, OrderDetailStruct orderDetail)
    {
        Map<CboeOrderIdStructContainer, OrderDetailStruct> ordersByProductById = ordersByProductMap.get(productKey);

        if (ordersByProductById == null)
        {
            ordersByProductById = new HashMap<CboeOrderIdStructContainer, OrderDetailStruct>();
            ordersByProductMap.put(productKey, ordersByProductById);
        }
        ordersByProductById.put(orderId, orderDetail);
    }

    /**
     * Updates ordersByProductTypeMap with the passed in order.
     * 
     * @param productKey
     *            java.lang.Integer
     * @param orderId
     *            com.cboe.application.order.OrderIdStructContainer
     * @param orderDetail
     *            com.cboe.idl.cmiOrder.OrderDetailStruct
     */
    private synchronized void updateOrderByProductType(short productType, CboeOrderIdStructContainer orderId, OrderDetailStruct orderDetail)
    {
        Map<CboeOrderIdStructContainer, OrderDetailStruct> ordersByProductTypeById = ordersByProductTypeMap.get(productType);

        if (ordersByProductTypeById == null)
        {
            ordersByProductTypeById =  new HashMap<CboeOrderIdStructContainer, OrderDetailStruct>();
            ordersByProductTypeMap.put(productType, ordersByProductTypeById);
        }
        ordersByProductTypeById.put(orderId, orderDetail);
    }

    private synchronized void updateOrderBySession(String sessionName, CboeOrderIdStructContainer orderId, OrderDetailStruct orderDetail)
    {
        Map<CboeOrderIdStructContainer, OrderDetailStruct> ordersBySessionById = ordersBySession.get(sessionName);

        if (ordersBySessionById == null)
        {
            ordersBySessionById =  new HashMap<CboeOrderIdStructContainer, OrderDetailStruct>();
            ordersBySession.put(sessionName, ordersBySessionById);
        }
        ordersBySessionById.put(orderId, orderDetail);
    }

    /**
     * This method creates a valid base OrderIdStructContainer ( Cmi or Cboe ) for use to lookup
     * against the x-ref hashtable and retrieve a CboeOrderIdStructContainer
     */
    private CboeOrderIdStructContainer getValidOrderIdStructContainer(OrderIdStruct orderId)
    {
        if (orderId.highCboeId == 0 && orderId.lowCboeId == 0)
        {
            CmiOrderIdStructContainer cmiOrderIdStructContainer = OrderIdStructContainerFactory.createCmiOrderIdStructContainer(orderId);
            synchronized (ordersByIdCrossReferenceMap)
            {
                return ordersByIdCrossReferenceMap.get(cmiOrderIdStructContainer);
            }
        }
        else
        {
            return OrderIdStructContainerFactory.createCboeOrderIdStructContainer(orderId);
        }
    }

    // sort OrderDetailStructs by their receivedTimes
    class OrdersByTimeComparator implements Comparator<OrderDetailStruct>
    {
        private DateTimeComparator dtComp = new DateTimeComparator();

        public int compare(OrderDetailStruct o1, OrderDetailStruct o2)
        {
            DateTime dt1 = DateTimeFactory.getDateTime(o1.orderStruct.receivedTime);
            DateTime dt2 = DateTimeFactory.getDateTime(o2.orderStruct.receivedTime);
            // newest to oldest
            return dtComp.compare(dt2, dt1);
        }
    }
}
