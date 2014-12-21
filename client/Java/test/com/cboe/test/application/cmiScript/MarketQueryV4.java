package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV4.MarketQuery;

public class MarketQueryV4 // does not extend MarketQueryV3
{
    private EngineAccess engineAccess;
    private MarketQuery marketQueryV4;
    private CurrentMarketConsumerV4 currentMarketConsumerV4;
    private RecapConsumerV4 recapConsumerV4;
    private TickerConsumerV4 tickerConsumerV4;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public MarketQueryV4(EngineAccess ea, MarketQuery mq)
    {
        engineAccess = ea;
        marketQueryV4 = mq;

        currentMarketConsumerV4 = new CurrentMarketConsumerV4();
        engineAccess.associateWithOrb(currentMarketConsumerV4);
        recapConsumerV4 = new RecapConsumerV4();
        engineAccess.associateWithOrb(recapConsumerV4);
        tickerConsumerV4 = new TickerConsumerV4();
        engineAccess.associateWithOrb(tickerConsumerV4);
    }

    /** Execute a command on a MarketQuery object.
     * @param command Words from command line: MarketQueryV4 function args...
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
            if (cmd.equalsIgnoreCase("subscribeCurrentMarket"))
            {
                doSubscribeCurrentMarket(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarket"))
            {
                doUnsubscribeCurrentMarket(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeTicker"))
            {
                doSubscribeTicker(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeTicker"))
            {
                doUnsubscribeTicker(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeRecap"))
            {
                doSubscribeRecap(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRecap"))
            {
                doUnsubscribeRecap(command);
            }
            else
            {
                Log.message("Unknown function:" + cmd + " for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doSubscribeCurrentMarket(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "actionOnQueue" };
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
            Log.message("Missing actionOnQueue");
            return;
        }
        short actionOnQueue = Short.parseShort(values[1]);

        marketQueryV4.subscribeCurrentMarket(classKey,
                currentMarketConsumerV4._this(), actionOnQueue);
    }

    private void doUnsubscribeCurrentMarket(String command[])
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

        marketQueryV4.unsubscribeCurrentMarket(classKey,
                currentMarketConsumerV4._this());
    }

    private void doSubscribeTicker(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "actionOnQueue" };
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
            Log.message("Missing actionOnQueue");
            return;
        }
        short actionOnQueue = Short.parseShort(values[1]);

        marketQueryV4.subscribeTicker(classKey, tickerConsumerV4._this(),
                actionOnQueue);
    }

    private void doUnsubscribeTicker(String command[])
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

        marketQueryV4.unsubscribeTicker(classKey, tickerConsumerV4._this());
    }

    private void doSubscribeRecap(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "actionOnQueue" };
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
            Log.message("Missing actionOnQueue");
            return;
        }
        short actionOnQueue = Short.parseShort(values[1]);

        marketQueryV4.subscribeRecap(classKey, recapConsumerV4._this(),
                actionOnQueue);
    }

    private void doUnsubscribeRecap(String command[])
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

        marketQueryV4.unsubscribeRecap(classKey, recapConsumerV4._this());
    }
}
