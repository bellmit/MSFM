package com.cboe.test.application.cmiScript;

public class UserSessionManagerV9 extends UserSessionManagerV8
{
    UserSessionManagerV9(EngineAccess ea,
            com.cboe.idl.cmiV9.UserSessionManagerV9 usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: UserSessionManagerV9 function args...
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
            // No commands added here, pass request to V8 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
