package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV5.UserTradingParameters;
import com.cboe.idl.cmiUtil.KeyValueStruct;

public class UserTradingParametersV5 extends UserTradingParametersV1
{
    private EngineAccess engineAccess;
    private UserTradingParameters userTradingParametersV5;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public UserTradingParametersV5(EngineAccess ea, UserTradingParameters uh)
    {
        super(ea, uh);
        engineAccess = ea;
        userTradingParametersV5 = uh;
    }

    /** Execute a command on a UserTradingParameters object.
     * @param command Words from command line: UserTradingParametersV5 function args...
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
            if (cmd.equalsIgnoreCase("getUserRateSettings"))
            {
                doGetUserRateSettings(command);
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

    private void doGetUserRateSettings(String command[])
            throws Throwable
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

        KeyValueStruct kvseq[] =
                userTradingParametersV5.getUserRateSettings(sessionName);
        Log.message(Struct.toString(kvseq));
    }
}
