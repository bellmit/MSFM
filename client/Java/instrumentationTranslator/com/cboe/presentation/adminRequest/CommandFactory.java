//
// -----------------------------------------------------------------------------------
// Source file: CommandFactory.java
//
// PACKAGE: com.cboe.presentation.adminRequest;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
 * Provides a factory for obtaining a list of available commands for an ORB. This factory
 * implements a cache on top of the service interfaces. It is advised to use this factory for
 * obtaining any list of AR commands for an ORB. It is implemented as a Singleton.
 */
public class CommandFactory
{
    protected AdminServiceCommandCache cmdCache;

    private static CommandFactory ourInstance = new CommandFactory();

    /**
     * Gets the singleton instance of this manager.
     */
    public static CommandFactory getInstance()
    {
        return ourInstance;
    }

    /**
     * Provides a way to create a new ARCommand object from an IDL Command.
     * Generally this should not be used, but rather call one of the get methods to find
     * an existing one. This can be used for creating a new one manually.
     * @param command from IDL to wrap with ARCommand
     * @return ARCommand that wraps the passed command
     */
    public static ARCommand buildARCommand(Command command)
    {
        return new ARCommandImpl(command);
    }

    private CommandFactory()
    {
        cmdCache = new AdminServiceCommandCache();
    }

    /**
     * Parses short name from the full name. This common implementation should be used
     * by anything needing to parse the short name. This ensures a common implementation for
     * everything, even if we need to change the implementation later.
     * @param fullName to parse short name from
     * @return short name from fullName. If a short name was not parsible from the fullName,
     * the fullName itself is returned.
     */
    public String parseShortName(String fullName)
    {
        String shortName = fullName;
        if(fullName != null)
        {
            int pos = fullName.lastIndexOf('.');
            if(pos != -1)
            {
                shortName = fullName.substring(pos + 1);
            }
        }
        return shortName;
    }

    /**
     * Parses the qualifying part of the name from the full name. This common implementation should
     * be used by anything needing to parse the qualifying part of the name. This ensures a
     * common implementation for everything, even if we need to change the implementation later.
     * @param fullName to parse the qualifying part of the name from
     * @return qualifying part of the name from fullName. If a qualifying part of the name
     * was not parsible from the fullName, the fullName itself is returned.
     */
    public String parseQualifierFromName(String fullName)
    {
        String shortName = fullName;
        if(fullName != null)
        {
            int pos = fullName.lastIndexOf('.');
            if(pos != -1)
            {
                shortName = fullName.substring(0, pos);
            }
        }
        return shortName;
    }

    /**
     * Gets all the available ARCommand's for the passed ORB. This implementation will use
     * the internal cache.
     * @param orbName to get ARCommand's for
     * @return array of ARCommand's for ORB. May return a zero-length array. Will not return null.
     */
    public ARCommand[] getAllCommands(String orbName)
    {
        return cmdCache.getAllCommands(orbName);
    }

    /**
     * Gets a command with the specified name. The commandName is the short name
     * (the last segment of the fully qualified command name). This implementation will use
     * the internal cache.
     * @param orbName to retrieve command list from
     * @param commandName short name of command
     * @return the appropriate command. If the command with commandName could not be found,
     * a new one will be created on demand. This new one will not be cached.
     */
    public ARCommand getCommand(String orbName, String commandName)
    {
        ARCommand returnCommand = cmdCache.getCommand(orbName, commandName);
        if(returnCommand == null)
        {
            returnCommand = createCommand(commandName);
        }
        return returnCommand;
    }

    /**
     * Gets a command with the specified name. The commandName is the short name
     * (the last segment of the fully qualified command name). The commandQualifier is
     * supplied to distinguish between multiple instances of the same short name.
     * This implementation will use the internal cache.
     * @param orbName to retrieve command list from
     * @param commandName short name of command
     * @param commandQualifier is an additional string that must be included in
     * the fully qualified command name in order for it to be returned from the
     * orb command list.
     * @return the appropriate command. If the command with commandName could not be found,
     * a new one will be created on demand. This new one will not be cached.
     */
    public ARCommand getCommand(String orbName, String commandName, String commandQualifier)
    {
        ARCommand returnCommand = cmdCache.getCommand(orbName, commandName, commandQualifier);
        if(returnCommand == null)
        {
            returnCommand = createCommand(commandName);
        }
        return returnCommand;
    }

    /**
     * Determines if the cache contains a command.
     * @param orbName to check cache for
     * @param commandName to check cache for. The commandName is the short name
     * (the last segment of the fully qualified command name).
     * @return true if found a command for passed orbName, with a short name of passed
     * commandName.
     */
    public boolean containsCommand(String orbName, String commandName)
    {
        ARCommand returnCommand = cmdCache.getCommand(orbName, commandName);
        return (returnCommand != null);
    }

    /**
	 * Create a new command with the given command name.
	 * @param commandName to be created
	 * @return completed command
	 */
    private ARCommand createCommand(String commandName)
    {
        Command newCommand = new Command();
        newCommand.name = commandName;
        return buildARCommand(newCommand);
    }
}
