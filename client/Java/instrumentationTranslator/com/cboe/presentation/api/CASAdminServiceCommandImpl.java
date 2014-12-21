//
// ------------------------------------------------------------------------
// FILE: CASAdminServiceCommandImpl.java
// 
// PACKAGE: com.cboe.presentation.api
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.api;

/**
 * @author torresl@cboe.com
 */
public class CASAdminServiceCommandImpl implements CASAdminServiceCommand
{
    protected String processName;
    protected String orbName;
    protected String shortName;
    protected String commandName;
    public CASAdminServiceCommandImpl()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        processName = "";
        orbName = "";
        shortName = "";
        commandName = "";
    }

    public String getProcessName()
    {
        return processName;
    }

    public void setProcessName(String processName)
    {
        this.processName = processName;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public void setOrbName(String orbName)
    {
        this.orbName = orbName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public void setCommandName(String commandName)
    {
        this.commandName = commandName;
    }
}
