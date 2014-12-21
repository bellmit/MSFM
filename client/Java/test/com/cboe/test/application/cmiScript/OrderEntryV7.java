package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV7.OrderEntry;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.CrossOrderStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStructV2;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;

public class OrderEntryV7 extends OrderEntryV5
{
    private EngineAccess engineAccess;
    private OrderEntry orderEntryV7;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderEntryV7(EngineAccess ea, OrderEntry oe)
    {
        super(ea, oe);
        engineAccess = ea;
        orderEntryV7 = oe;
    }

    /** Execute a command on an OrderEntry object.
     * @param command Words from command line: OrderEntryV7 function args...
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
            if (cmd.equalsIgnoreCase("acceptOrderNoAckV7"))
            {
                doAcceptOrderNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderByProductNameNoAckV7"))
            {
                doAcceptOrderByProductNameNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderCancelReplaceRequestNoAckV7"))
            {
                doAcceptOrderCancelReplaceRequestNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderNoAckV7"))
            {
                doAcceptStrategyOrderNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderV7"))
            {
                doAcceptStrategyOrderV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderCancelReplaceRequestNoAckV7"))
            {
                doAcceptStrategyOrderCancelReplaceRequestNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderCancelReplaceRequestV7"))
            {
                doAcceptStrategyOrderCancelReplaceRequestV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptInternalizationOrderNoAckV7"))
            {
                doAcceptInternalizationOrderNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptInternalizationStrategyOrderNoAckV7"))
            {
                doAcceptInternalizationStrategyOrderNoAckV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptInternalizationStrategyOrderV7"))
            {
                doAcceptInternalizationStrategyOrderV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptCrossingOrderNoAckV7"))
            {
                doAcceptCrossingOrderNoAckV7(command);
            }
            else
            {
                // Maybe it's a V5 command; pass it to the V5 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptOrderNoAckV7(String command[]) throws Throwable
    {
        String names[] = { "anOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing anOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct anOrder = (OrderEntryStruct) o;

        OrderStruct os = orderEntryV7.acceptOrderNoAckV7(anOrder);
        Log.message(Struct.toString(os));
    }

    private void doAcceptOrderByProductNameNoAckV7(String command[])
            throws Throwable
    {
        String names[] = { "product", "anOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing product");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ProductNameStruct))
        {
            Log.message("Not a ProductNameStruct:" + objName);
            return;
        }
        ProductNameStruct product = (ProductNameStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing anOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct anOrder = (OrderEntryStruct) o;

        OrderStruct os =
                orderEntryV7.acceptOrderByProductNameNoAckV7(product, anOrder);
        Log.message(Struct.toString(os));
    }

    private void doAcceptOrderCancelReplaceRequestNoAckV7(String command[])
            throws Throwable
    {
        String names[] = { "cancelRequest", "newOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing cancelRequest");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof CancelRequestStruct))
        {
            Log.message("Not a CancelRequestStruct:" + objName);
            return;
        }
        CancelRequestStruct cancelRequest = (CancelRequestStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing newOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct newOrder = (OrderEntryStruct) o;

        OrderStruct os = orderEntryV7.acceptOrderCancelReplaceRequestNoAckV7(
                cancelRequest, newOrder);
        Log.message(Struct.toString(os));
    }

    private void doAcceptStrategyOrderNoAckV7(String command[]) throws Throwable
    {
        String names[] = { "anOrder", "legEntryDetailsV2" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing anOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct anOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing legEntryDetailsV2");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 legEntryDetailsV2[] = (LegOrderEntryStructV2[]) o;

        OrderStruct os = orderEntryV7.acceptStrategyOrderNoAckV7(
                anOrder, legEntryDetailsV2);
        Log.message(Struct.toString(os));
    }

    private void doAcceptStrategyOrderV7(String command[]) throws Throwable
    {
        String names[] = { "anOrder", "legEntryDetailsV2" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing anOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct anOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing legEntryDetailsV2");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 legEntryDetailsV2[] = (LegOrderEntryStructV2[]) o;

        OrderIdStruct oi = orderEntryV7.acceptStrategyOrderV7(
                anOrder, legEntryDetailsV2);
        Log.message(Struct.toString(oi));
    }

    private void doAcceptStrategyOrderCancelReplaceRequestNoAckV7(
            String command[]) throws Throwable
    {
        String names[] = { "cancelRequest", "newOrder", "legEntryDetailsV2" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing cancelRequest");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof CancelRequestStruct))
        {
            Log.message("Not a CancelRequestStruct:" + objName);
            return;
        }
        CancelRequestStruct cancelRequest = (CancelRequestStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing newOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct newOrder = (OrderEntryStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing legEntryDetailsV2");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 legEntryDetailsV2[] = (LegOrderEntryStructV2[]) o;

        OrderStruct os =
                orderEntryV7.acceptStrategyOrderCancelReplaceRequestNoAckV7(
                        cancelRequest, newOrder, legEntryDetailsV2);
        Log.message(Struct.toString(os));
    }

    private void doAcceptStrategyOrderCancelReplaceRequestV7(String command[])
            throws Throwable
    {
        String names[] = { "cancelRequest", "newOrder", "legEntryDetailsV2" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing cancelRequest");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof CancelRequestStruct))
        {
            Log.message("Not a CancelRequestStruct:" + objName);
            return;
        }
        CancelRequestStruct cancelRequest = (CancelRequestStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing newOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct newOrder = (OrderEntryStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing legEntryDetailsV2");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 legEntryDetailsV2[] = (LegOrderEntryStructV2[]) o;

        OrderIdStruct oi =
                orderEntryV7.acceptStrategyOrderCancelReplaceRequestV7(
                        cancelRequest, newOrder, legEntryDetailsV2);
        Log.message(Struct.toString(oi));
    }

    private void doAcceptInternalizationOrderNoAckV7(String command[])
            throws Throwable
    {
        String names[] = { "primaryOrder", "matchOrder", "matchType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing primaryOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct primaryOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing matchOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct matchOrder = (OrderEntryStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing matchType");
            return;
        }
        short matchType = Short.parseShort(values[2]);

        InternalizationOrderResultStructV2 ior =
                orderEntryV7.acceptInternalizationOrderNoAckV7(
                        primaryOrder, matchOrder, matchType);
        Log.message(Struct.toString(ior));
    }

    private void doAcceptInternalizationStrategyOrderNoAckV7(String command[])
            throws Throwable
    {
        String names[] = { "primaryOrder", "primaryOrderLegEntriesV2",
                "matchOrder", "matchOrderLegEntriesV2", "matchType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing primaryOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct primaryOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing primaryOrderLegEntriesV2");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 primaryOrderLegEntriesV2[] =
                (LegOrderEntryStructV2[]) o;
        
        if (values[2] == null)
        {
            Log.message("Missing matchOrder");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct matchOrder = (OrderEntryStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing matchOrderLegEntriesV2");
            return;
        }
        objName = values[3];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 matchOrderLegEntriesV2[] =
                (LegOrderEntryStructV2[]) o;

        if (values[4] == null)
        {
            Log.message("Missing matchType");
            return;
        }
        short matchType = Short.parseShort(values[4]);

        InternalizationOrderResultStructV2 ior =
                orderEntryV7.acceptInternalizationStrategyOrderNoAckV7(
                        primaryOrder, primaryOrderLegEntriesV2,
                        matchOrder, matchOrderLegEntriesV2, matchType);
        Log.message(Struct.toString(ior));
    }

    private void doAcceptInternalizationStrategyOrderV7(String command[])
            throws Throwable
    {
        String names[] = { "primaryOrder", "primaryOrderLegEntriesV2",
                "matchOrder", "matchOrderLegEntriesV2", "matchType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing primaryOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct primaryOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing primaryOrderLegEntriesV2");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 primaryOrderLegEntriesV2[] =
                (LegOrderEntryStructV2[]) o;

        if (values[2] == null)
        {
            Log.message("Missing matchOrder");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct matchOrder = (OrderEntryStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing matchOrderLegEntriesV2");
            return;
        }
        objName = values[3];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStructV2[]))
        {
            Log.message("Not a LegOrderEntryStructV2Sequence:" + objName);
            return;
        }
        LegOrderEntryStructV2 matchOrderLegEntriesV2[] =
                (LegOrderEntryStructV2[]) o;

        if (values[4] == null)
        {
            Log.message("Missing matchType");
            return;
        }
        short matchType = Short.parseShort(values[4]);

        InternalizationOrderResultStruct ior =
                orderEntryV7.acceptInternalizationStrategyOrderV7(
                        primaryOrder, primaryOrderLegEntriesV2,
                        matchOrder, matchOrderLegEntriesV2, matchType);
        Log.message(Struct.toString(ior));
    }

    private void doAcceptCrossingOrderNoAckV7(String command[]) throws Throwable
    {
        String names[] = { "buyCrossingOrder", "sellCrossingOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing buyCrossingOrder");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct buyCrossingOrder = (OrderEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing sellCrossingOrder");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not an OrderEntryStruct:" + objName);
            return;
        }
        OrderEntryStruct sellCrossingOrder = (OrderEntryStruct) o;

        CrossOrderStruct co = orderEntryV7.acceptCrossingOrderNoAckV7(
                        buyCrossingOrder, sellCrossingOrder);
        Log.message(Struct.toString(co));
    }
}
