package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.OrderQuery;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;


public class OrderQueryV1
{
    private OrderStatusConsumer orderStatusConsumer;

    private EngineAccess engineAccess;
    private OrderQuery orderQueryV1;

    private static final boolean PUBLISH = true;
    private static final boolean NO_PUBLISH = false;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderQueryV1(EngineAccess ea, OrderQuery oq)
    {
        engineAccess = ea;
        orderQueryV1 = oq;

        orderStatusConsumer = new OrderStatusConsumer();
        engineAccess.associateWithOrb(orderStatusConsumer);
    }

    /** Execute a command on an OrderQuery object.
     * @param command Words from command line: OrderQueryV1 function args...
     **/
    public void doCommand(String command[])
    {
        if (command.length < 2)
        {
            Log.message("Command line must have at least object, function");
            return;
        }

        try
        {
            String cmd = command[1];
            if (cmd.equalsIgnoreCase("getOrdersForProduct"))
            {
                doGetOrdersForProduct(command);
            }
            else if (cmd.equalsIgnoreCase("getOrdersForSession"))
            {
                doGetOrdersForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getOrderById"))
            {
                doGetOrderById(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeOrdersByFirm"))
            {
                doSubOrdersByFirm(command, PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("subscribeOrdersByFirmWithoutPublish"))
            {
                doSubOrdersByFirm(command, NO_PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("subscribeOrders"))
            {
                doSubOrders(command, PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("subscribeOrdersWithoutPublish"))
            {
                doSubOrders(command, NO_PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("getOrdersForType"))
            {
                doGetOrdersForType(command);
            }
            else if (cmd.equalsIgnoreCase("getOrdersForClass"))
            {
                doGetOrdersForClass(command);
            }
            else if (cmd.equalsIgnoreCase("queryOrderHistory"))
            {
                doQueryOrderHistory(command);
            }
            else if (cmd.equalsIgnoreCase("getPendingAdjustmentOrdersByProduct"))
            {
                doGetPendingAdjustmentOrdersByProduct(command);
            }
            else if (cmd.equalsIgnoreCase("getPendingAdjustmentOrdersByClass"))
            {
                doGetPendingAdjustmentOrdersByClass(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForProduct"))
            {
                doUnsubscribeOrderStatusForProduct(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForSession"))
            {
                doUnsubscribeOrderStatusForSession(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForFirm"))
            {
                doUnsubscribeOrderStatusForFirm();
            }
            else if (cmd.equalsIgnoreCase("unsubscribeAllOrderStatusForType"))
            {
                doUnsubscribeAllOrderStatusForType(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusByClass"))
            {
                doUnsubscribeOrderStatusByClass(command);
            }
            else
            {
                Log.message("Unknown function:" + cmd + "  for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doGetOrdersForProduct(String command[]) throws Throwable
    {
        String names[] = { "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        OrderDetailStruct ordseq[] =
                orderQueryV1.getOrdersForProduct(productKey);
        Log.message(Struct.toString(ordseq));
    }

    private void doGetOrdersForSession(String command[]) throws Throwable
    {
        String names[] = { "sessionName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[0];

        OrderDetailStruct odseq[] = orderQueryV1.getOrdersForSession(sessionName);
        Log.message(Struct.toString(odseq));
    }

    private void doGetOrderById(String command[]) throws Throwable
    {
        String names[] = { "orderId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing orderId");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct oi = (OrderIdStruct) o;
        OrderDetailStruct od = orderQueryV1.getOrderById(oi);
        Log.message(Struct.toString(od));
    }

    private void doSubOrdersByFirm(String command[], boolean publish)
            throws Throwable
    {
        String names[] = { "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[0]);

        if (publish)
        {
            orderQueryV1.subscribeOrdersByFirm(orderStatusConsumer._this(),
                    gmdCallback);
        }
        else
        {
            orderQueryV1.subscribeOrdersByFirmWithoutPublish(
                    orderStatusConsumer._this(), gmdCallback);
        }
    }

    private void doSubOrders(String command[], boolean publish) throws Throwable
    {
        String names[] = { "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[0]);

        if (publish)
        {
            orderQueryV1.subscribeOrders(orderStatusConsumer._this(),
                    gmdCallback);
        }
        else
        {
            orderQueryV1.subscribeOrdersWithoutPublish(
                    orderStatusConsumer._this(), gmdCallback);
        }
    }

    private void doGetOrdersForType(String command[]) throws Throwable
    {
        String names[] = { "productType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[0]);

        OrderDetailStruct odseq[] = orderQueryV1.getOrdersForType(productType);
        Log.message(Struct.toString(odseq));
    }

    private void doGetOrdersForClass(String command[]) throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Short.parseShort(values[0]);

        OrderDetailStruct odseq[] = orderQueryV1.getOrdersForClass(classKey);
        Log.message(Struct.toString(odseq));
    }

    private void doQueryOrderHistory(String command[]) throws Throwable
    {
        String names[] = { "orderId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        String objName = values[0];
        if (objName == null)
        {
            Log.message("Missing orderId");
            return;
        }
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return;
        }
        OrderIdStruct oi = (OrderIdStruct) o;
        ActivityHistoryStruct ah = orderQueryV1.queryOrderHistory(oi);
        Log.message(Struct.toString(ah));
    }

    private void doGetPendingAdjustmentOrdersByProduct(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        PendingOrderStruct poseq[] =
                orderQueryV1.getPendingAdjustmentOrdersByProduct(sessionName, productKey);
        Log.message(Struct.toString(poseq));
    }

    private void doGetPendingAdjustmentOrdersByClass(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[0];

        if (values[1] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[1]);

        PendingOrderStruct poseq[] =
                orderQueryV1.getPendingAdjustmentOrdersByClass(sessionName, classKey);
        Log.message(Struct.toString(poseq));
    }

    private void doUnsubscribeOrderStatusForProduct(String command[])
            throws Throwable
    {
        String names[] = { "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        orderQueryV1.unsubscribeOrderStatusForProduct(productKey,
                orderStatusConsumer._this());
    }

    private void doUnsubscribeOrderStatusForSession(String command[])
            throws Throwable
    {
        String names[] = { "sessionName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[0];

        orderQueryV1.unsubscribeOrderStatusForSession(sessionName,
                orderStatusConsumer._this());
    }

    private void doUnsubscribeOrderStatusForFirm() throws Throwable
    {
        orderQueryV1.unsubscribeOrderStatusForFirm(orderStatusConsumer._this());
    }

    private void doUnsubscribeAllOrderStatusForType(String command[])
            throws Throwable
    {
        String names[] = { "productType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[0]);

        orderQueryV1.unsubscribeAllOrderStatusForType(productType,
                orderStatusConsumer._this());
    }

    private void doUnsubscribeOrderStatusByClass(String command[])
            throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        orderQueryV1.unsubscribeOrderStatusByClass(classKey,
                orderStatusConsumer._this());
    }
}
