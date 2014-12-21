package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiOrder.AuctionSubscriptionResultStruct;
import com.cboe.idl.cmiV3.OrderQuery;


public class OrderQueryV3 extends OrderQueryV2
{
    private AuctionConsumer auctionConsumer;

    private EngineAccess engineAccess;
    private OrderQuery orderQueryV3;

    private static final int INDEX_FIRST_PARAMETER = 2;

    private static final boolean SUBSCRIBE = true;
    private static final boolean UNSUBSCRIBE = false;

    public OrderQueryV3(EngineAccess ea, OrderQuery oq)
    {
        super(ea, oq);
        engineAccess = ea;
        orderQueryV3 = oq;

        auctionConsumer = new AuctionConsumer();
        engineAccess.associateWithOrb(auctionConsumer);
    }

    /** Execute a command on an OrderQuery object.
     * @param command Words from command line: OrderQueryV3 function args...
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
            if (cmd.equalsIgnoreCase("subscribeAuctionForClass"))
            {
                doSubAuctionForClass(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeAuctionForClass"))
            {
                doSubAuctionForClass(command, UNSUBSCRIBE);
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

    private void doSubAuctionForClass(String command[], boolean subscribe)
            throws Throwable
    {
        String names[] = { "sessionName", "classKey", "auctionTypes" };
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
            Log.message("Missing auctionTypes");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof short[]))
        {
            Log.message("Not an AuctionTypeSequence:" + objName);
            return;
        }
        short atseq[] = (short[]) o;

        if (subscribe)
        {
            AuctionSubscriptionResultStruct asrseq[] =
                    orderQueryV3.subscribeAuctionForClass(sessionName, classKey,
                            atseq, auctionConsumer._this());
            Log.message(Struct.toString(asrseq));
        }
        else
        {
            orderQueryV3.unsubscribeAuctionForClass(sessionName, classKey,
                    atseq, auctionConsumer._this());
        }
    }
}
