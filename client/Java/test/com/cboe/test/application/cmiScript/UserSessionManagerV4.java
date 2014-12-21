package com.cboe.test.application.cmiScript;

public class UserSessionManagerV4 extends UserSessionManagerV3
{
    UserSessionManagerV4(EngineAccess ea,
            com.cboe.idl.cmiV4.UserSessionManagerV4 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV4 function args...
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
            // No commands added here, pass request to V3 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
