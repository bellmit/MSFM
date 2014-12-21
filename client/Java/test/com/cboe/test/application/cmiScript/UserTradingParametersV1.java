package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.UserTradingParameters;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;

public class UserTradingParametersV1
{
    private EngineAccess engineAccess;
    private UserTradingParameters userTradingParametersV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public UserTradingParametersV1(EngineAccess ea, UserTradingParameters uh)
    {
        engineAccess = ea;
        userTradingParametersV1 = uh;
    }

    /** Execute a command on a UserTradingParameters object.
     * @param command Words from command line: UserTradingParametersV1 function args...
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
            if (cmd.equalsIgnoreCase("getAllQuoteRiskProfiles"))
            {
                doGetAllQuoteRiskProfiles();
            }
            else if (cmd.equalsIgnoreCase("getQuoteRiskManagementProfileByClass"))
            {
                doGetQuoteRiskManagementProfileByClass(command);
            }
            else if (cmd.equalsIgnoreCase("setQuoteRiskManagementEnabledStatus"))
            {
                doSetQuoteRiskManagementEnabledStatus(command);
            }
            else if (cmd.equalsIgnoreCase("getQuoteRiskManagementEnabledStatus"))
            {
                doGetQuoteRiskManagementEnabledStatus();
            }
            else if (cmd.equalsIgnoreCase("getDefaultQuoteRiskProfile"))
            {
                doGetDefaultQuoteRiskProfile();
            }
            else if (cmd.equalsIgnoreCase("setQuoteRiskProfile"))
            {
                doSetQuoteRiskProfile(command);
            }
            else if (cmd.equalsIgnoreCase("removeQuoteRiskProfile"))
            {
                doRemoveQuoteRiskProfile(command);
            }
            else if (cmd.equalsIgnoreCase("removeAllQuoteRiskProfiles"))
            {
                doRemoveAllQuoteRiskProfiles();
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

    private void doGetAllQuoteRiskProfiles() throws Throwable
    {
        UserQuoteRiskManagementProfileStruct uqrmp =
                userTradingParametersV1.getAllQuoteRiskProfiles();
        Log.message(Struct.toString(uqrmp));
    }

    private void doGetQuoteRiskManagementProfileByClass(String command[])
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

        QuoteRiskManagementProfileStruct uqrmp =
                userTradingParametersV1.getQuoteRiskManagementProfileByClass(
                        classKey);
        Log.message(Struct.toString(uqrmp));
    }

    private void doSetQuoteRiskManagementEnabledStatus(String command[])
            throws Throwable
    {
        String names[] = { "status" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing status");
            return;
        }
        boolean status = CommandLine.booleanValue(values[0]);

        userTradingParametersV1.setQuoteRiskManagementEnabledStatus(status);
    }

    private void doGetQuoteRiskManagementEnabledStatus() throws Throwable
    {
        boolean status =
                userTradingParametersV1.getQuoteRiskManagementEnabledStatus();
        Log.message(Boolean.valueOf(status).toString());
    }

    private void doGetDefaultQuoteRiskProfile() throws Throwable
    {
        QuoteRiskManagementProfileStruct qrmp =
                userTradingParametersV1.getDefaultQuoteRiskProfile();
        Log.message(Struct.toString(qrmp));
    }

    private void doSetQuoteRiskProfile(String command[]) throws Throwable
    {
        String names[] = { "quoteRiskProfile" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing quoteRiskProfile");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof QuoteRiskManagementProfileStruct))
        {
            Log.message("Not a QuoteRiskManagementProfileStruct:" + objName);
            return;
        }
        QuoteRiskManagementProfileStruct qrmp =
                (QuoteRiskManagementProfileStruct) o;

        userTradingParametersV1.setQuoteRiskProfile(qrmp);
    }

    private void doRemoveQuoteRiskProfile(String command[]) throws Throwable
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

        userTradingParametersV1.removeQuoteRiskProfile(classKey);
    }

    private void doRemoveAllQuoteRiskProfiles() throws Throwable
    {
        userTradingParametersV1.removeAllQuoteRiskProfiles();
    }
}
