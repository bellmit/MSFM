package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV8.TradingClassStatusQuery;


public class TradingClassStatusQueryV8
{
    private TradingClassStatusQueryConsumerV5 tradingClassStatusQueryConsumerV5;

    private EngineAccess engineAccess;
    private TradingClassStatusQuery tradingClassStatusQueryV8;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public TradingClassStatusQueryV8(EngineAccess ea, TradingClassStatusQuery tcsq)
    {
        engineAccess = ea;
        tradingClassStatusQueryV8 = tcsq;

        tradingClassStatusQueryConsumerV5 =
                new TradingClassStatusQueryConsumerV5();
        engineAccess.associateWithOrb(tradingClassStatusQueryConsumerV5);
    }

    /** Execute a command on a TradingClassStatusQueryV8 object.
     * @param command Words from command line: TradingClassStatusQueryV8 function args...
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
            if (cmd.equalsIgnoreCase("getProductGroups"))
            {
                doGetProductGroups();
            }
            else if (cmd.equalsIgnoreCase("getClassesForProductGroup"))
            {
                doGetClassesForProductGroup(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeTradingClassStatusForProductGroup"))
            {
                doSubscribeTradingClassStatusForProductGroup(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeTradingClassStatusForClasses"))
            {
                doSubscribeTradingClassStatusForClasses(command);
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

    private void doGetProductGroups() throws Throwable
    {
        String pgseq[] = tradingClassStatusQueryV8.getProductGroups();
        Log.message(Struct.toString(pgseq));
    }

    private void doGetClassesForProductGroup(String command[]) throws Throwable
    {
        String names[] = { "productGroupName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productGroupName");
            return;
        }
        String productGroupName = values[0];

        int classKeys[] = tradingClassStatusQueryV8
                .getClassesForProductGroup(productGroupName);
        Log.message(Struct.toString(classKeys));
    }

    private void doSubscribeTradingClassStatusForProductGroup(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productGroupNames" };
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
            Log.message("Missing productGroupNames");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof String[]))
        {
            Log.message("Not a ProductGroupSequence:" + objName);
            return;
        }
        String productGroupNames[] = (String[]) o;

        tradingClassStatusQueryV8.subscribeTradingClassStatusForProductGroup(
                sessionName, productGroupNames,
                tradingClassStatusQueryConsumerV5._this());
    }

    private void doSubscribeTradingClassStatusForClasses(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKeys" };
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
            Log.message("Missing classKeys");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof int[]))
        {
            Log.message("Not a ClassKeySequence:" + objName);
            return;
        }
        int classKeys[] = (int[]) o;

        tradingClassStatusQueryV8.subscribeTradingClassStatusForClasses(
                sessionName, classKeys,
                tradingClassStatusQueryConsumerV5._this());
    }
}
