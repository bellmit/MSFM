package com.cboe.test.application.cmiScript;

public class UserSessionManagerV6 extends UserSessionManagerV5
{
    UserSessionManagerV6(EngineAccess ea,
            com.cboe.idl.cmiV6.UserSessionManagerV6 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV6 function args...
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
            // No commands added here, pass request to V5 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
