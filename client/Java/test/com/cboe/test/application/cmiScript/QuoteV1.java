package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.Quote;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;


public class QuoteV1
{
    private QuoteStatusConsumer quoteStatusConsumer;
    private RFQConsumer rfqConsumer;

    private EngineAccess engineAccess;
    private Quote quoteV1;

    private static final boolean SUBSCRIBE = true;
    private static final boolean UNSUBSCRIBE = false;
    private static final boolean PUBLISH = true;
    private static final boolean NO_PUBLISH = false;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public QuoteV1(EngineAccess ea, Quote q)
    {
        engineAccess = ea;
        quoteV1 = q;

        quoteStatusConsumer = new QuoteStatusConsumer();
        engineAccess.associateWithOrb(quoteStatusConsumer);
        rfqConsumer = new RFQConsumer();
        engineAccess.associateWithOrb(rfqConsumer);
    }

    /** Execute a command on a Quote object.
     * @param command Words from command line: QuoteV1 function args...
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
            if (cmd.equalsIgnoreCase("acceptQuote"))
            {
                doAcceptQuote(command);
            }
            else if (cmd.equalsIgnoreCase("acceptQuotesForClass"))
            {
                doAcceptQuotesForClass(command);
            }
            else if (cmd.equalsIgnoreCase("getQuote"))
            {
                doGetQuote(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatus"))
            {
                doSubQuoteStatus(command, PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusWithoutPublish"))
            {
                doSubQuoteStatus(command, NO_PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatus"))
            {
                doUnsubscribeQuoteStatus();
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusForFirm"))
            {
                doSubQuoteStatusForFirm(command, PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("subscribeQuoteStatusForFirmWithoutPublish"))
            {
                doSubQuoteStatusForFirm(command, NO_PUBLISH);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeQuoteStatusForFirm"))
            {
                doUnsubscribeQuoteStatusForFirm();
            }
            else if (cmd.equalsIgnoreCase("cancelQuote"))
            {
                doCancelQuote(command);
            }
            else if (cmd.equalsIgnoreCase("cancelAllQuotes"))
            {
                doCancelAllQuotes(command);
            }
            else if (cmd.equalsIgnoreCase("cancelQuotesByClass"))
            {
                doCancelQuotesByClass(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeRFQ"))
            {
                doSubRFQ(command, SUBSCRIBE);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeRFQ"))
            {
                doSubRFQ(command, UNSUBSCRIBE);
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

    private void doAcceptQuote(String command[]) throws Throwable
    {
        String names[] = { "quote" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing quote");
            return;
        }
        
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof QuoteEntryStruct))
        {
            Log.message("Not a QuoteEntryStruct:" + objName);
            return;
        }
        QuoteEntryStruct qe = (QuoteEntryStruct) o;
        quoteV1.acceptQuote(qe);
    }

    private void doAcceptQuotesForClass(String command[]) throws Throwable
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

        String objName = values[1];
        if (objName == null)
        {
            Log.message("Missing quotes");
            return;
        }
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
        QuoteEntryStruct qesseq[] = (QuoteEntryStruct[]) o;

        quoteV1.acceptQuotesForClass(classKey, qesseq);
    }

    private void doGetQuote(String command[]) throws Throwable
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

        QuoteDetailStruct qd = quoteV1.getQuote(sessionName, productKey);
        Log.message(Struct.toString(qd));
    }

    private void doSubQuoteStatus(String command[], boolean publish)
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

        if (publish)
        {
            quoteV1.subscribeQuoteStatus(quoteStatusConsumer._this(), gmdCallback);
        }
        else
        {
            quoteV1.subscribeQuoteStatusWithoutPublish(quoteStatusConsumer._this(), gmdCallback);
        }
    }

    private void doUnsubscribeQuoteStatus() throws Throwable
    {
        quoteV1.unsubscribeQuoteStatus(quoteStatusConsumer._this());
    }

    private void doSubQuoteStatusForFirm(String command[], boolean publish)
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

        if (publish)
        {
            quoteV1.subscribeQuoteStatusForFirm(quoteStatusConsumer._this(), gmdCallback);
        }
        else
        {
            quoteV1.subscribeQuoteStatusForFirmWithoutPublish(quoteStatusConsumer._this(), gmdCallback);
        }
    }

    private void doUnsubscribeQuoteStatusForFirm() throws Throwable
    {
        quoteV1.unsubscribeQuoteStatusForFirm(quoteStatusConsumer._this());
    }

    private void doCancelQuote(String command[]) throws Throwable
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

        quoteV1.cancelQuote(sessionName, productKey);
    }

    private void doCancelAllQuotes(String command[]) throws Throwable
    {
        String names[] = { "sessionName" };
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

        quoteV1.cancelAllQuotes(sessionName);
    }

    private void doCancelQuotesByClass(String command[]) throws Throwable
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

        quoteV1.cancelQuotesByClass(sessionName, classKey);
    }

    private void doSubRFQ(String command[], boolean subscribe) throws Throwable
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
            quoteV1.subscribeRFQ(sessionName, classKey, rfqConsumer._this());
        }
        else
        {
            quoteV1.unsubscribeRFQ(sessionName, classKey, rfqConsumer._this());
        }
    }
}
