//
// -----------------------------------------------------------------------------------
// Source file: AdminServiceCommandCache.java
//
// PACKAGE: com.cboe.presentation.adminRequest;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import java.util.*;
import java.util.concurrent.*;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.GetAllCommandsService;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

/**
* An AdminServiceCommandCache implements a cache of Admin Request commands.
*/
class AdminServiceCommandCache
{
	@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private Map<String, Map<String, ARCommand[]>> commandMapsByOrbName;

    private final Map<String, String> keyLockObjectMap = new HashMap<String, String>(101);

    AdminServiceCommandCache()
    {
        initialize();
    }

    /**
     * Get the specified command for the specified orb.
     * @param orbName name of orb that the command will be sent to
     * @param shortName of the command
     * @return the command if a command with the short name can be found for the orb.  If multiple
     *         commands map to the same short name, the first command that matches the short name
     *         will be returned. If the command cannot be found, null will be returned.
     */
    ARCommand getCommand(String orbName, String shortName)
    {
        ARCommand returnCommand = null;

        Object lockObject = findKeyLockObject(orbName, true);

        synchronized(lockObject)
        {
            Map<String, ARCommand[]> commandMap = commandMapsByOrbName.get(orbName);
            if(commandMap == null || commandMap.isEmpty())
            {
                if(commandMap == null)
                {
                    commandMap = new HashMap<String, ARCommand[]>(3);
                    commandMapsByOrbName.put(orbName, commandMap);
                }
                populateCommandMap(orbName, commandMap);
            }
            ARCommand[] commandArray = commandMap.get(shortName);
            if(commandArray != null && commandArray.length > 0)
            {
                returnCommand = commandArray[0];
            }
        }
        return returnCommand;
    }

    ARCommand[] getAllCommands(String orbName)
    {
        ARCommand[] returnCommands;

        Object lockObject = findKeyLockObject(orbName, true);

        synchronized(lockObject)
        {
            Map<String, ARCommand[]> commandMap = commandMapsByOrbName.get(orbName);
            if(commandMap == null || commandMap.isEmpty())
            {
                if(commandMap == null)
                {
                    commandMap = new HashMap<String, ARCommand[]>(3);
                    commandMapsByOrbName.put(orbName, commandMap);
                }
                populateCommandMap(orbName, commandMap);
            }

            List<ARCommand> commandsList = new ArrayList<ARCommand>(10);
            for(ARCommand[] commands : commandMap.values())
            {
                for(ARCommand command : commands)
                {
                    commandsList.add(command);
                }
            }

            returnCommands = commandsList.toArray(new ARCommand[commandsList.size()]);
        }
        return returnCommands;
    }

    /**
     * Get the specified command for the specified orb. The commandQualifier can be used to
     * further qualify the command if multiple commands map to the same short command name.
     * @param orbName name of orb that the command will be sent to
     * @param shortName of the command
     * @param commandQualifier additional qualifier that must be present in the full name to cause a
     * match.
     * @return the command if a command with the short name can be found for the orb.  If multiple
     *         commands map to the same short name, the first command that matches the short name
     *         and also contains the commandQualifier will be returned.  If the command cannot be
     *         found, null will be returned.
     */
    ARCommand getCommand(String orbName, String shortName, String commandQualifier)
    {
        ARCommand returnCommand = null;

        Object lockObject = findKeyLockObject(orbName, true);

        synchronized(lockObject)
        {
            Map<String, ARCommand[]> commandMap = commandMapsByOrbName.get(orbName);
            if(commandMap == null || commandMap.isEmpty())
            {
                if(commandMap == null)
                {
                    commandMap = new HashMap<String, ARCommand[]>(3);
                    commandMapsByOrbName.put(orbName, commandMap);
                }
                populateCommandMap(orbName, commandMap);
            }
            ARCommand[] commandArray = commandMap.get(shortName);
            if(commandArray != null)
            {
                for(ARCommand aCommandArray : commandArray)
                {
                    //noinspection NonPrivateFieldAccessedInSynchronizedContext
                    if(aCommandArray.getFullName().contains(commandQualifier))
                    {
                        returnCommand = aCommandArray;
                    }
                }
            }
        }
        return returnCommand;
    }

    private void initialize()
    {
        commandMapsByOrbName = new ConcurrentHashMap<String, Map<String, ARCommand[]>>(101);
    }

    /**
     * Populate the cmdMap with all of the commands defined for orbName.  A call is made to the orb
     * to retrieve the commands.  This call  may block while waiting for the list of commands to be
     * returned.
     * @param orbName to query for the list of commands
     * @param commandMap to put the list of commands in
     */
    private void populateCommandMap(String orbName, Map<String, ARCommand[]> commandMap)
    {
        CBOEProcess orbProcess;
        String orbProcessName = orbName;
        try
        {
            orbProcess = InstrumentationTranslatorFactory.find().getProcess(orbName, null);
            if(orbProcess != null)
            {
                orbProcessName = orbProcess.getProcessName();
            }
        }
        catch(DataValidationException e)
        {
            GUILoggerHome.find().exception("Unable to find process for orb " + orbName, e);
        }

        GetAllCommandsService service = ARCommandServiceFactory.getAllCommandsService();
        service.setDestination(orbProcessName);
        try
        {
            Command[] commands = service.getAllCommands();
            if(commands != null)
            {
                for(Command command : commands)
                {
                    ARCommand arCommand = CommandFactory.buildARCommand(command);
                    addCommand(commandMap, arCommand);
                }
            }
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception("Exception retrieving Admin Service Commands for " +
                                           orbName, e);
        }
        catch(TimedOutException e)
        {
            GUILoggerHome.find().exception("Exception retrieving Admin Service Commands for " +
                                           orbName, e);
        }
    }

    /**
	 * Adds the specified command to the cache
	 * @param commandMap to hold the command
	 * @param command the command to add.  It must be initialized before
	 * calling this method.
	 */
    private void addCommand(Map<String, ARCommand[]> commandMap, ARCommand command)
    {
        if(command != null && commandMap != null)
        {
            if(GUILoggerHome.find().isDebugOn() &&
               GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON))
            {
                StringBuilder buffer = new StringBuilder(1000);
                buffer.append("Adding Command:\n");
                buffer.append(command.toString());
                GUILoggerHome.find().debug(buffer.toString(), GUILoggerBusinessProperty.COMMON);
            }
            ARCommand[] commandArray = commandMap.get(command.getName());
            if(commandArray != null)
            {
                ARCommand[] newArray = new ARCommand[commandArray.length + 1];
                System.arraycopy(commandArray, 0, newArray, 0, commandArray.length);
                newArray[commandArray.length] = command;
                commandArray = newArray;
            }
            else
            {
                commandArray = new ARCommand[1];
                commandArray[0] = command;
            }
            commandMap.put(command.getName(), commandArray);
        }
    }

    /**
     * Tries to find the existing cache String lock object for matching key
     * @param key to find lock object for
     * @param addIfNotFound If true and lock object was not found, one will be created and returned.
     * If false and one is not found, null will be returned.
     * @return found lock object, new lock object, or null depending on the value of addIfNotFound
     *         parameter
     */
    protected Object findKeyLockObject(String key, boolean addIfNotFound)
    {
        String cacheKey = key;
        synchronized(keyLockObjectMap)
        {
            String foundCacheKey = keyLockObjectMap.get(cacheKey);
            if(foundCacheKey != null)
            {
                cacheKey = foundCacheKey;
            }
            else
            {
                if(addIfNotFound)
                {
                    //noinspection RedundantStringConstructorCall
                    keyLockObjectMap.put(cacheKey, new String(cacheKey));
                }
                else
                {
                    cacheKey = null;
                }
            }
        }

        return cacheKey;
    }
}