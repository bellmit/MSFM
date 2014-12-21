package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV3.MarketQuery;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

public class MarketQueryV3 extends MarketQueryV2
{
    private EngineAccess engineAccess;
    private MarketQuery marketQueryV3;
    private CurrentMarketConsumerV3 currentMarketConsumerV3;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public MarketQueryV3(EngineAccess ea, MarketQuery mq)
    {
        super(ea, mq);
        engineAccess = ea;
        marketQueryV3 = mq;

        currentMarketConsumerV3 = new CurrentMarketConsumerV3();
        engineAccess.associateWithOrb(currentMarketConsumerV3);
    }

    /** Execute a command on a MarketQuery object.
     * @param command Words from command line: MarketQueryV3 function args...
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
            if (cmd.equalsIgnoreCase("getDetailMarketDataHistoryByTime"))
            {
                doGetDetailMarketDataHistoryByTime(command);
            }
            else if (cmd.equalsIgnoreCase("getPriorityMarketDataHistoryByTime"))
            {
                doGetPriorityMarketDataHistoryByTime(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForClassV3"))
            {
                doSubscribeCurrentMarketForClassV3(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarketForClassV3"))
            {
                doUnsubscribeCurrentMarketForClassV3(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForProductV3"))
            {
                doSubscribeCurrentMarketForProductV3(command);
            }
            else if (cmd.equalsIgnoreCase(
                    "unsubscribeCurrentMarketForProductV3"))
            {
                doUnsubscribeCurrentMarketForProductV3(command);
            }
            else
            {
                // Maybe it's a V2 command; pass it to the V2 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doGetDetailMarketDataHistoryByTime(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productKey", "startTime",
                "direction" };
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

        if (values[2] == null)
        {
            Log.message("Missing startTime");
            return;
        }
        DateTimeStruct startTime = Struct.makeDateTimeStruct(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing direction");
            return;
        }
        short direction = Short.parseShort(values[3]);

        MarketDataHistoryDetailStruct mdhd =
                marketQueryV3.getDetailMarketDataHistoryByTime(
                        sessionName, productKey, startTime, direction);
        Log.message(Struct.toString(mdhd));
    }

    private void doGetPriorityMarketDataHistoryByTime(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productKey", "startTime",
                "direction" };
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

        if (values[2] == null)
        {
            Log.message("Missing startTime");
            return;
        }
        DateTimeStruct startTime = Struct.makeDateTimeStruct(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing direction");
            return;
        }
        short direction = Short.parseShort(values[3]);

        MarketDataHistoryDetailStruct mdhd =
                marketQueryV3.getPriorityMarketDataHistoryByTime(
                        sessionName, productKey, startTime, direction);
        Log.message(Struct.toString(mdhd));
    }

    private void doSubscribeCurrentMarketForClassV3(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKey", "actionOnQueue" };
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

        if (values[2] == null)
        {
            Log.message("Missing actionOnQueue");
            return;
        }
        short actionOnQueue = Short.parseShort(values[2]);

        marketQueryV3.subscribeCurrentMarketForClassV3(sessionName, classKey,
                currentMarketConsumerV3._this(), actionOnQueue);
    }

    private void doUnsubscribeCurrentMarketForClassV3(String command[])
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

        marketQueryV3.unsubscribeCurrentMarketForClassV3(sessionName, classKey,
                currentMarketConsumerV3._this());
    }

    private void doSubscribeCurrentMarketForProductV3(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productKey", "actionOnQueue" };
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

        if (values[2] == null)
        {
            Log.message("Missing actionOnQueue");
            return;
        }
        short actionOnQueue = Short.parseShort(values[2]);

        marketQueryV3.subscribeCurrentMarketForProductV3(sessionName,
                productKey, currentMarketConsumerV3._this(), actionOnQueue);
    }

    private void doUnsubscribeCurrentMarketForProductV3(String command[])
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

        marketQueryV3.unsubscribeCurrentMarketForProductV3(sessionName,
                productKey, currentMarketConsumerV3._this());
    }
}
