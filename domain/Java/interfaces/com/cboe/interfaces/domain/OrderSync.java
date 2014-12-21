package com.cboe.interfaces.domain;

public interface OrderSync
{
    public void updateToSync(OrderTTE p_order);
    public void updateToSync(Order p_order);
    public void remove(Order p_order);
    public void updateAllOrdersFromSync();
    public void processAllOrders();
    public int getDistributedCacheOrderCount();
    public void clearOrderHolders();
}
