package com.cboe.test.application.cmiScript;

public class UserSessionManagerV5 extends TMSUserSessionManager
{
    UserSessionManagerV5(EngineAccess ea,
            com.cboe.idl.cmiV5.UserSessionManagerV5 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV5 function args...
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
            // No commands added here, pass request to TMS object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
