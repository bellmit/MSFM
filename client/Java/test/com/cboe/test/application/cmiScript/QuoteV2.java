package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV2.Quote;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;


public class QuoteV2 extends QuoteV1
{
    private QuoteStatusConsumerV2 quoteStatusConsumerV2;
    private LockedQuoteStatusConsumerV2 lockedQuoteStatusConsumerV2;
    private RFQConsumerV2 rfqConsumerV2;

    private EngineAccess engineAccess;
    private Quote quoteV2;

    private static final boolean SUBSCRIBE = true;
    private static final boolean UNSUBSCRIBE = false;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public QuoteV2(EngineAccess ea, Quote q)
    {
        super(ea, q);
        engineAccess = ea;
        quoteV2 = q;

        quoteStatusConsumerV2 = new QuoteStatusConsumerV2();
        engineAccess.associateWithOrb(quoteStatusConsumerV2);
        lockedQuoteStatusConsumerV2 = new LockedQuoteStatusConsumerV2();
        engineAccess.associateWithOrb(lockedQuoteStatusConsumerV2);
        rfqConsumerV2 = new RFQConsumerV2();
        engineAccess.associateWithOrb(rfqConsumerV2);
    }

    /** Execute a command on a Quote object.
     * @param command Words from command line: QuoteV2 function args...
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
            if (cmd.equalsIgnoreCase("acceptQuotesForClassV2"))
            {
                doAcceptQuotesForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusV2"))
            {
                doSubscribeQuoteStatusV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatusV2"))
            {
                doUnsubscribeQuoteStatusV2();
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusForFirmV2"))
            {
                doSubscribeQuoteStatusForFirmV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatusForFirmV2"))
            {
                doUnsubscribeQuoteStatusForFirmV2();
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusForClassV2"))
            {
                doSubscribeQuoteStatusForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatusForClassV2"))
            {
                doUnsubscribeQuoteStatusForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusForFirmForClassV2"))
            {
                doSubscribeQuoteStatusForFirmForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatusForFirmForClassV2"))
            {
                doUnsubscribeQuoteStatusForFirmForClassV2(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteLockedNotification"))
            {
                doSubscribeQuoteLockedNotification(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteLockedNotification"))
            {
                doUnsubscribeQuoteLockedNotification();
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteLockedNotificationForClass"))
            {
                doSubscribeQuoteLockedNotificationForClass(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteLockedNotificationForClass"))
            {
                doUnsubscribeQuoteLockedNotificationForClass(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeRFQV2"))
            {
                doSubRFQV2(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRFQV2"))
            {
                doSubRFQV2(command, UNSUBSCRIBE);
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

    private void doAcceptQuotesForClassV2(String command[]) throws Throwable
    {
        String names[] = { "classKey", "quotes" };
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
            Log.message("Missing quotes");
            return;
        }        
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof QuoteEntryStruct[]))
        {
            Log.message("Not a QuoteEntryStructSequence:" + objName);
            return;
        }
        QuoteEntryStruct qeseq[] = (QuoteEntryStruct[]) o;
        
        ClassQuoteResultStructV2 cqrseq[] =
                quoteV2.acceptQuotesForClassV2(classKey, qeseq);
        Log.message(Struct.toString(cqrseq));
    }

    private void doSubscribeQuoteStatusV2(String command[]) throws Throwable
    {
        String names[] = { "publishOnSubscribe", "includeUserInitiatedStatus",
                "gmdCallback" };
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
            Log.message("Missing includeUserInitiatedStatus");
            return;
        }
        boolean includeUserInitiatedStatus =
                CommandLine.booleanValue(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[2]);

        quoteV2.subscribeQuoteStatusV2(quoteStatusConsumerV2._this(),
                publishOnSubscribe, includeUserInitiatedStatus, gmdCallback);
    }

    private void doUnsubscribeQuoteStatusV2() throws Throwable
    {
        quoteV2.unsubscribeQuoteStatusV2(quoteStatusConsumerV2._this());    
    }

    private void doSubscribeQuoteStatusForFirmV2(String command[])
            throws Throwable
    {
        String names[] = { "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[0]);

        quoteV2.subscribeQuoteStatusForFirmV2(quoteStatusConsumerV2._this(),
                gmdCallback);    
    }

    private void doUnsubscribeQuoteStatusForFirmV2() throws Throwable
    {
        quoteV2.unsubscribeQuoteStatusForFirmV2(quoteStatusConsumerV2._this());
    }

    private void doSubscribeQuoteStatusForClassV2(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "publishOnSubscribe",
                "includeUserInitiatedStatus", "gmdCallback" };
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
            Log.message("Missing includeUserInitiatedStatus");
            return;
        }
        boolean includeUserInitiatedStatus =
                CommandLine.booleanValue(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[3]);

        quoteV2.subscribeQuoteStatusForClassV2(classKey, publishOnSubscribe,
                includeUserInitiatedStatus, quoteStatusConsumerV2._this(),
                gmdCallback);
    }

    private void doUnsubscribeQuoteStatusForClassV2(String command[])
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

        quoteV2.unsubscribeQuoteStatusForClassV2(classKey,
                quoteStatusConsumerV2._this());
    }

    private void doSubscribeQuoteStatusForFirmForClassV2(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "gmdCallback" };
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
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[1]);

        quoteV2.subscribeQuoteStatusForFirmForClassV2(classKey,
                quoteStatusConsumerV2._this(), gmdCallback);
    }

    private void doUnsubscribeQuoteStatusForFirmForClassV2(String command[])
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

        quoteV2.unsubscribeQuoteStatusForFirmForClassV2(classKey,
                quoteStatusConsumerV2._this());
    }

    private void doSubscribeQuoteLockedNotification(String command[])
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

        quoteV2.subscribeQuoteLockedNotification(publishOnSubscribe,
                lockedQuoteStatusConsumerV2._this(), gmdCallback);
    }

    private void doUnsubscribeQuoteLockedNotification() throws Throwable
    {
        quoteV2.unsubscribeQuoteLockedNotification(
                lockedQuoteStatusConsumerV2._this());
    }

    private void doSubscribeQuoteLockedNotificationForClass(String command[])
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

        quoteV2.subscribeQuoteLockedNotificationForClass(classKey,
                publishOnSubscribe, lockedQuoteStatusConsumerV2._this(),
                gmdCallback);
    }

    private void doUnsubscribeQuoteLockedNotificationForClass(String command[])
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

        quoteV2.unsubscribeQuoteLockedNotificationForClass(
                classKey, lockedQuoteStatusConsumerV2._this());
    }

    private void doSubRFQV2(String command[], boolean subscribe) throws Throwable
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
            quoteV2.subscribeRFQV2(sessionName, classKey, rfqConsumerV2._this());
        }
        else
        {
            quoteV2.unsubscribeRFQV2(sessionName, classKey, rfqConsumerV2._this());
        }
    }
}
