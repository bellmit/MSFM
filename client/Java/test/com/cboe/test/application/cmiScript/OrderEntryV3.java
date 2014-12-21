package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV3.OrderEntry;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;

public class OrderEntryV3 extends OrderEntryV1
{
    private EngineAccess engineAccess;
    private OrderEntry orderEntryV3;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderEntryV3(EngineAccess ea, OrderEntry oe)
    {
        super(ea, oe);
        engineAccess = ea;
        orderEntryV3 = oe;
    }

    /** Execute a command on an OrderEntry object.
     * @param command Words from command line: OrderEntryV3 function args...
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
            if (cmd.equalsIgnoreCase("acceptInternalizationOrder"))
            {
                doAcceptInternalizationOrder(command);
            }
            else
            {
                // Maybe it's a V1 command; pass it to the V1 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptInternalizationOrder(String command[]) throws Throwable
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

        InternalizationOrderResultStruct ior =
                orderEntryV3.acceptInternalizationOrder(primaryOrder,
                        matchOrder, matchType);
        Log.message(Struct.toString(ior));
    }
}
