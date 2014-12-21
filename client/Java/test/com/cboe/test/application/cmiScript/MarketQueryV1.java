package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.MarketQuery;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

public class MarketQueryV1
{
    private EngineAccess engineAccess;
    private MarketQuery marketQueryV1;
    private RecapConsumer recapConsumer;
    private CurrentMarketConsumer currentMarketConsumer;
    private NBBOConsumer nbboConsumer;
    private TickerConsumer tickerConsumer;
    private ExpectedOpeningPriceConsumer expectedOpeningPriceConsumer;
    private OrderBookConsumer orderBookConsumer;
    private OrderBookUpdateConsumer orderBookUpdateConsumer;

    private static final boolean SUBSCRIBE = true;
    private static final boolean UNSUBSCRIBE = false;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public MarketQueryV1(EngineAccess ea, MarketQuery mq)
    {
        engineAccess = ea;
        marketQueryV1 = mq;

        recapConsumer = new RecapConsumer();
        engineAccess.associateWithOrb(recapConsumer);
        currentMarketConsumer = new CurrentMarketConsumer();
        engineAccess.associateWithOrb(currentMarketConsumer);
        nbboConsumer = new NBBOConsumer();
        engineAccess.associateWithOrb(nbboConsumer);
        tickerConsumer = new TickerConsumer();
        engineAccess.associateWithOrb(tickerConsumer);
        expectedOpeningPriceConsumer = new ExpectedOpeningPriceConsumer();
        engineAccess.associateWithOrb(expectedOpeningPriceConsumer);
        orderBookConsumer = new OrderBookConsumer();
        engineAccess.associateWithOrb(orderBookConsumer);
        orderBookUpdateConsumer = new OrderBookUpdateConsumer();
        engineAccess.associateWithOrb(orderBookUpdateConsumer);
    }

    /** Execute a command on a MarketQuery object.
     * @param command Words from command line: MarketQueryV1 function args...
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
            if (cmd.equalsIgnoreCase("subscribeRecapForClass"))
            {
                doSubRecapForClass(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeRecapForProduct"))
            {
                doSubRecapForProduct(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForClass"))
            {
                doSubCurrentMarketForClass(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeCurrentMarketForProduct"))
            {
                doSubCurrentMarketForProduct(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeNBBOForClass"))
            {
                doSubNBBOForClass(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeNBBOForProduct"))
            {
                doSubNBBOForProduct(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarketForProduct"))
            {
                doSubCurrentMarketForProduct(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeCurrentMarketForClass"))
            {
                doSubCurrentMarketForClass(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeNBBOForProduct"))
            {
                doSubNBBOForProduct(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeNBBOForClass"))
            {
                doSubNBBOForClass(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeTicker"))
            {
                doSubTicker(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRecapForProduct"))
            {
                doSubRecapForProduct(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRecapForClass"))
            {
                doSubRecapForClass(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeTicker"))
            {
                doSubTicker(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("getMarketDataHistoryByTime"))
            {
                doGetMarketDataHistoryByTime(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeExpectedOpeningPrice"))
            {
                doSubExpectedOpeningPrice(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeExpectedOpeningPrice"))
            {
                doSubExpectedOpeningPrice(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("getBookDepth"))
            {
                doGetBookDepth(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepth"))
            {
                doSubBookDepth(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepth"))
            {
                doSubBookDepth(command, UNSUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("subscribeBookDepthUpdate"))
            {
                doSubBookDepthUpdate(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeBookDepthUpdate"))
            {
                doSubBookDepthUpdate(command, UNSUBSCRIBE);
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

    private void doSubRecapForClass(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeRecapForClass(sessionName, classKey,
                    recapConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeRecapForClass(sessionName, classKey,
                    recapConsumer._this());
        }
    }

    private void doSubRecapForProduct(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeRecapForClass(sessionName, productKey,
                    recapConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeRecapForClass(sessionName, productKey,
                    recapConsumer._this());
        }
    }

    private void doSubCurrentMarketForClass(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeCurrentMarketForClass(sessionName, classKey,
                    currentMarketConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeCurrentMarketForClass(sessionName, classKey,
                    currentMarketConsumer._this());
        }
    }

    private void doSubCurrentMarketForProduct(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeCurrentMarketForProduct(sessionName, productKey,
                    currentMarketConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeCurrentMarketForProduct(sessionName, productKey,
                    currentMarketConsumer._this());
        }
    }

    private void doSubNBBOForClass(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeNBBOForClass(sessionName, classKey,
                    nbboConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeNBBOForClass(sessionName, classKey,
                    nbboConsumer._this());
        }
    }

    private void doSubNBBOForProduct(String command[], boolean subscribe) throws Throwable
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

        if (subscribe)
        {
            marketQueryV1.subscribeNBBOForProduct(sessionName, productKey,
                    nbboConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeNBBOForProduct(sessionName, productKey,
                    nbboConsumer._this());
        }
    }


    private void doSubTicker(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeTicker(sessionName, productKey,
                    tickerConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeTicker(sessionName, productKey,
                    tickerConsumer._this());
        }
    }

    private void doGetMarketDataHistoryByTime(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey", "startTime",
                           "direction" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return;     // error already reported, leave now.
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

        MarketDataHistoryStruct mdh = marketQueryV1.getMarketDataHistoryByTime(
                sessionName, productKey, startTime, direction);
        Log.message(Struct.toString(mdh));
    }

    private void doSubExpectedOpeningPrice(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeExpectedOpeningPrice(sessionName, classKey,
                    expectedOpeningPriceConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeExpectedOpeningPrice(sessionName, classKey,
                    expectedOpeningPriceConsumer._this());
        }
    }

    private void doGetBookDepth(String command[]) throws Throwable
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

        BookDepthStruct bd = marketQueryV1.getBookDepth(sessionName, productKey);
        Log.message(Struct.toString(bd));
    }

    private void doSubBookDepth(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeBookDepth(sessionName, productKey,
                    orderBookConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeBookDepth(sessionName, productKey,
                    orderBookConsumer._this());
        }
    }

    private void doSubBookDepthUpdate(String command[], boolean subscribe)
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

        if (subscribe)
        {
            marketQueryV1.subscribeBookDepthUpdate(sessionName, productKey,
                    orderBookUpdateConsumer._this());
        }
        else
        {
            marketQueryV1.unsubscribeBookDepthUpdate(sessionName, productKey,
                    orderBookUpdateConsumer._this());
        }
    }
}
