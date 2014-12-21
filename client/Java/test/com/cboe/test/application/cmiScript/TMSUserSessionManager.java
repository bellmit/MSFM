package com.cboe.test.application.cmiScript;

public class TMSUserSessionManager extends UserSessionManagerV4
{
    TMSUserSessionManager(EngineAccess ea,
            com.cboe.idl.cmiTradeMaintenanceService.TMSUserSessionManager usm)
    {
        super(ea, usm);
    }
    
    /** Execute a command on a UserSessionManager object.
     * @param command Command line: TMSUserSessionManager function args...
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
            // No commands added here, pass request to V4 object
            super.doCommand(command);
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }
}
