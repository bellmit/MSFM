package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV7.Quote;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteEntryStructV4;


public class QuoteV7 extends QuoteV5
{
    private EngineAccess engineAccess;
    private Quote quoteV7;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public QuoteV7(EngineAccess ea, Quote q)
    {
        super(ea, q);
        engineAccess = ea;
        quoteV7 = q;
    }

    /** Execute a command on a QuoteV7 object.
     * @param command Words from command line: QuoteV7 function args...
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
            if (cmd.equalsIgnoreCase("acceptQuotesForClassV7"))
            {
                doAcceptQuotesForClassV7(command);
            }
            else if (cmd.equalsIgnoreCase("acceptQuoteV7"))
            {
                doAcceptQuoteV7(command);
            }
            else
            {
                // Maybe it's a V5 command; pass it to the V5 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptQuotesForClassV7(String command[]) throws Throwable
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
        if (! (o instanceof QuoteEntryStructV4[]))
        {
            Log.message("Not a QuoteEntryStructV4Sequence:" + objName);
            return;
        }
        QuoteEntryStructV4 quotes[] = (QuoteEntryStructV4[]) o;

        ClassQuoteResultStructV3 cqrseq[] =
                quoteV7.acceptQuotesForClassV7(classKey, quotes);
        Log.message(Struct.toString(cqrseq));
    }

    private void doAcceptQuoteV7(String command[]) throws Throwable
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
        if (! (o instanceof QuoteEntryStructV4))
        {
            Log.message("Not a QuoteEntryStructV4:" + objName);
            return;
        }
        QuoteEntryStructV4 quote = (QuoteEntryStructV4) o;

        quoteV7.acceptQuoteV7(quote);
    }
}
