package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.OrderEntry;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;

public class OrderEntryV1
{
    private EngineAccess engineAccess;
    private OrderEntry orderEntryV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderEntryV1(EngineAccess ea, OrderEntry oe)
    {
        engineAccess = ea;
        orderEntryV1 = oe;
    }

    /** Execute a command on an OrderEntry object.
     * @param command Words from command line: OrderEntryV1 function args...
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
            if (cmd.equalsIgnoreCase("acceptOrder"))
            {
                doAcceptOrder(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderByProductName"))
            {
                doAcceptOrderByProductName(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderCancelRequest"))
            {
                doAcceptOrderCancelRequest(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderUpdateRequest"))
            {
                doAcceptOrderUpdateRequest(command);
            }
            else if (cmd.equalsIgnoreCase("acceptOrderCancelReplaceRequest"))
            {
                doAcceptOrderCancelReplaceRequest(command);
            }
            else if (cmd.equalsIgnoreCase("acceptCrossingOrder"))
            {
                doAcceptCrossingOrder(command);
            }
            else if (cmd.equalsIgnoreCase("acceptRequestForQuote"))
            {
                doAcceptRequestForQuote(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrder"))
            {
                doAcceptStrategyOrder(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderUpdateRequest"))
            {
                doAcceptStrategyOrderUpdateRequest(command);
            }
            else if (cmd.equalsIgnoreCase("acceptStrategyOrderCancelReplaceRequest"))
            {
                doAcceptStrategyOrderCancelReplaceRequest(command);
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

    private void doAcceptOrder(String command[]) throws Throwable
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
        OrderEntryStruct oe = (OrderEntryStruct) o;

        OrderIdStruct oid = orderEntryV1.acceptOrder(oe);
        Log.message(Struct.toString(oid));
    }

    private void doAcceptOrderByProductName(String command[]) throws Throwable
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
        ProductNameStruct pn = (ProductNameStruct) o;

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
        OrderEntryStruct oe = (OrderEntryStruct) o;

        OrderIdStruct oid = orderEntryV1.acceptOrderByProductName(pn, oe);
        Log.message(Struct.toString(oid));
    }

    private void doAcceptOrderCancelRequest(String command[]) throws Throwable
    {
        String names[] = { "cancelRequest" };
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
        CancelRequestStruct cr = (CancelRequestStruct) o;

        orderEntryV1.acceptOrderCancelRequest(cr);
    }

    private void doAcceptOrderUpdateRequest(String command[]) throws Throwable
    {
        String names[] = { "currentRemainingQuantity", "updatedOrder" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing currentRemainingQuantity");
            return;
        }
        int currentRemainingQuantity = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing updatedOrder");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof OrderEntryStruct))
        {
            Log.message("Not a CancelRequestStruct:" + objName);
            return;
        }
        OrderEntryStruct oe = (OrderEntryStruct) o;

        orderEntryV1.acceptOrderUpdateRequest(currentRemainingQuantity, oe);
    }

    private void doAcceptOrderCancelReplaceRequest(String command[])
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
        CancelRequestStruct cr = (CancelRequestStruct) o;

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
        OrderEntryStruct oe = (OrderEntryStruct) o;

        OrderIdStruct oi = orderEntryV1.acceptOrderCancelReplaceRequest(cr, oe);
        Log.message(Struct.toString(oi));
    }

    private void doAcceptCrossingOrder(String command[]) throws Throwable
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

        orderEntryV1.acceptCrossingOrder(buyCrossingOrder, sellCrossingOrder);
    }

    private void doAcceptRequestForQuote(String command[]) throws Throwable
    {
        String names[] = { "rfq" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing rfq");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof RFQEntryStruct))
        {
            Log.message("Not an RFQEntryStruct:" + objName);
            return;
        }
        RFQEntryStruct rfq = (RFQEntryStruct) o;

        orderEntryV1.acceptRequestForQuote(rfq);
    }

    private void doAcceptStrategyOrder(String command[]) throws Throwable
    {
        String names[] = { "anOrder", "legEntryDetails" };
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
            Log.message("Missing legEntryDetails");
            return;
        }
        objName = values[1];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStruct[]))
        {
            Log.message("Not a LegOrderEntryStructSequence:" + objName);
            return;
        }
        LegOrderEntryStruct legEntryDetails[] = (LegOrderEntryStruct[]) o;

        OrderIdStruct oi =
                orderEntryV1.acceptStrategyOrder(anOrder, legEntryDetails);
        Log.message(Struct.toString(oi));
    }

    private void doAcceptStrategyOrderUpdateRequest(String command[])
            throws Throwable
    {
        String names[] = { "currentRemainingQuantity", "updatedOrder", "legEntryDetails" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing currentRemainingQuantity");
            return;
        }
        int currentRemainingQuantity = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing updatedOrder");
            return;
        }
        String objName = values[1];
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
        OrderEntryStruct updatedOrder = (OrderEntryStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing legEntryDetails");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStruct[]))
        {
            Log.message("Not a LegOrderEntryStructSequence:" + objName);
            return;
        }
        LegOrderEntryStruct legEntryDetails[] = (LegOrderEntryStruct[]) o;

        orderEntryV1.acceptStrategyOrderUpdateRequest(currentRemainingQuantity,
                updatedOrder, legEntryDetails);
    }

    private void doAcceptStrategyOrderCancelReplaceRequest(String command[])
            throws Throwable
    {
        String names[] = { "cancelRequest", "newOrder", "legEntryDetails" };
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
            Log.message("Missing legEntryDetails");
            return;
        }
        objName = values[2];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof LegOrderEntryStruct[]))
        {
            Log.message("Not a LegOrderEntryStructSequence:" + objName);
            return;
        }
        LegOrderEntryStruct legEntryDetails[] = (LegOrderEntryStruct[]) o;

        OrderIdStruct oi = orderEntryV1.acceptStrategyOrderCancelReplaceRequest(
                cancelRequest, newOrder, legEntryDetails);
        Log.message(Struct.toString(oi));
    }
}
