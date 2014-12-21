package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.UserHistory;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

public class UserHistoryV1
{
    private EngineAccess engineAccess;
    private UserHistory userHistoryV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public UserHistoryV1(EngineAccess ea, UserHistory uh)
    {
        engineAccess = ea;
        userHistoryV1 = uh;
    }

    /** Execute a command on a UserHistory object.
     * @param command Words from command line: UserHistoryV1 function args...
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
            if (cmd.equalsIgnoreCase("getTraderClassActivityByTime"))
            {
                doGetTraderClassActivityByTime(command);
            }
            else if (cmd.equalsIgnoreCase("getTraderProductActivityByTime"))
            {
                doGetTraderProductActivityByTime(command);
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

    private void doGetTraderClassActivityByTime(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "classKey", "startTime", "direction" };
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
            Log.message("Missing startTime");
            return;
        }
        DateTimeStruct startTime = Struct.makeDateTimeStruct(values[2]);
        if (startTime == null)
        {
            return; // error already reported, leave now.
        }

        if (values[3] == null)
        {
            Log.message("Missing direction");
            return;
        }
        short direction = Short.parseShort(values[3]);

        ActivityHistoryStruct ah = userHistoryV1.getTraderClassActivityByTime(
                sessionName, classKey, startTime, direction);
        Log.message(Struct.toString(ah));
    }

    private void doGetTraderProductActivityByTime(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey", "startTime", "direction" };
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
            Log.message("Missing startTime");
            return;
        }
        DateTimeStruct startTime = Struct.makeDateTimeStruct(values[2]);
        if (startTime == null)
        {
            return; // error already reported, leave now.
        }

        if (values[3] == null)
        {
            Log.message("Missing direction");
            return;
        }
        short direction = Short.parseShort(values[3]);

        ActivityHistoryStruct ah = userHistoryV1.getTraderProductActivityByTime(
                sessionName, productKey, startTime, direction);
        Log.message(Struct.toString(ah));
    }
}
