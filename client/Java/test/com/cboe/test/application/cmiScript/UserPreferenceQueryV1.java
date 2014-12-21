package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.UserPreferenceQuery;
import com.cboe.idl.cmiUser.PreferenceStruct;

public class UserPreferenceQueryV1
{
    private EngineAccess engineAccess;
    private UserPreferenceQuery userPreferenceQueryV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public UserPreferenceQueryV1(EngineAccess ea, UserPreferenceQuery upq)
    {
        engineAccess = ea;
        userPreferenceQueryV1 = upq;
    }

    /** Execute a command on a UserPreferenceQuery object.
     * @param command Words from command line: UserPreferenceQueryV1 function args...
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
            if (cmd.equalsIgnoreCase("setUserPreferences"))
            {
                doSetUserPreferences(command);
            }
            else if (cmd.equalsIgnoreCase("removeUserPreference"))
            {
                doRemoveUserPreference(command);
            }
            else if (cmd.equalsIgnoreCase("getAllUserPreferences"))
            {
                doGetAllUserPreferences();
            }
            else if (cmd.equalsIgnoreCase("getUserPreferencesByPrefix"))
            {
                doGetUserPreferencesByPrefix(command);
            }
            else if (cmd.equalsIgnoreCase("removeUserPreferencesByPrefix"))
            {
                doRemoveUserPreferencesByPrefix(command);
            }
            else if (cmd.equalsIgnoreCase("getAllSystemPreferences"))
            {
                doGetAllSystemPreferences();
            }
            else if (cmd.equalsIgnoreCase("getSystemPreferencesByPrefix"))
            {
                doGetSystemPreferencesByPrefix(command);
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

    private void doSetUserPreferences(String command[]) throws Throwable
    {
        String names[] = { "preferenceSequence" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing preferenceSequence");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof PreferenceStruct[]))
        {
            Log.message("Not an UserPreferenceQueryStruct:" + objName);
            return;
        }
        PreferenceStruct pseq[] = (PreferenceStruct []) o;

        userPreferenceQueryV1.setUserPreferences(pseq);
    }

    private void doRemoveUserPreference(String command[]) throws Throwable
    {
        String names[] = { "preferenceSequence" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing preferenceSequence");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof PreferenceStruct[]))
        {
            Log.message("Not an UserPreferenceQueryStruct:" + objName);
            return;
        }
        PreferenceStruct pseq[] = (PreferenceStruct []) o;

        userPreferenceQueryV1.removeUserPreference(pseq);
    }

    private void doGetAllUserPreferences() throws Throwable
    {
        PreferenceStruct pseq[] = userPreferenceQueryV1.getAllUserPreferences();
        Log.message(Struct.toString(pseq));
    }

    private void doGetUserPreferencesByPrefix(String command[]) throws Throwable
    {
        String names[] = { "prefix" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing prefix");
            return;
        }
        String prefix = values[0];

        PreferenceStruct pseq[] =
                userPreferenceQueryV1.getUserPreferencesByPrefix(prefix);
        Log.message(Struct.toString(pseq));
    }

    private void doRemoveUserPreferencesByPrefix(String command[])
            throws Throwable
    {
        String names[] = { "prefix" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing prefix");
            return;
        }
        String prefix = values[0];

        userPreferenceQueryV1.removeUserPreferencesByPrefix(prefix);
    }

    private void doGetAllSystemPreferences() throws Throwable
    {
        PreferenceStruct pseq[] =
                userPreferenceQueryV1.getAllSystemPreferences();
        Log.message(Struct.toString(pseq));
    }

    private void doGetSystemPreferencesByPrefix(String command[]) throws Throwable
    {
        String names[] = { "prefix" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing prefix");
            return;
        }
        String prefix = values[0];

        PreferenceStruct pseq[] =
                userPreferenceQueryV1.getSystemPreferencesByPrefix(prefix);
        Log.message(Struct.toString(pseq));
    }
}
