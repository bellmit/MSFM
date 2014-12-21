package com.cboe.test.application.cmiScript;

public class UserSessionManagerV8 extends UserSessionManagerV7
{
    UserSessionManagerV8(EngineAccess ea,
            com.cboe.idl.cmiV8.UserSessionManagerV8 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV8 function args...
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
            // No commands added here, pass request to V7 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
