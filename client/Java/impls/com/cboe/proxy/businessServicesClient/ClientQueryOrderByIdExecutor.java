package com.cboe.proxy.businessServicesClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.cboe.idl.businessServices.OrderHandlingService;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

/**
 * This class is used to create a fixed size threapool and execute the inner class QueryOrderById.
 * 
 * @author lowery
 */
public class ClientQueryOrderByIdExecutor
{
    private int threadPoolSize;
    //private Future<OrderStruct> orderStructTask;
    private ExecutorService orderQueryExecutorService;
    //private Map<OrderHandlingService, Future<OrderStruct>> returnedOrders = new HashMap< OrderHandlingService, Future<OrderStruct> >();

    public ClientQueryOrderByIdExecutor(int threadPoolSize)
    {
        this.threadPoolSize = threadPoolSize;
        this.orderQueryExecutorService = getExecutorService();
    }
   
    private ExecutorService getExecutorService()
    {
        if (orderQueryExecutorService == null ||  orderQueryExecutorService.isShutdown())
        {
            orderQueryExecutorService = Executors.newFixedThreadPool(threadPoolSize);
        }
        return orderQueryExecutorService;
    }
    
    public void shutdown()
    {
        orderQueryExecutorService.shutdown();
    }
    
    /**
     * This method starts a thread from the threadpool to execute the call method of the 
     * inner class QueryOrderById.
     */
    public Future<OrderStruct> query(OrderHandlingService ohs, String userId, OrderIdStruct orderId, ClientQueryOrderByIdTypes type) throws Exception
    {
        return getExecutorService().submit(new QueryOrderById(ohs, userId, orderId, type));  
        //returnedOrders.put(ohs, orderStructTask);
    }

    /**
     * This method starts a thread from the threadpool to execute the call method of the 
     * inner class QueryOrderById.
     */
    public Future<OrderStruct> query(OrderHandlingService ohs, String userId, String ORSId, ClientQueryOrderByIdTypes type) throws Exception
    {
        return getExecutorService().submit(new QueryOrderById(ohs, userId, ORSId, type));  
        //returnedOrders.put(ohs, orderStructTask);
    }
    
    /**
     * This method has to be called after the query() gets called to check for the query result, waiting
     * for completion if needed.
     */
    public OrderStruct getResult(Future<OrderStruct> ohs) throws Exception
    {
        return ohs.get();
    }
    
    private class QueryOrderById implements Callable<OrderStruct>
    {
        private OrderHandlingService ohs;
        private String userId;
        private OrderIdStruct orderId;
        private ClientQueryOrderByIdTypes type;
        private String ORSId;
        
        QueryOrderById(OrderHandlingService ohs, String userId, OrderIdStruct orderId, ClientQueryOrderByIdTypes type)
        {
            this.ohs = ohs;
            this.userId = userId;
            this.orderId = orderId;
            this.type = type;
        }
 
        QueryOrderById(OrderHandlingService ohs, String userId, String ORSId, ClientQueryOrderByIdTypes type)
        {
            this.ohs = ohs;
            this.userId = userId;
            this.type = type;
            this.ORSId = ORSId;
        }
        
        public OrderStruct call() throws Exception
        {
            if (type == ClientQueryOrderByIdTypes.QUERY_ORDER_BY_ID_V2) {
                return ohs.getOrderByIdV2(userId, orderId);
            }
            else if (type == ClientQueryOrderByIdTypes.QUERY_ORDER_BY_ID) {
                return ohs.getOrderById(userId, orderId);
            }
            else if (type == ClientQueryOrderByIdTypes.QUERY_ORDER_BY_ORSID) {
                return ohs.getOrderByORSID(userId, ORSId);
            }
            else {
                return null;
            }
        }
    } 
}
