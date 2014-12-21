//
// ------------------------------------------------------------------------
// FILE: XmlBindingHelper.java
//
// PACKAGE: com.cboe.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.ExceptionDetails;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.SystemMonitorCommandMethodNames;
import com.cboe.interfaces.instrumentation.adminRequest.ARCommand;
import com.cboe.interfaces.instrumentation.adminRequest.ExecuteCommandService;
import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;
import com.cboe.interfaces.presentation.api.TimedOutException;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

import com.cboe.util.ExceptionBuilder;

import com.cboe.presentation.adminRequest.ARCommandServiceFactory;
import com.cboe.presentation.adminRequest.CommandFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.GIUserExceptionType;
import com.cboe.infrastructureServices.interfaces.adminService.Command;

public class XmlBindingHelper
{
    private XmlBindingHelper()
    {
    }

    public static java.lang.Object sendRequest(String requestXml)
    throws TimedOutException, UserException
    {
        //Get data from the AdminService
        String queryHost = InstrumentationTranslatorFactory.find().getSACASForProductQuery();
        if(queryHost == null)
        {
            queryHost = getSACASForProductQuery();
        }
        if(queryHost == null)
        {
            throw ExceptionBuilder.dataValidationException("Unable to find Query Host for AdminService", DataValidationCodes.INVALID_PROCESS_NAME);
        }
        // Build command or get it from the factory
        // queryHost is an orbName at this stage
        String commandName = SystemMonitorCommandMethodNames.PRODUCT_DATA_QUERY;
        ARCommand arCommand = CommandFactory.getInstance().getCommand(queryHost, commandName);
        Command executeCommand = arCommand.buildExecuteCommand(requestXml);

        String processName = queryHost;
        try
        {
            CBOEProcess process = InstrumentationTranslatorFactory.find().getProcess(queryHost, null);
            processName = process.getProcessName();
        }
        catch(DataValidationException dve)
        {
            // did not find a process for this orb, do nothing and try to use the original query host
        }
        ExecuteCommandService service = ARCommandServiceFactory.getExecuteCommandService();
        service.setDestination(processName);

        String responseXml = null;
        ExecutionResult execResult = service.executeCommand(executeCommand);
        if(execResult != null && execResult.isSuccess())
        {
            Command resultCommand = execResult.getCommandResult();
            responseXml = resultCommand.retValues[0].value;
        }
        Object xbObject = null;
        if(responseXml != null && responseXml.length() > 0)
        {
            xbObject = getXmlBindingObject(responseXml);
        }
        return xbObject;
    }

    private static String getSACASForProductQuery()
    {
        String sacas = InstrumentationTranslatorFactory.find().getSACASForProductQuery();
        if (sacas == null || !InstrumentationTranslatorFactory.find().isValidSACASForProductQuery(sacas))
        {
            sacas = null;
            String[] sacasNames = InstrumentationTranslatorFactory.find().getAllSACASNames();
            if (sacasNames != null && sacasNames.length > 0)
            {
                for (int i = 0; i < sacasNames.length; i++)
                {
                    if (InstrumentationTranslatorFactory.find().isValidSACASForProductQuery(sacasNames[i]))
                    {
                        sacas = sacasNames[i];
                        break;
                    }
                }
            }
        }
        return sacas;
    }


    /**
     * Gets an unmarshalled object from the XML
     * @param responseXml XML text to be unmarshalled
     * @return Object representing the XML
     * @throws org.omg.CORBA.UserException thrown if the XML represents an Exception
     */
    public static java.lang.Object getXmlBindingObject(String responseXml) throws UserException
    {
        java.lang.Object object = XmlBindingFacade.getInstance().unmarshallXmlString(responseXml);
        if(object == null)
        {
            // this is not a known response xml
            GUILoggerHome.find().alarm("Unknown Response", responseXml);
            throw ExceptionBuilder.systemException("Unknown Response", 0);
        }
        else if(object instanceof GIUserExceptionType)
        {
            createAndThrowException((GIUserExceptionType) object);
        }
        return object;
    }

    public static void createAndThrowException(GIUserExceptionType userExceptionType) throws UserException
    {
        try
        {
            Class exceptionClass = Class.forName(userExceptionType.getClassName());
            if( exceptionClass.getSuperclass() == UserException.class ) // a user exception
            {
                // find the constructor with a String as its parameter
                Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class });
                // instantiate a new exception
                Object exceptionObject = constructor.newInstance(new Object[] { getReason(userExceptionType) });
                UserException userException = (UserException) exceptionObject;
                // find the details field
                Field detailsField = null;
                Field[] fields = exceptionClass.getFields();
                for (int i = 0; i < fields.length; i++)
                {
                    Field field = fields[i];
                    if(field.getType() == ExceptionDetails.class)
                    {
                        detailsField = field;
                        break;
                    }
                }
                if(detailsField != null && userExceptionType.getDetail() != null)
                {
                    ExceptionDetails exceptionDetails = new ExceptionDetails();
                    exceptionDetails.message = userExceptionType.getDetail().getMessage();
                    exceptionDetails.dateTime = userExceptionType.getDetail().getDateTime();
                    exceptionDetails.error = userExceptionType.getDetail().getError();
                    exceptionDetails.severity = userExceptionType.getDetail().getSeverity();
                    detailsField.set(userException, exceptionDetails);
                }

                throw userException;
            }
            else if( exceptionClass.getSuperclass() == Throwable.class)
            {
                // find the constructor with a String as its parameter
                Constructor constructor = exceptionClass.getConstructor(new Class[] { String.class });
                // instantiate a new exception
                Object exceptionObject = constructor.newInstance(new Object[] { getReason(userExceptionType) });
                Throwable throwable = (Throwable) exceptionObject;
                SystemException se = ExceptionBuilder.systemException(getReason(userExceptionType), 0);
                se.initCause(throwable);
                throw se;
            }
            else // not a throwable??? this should never happen
            {
                throw ExceptionBuilder.systemException(getReason(userExceptionType), 0);
            }
        }
        catch (ClassNotFoundException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (InstantiationException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (IllegalAccessException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (NoSuchMethodException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (InvocationTargetException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
    }

    public static String getReason(GIUserExceptionType userExceptionType)
    {
        String reason = userExceptionType.getMessage();
        String stackTrace = userExceptionType.getStackTraceText() ;
        StringBuffer reasonBuffer = new StringBuffer(reason.length());
        if(reason == null || reason.length() == 0)
        {
            reasonBuffer.append(reason);
        }
        if( stackTrace != null && stackTrace.length()>0)
        {
            reasonBuffer.append("\n");
            reasonBuffer.append("--------------- linked to ------------------");
            reasonBuffer.append("\n");
            reasonBuffer.append(stackTrace);
            reasonBuffer.append("\n");
        }
        return reasonBuffer.toString();
    }

}

