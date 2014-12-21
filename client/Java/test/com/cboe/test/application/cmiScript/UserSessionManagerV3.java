package com.cboe.test.application.cmiScript;

public class UserSessionManagerV3 extends UserSessionManagerV1
{
    UserSessionManagerV3(EngineAccess ea,
            com.cboe.idl.cmiV3.UserSessionManagerV3 usm)
    {
        super(ea, usm);
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
            // No commands added here, pass request to V1 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
