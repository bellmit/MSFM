//
// -----------------------------------------------------------------------------------
// Source file: PersistentCommandImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest.savedCommand
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest.savedCommand;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.Delimeter;
import com.cboe.interfaces.instrumentation.Status;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.PersistentCommand;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.adminRequest.CommandFactory;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

/**
 * Implements the PersistenCommand interfaces and to represent an  ARCommand that is saved
 * with a list of ORB's and data, for quick recall and execution.
 */
public class PersistentCommandImpl implements PersistentCommand
{
    public static final String NULL_VALUE = "\u0003\u0004";

    private String name;
    private String description;
    private String fullCommandName;
    private List<String> orbNames;
    private List<String> argumentValues;
    private int timeoutMillis;
    private ARCommand cachedCommand;

    private PersistentCommandImpl()
    {
        orbNames = new ArrayList<String>(5);
        argumentValues = new ArrayList<String>(5);
        cachedCommand = null;
    }

    /**
     * Constructs with specific data
     * @param name that this PersistentCommand will be saved as
     * @param description that this PersistentCommand will be saved with
     * @param fullCommandName the fully qualified name of the command to save
     * @param orbNames to save with
     * @param argumentValues to save with
     * @param timeoutMillis milliseconds to wait for each ORB to respond
     */
    public PersistentCommandImpl(String name, String description, String fullCommandName,
                                 String[] orbNames, String[] argumentValues, int timeoutMillis)
    {
        this();

        setName(name);
        setDescription(description);
        setFullCommandName(fullCommandName);
        setOrbNames(orbNames);
        setArgumentValues(argumentValues);
        setOrbTimeout(timeoutMillis);
    }

    /**
     * Constructs from persistence state
     * @param name that this PersistentCommand will be saved as
     * @param encodedValues that contains a delimited String to parse all the internal
     * attributes from
     * @throws DataValidationException will be thrown if the encodedValues could not be parsed
     * successfully
     */
    public PersistentCommandImpl(String name, String encodedValues) throws DataValidationException
    {
        this();
        setName(name);
        decodeValues(encodedValues);
    }

    /**
     * Provides public access to clone the PersistentCommand.
     * @return a clone of this request
     * @throws CloneNotSupportedException will be thrown if some class up the hierarchy
     * cannot be cloned
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        PersistentCommandImpl newObject = (PersistentCommandImpl) super.clone();
        newObject.setName(getName());
        newObject.setDescription(getDescription());
        newObject.setFullCommandName(getFullCommandName());
        newObject.setOrbNames(getAllOrbNames());
        newObject.setArgumentValues(getArgumentValues());
        newObject.setOrbTimeout(getOrbTimeout());
        return newObject;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(200);
        sb.append("PersistentCommandImpl");
        sb.append("{name='").append(getName()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", commandName='").append(getFullCommandName()).append('\'');
        sb.append(", orbNames=").append(orbNames);
        sb.append(", argumentValues=").append(argumentValues);
        sb.append('}');
        return sb.toString();
    }

    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public boolean equals(Object otherObject)
    {
        boolean isEqual = false;

        if(this == otherObject)
        {
            isEqual = true;
        }
        if(otherObject != null && otherObject instanceof PersistentCommand)
        {
            PersistentCommand that = (PersistentCommand) otherObject;

            isEqual = getName().equals(that.getName());
        }

        return isEqual;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public int compareTo(Object o)
    {
        PersistentCommand otherFav = (PersistentCommand) o;
        return getName().compareTo(otherFav.getName());
    }

    /**
     * Gets the full name of the command
     * @return the fully qualified name of the command to save
     */
    public String getFullCommandName()
    {
        return fullCommandName;
    }

    /**
     * Gets the ARCommand that was saved
     * @return ARCommand object that was saved
     */
    public ARCommand getCommand()
    {
        if(cachedCommand == null && getOrbCount() > 0)
        {
            String[] allOrbNames = getAllOrbNames();
            for(String orbName : allOrbNames)
            {
                //noinspection UnusedCatchParameter
                try
                {
                    CBOEProcess orbProcess =
                            InstrumentationTranslatorFactory.find().getProcess(orbName, null);
                    if(orbProcess != null && orbProcess.getOnlineStatus() == Status.UP)
                    {
                        cachedCommand = getCommand(orbName);
                        break;
                    }
                }
                catch(DataValidationException e)
                {
                    //going to try next one
                }
            }
        }
        return cachedCommand;
    }

    /**
     * Gets the name that this PersistentCommand was saved as.
     * @return user defined name that this PersistentCommand was saved as
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name that this PersistentCommand will be saved as.
     * @param name that this PersistentCommand will be saved as
     */
    public void setName(String name)
    {
        if(name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("name may not be null or empty.");
        }
        this.name = name;
    }

    /**
     * Gets the description that this PersistentCommand will be as saved with.
     * @return user defined description that this PersistentCommand will be saved with
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description that this PersistentCommand will be saved with.
     * @param description that this PersistentCommand will be saved with
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the number of ORB's that was added
     * @return count of ORB's that were added
     */
    public int getOrbCount()
    {
        return orbNames.size();
    }

    /**
     * Gets the name of the ORB at the index
     * @param index to get ORB name of
     * @return ORB name
     */
    public String getOrbName(int index)
    {
        return orbNames.get(index);
    }

    /**
     * Gets all the ORB names
     * @return all ORB names
     */
    public String[] getAllOrbNames()
    {
        return orbNames.toArray(new String[orbNames.size()]);
    }

    /**
     * Convenience method to get the CBOEProcess for the ORB name that is at index
     * @param index of ORB name to get as CBOEProcess
     * @return CBOEProcess for the ORB name
     * @throws DataValidationException may be thrown if the CBOEProcess could not be obtained
     */
    public CBOEProcess getOrbProcess(int index) throws DataValidationException
    {
        String orbName = getOrbName(index);
        return InstrumentationTranslatorFactory.find().getProcess(orbName, null);
    }

    /**
     * Convenience method to get the CBOEProcess'es for all the ORB names
     * @return a sequence of CBOEProcess objects that each represent one of the ORB's
     * @throws DataValidationException may be thrown if the CBOEProcess could not be obtained
     */
    public CBOEProcess[] getAllOrbProcesses() throws DataValidationException
    {
        CBOEProcess[] allProcesses = new CBOEProcess[getOrbCount()];
        for(int i = 0; i < getOrbCount(); i++)
        {
            allProcesses[i] = getOrbProcess(i);
        }
        return allProcesses;
    }

    /**
     * Determines if the orbName exists
     * @param orbName to search for
     * @return true if this will be saved with orbName, false otherwise
     */
    public boolean containsOrbName(String orbName)
    {
        return orbNames.contains(orbName);
    }

    /**
     * Adds a new ORB name to save
     * @param orbName to add
     */
    public void addOrbName(String orbName)
    {
        if(orbName != null && orbName.length() > 0)
        {
            orbNames.add(orbName);
        }
    }

    /**
     * Removes an ORB name
     * @param orbName to remove
     */
    public void removeOrbName(String orbName)
    {
        orbNames.remove(orbName);
    }

    /**
     * Removes an ORB name
     * @param index of ORB name to remove
     */
    public void removeOrbName(int index)
    {
        orbNames.remove(index);
    }

    /**
     * Removes all ORB names
     */
    public void clearOrbNames()
    {
        orbNames.clear();
    }

    /**
     * Gets the amount of time that each ORB should wait for a response from a command
     * @return milliseconds to wait for each ORB to respond
     */
    public int getOrbTimeout()
    {
        return timeoutMillis;
    }

    /**
     * Sets the amount of time that each ORB should wait for a response from a command
     * @param millis milliseconds to wait for each ORB to respond
     */
    public void setOrbTimeout(int millis)
    {
        timeoutMillis = millis;
    }

    /**
     * Gets the number of argument values
     * @return count of argument values
     */
    public int getArgumentCount()
    {
        return argumentValues.size();
    }

    /**
     * Gets the argument values at index
     * @param index of argument value to obtain
     * @return argument value at index. Maybe null or empty String.
     */
    public String getArgumentValue(int index)
    {
        return argumentValues.get(index);
    }

    /**
     * Gets all the argument values to be saved
     * @return sequence of all argument values. Any element may be null or empty String.
     */
    public String[] getArgumentValues()
    {
        return argumentValues.toArray(new String[argumentValues.size()]);
    }

    /**
     * Adds a new argument value
     * @param value to add
     */
    public void addArgumentValue(String value)
    {
        argumentValues.add(value);
    }

    /**
     * Sets a specific argument value
     * @param index of argument to set. Make sure index represents a valid index or an
     * exception may occur.
     * @param value to set at index
     */
    public void setArgumentValue(int index, String value)
    {
        argumentValues.set(index, value);
    }

    /**
     * Removes an argument value
     * @param index of argument value to remove
     */
    public void removeArgumentValue(int index)
    {
        argumentValues.remove(index);
    }

    /**
     * Removes all argument values.
     */
    public void clearArgumentValues()
    {
        argumentValues.clear();
    }

    /**
     * Sets the full name of the command
     * @param fullCommandName the fully qualified name of the command to save
     */
    public void setFullCommandName(String fullCommandName)
    {
        if(fullCommandName == null || fullCommandName.length() == 0)
        {
            throw new IllegalArgumentException("fullCommandName may not be null or empty.");
        }
        this.fullCommandName = fullCommandName;
    }

    /**
     * Gets the values of this saved command encoded as a single String for persistence.
     * @return values encoded within this String
     */
    public String encodeValues()
    {
        StringBuilder encoding = new StringBuilder(150);
        if(getDescription() != null)
        {
            encoding.append(getDescription());
        }
        else
        {
            encoding.append(NULL_VALUE);
        }
        encoding.append(Delimeter.PROPERTY_DELIMETER);
        encoding.append(getFullCommandName()).append(Delimeter.PROPERTY_DELIMETER);

        if(orbNames.isEmpty())
        {
            encoding.append(NULL_VALUE);
        }
        else
        {
            for(String orbName : orbNames)
            {
                encoding.append(orbName).append(VALUE_SEPARATOR);
            }
        }
        encoding.append(Delimeter.PROPERTY_DELIMETER);

        if(argumentValues.isEmpty())
        {
            encoding.append(NULL_VALUE);
        }
        else
        {
            for(String argumentValue : argumentValues)
            {
                if(argumentValue == null)
                {
                    encoding.append(NULL_VALUE);
                }
                else
                {
                    encoding.append(argumentValue);
                }
                encoding.append(VALUE_SEPARATOR);
            }
        }
        encoding.append(Delimeter.PROPERTY_DELIMETER);

        encoding.append(getOrbTimeout());
        encoding.append(Delimeter.PROPERTY_DELIMETER);

        return encoding.toString();
    }

    protected void decodeValues(String encodedValues) throws DataValidationException
    {
        StringTokenizer tokenizer =
                new StringTokenizer(encodedValues, Character.toString(Delimeter.PROPERTY_DELIMETER),
                                    false);
        try
        {
            String decodedDescription = tokenizer.nextToken();
            if(!NULL_VALUE.equals(decodedDescription))
            {
                setDescription(decodedDescription);
            }

            String decodedCommandName = tokenizer.nextToken();
            setFullCommandName(decodedCommandName);

            String decodedOrbNames = tokenizer.nextToken();
            if(!NULL_VALUE.equals(decodedOrbNames))
            {
                parseEncodedOrbNames(decodedOrbNames);
            }

            String decodedValues = tokenizer.nextToken();
            if(!NULL_VALUE.equals(decodedValues))
            {
                parseEncodedArgumentValues(decodedValues);
            }

            String decodedTimeout = tokenizer.nextToken();
            setOrbTimeout(Integer.parseInt(decodedTimeout));
        }
        catch(IllegalArgumentException e)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(
                            "Could not decode PersistentCommand values.", 0);
            exception.initCause(e);
            throw exception;
        }
        catch(NoSuchElementException e)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(
                            "Could not decode PersistentCommand values.", 0);
            exception.initCause(e);
            throw exception;
        }
    }

    protected void parseEncodedOrbNames(String orbNames)
    {
        StringTokenizer tokenizer =
                new StringTokenizer(orbNames, Character.toString(VALUE_SEPARATOR), false);

        while(tokenizer.hasMoreTokens())
        {
            String orbName = tokenizer.nextToken();
            if(orbName != null && orbName.length() > 0)
            {
                addOrbName(orbName);
            }
        }
    }

    protected void parseEncodedArgumentValues(String values)
    {
        StringTokenizer tokenizer =
                new StringTokenizer(values, Character.toString(VALUE_SEPARATOR), false);

        while(tokenizer.hasMoreTokens())
        {
            String value = tokenizer.nextToken();
            if(value == null || value.length() == 0 || NULL_VALUE.equals(value))
            {
                addArgumentValue(null);
            }
            else
            {
                addArgumentValue(value);
            }
        }
    }

    protected ARCommand getCommand(String orbName)
    {
        CommandFactory factory = CommandFactory.getInstance();
        String shortName = factory.parseShortName(getFullCommandName());
        String qualifier = factory.parseQualifierFromName(getFullCommandName());
        return factory.getCommand(orbName, shortName, qualifier);
    }

    protected void setOrbNames(String[] orbNames)
    {
        this.orbNames.clear();
        if(orbNames != null)
        {
            for(String orbName : orbNames)
            {
                addOrbName(orbName);
            }
        }
    }

    protected void setArgumentValues(String[] argumentValues)
    {
        this.argumentValues.clear();
        if(argumentValues != null)
        {
            for(String argumentValue : argumentValues)
            {
                addArgumentValue(argumentValue);
            }
        }
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "ObjectToString", "CatchGenericClass",
            "OverlyLongMethod"})
    public static void main(String[] args)
    {
        String[] orbNames = {"ics01ics0002", "bc01prodbc96", "cas01cas2006"};
        String[] argumentValues = {"true", "5", null, "valueA"};
        PersistentCommandImpl impl =
                new PersistentCommandImpl("theName", "theDescription", "initializeService",
                                          orbNames, argumentValues, 30000);
        System.out.println("impl:" + impl.toString());

        PersistentCommandImpl impl2 =
                new PersistentCommandImpl("theName", "theDescription", "initializeService",
                                          null, null, 30000);
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        impl2 =
                new PersistentCommandImpl("theName2", "theDescription", "initializeService",
                                          null, null, 30000);
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        impl2 =
                new PersistentCommandImpl("theName", null, "initializeService",
                                          null, null, 30000);
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        impl2 =
                new PersistentCommandImpl("theName", "", "initializeService",
                                          null, null, 30000);
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        System.out.println("impl.getOrbCount() = " + impl.getOrbCount());
        System.out.println("impl.getAllOrbNames() = " + Arrays.toString(impl.getAllOrbNames()));
        System.out.println("impl.getOrbName(1) = " + impl.getOrbName(1));

        System.out.println("impl.containsOrbName(\"addedOrbName\") = " +
                           impl.containsOrbName("addedOrbName"));
        System.out.println("impl.addOrbName(\"addedOrbName\")");
        impl.addOrbName("addedOrbName");
        System.out.println("impl.getOrbCount() = " + impl.getOrbCount());
        System.out.println("impl.getAllOrbNames() = " + Arrays.toString(impl.getAllOrbNames()));

        System.out.println("impl.containsOrbName(\"addedOrbName\") = " +
                           impl.containsOrbName("addedOrbName"));

        System.out.println("impl.removeOrbName(\"addedOrbName\")");
        impl.removeOrbName("addedOrbName");
        System.out.println("impl.getOrbCount() = " + impl.getOrbCount());
        System.out.println("impl.getAllOrbNames() = " + Arrays.toString(impl.getAllOrbNames()));

        System.out.println("impl.containsOrbName(\"addedOrbName\") = " +
                           impl.containsOrbName("addedOrbName"));

        System.out.println("impl.removeOrbName(0)");
        impl.removeOrbName(0);
        System.out.println("impl.getOrbCount() = " + impl.getOrbCount());
        System.out.println("impl.getAllOrbNames() = " + Arrays.toString(impl.getAllOrbNames()));

        System.out.println("impl.clearOrbNames()");
        impl.clearOrbNames();
        System.out.println("impl.getOrbCount() = " + impl.getOrbCount());
        System.out.println("impl.getAllOrbNames() = " + Arrays.toString(impl.getAllOrbNames()));

        try
        {
            System.out.print("impl.getOrbName(1) = ");
            System.out.println(impl.getOrbName(1));
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            System.out.println("impl.removeOrbName(0)");
            impl.removeOrbName(0);
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }

        System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
        System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));
        System.out.println("impl.getArgumentValue(1) = " + impl.getArgumentValue(1));

        System.out.println("impl.addArgumentValue(\"newValue\")");
        impl.addArgumentValue("newValue");
        System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
        System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));

        System.out.println("impl.removeArgumentValue(4)");
        impl.removeArgumentValue(4);
        System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
        System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));

        System.out.println("impl.setArgumentValue(2, \"notNull\")");
        impl.setArgumentValue(2, "notNull");
        System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
        System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));

        try
        {
            System.out.println("impl.setArgumentValue(10, \"fail\")");
            impl.setArgumentValue(10, "fail");
            System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
            System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }

        System.out.println("impl.clearArgumentValues()");
        impl.clearArgumentValues();
        System.out.println("impl.getArgumentCount() = " + impl.getArgumentCount());
        System.out.println("impl.getArgumentValues() = " + Arrays.toString(impl.getArgumentValues()));

        try
        {
            System.out.print("impl.getArgumentValue(1) = ");
            System.out.println(impl.getArgumentValue(1));
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            System.out.println("impl.removeArgumentValue(0)");
            impl.removeArgumentValue(0);
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            System.out.println("impl.setArgumentValue(0, \"test\")");
            impl.setArgumentValue(0, "test");
        }
        catch(Exception e)
        {
            System.out.println("e = " + e);
        }
    }
}
