package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;

public class UserSessionManagerV1
{
    private EngineAccess engineAccess;
    private UserSessionManager userSessionManagerV1;

    private static final int INDEX_FIRST_PARAMETER = 2;

    UserSessionManagerV1(EngineAccess ea, UserSessionManager usm)
    {
        engineAccess = ea;
        userSessionManagerV1 = usm;
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Words from command line: UserAccessVn function args...
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
            if (cmd.equalsIgnoreCase("logout"))
            {
                doLogout();
            }
            else if (cmd.equalsIgnoreCase("authenticate"))
            {
                doAuthenticate(command);
            }
            else if (cmd.equalsIgnoreCase("getValidUser"))
            {
                doGetValidUser();
            }
            else if (cmd.equalsIgnoreCase("getValidSessionProfileUser"))
            {
                doGetValidSessionProfileUser();
            }
            else if (cmd.equalsIgnoreCase("changePassword"))
            {
                doChangePassword(command);
            }
            else if (cmd.equalsIgnoreCase("getVersion"))
            {
                doGetVersion();
            }
            else if (cmd.equalsIgnoreCase("getSystemDateTime"))
            {
                doGetSystemDateTime();
            }
            else
            {
                Log.message("Unknown function:" + cmd + " for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    void doLogout() throws Throwable
    {
        userSessionManagerV1.logout();
    }

    void doAuthenticate(String command[]) throws Throwable
    {
        String names[] = { "logonStruct" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            // getParameters reported an error, leave now.
            return;
        }

        if (values[0] == null)
        {
            Log.message("Missing logonStruct");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof UserLogonStruct))
        {
            Log.message("Not a UserLogonStruct:" + objName);
            return;
        }
        UserLogonStruct uls = (UserLogonStruct) o;
        userSessionManagerV1.authenticate(uls);
    }

    void doGetValidUser() throws Throwable
    {
        UserStruct userStruct = userSessionManagerV1.getValidUser();
        Log.message(Struct.toString(userStruct));
    }

    void doGetValidSessionProfileUser() throws Throwable
    {
        SessionProfileUserStruct spu = userSessionManagerV1.getValidSessionProfileUser();
        Log.message(Struct.toString(spu));
    }

    void doChangePassword(String command[]) throws Throwable
    {
        String names[] = { "oldPassword", "newPassword" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            // getParameters reported an error, leave now.
            return;
        }

        if (values[0] == null)
        {
            Log.message("Missing oldPassword");
            return;
        }
        String oldPassword = values[0];

        if (values[1] == null)
        {
            Log.message("Missing newPassword");
            return;
        }
        String newPassword = values[1];
        userSessionManagerV1.changePassword(oldPassword, newPassword);
    }

    void doGetVersion() throws Throwable
    {
        String version = userSessionManagerV1.getVersion();
        Log.message(version);
    }

    void doGetSystemDateTime() throws Throwable
    {
        DateTimeStruct dt = userSessionManagerV1.getSystemDateTime();
        Log.message(Struct.toString(dt));
    }
}
