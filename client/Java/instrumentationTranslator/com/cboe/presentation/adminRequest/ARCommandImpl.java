//
// -----------------------------------------------------------------------------------
// Source file: ARCommandImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import java.util.*;

import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;

import com.cboe.infrastructureServices.interfaces.adminService.DataItem;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.domain.util.StructBuilder;

/**
 * Wraps an IDL Command object with behavior.
 */
class ARCommandImpl implements ARCommand
{
    private Command command;
    private String shortName;
    private Type type;
    private Severity severity;
    private String description;

    private Properties extAttributes;

    private String ffServiceName;
    private String ffContainerName;
    private String serviceName;
    private boolean isFFCommand;
    private boolean isFFServiceCommand;

    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String SEVERITY_ATTRIBUTE_NAME = "severity";
    private static final String ATTRIBUTE_DELIMITER = "\u0001";
    private static final String ATTRIBUTE_NAME_VALUE_DELIMITER = "=";

    private static final String FF_CMD_NAME_STARTS_WITH = "GlobalFoundationFramework";
    private static final String FF_SVC_NAME_SEARCH_TEXT = "BOHome(";
    private static final String FF_SVC_CONTAINER_NAME_SEARCH_TEXT = "BOContainer(";
    private static final String FF_SVC_SEARCH_TEXT_END_DELIMITER = ")";
    private static final String STD_CMD_SVC_NAME_END_DELIMITER = "(";

    ARCommandImpl(Command command)
    {
        setCommand(command);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(200);
        builder.append("Name:").append(getName()).append('\n');
        builder.append("FullName:").append(getFullName()).append('\n');
        builder.append("Description:").append(getDescription()).append('\n');
        builder.append("Type:").append(getType()).append('\n');
        builder.append("Severity:").append(getSeverity()).append('\n');
        builder.append("isFoundationFrameworkServiceCommand:");
        builder.append(isFoundationFrameworkServiceCommand()).append('\n');
        builder.append("isFoundationFrameworkCommand:");
        builder.append(isFoundationFrameworkCommand()).append('\n');
        builder.append("getFoundationFrameworkServiceName:");
        builder.append(getFoundationFrameworkServiceName()).append('\n');
        builder.append("getFoundationFrameworkContainerName:");
        builder.append(getFoundationFrameworkContainerName()).append('\n');
        builder.append("getServiceName:").append(getServiceName()).append('\n');
        builder.append("Extended Attributes:");
        builder.append(extAttributes.toString());

        return builder.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        ARCommandImpl clone = (ARCommandImpl) super.clone();
        Command newCommand = StructBuilder.cloneCommand(command);
        clone.setCommand(newCommand);
        return clone;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual = false;

        if(this == object)
        {
            isEqual = true;
        }
        else if(object != null && object instanceof ARCommand)
        {
            ARCommand arCommand = (ARCommand) object;

            isEqual = getFullName().equals(arCommand.getFullName()) &&
                      getSeverity().equals(arCommand.getSeverity()) &&
                      getType().equals(arCommand.getType()) &&
                      isDataItemsEqual(getArguments(), arCommand.getArguments()) &&
                      isDataItemsEqual(getReturnValues(), arCommand.getReturnValues());
        }

        return isEqual;
    }

    @Override
    public int hashCode()
    {
        return getFullName().hashCode();
    }

    /**
     * @deprecated Individual accessor methods should be used.
     */
    public Command getCommand()
    {
        return StructBuilder.cloneCommand(command);
    }

    /**
     * Gets the argument structure for this command
     * @return an array of DataItem's that define the argument structure for this command
     * @see DataItem
     */
    public DataItem[] getArguments()
    {
        return command.args;
    }

    /**
     * Gets the description of this command
     * @return description from the command structure
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets the full name of this command
     * @return fully qualified name of this command
     */
    public String getFullName()
    {
        return command.name;
    }

    /**
     * Gets the short name of this command
     * @return short name
     */
    public String getName()
    {
        return shortName;
    }

    /**
     * Gets the return values structure for this command
     * @return an array of DataItem's that define the return valuesstructure for this command
     * @see DataItem
     */
    public DataItem[] getReturnValues()
    {
        return command.retValues;
    }

    /**
     * Gets the severity of this command.
     * @return Severity enum representing the severity of this command
     */
    public Severity getSeverity()
    {
        return severity;
    }

    /**
     * Gets the type of command this is.
     * @return Type enum representing the type of command this is
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Determines if an extended attribute is defined for attributeName
     * @param attributeName to determine whether value exists for
     * @return true if exists, false otherwise
     */
    public boolean containsExtAttribute(String attributeName)
    {
        return extAttributes.containsKey(attributeName);
    }

    /**
     * Gets a specific attribute value
     * @param attributeName to get value for.
     * @return value from extended attributes defined by attributeName
     */
    public String getExtAttribute(String attributeName)
    {
        return extAttributes.getProperty(attributeName);
    }

    /**
     * Gets all the extended attributes that describes this command
     * @return a Properties collection that contains the extended attributes or this command.
     * The name and value of a property describes the name and value of the attribute,
     * respectively.
     */
    public Properties getExtAttributes()
    {
        return (Properties) extAttributes.clone();
    }

    /**
     * Builds a Command object from this ARCommand with the passed data
     * @param data to contain in Command
     * @return Command to be executed
     */
    public Command buildExecuteCommand(String data)
    {
        String[] dataArray = null;
        if(data != null)
        {
            dataArray = new String[]{data};
        }
        return buildExecuteCommand(dataArray);
    }

    /**
     * Builds a Command object from this ARCommand with the passed data
     * @param data to contain in Command
     * @return Command to be executed
     */
    public Command buildExecuteCommand(String[] data)
    {
        Command newCommand = StructBuilder.cloneCommand(command);

        if(data != null)
        {
            DataItem[] items = new DataItem[data.length];
            for(int i = 0; i < data.length; i++)
            {
                items[i] = new DataItem();
                items[i].value = data[i];
                if(newCommand.args != null && newCommand.args.length > i)
                {
                    items[i].description = newCommand.args[i].description;
                    items[i].name = newCommand.args[i].name;
                    items[i].type = newCommand.args[i].type;
                }
                else
                {
                    items[i].description = "";
                    items[i].name = "";
                    items[i].type = "";
                }
            }
            newCommand.args = items;
        }

        return newCommand;
    }

    /**
     * Determines if this command is from the FoundationFramework.
     * @return true if from FoundationFramework, false if process implemented.
     */
    public boolean isFoundationFrameworkCommand()
    {
        return isFFCommand;
    }

    /**
     * Determines if this command is from the FoundationFramework and represents a service.
     * @return true if from FoundationFramework and represents a service, false if process
     *         implemented.
     */
    public boolean isFoundationFrameworkServiceCommand()
    {
        return isFFServiceCommand;
    }

    /**
     * Gets the FoundationFramework service that this command represents. Will return null if this
     * command is not for a FoundationFramework service. Check isFoundationFrameworkServiceCommand()
     * @return the service name, if this command represents a FoundationFramework service, otherwise
     *         null.
     */
    public String getFoundationFrameworkServiceName()
    {
        return ffServiceName;
    }

    /**
     * Gets the FoundationFramework container name for the service that this command represents.
     * Will return null if this command is not for a FoundationFramework service. Check
     * isFoundationFrameworkServiceCommand()
     * @return the container name, if this command represents a FoundationFramework service,
     *         otherwise null.
     */
    public String getFoundationFrameworkContainerName()
    {
        return ffContainerName;
    }

    /**
     * If this command is NOT a FoundationFramework command, then returns the service that
     * implements the command for the process.
     * Check isFoundationFrameworkCommand()
     * @return the service name preceding the command name that implements the command.
     * If this command represents a FoundationFramework command, null will be returned.
     */
    public String getServiceName()
    {
        return serviceName;
    }

    protected void setCommand(Command command)
    {
        if(command == null)
        {
            throw new IllegalArgumentException("Command may not be null.");
        }
        this.command = command;
        initialize();
    }

    private void initialize()
    {
        parseNameParts();
        parseAttributes();

        determineType();
        determineSeverity();
    }

    private void parseNameParts()
    {
        parseShortName();

        ffServiceName = "";
        ffContainerName = "";
        serviceName = "";
        isFFCommand = false;
        isFFServiceCommand = false;

        String fullName = command.name;

        isFFCommand = fullName.startsWith(FF_CMD_NAME_STARTS_WITH);
        if(isFFCommand)
        {
            isFFServiceCommand = fullName.contains(FF_SVC_NAME_SEARCH_TEXT);
            if(isFFServiceCommand)
            {
                int indexOfSvcName = fullName.indexOf(FF_SVC_NAME_SEARCH_TEXT);
                if(indexOfSvcName > -1)
                {
                    indexOfSvcName += FF_SVC_NAME_SEARCH_TEXT.length();
                    int endIndexOfSvcName =
                            fullName.indexOf(FF_SVC_SEARCH_TEXT_END_DELIMITER, indexOfSvcName);
                    if(endIndexOfSvcName != -1)
                    {
                        ffServiceName =
                                fullName.substring(indexOfSvcName, endIndexOfSvcName);
                    }
                }

                int indexOfContainerName = fullName.indexOf(FF_SVC_CONTAINER_NAME_SEARCH_TEXT);
                if(indexOfContainerName > -1)
                {
                    indexOfContainerName += FF_SVC_CONTAINER_NAME_SEARCH_TEXT.length();
                    int endIndexOfContainerName =
                            fullName.indexOf(FF_SVC_SEARCH_TEXT_END_DELIMITER,
                                             indexOfContainerName);
                    if(endIndexOfContainerName != -1)
                    {
                        ffContainerName = fullName.substring(indexOfContainerName,
                                                             endIndexOfContainerName);
                    }
                }
            }
        }
        else
        {
            int periodB4Command = fullName.lastIndexOf('.');
            if(periodB4Command > -1)
            {
                int periodB4Service = fullName.lastIndexOf('.', periodB4Command - 1);
                if(periodB4Service > -1)
                {
                    int paranPos = fullName.indexOf(STD_CMD_SVC_NAME_END_DELIMITER, periodB4Service);
                    //noinspection IfStatementWithNegatedCondition
                    if(paranPos != -1)
                    {
                        serviceName = fullName.substring(periodB4Service + 1, paranPos);
                    }
                    else
                    {
                        serviceName = fullName.substring(periodB4Service + 1, periodB4Command);
                    }
                }
                else
                {
                    serviceName = fullName.substring(0, periodB4Command);
                }
            }
        }
    }

    private void parseShortName()
    {
        shortName = command.name;
        if(command.name != null)
        {
            shortName = CommandFactory.getInstance().parseShortName(command.name);
        }
    }

    private void parseAttributes()
    {
        extAttributes = new Properties();

        if(command.description != null)
        {
            description = command.description;
            int pos = description.indexOf(ATTRIBUTE_DELIMITER);
            if(pos != -1)
            {
                String attributes = description.substring(pos);
                description = description.substring(0, pos);

                StringTokenizer tokenizer = new StringTokenizer(attributes, ATTRIBUTE_DELIMITER, false);
                while(tokenizer.hasMoreTokens())
                {
                    String token = tokenizer.nextToken();
                    if(token != null && token.length() > 0)
                    {
                        int equalPos = token.indexOf(ATTRIBUTE_NAME_VALUE_DELIMITER);
                        if(equalPos > 0 &&
                           token.length() > (equalPos + 1))
                        {
                            String key = token.substring(0, equalPos).toLowerCase();
                            String value = token.substring(equalPos + 1).toLowerCase();
                            extAttributes.setProperty(key, value);
                        }
                    }
                }
            }
        }
        else
        {
            description = "";
        }
    }

    private void determineType()
    {
        type = Type.NOT_DEFINED;
        String typeText = getExtAttribute(TYPE_ATTRIBUTE_NAME);

        if(typeText != null)
        {
            if(typeText.equalsIgnoreCase(Type.DISPLAY.name()))
            {
                type = Type.DISPLAY;
            }
            else if(typeText.equalsIgnoreCase(Type.EXECUTE.name()))
            {
                type = Type.EXECUTE;
            }
        }
    }

    private void determineSeverity()
    {
        severity = Severity.NOT_DEFINED;
        String severityText = getExtAttribute(SEVERITY_ATTRIBUTE_NAME);

        if(severityText != null)
        {
            if(severityText.equalsIgnoreCase(Severity.LOW.name()))
            {
                severity = Severity.LOW;
            }
            else if(severityText.equalsIgnoreCase(Severity.MEDIUM.name()))
            {
                severity = Severity.MEDIUM;
            }
            else if(severityText.equalsIgnoreCase(Severity.HIGH.name()))
            {
                severity = Severity.HIGH;
            }
            else if(severityText.equalsIgnoreCase(Severity.RESTRICTED.name()))
            {
                severity = Severity.RESTRICTED;
            }
        }
    }

    private boolean isDataItemsEqual(DataItem[] dataItems1, DataItem[] dataItems2)
    {
        boolean isEqual = dataItems1.length == dataItems2.length;
        if(isEqual)
        {
            for(int i = 0; i < dataItems1.length; i++)
            {
                DataItem dataItem1 = dataItems1[i];
                DataItem dataItem2 = dataItems2[i];

                if(!(dataItem1.name.equals(dataItem2.name) &&
                     dataItem1.description.equals(dataItem2.description) &&
                     dataItem1.type.equals(dataItem2.type) &&
                     dataItem1.value.equals(dataItem2.value)))
                {
                    isEqual = false;
                    break;
                }
            }
        }
        return isEqual;
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "OverlyLongMethod"})
    public static void main(String[] args)
    {
        Command newCommand = new Command();
        newCommand.name = "hostName.orbName.commandName";
        newCommand.description =
                "This is a description\u0001Type=Execute\u0001Severity=Restricted\u0001";

        ARCommandImpl arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();

        newCommand = new Command();
        newCommand.name = "commandName";
        newCommand.description =
                "This is a description\u0001OtherAttribute=Other value\u0001Someattribute=value2";

        arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();

        newCommand = new Command();
        newCommand.name = "commandName";
        newCommand.description =
                "This is a description\u0001=Other value\u0001Someattribute=";

        arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();

        newCommand = new Command();
        newCommand.name = "GlobalHome.HomeImpl(ServiceName).commandName";
        newCommand.description =
                "This is a description\u0001Type=Execute\u0001Severity=Restricted\u0001";

        arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();

        newCommand = new Command();
        newCommand.name = "GlobalHome.HomeImpl.commandName";
        newCommand.description =
                "This is a description\u0001Type=Execute\u0001Severity=Restricted\u0001";

        arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();

        newCommand = new Command();
        newCommand.name = "CMS.commandName";
        newCommand.description =
                "This is a description\u0001Type=Execute\u0001Severity=Restricted\u0001";

        arCommand = new ARCommandImpl(newCommand);
        System.out.println(arCommand.toString());
        System.out.println();
    }
}
