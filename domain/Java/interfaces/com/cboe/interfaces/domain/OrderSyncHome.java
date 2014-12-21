package com.cboe.interfaces.domain;

/**
 * This home is a container trade-engine order data synchronization between master
 * and slave trade servers.
 * 
 * @author Hemant Thakkar
 *
 */

public interface OrderSyncHome
{
    public static final String HOME_NAME = "OrderSyncHome";
    public OrderSync getOrderSync();
    public int getOrderCount();
    public void updateToSync(Order order);
}
