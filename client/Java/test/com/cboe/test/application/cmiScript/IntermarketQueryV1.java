package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiIntermarket.IntermarketQuery;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

public class IntermarketQueryV1
{
    private EngineAccess engineAccess;
    private IntermarketQuery intermarketQueryV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public IntermarketQueryV1(EngineAccess ea, IntermarketQuery iq)
    {
        engineAccess = ea;
        intermarketQueryV1 = iq;
    }

    /** Execute a command on an IntermarketQuery object.
     * @param command Words from command line: IntermarketQueryV1 function args...
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
            if (cmd.equalsIgnoreCase("getIntermarketByProductForSession"))
            {
                doGetIntermarketByProductForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getIntermarketByClassForSession"))
            {
                doGetIntermarketByClassForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getAdminMessage"))
            {
                doGetAdminMessage(command);
            }
            else if (cmd.equalsIgnoreCase("getDetailedOrderBook"))
            {
                doGetDetailedOrderBook(command);
            }
            else if (cmd.equalsIgnoreCase("showMarketableOrderBookAtPrice"))
            {
                doShowMarketableOrderBookAtPrice(command);
            }
            else if (cmd.equalsIgnoreCase("getOrderBookStatus"))
            {
                doGetOrderBookStatus(command);
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

    private void doGetIntermarketByProductForSession(String command[])
            throws Throwable
    {
        String names[] = { "productKey", "session" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        CurrentIntermarketStruct ci = intermarketQueryV1
                .getIntermarketByProductForSession(productKey, session);
        Log.message(Struct.toString(ci));
    }

    private void doGetIntermarketByClassForSession(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "session" };
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
            Log.message("Missing session");
            return;
        }
        String session = values[1];

        CurrentIntermarketStruct ci[] = intermarketQueryV1
                .getIntermarketByClassForSession(classKey, session);
        Log.message(Struct.toString(ci));
    }

    private void doGetAdminMessage(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "adminMessageKey",
                "sourceExchange" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing adminMessageKey");
            return;
        }
        int adminMessageKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing sourceExchange");
            return;
        }
        String sourceExchange = values[3];

        AdminStruct asseq[] = intermarketQueryV1.getAdminMessage(
                session, productKey, adminMessageKey, sourceExchange);
        Log.message(Struct.toString(asseq));
    }

    private void doGetDetailedOrderBook(String command[]) throws Throwable
    {
        String names[] = { "session", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        BookDepthDetailedStruct bdd = intermarketQueryV1
                .getDetailedOrderBook(session, productKey);
        Log.message(Struct.toString(bdd));
    }

    private void doShowMarketableOrderBookAtPrice(String command[])
            throws Throwable
    {
        String names[] = { "session", "productKey", "openingPrice" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing openingPrice");
            return;
        }
        PriceStruct openingPrice = Struct.makePriceStruct(values[2]);

        BookDepthDetailedStruct bdd =
                intermarketQueryV1.showMarketableOrderBookAtPrice(
                        session, productKey, openingPrice);
        Log.message(Struct.toString(bdd));
    }

    private void doGetOrderBookStatus(String command[]) throws Throwable
    {
        String names[] = { "session", "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing session");
            return;
        }
        String session = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        short orderBookStatus =
                intermarketQueryV1.getOrderBookStatus(session, productKey);

        Log.message(Short.toString(orderBookStatus));
    }
}
