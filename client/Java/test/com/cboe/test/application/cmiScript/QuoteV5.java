package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV5.Quote;


public class QuoteV5 extends QuoteV3
{
    private EngineAccess engineAccess;
    private Quote quoteV5;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public QuoteV5(EngineAccess ea, Quote q)
    {
        super(ea, q);
        engineAccess = ea;
        quoteV5 = q;
    }

    /** Execute a command on a QuoteV5 object.
     * @param command Words from command line: QuoteV5 function args...
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
            if (cmd.equalsIgnoreCase("cancelQuoteV5"))
            {
                doCancelQuoteV5(command);
            }
            else if (cmd.equalsIgnoreCase("cancelQuotesByClassV5"))
            {
                doCancelQuotesByClassV5(command);
            }
            else if (cmd.equalsIgnoreCase("cancelAllQuotesV5"))
            {
                doCancelAllQuotesV5(command);
            }
            else
            {
                // Maybe it's a V3 command; pass it to the V3 object
                super.doCommand(command);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doCancelQuoteV5(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey", "sendCancelReport" };
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
            Log.message("Missing sendCancelReport");
            return;
        }
        boolean sendCancelReport = CommandLine.booleanValue(values[2]);

        quoteV5.cancelQuoteV5(sessionName, productKey, sendCancelReport);
    }

    private void doCancelQuotesByClassV5(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "classKey", "sendCancelReports" };
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
            Log.message("Missing sendCancelReports");
            return;
        }
        boolean sendCancelReports = CommandLine.booleanValue(values[2]);

        quoteV5.cancelQuotesByClassV5(sessionName, classKey, sendCancelReports);
    }

    private void doCancelAllQuotesV5(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "sendCancelReports" };
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
            Log.message("Missing sendCancelReports");
            return;
        }
        boolean sendCancelReports = CommandLine.booleanValue(values[1]);

        quoteV5.cancelAllQuotesV5(sessionName, sendCancelReports);
    }
}
