package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV2.OrderQuery;


public class OrderQueryV2 extends OrderQueryV1
{
    private OrderStatusConsumerV2 orderStatusConsumerV2;

    private EngineAccess engineAccess;
    private OrderQuery orderQueryV2;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public OrderQueryV2(EngineAccess ea, OrderQuery oq)
    {
        super(ea, oq);
        engineAccess = ea;
        orderQueryV2 = oq;

        orderStatusConsumerV2 = new OrderStatusConsumerV2();
        engineAccess.associateWithOrb(orderStatusConsumerV2);
    }

    /** Execute a command on an OrderQuery object.
     * @param command Words from command line: OrderQueryV2 function args...
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
            if (cmd.equalsIgnoreCase("subscribeOrderStatusV2"))
            {
                doSubscribeOrderStatusV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusV2"))
            {
                doUnsubscribeOrderStatusV2();
            }
            else if (cmd.equalsIgnoreCase("subscribeOrderStatusForClassV2"))
            {
                doSubscribeOrderStatusForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForClassV2"))
            {
                doUnsubscribeOrderStatusForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeOrderStatusForFirmV2"))
            {
                doSubscribeOrderStatusForFirmV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForFirmV2"))
            {
                doUnsubscribeOrderStatusForFirmV2();
            }
            else if (cmd.equalsIgnoreCase("subscribeOrderStatusForFirmForClassV2"))
            {
                doSubscribeOrderStatusForFirmForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeOrderStatusForFirmForClassV2"))
            {
                doUnsubscribeOrderStatusForFirmForClassV2(command);
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

    private void doSubscribeOrderStatusV2(String command[]) throws Throwable
    {
        String names[] = { "publishOnSubscribe", "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing publishOnSubscribe");
            return;
        }
        boolean publishOnSubscribe = CommandLine.booleanValue(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[1]);

        orderQueryV2.subscribeOrderStatusV2(
                orderStatusConsumerV2._this(), publishOnSubscribe, gmdCallback);
    }

    private void doUnsubscribeOrderStatusV2() throws Throwable
    {
        orderQueryV2.unsubscribeOrderStatusV2(orderStatusConsumerV2._this());
    }

    private void doSubscribeOrderStatusForClassV2(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "publishOnSubscribe", "gmdCallback" };
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

        if (values[1] == null)
        {
            Log.message("Missing publishOnSubscribe");
            return;
        }
        boolean publishOnSubscribe = CommandLine.booleanValue(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[2]);

        orderQueryV2.subscribeOrderStatusForClassV2(classKey,
                orderStatusConsumerV2._this(), publishOnSubscribe, gmdCallback);
    }

    private void doUnsubscribeOrderStatusForClassV2(String command[])
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
        orderQueryV2.unsubscribeOrderStatusForClassV2(
                classKey, orderStatusConsumerV2._this());
    }

    private void doSubscribeOrderStatusForFirmV2(String command[])
            throws Throwable
    {
        String names[] = { "publishOnSubscribe", "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing publishOnSubscribe");
            return;
        }
        boolean publishOnSubscribe = CommandLine.booleanValue(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[1]);

        orderQueryV2.subscribeOrderStatusForFirmV2(
                orderStatusConsumerV2._this(), publishOnSubscribe, gmdCallback);
    }

    private void doUnsubscribeOrderStatusForFirmV2() throws Throwable
    {
        orderQueryV2.unsubscribeOrderStatusForFirmV2(
                orderStatusConsumerV2._this());
    }

    private void doSubscribeOrderStatusForFirmForClassV2(String command[]) throws Throwable
    {
        String names[] = { "classKey", "publishOnSubscribe", "gmdCallback" };
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

        if (values[1] == null)
        {
            Log.message("Missing publishOnSubscribe");
            return;
        }
        boolean publishOnSubscribe = CommandLine.booleanValue(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[2]);

        orderQueryV2.subscribeOrderStatusForFirmForClassV2(classKey,
                orderStatusConsumerV2._this(), publishOnSubscribe, gmdCallback);
    }

    private void doUnsubscribeOrderStatusForFirmForClassV2(String command[]) throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        String objName = values[0];
        if (objName == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        orderQueryV2.unsubscribeOrderStatusForFirmForClassV2(classKey,
                orderStatusConsumerV2._this());
    }
}
