package com.cboe.application.order;

import com.cboe.application.shared.IOrderAckConstraints;
import com.cboe.application.supplier.OrderStatusCollectorSupplier;
import com.cboe.domain.util.CmiOrderIdStructContainer;
import com.cboe.domain.util.CompleteOrderIdStructContainer;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.application.inprocess.CasSession;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelEvent;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Faibishe
 * Date: Nov 29, 2010
 * Time: 11:02:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IOrderQueryCache extends InstrumentedEventChannelListener {

    void setFirmKey(ExchangeFirmStruct firmKey);

    void setFirmGroupMembers(List<String> grpMembers);

    List<String> getGroupMembers();

    ExchangeFirmStruct getFirmKey();

    OrderStatusCollectorSupplier getOrderStatusCollectorSupplier();

    OrderStruct[] getOrdersByUser(String[] userIds);

    OrderStruct[] publishAllOrders();

    OrderStruct[] publishUserOrders();

    OrderStruct[] getAllOrdersForType(int type);

    OrderStruct[] getOrdersByClass(int productClass);

    OrderStruct[] getOrdersForProduct(int productKey);

    OrderStruct[] getOrdersForSession(String sessionName);

    OrderStruct getOrder(OrderIdStruct orderId, short statusChange);

    void put(OrderStruct order);

    boolean containsOrderIdKey(OrderIdStruct orderId, short statusChange);

    void cacheCleanUp();

    void channelUpdate(ChannelEvent event);

    Object getOrderLock(CompleteOrderIdStructContainer orderIndex);

    Object removeOrderLock(CompleteOrderIdStructContainer orderIndex);

    OrderStruct getOrderFromOrderCache(OrderIdStruct orderId);

    void enqueue(ChannelEvent event);

    void addToOrderEntryCache(CmiOrderIdStructContainer cmiOrderId, int pKey);

    void orderEntryComplete(OrderIdStruct orderId);

    void orderEntryComplete(CmiOrderIdStructContainer cmiOrderId);

    void orderEntryFailed(CmiOrderIdStructContainer cmiOrderId, boolean maybe);

    int getProdKeyFromOrderEntryCache(OrderIdStruct orderId);
    public FixOrderQueryCache.OrderState getOrderStateFromOrderEntryCache(OrderIdStruct orderId);

    boolean checkOrderEntryCache(OrderIdStruct orderId);
}
