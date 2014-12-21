package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV5.OrderEntry;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;

public class OrderEntryV5 extends OrderEntryV3
{
    private EngineAccess engineAccess;
    private OrderEntry orderEntryV5;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderEntryV5(EngineAccess ea, OrderEntry oe)
    {
        super(ea, oe);
        engineAccess = ea;
        orderEntryV5 = oe;
    }

    /** Execute a command on an OrderEntry object.
     * @param command Words from command line: OrderEntryV5 function args...
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
            if (cmd.equalsIgnoreCase("acceptInternalizationStrategyOrder"))
            {
                doAcceptInternalizationStrategyOrder(command);
            }
            else
            {
                // Maybe it's a V3 command; pass it to the V3 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptInternalizationStrategyOrder(String command[]) throws Throwable
    {
        String names[] = { "primaryOrder", "primaryOrderLegEntries",
                "matchOrder", "matchOrderLegEntries", "matchType" };
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
            Log.message("Missing primaryOrderLegEntries");
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
        LegOrderEntryStruct primaryOrderLegEntries[] = (LegOrderEntryStruct[]) o;

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
            Log.message("Missing matchOrderLegEntries");
            return;
        }
        objName = values[3];
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
        LegOrderEntryStruct matchOrderLegEntries[] = (LegOrderEntryStruct[]) o;

        if (values[4] == null)
        {
            Log.message("Missing matchType");
            return;
        }
        short matchType = Short.parseShort(values[4]);

        InternalizationOrderResultStruct ior =
                orderEntryV5.acceptInternalizationStrategyOrder(primaryOrder,
                        primaryOrderLegEntries, matchOrder,
                        matchOrderLegEntries, matchType);
        Log.message(Struct.toString(ior));
    }
}
