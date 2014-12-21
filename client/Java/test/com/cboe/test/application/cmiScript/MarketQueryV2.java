package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV2.MarketQuery;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;

public class MarketQueryV2 extends MarketQueryV1
{
    private EngineAccess engineAccess;
    private MarketQuery marketQueryV2;
    private RecapConsumerV2 recapConsumerV2;
    private CurrentMarketConsumerV2 currentMarketConsumerV2;
    private NBBOConsumerV2 nbboConsumerV2;
    private TickerConsumerV2 tickerConsumerV2;
    private ExpectedOpeningPriceConsumerV2 expectedOpeningPriceConsumerV2;
    private OrderBookConsumerV2 orderBookConsumerV2;
    private OrderBookUpdateConsumerV2 orderBookUpdateConsumerV2;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public MarketQueryV2(EngineAccess ea, MarketQuery mq)
    {
        super(ea, mq);
        engineAccess = ea;
        marketQueryV2 = mq;

        recapConsumerV2 = new RecapConsumerV2();
        engineAccess.associateWithOrb(recapConsumerV2);
        currentMarketConsumerV2 = new CurrentMarketConsumerV2();
        engineAccess.associateWithOrb(currentMarketConsumerV2);
        nbboConsumerV2 = new NBBOConsumerV2();
        engineAccess.associateWithOrb(nbboConsumerV2);
        tickerConsumerV2 = new TickerConsumerV2();
        engineAccess.associateWithOrb(tickerConsumerV2);
        expectedOpeningPriceConsumerV2 = new ExpectedOpeningPriceConsumerV2();
        engineAccess.associateWithOrb(expectedOpeningPriceConsumerV2);
        orderBookConsumerV2 = new OrderBookConsumerV2();
        engineAccess.associateWithOrb(orderBookConsumerV2);
        orderBookUpdateConsumerV2 = new OrderBookUpdateConsumerV2();
        engineAccess.associateWithOrb(orderBookUpdateConsumerV2);
    }

    /** Execute a command on a MarketQuery object.
     * @param command Words from command line: MarketQueryV2 function args...
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
            if (cmd.equalsIgnoreCase("subscribeRecapForClassV2"))
            {
                doSubscribeRecapForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRecapForClassV2"))
            {
                doUnsubscribeRecapForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeRecapForProductV2"))
            {
                doSubscribeRecapForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRecapForProductV2"))
            {
                doUnsubscribeRecapForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForClassV2"))
            {
                doSubscribeCurrentMarketForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarketForClassV2"))
            {
                doUnsubscribeCurrentMarketForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForProductV2"))
            {
                doSubscribeCurrentMarketForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarketForProductV2"))
            {
                doUnsubscribeCurrentMarketForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeNBBOForClassV2"))
            {
                doSubscribeNBBOForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeNBBOForClassV2"))
            {
                doUnsubscribeNBBOForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeNBBOForProductV2"))
            {
                doSubscribeNBBOForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeNBBOForProductV2"))
            {
                doUnsubscribeNBBOForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeTickerForProductV2"))
            {
                doSubscribeTickerForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeTickerForProductV2"))
            {
                doUnsubscribeTickerForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeTickerForClassV2"))
            {
                doSubscribeTickerForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeTickerForClassV2"))
            {
                doUnsubscribeTickerForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeExpectedOpeningPriceForProductV2"))
            {
                doSubscribeExpectedOpeningPriceForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeExpectedOpeningPriceForProductV2"))
            {
                doUnsubscribeExpectedOpeningPriceForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeExpectedOpeningPriceForClassV2"))
            {
                doSubscribeExpectedOpeningPriceForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeExpectedOpeningPriceForClassV2"))
            {
                doUnsubscribeExpectedOpeningPriceForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepthForClassV2"))
            {
                doSubscribeBookDepthForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepthForClassV2"))
            {
                doUnsubscribeBookDepthForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepthForProductV2"))
            {
                doSubscribeBookDepthForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepthForProductV2"))
            {
                doUnsubscribeBookDepthForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepthUpdateForClassV2"))
            {
                doSubscribeBookDepthUpdateForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepthUpdateForClassV2"))
            {
                doUnsubscribeBookDepthUpdateForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepthUpdateForProductV2"))
            {
                doSubscribeBookDepthUpdateForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepthUpdateForProductV2"))
            {
                doUnsubscribeBookDepthUpdateForProductV2(command);
            }
            else if (cmd.equalsIgnoreCase("getBookDepthDetails"))
            {
                doGetBookDepthDetails(command);
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

    private void doSubscribeRecapForClassV2(String command[])
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

        marketQueryV2.subscribeRecapForClassV2(sessionName, classKey,
                recapConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeRecapForClassV2(String command[])
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

        marketQueryV2.unsubscribeRecapForClassV2(sessionName, classKey,
                recapConsumerV2._this());
    }

    private void doSubscribeRecapForProductV2(String command[])
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

        marketQueryV2.subscribeRecapForProductV2(sessionName, productKey,
                recapConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeRecapForProductV2(String command[])
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

        marketQueryV2.unsubscribeRecapForProductV2(sessionName, productKey,
                recapConsumerV2._this());
    }

    private void doSubscribeCurrentMarketForClassV2(String command[])
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

        marketQueryV2.subscribeCurrentMarketForClassV2(sessionName, classKey,
                currentMarketConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeCurrentMarketForClassV2(String command[])
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

        marketQueryV2.unsubscribeCurrentMarketForClassV2(sessionName, classKey,
                currentMarketConsumerV2._this());
    }

    private void doSubscribeCurrentMarketForProductV2(String command[])
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

        marketQueryV2.subscribeCurrentMarketForProductV2(sessionName,
                productKey, currentMarketConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeCurrentMarketForProductV2(String command[])
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

        marketQueryV2.unsubscribeCurrentMarketForProductV2(sessionName,
                productKey, currentMarketConsumerV2._this());
    }

    private void doSubscribeNBBOForClassV2(String command[])
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

        marketQueryV2.subscribeNBBOForClassV2(sessionName, classKey,
                nbboConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeNBBOForClassV2(String command[])
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

        marketQueryV2.unsubscribeNBBOForClassV2(sessionName, classKey,
                nbboConsumerV2._this());
    }

    private void doSubscribeNBBOForProductV2(String command[])
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

        marketQueryV2.subscribeNBBOForProductV2(sessionName, productKey,
                nbboConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeNBBOForProductV2(String command[])
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

        marketQueryV2.unsubscribeNBBOForProductV2(sessionName, productKey,
                nbboConsumerV2._this());
    }

    private void doSubscribeTickerForProductV2(String command[])
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

        marketQueryV2.subscribeTickerForProductV2(sessionName, productKey,
                tickerConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeTickerForProductV2(String command[])
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

        marketQueryV2.unsubscribeTickerForProductV2(sessionName, productKey,
                tickerConsumerV2._this());
    }

    private void doSubscribeTickerForClassV2(String command[])
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

        marketQueryV2.subscribeTickerForClassV2(sessionName, classKey,
                tickerConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeTickerForClassV2(String command[])
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

        marketQueryV2.unsubscribeTickerForClassV2(sessionName, classKey,
                tickerConsumerV2._this());
    }

    private void doSubscribeExpectedOpeningPriceForProductV2(String command[])
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

        marketQueryV2.subscribeExpectedOpeningPriceForProductV2(sessionName,
                productKey, expectedOpeningPriceConsumerV2._this(),
                actionOnQueue);
    }

    private void doUnsubscribeExpectedOpeningPriceForProductV2(String command[])
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

        marketQueryV2.unsubscribeExpectedOpeningPriceForProductV2(sessionName,
                productKey, expectedOpeningPriceConsumerV2._this());
    }

    private void doSubscribeExpectedOpeningPriceForClassV2(String command[])
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

        marketQueryV2.subscribeExpectedOpeningPriceForClassV2(sessionName,
                classKey, expectedOpeningPriceConsumerV2._this(),
                actionOnQueue);
    }

    private void doUnsubscribeExpectedOpeningPriceForClassV2(String command[])
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

        marketQueryV2.unsubscribeExpectedOpeningPriceForClassV2(sessionName,
                classKey, expectedOpeningPriceConsumerV2._this());
    }

    private void doSubscribeBookDepthForClassV2(String command[])
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

        marketQueryV2.subscribeBookDepthForClassV2(sessionName, classKey,
                orderBookConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeBookDepthForClassV2(String command[])
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

        marketQueryV2.unsubscribeBookDepthForClassV2(sessionName, classKey,
                orderBookConsumerV2._this());
    }

    private void doSubscribeBookDepthForProductV2(String command[])
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

        marketQueryV2.subscribeBookDepthForProductV2(sessionName,
                productKey, orderBookConsumerV2._this(),
                actionOnQueue);
    }

    private void doUnsubscribeBookDepthForProductV2(String command[])
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

        marketQueryV2.unsubscribeBookDepthForProductV2(sessionName,
                productKey, orderBookConsumerV2._this());
    }

    private void doSubscribeBookDepthUpdateForClassV2(String command[])
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

        marketQueryV2.subscribeBookDepthUpdateForClassV2(sessionName, classKey,
                orderBookUpdateConsumerV2._this(), actionOnQueue);
    }

    private void doUnsubscribeBookDepthUpdateForClassV2(String command[])
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

        marketQueryV2.unsubscribeBookDepthUpdateForClassV2(sessionName,
                classKey, orderBookUpdateConsumerV2._this());
    }

    private void doSubscribeBookDepthUpdateForProductV2(String command[])
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

        marketQueryV2.subscribeBookDepthUpdateForProductV2(sessionName,
                productKey, orderBookUpdateConsumerV2._this(),
                actionOnQueue);
    }

    private void doUnsubscribeBookDepthUpdateForProductV2(String command[])
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

        marketQueryV2.unsubscribeBookDepthUpdateForProductV2(sessionName,
                productKey, orderBookUpdateConsumerV2._this());
    }

    private void doGetBookDepthDetails(String command[])
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

        BookDepthStructV2 bd =
                marketQueryV2.getBookDepthDetails(sessionName, productKey);
        Log.message(Struct.toString(bd));
    }
}
