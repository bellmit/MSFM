package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV3.Quote;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;


public class QuoteV3 extends QuoteV2
{
    private EngineAccess engineAccess;
    private Quote quoteV3;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public QuoteV3(EngineAccess ea, Quote q)
    {
        super(ea, q);
        engineAccess = ea;
        quoteV3 = q;
    }

    /** Execute a command on a QuoteV3 object.
     * @param command Words from command line: QuoteV3 function args...
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
            if (cmd.equalsIgnoreCase("acceptQuotesForClassV3"))
            {
                doAcceptQuotesForClassV3(command);
            }
            else if (cmd.equalsIgnoreCase("cancelAllQuotesV3"))
            {
                doCancelAllQuotesV3(command);
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

    private void doAcceptQuotesForClassV3(String command[]) throws Throwable
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
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof QuoteEntryStructV3[]))
        {
            Log.message("Not a QuoteEntryStructV3Sequence:" + objName);
            return;
        }
        QuoteEntryStructV3 qeseq[] = (QuoteEntryStructV3[]) o;
        
        ClassQuoteResultStructV3 cqrseq[] =
                quoteV3.acceptQuotesForClassV3(classKey, qeseq);
        Log.message(Struct.toString(cqrseq));
    }

    private void doCancelAllQuotesV3(String command[]) throws Throwable
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

        quoteV3.cancelAllQuotesV3(sessionName);
    }
}
