package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV9.OrderEntry;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;

public class OrderEntryV9 extends OrderEntryV7
{
    private EngineAccess engineAccess;
    private OrderEntry orderEntryV9;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderEntryV9(EngineAccess ea, OrderEntry oe)
    {
        super(ea, oe);
        engineAccess = ea;
        orderEntryV9 = oe;
    }

    /** Execute a command on an OrderEntry object.
     * @param command Words from command line: OrderEntryV9 function args...
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
            if (cmd.equalsIgnoreCase("acceptLightOrder"))
            {
                doAcceptLightOrder(command);
            }
            else if (cmd.equalsIgnoreCase("acceptLightOrderCancelRequest"))
            {
                doAcceptLightOrderCancelRequest(command);
            }
            else if (cmd.equalsIgnoreCase("acceptLightOrderCancelRequestById"))
            {
                doAcceptLightOrderCancelRequestById(command);
            }
            else
            {
                // Maybe it's a V7 command; pass it to the V7 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptLightOrder(String command[]) throws Throwable
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
        if (! (o instanceof LightOrderEntryStruct))
        {
            Log.message("Not a LightOrderEntryStruct:" + objName);
            return;
        }
        LightOrderEntryStruct anOrder = (LightOrderEntryStruct) o;

        LightOrderResultStruct lor = orderEntryV9.acceptLightOrder(anOrder);
        Log.message(Struct.toString(lor));
    }

    private void doAcceptLightOrderCancelRequest(String command[])
            throws Throwable
    {
        String names[] = { "branch", "branchSequenceNumber", "productKey",
                "activeSession", "userAssignedCancelId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing branch");
            return;
        }
        String branch = values[0];

        if (values[1] == null)
        {
            Log.message("Missing branchSequenceNumber");
            return;
        }
        int branchSequenceNumber = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing activeSession");
            return;
        }
        String activeSession = values[3];

        if (values[4] == null)
        {
            Log.message("Missing userAssignedCancelId");
            return;
        }
        String userAssignedCancelId = values[4];

        LightOrderResultStruct lor = orderEntryV9.acceptLightOrderCancelRequest(
                branch, branchSequenceNumber, productKey, activeSession,
                userAssignedCancelId);
        Log.message(Struct.toString(lor));
    }

    private void doAcceptLightOrderCancelRequestById(String command[])
            throws Throwable
    {
        String names[] = { "orderHighId", "orderLowId", "productKey",
                "activeSession", "userAssignedCancelId" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing orderHighId");
            return;
        }
        int orderHighId = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing orderLowId");
            return;
        }
        int orderLowId = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing activeSession");
            return;
        }
        String activeSession = values[3];

        if (values[4] == null)
        {
            Log.message("Missing userAssignedCancelId");
            return;
        }
        String userAssignedCancelId = values[4];


        LightOrderResultStruct lor =
                orderEntryV9.acceptLightOrderCancelRequestById(orderHighId,
                        orderLowId, productKey, activeSession,
                        userAssignedCancelId);
        Log.message(Struct.toString(lor));
    }
}
