package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.Administrator;
import com.cboe.idl.cmiAdmin.MessageStruct;

public class AdministratorV1
{
    private EngineAccess engineAccess;
    private Administrator administratorV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public AdministratorV1(EngineAccess ea, Administrator a)
    {
        engineAccess = ea;
        administratorV1 = a;
    }

    /** Execute a command on a Administrator object.
     * @param command Words from command line: AdministratorV1 function args...
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
            if (cmd.equalsIgnoreCase("sendMessage"))
            {
                doSendMessage(command);
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

    private void doSendMessage(String command[]) throws Throwable
    {
        String names[] = { "message" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing message");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof MessageStruct))
        {
            Log.message("Not a MessageStruct:" + objName);
            return;
        }
        MessageStruct m = (MessageStruct) o;

        int messageKey = administratorV1.sendMessage(m);
        Log.message(Integer.toString(messageKey));
    }
}
