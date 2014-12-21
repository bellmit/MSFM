package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV6.OrderQuery;


public class OrderQueryV6 extends OrderQueryV3
{
    private EngineAccess engineAccess;
    private OrderQuery orderQueryV6;

    private static final int INDEX_FIRST_PARAMETER = 2;

    public OrderQueryV6(EngineAccess ea, OrderQuery oq)
    {
        super(ea, oq);
        engineAccess = ea;
        orderQueryV6 = oq;
    }

    /** Execute a command on an OrderQuery object.
     * @param command Words from command line: OrderQueryV6 function args...
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
            if (cmd.equalsIgnoreCase("registerForDirectedAIM"))
            {
                doRegisterForDirectedAIM(command);
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

    private void doRegisterForDirectedAIM(String command[])
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

        orderQueryV6.registerForDirectedAIM(sessionName, classKey);
    }
}
