package com.cboe.test.application.cmiScript;

public class UserSessionManagerV7 extends UserSessionManagerV6
{
    UserSessionManagerV7(EngineAccess ea,
            com.cboe.idl.cmiV7.UserSessionManagerV7 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV7 function args...
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
            // No commands added here, pass request to V6 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
