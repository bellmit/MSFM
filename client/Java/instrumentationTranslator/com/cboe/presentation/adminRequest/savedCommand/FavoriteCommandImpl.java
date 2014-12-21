//
// -----------------------------------------------------------------------------------
// Source file: FavoriteCommandImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest.savedCommand
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest.savedCommand;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.FavoriteCommand;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.adminRequest.CommandFactory;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;

/**
 * Implements the FavoriteCommand interface to represent any AR Commands that are saved as
 * favorites for a specific ORB.
 */
public class FavoriteCommandImpl implements FavoriteCommand
{
    private String fullCommandName;
    private String orbName;
    private CBOEProcess orbProcess;

    /**
     * Constructs with specific data
     * @param fullCommandName the fully qualified name of the command to save
     * @param orbName to save favorite for
     */
    public FavoriteCommandImpl(String fullCommandName, String orbName)
    {
        setFullCommandName(fullCommandName);
        setOrbName(orbName);
    }

    /**
     * Constructs from persistence state
     * @param encodedValues that contains a delimited String to parse all the internal
     * attributes from
     * @throws DataValidationException will be thrown if the encodedValues could not be parsed
     * successfully
     */
    public FavoriteCommandImpl(String encodedValues) throws DataValidationException
    {
        decodeValues(encodedValues);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(100);
        sb.append("FavoriteCommandImpl");
        sb.append("{commandName='").append(getFullCommandName()).append('\'');
        sb.append(", orbName='").append(getOrbName()).append('\'');
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
        if(otherObject != null && otherObject instanceof FavoriteCommand)
        {
            FavoriteCommand that = (FavoriteCommand) otherObject;

            isEqual = getFullCommandName().equals(that.getFullCommandName()) &&
                      getOrbName().equals(that.getOrbName());
        }

        return isEqual;
    }

    @Override
    public int hashCode()
    {
        int result;
        result = fullCommandName.hashCode();
        result = 29 * result + orbName.hashCode();
        return result;
    }

    public int compareTo(Object o)
    {
        FavoriteCommand otherFav = (FavoriteCommand) o;
        return getFullCommandName().compareTo(otherFav.getFullCommandName());
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
     * Gets the ARCommand that was saved as a favorite
     * @return ARCommand object that is a favorite
     */
    public ARCommand getCommand()
    {
        CommandFactory factory = CommandFactory.getInstance();
        String shortName = factory.parseShortName(getFullCommandName());
        String qualifier = factory.parseQualifierFromName(getFullCommandName());
        return factory.getCommand(getOrbName(), shortName, qualifier);
    }

    /**
     * The ORB name that this favorite is for.
     * @return ORB name
     */
    public String getOrbName()
    {
        return orbName;
    }

    /**
     * Convenience method to get the CBOEProcess for the ORB name that this favorite is for.
     * @return CBOEProcess for the ORB name
     * @throws DataValidationException may be thrown if the CBOEProcess could not be obtained
     */
    public CBOEProcess getOrbProcess() throws DataValidationException
    {
        if(orbProcess == null)
        {
            orbProcess = InstrumentationTranslatorFactory.find().getProcess(orbName, null);
        }

        return orbProcess;
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
     * Sets the ORB name to save the favorite for
     * @param orbName to save favorite for
     */
    public void setOrbName(String orbName)
    {
        if(orbName == null || orbName.length() == 0)
        {
            throw new IllegalArgumentException("orbName may not be null or empty.");
        }
        this.orbName = orbName;
        orbProcess = null;
    }

    /**
     * Gets the values of this saved command encoded as a single String for persistence.
     * @return values encoded within this String
     */
    public String encodeValues()
    {
        StringBuilder encoding = new StringBuilder(50);
        encoding.append(getOrbName()).append(VALUE_SEPARATOR).append(getFullCommandName());
        return encoding.toString();
    }

    protected void decodeValues(String encodedValues) throws DataValidationException
    {
        StringTokenizer tokenizer =
                new StringTokenizer(encodedValues, Character.toString(VALUE_SEPARATOR), false);

        try
        {
            setOrbName(tokenizer.nextToken());
            setFullCommandName(tokenizer.nextToken());
        }
        catch(IllegalArgumentException e)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(
                            "Could not decode FavoriteCommand values.", 0);
            exception.initCause(e);
            throw exception;
        }
        catch(NoSuchElementException e)
        {
            DataValidationException exception =
                    ExceptionBuilder.dataValidationException(
                            "Could not decode FavoriteCommand values.", 0);
            exception.initCause(e);
            throw exception;
        }
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "OverlyLongMethod"})
    public static void main(String[] args)
    {
        FavoriteCommandImpl impl = new FavoriteCommandImpl("initializeService", "ics01ics0002");
        System.out.println("impl:" + impl.toString());

        FavoriteCommandImpl impl2 = new FavoriteCommandImpl("initializeService", "ics01ics0002");
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        impl2 = new FavoriteCommandImpl("initializeService", "ics01ics0003");
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        impl2 = new FavoriteCommandImpl("initializeService2", "ics01ics0002");
        System.out.println("impl2:" + impl2.toString());
        System.out.println("impl.equals(impl2):" + impl.equals(impl2));
        System.out.println("impl.compareTo(impl2):" + impl.compareTo(impl2));

        String encodedValues = impl.encodeValues();
        System.out.println("encodedValues = " + encodedValues);
        try
        {
            impl2 = new FavoriteCommandImpl(encodedValues);
            System.out.println("impl2:" + impl2.toString());
        }
        catch(DataValidationException e)
        {
            System.out.println("e = " + e);
        }

        StringBuilder encoding = new StringBuilder(50);
        encoding.append("initializeService").append(VALUE_SEPARATOR);
        try
        {
            impl2 = new FavoriteCommandImpl(encoding.toString());
            System.out.println("impl2:" + impl2.toString());
        }
        catch(DataValidationException e)
        {
            System.out.println("e = " + e);
        }

        encoding = new StringBuilder(50);
        encoding.append(VALUE_SEPARATOR).append("ics01ics0002");
        try
        {
            impl2 = new FavoriteCommandImpl(encoding.toString());
            System.out.println("impl2:" + impl2.toString());
        }
        catch(DataValidationException e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            impl2 = new FavoriteCommandImpl(Character.toString(VALUE_SEPARATOR));
            System.out.println("impl2:" + impl2.toString());
        }
        catch(DataValidationException e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            impl2 = new FavoriteCommandImpl("");
            System.out.println("impl2:" + impl2.toString());
        }
        catch(DataValidationException e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            impl2 = new FavoriteCommandImpl("initializeService", null);
            System.out.println("impl2:" + impl2.toString());
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("e = " + e);
        }

        try
        {
            impl2 = new FavoriteCommandImpl(null, "ics01ics0002");
            System.out.println("impl2:" + impl2.toString());
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("e = " + e);
        }
    }
}
