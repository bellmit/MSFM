package com.cboe.presentation.common.logging;

//
// -----------------------------------------------------------------------------------
// Source file: /client/Java/messagingTranslator/com/cboe/presentation/common/logging/LoggingServiceAdapater.java
//
// PACKAGE: com.cboe.presentation.common.logging
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.Calendar;

import com.cboe.loggingService.*;

import com.cboe.idl.infrastructureServices.loggingService.corba.StdMsgType;

import com.cboe.interfaces.presentation.common.logging.IGUILogger;
import com.cboe.interfaces.presentation.common.logging.IGUILoggerBusinessProperty;
/** 
 *  An adapter that implements the LoggingServiceInterface and delegates the calls
 *  to the GUILogger.
 */
public class LoggingServiceAdapter implements LoggingServiceInterface 
{
    protected IGUILogger          guiLogger;
    protected IGUILoggerBusinessProperty defaultBusinessProperty;

    public LoggingServiceAdapter(IGUILogger guiLogger, IGUILoggerBusinessProperty defaultBusinessProperty )
    {
        this.guiLogger = guiLogger;
        this.defaultBusinessProperty = defaultBusinessProperty;
    }

    public String getComponentName()
    {
return "No Idea";
    }

    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Throwable exception)
    {
        guiLogger.exception(componentNameSuffix,message,exception);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Throwable exception)
    {
        guiLogger.exception(componentNameSuffix,exception);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, Calendar dateTimeStamp)
    {
        adaptMessage(category,componentNameSuffix);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String message, Calendar dateTimeStamp)
    {
        adaptMessage(category, componentNameSuffix, message);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
        adaptMessage(category, componentNameSuffix, parameters);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText, NonRepudiationData data)
    {
        guiLogger.nonRepudiationAudit(componentNameSuffix,messageText,data);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText, MsgParameter[] parameters)
    {
        // IGUILogger doesn't have something for [] and message, so do two
        adaptMessage(category, componentNameSuffix, messageText);
        adaptMessage(category, componentNameSuffix, parameters);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, String messageText)
    {
        adaptMessage(category, componentNameSuffix, messageText);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String componentNameSuffix, StdMsgType standardMessageType)
    {
        adaptMessage(category, componentNameSuffix);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String message, Throwable exception)
    {
        guiLogger.exception(message, exception);
    }
    
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Throwable exception)
    {
        guiLogger.exception(exception);
    }
    
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, Calendar dateTimeStamp)
    {
        // There is no text to this message, what is the point?!
        adaptMessage(category,"");
    }
    
    public void log(MsgPriority priority, MsgCategory category, String message, Calendar dateTimeStamp)
    {
        adaptMessage(category, message );
    }
    
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType, MsgParameter[] parameters)
    {
        adaptMessage(category, parameters);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String messageText, MsgParameter[] parameters)
    {
        // IGUILogger doesn't have something for [] and message, so do two
        adaptMessage(category, messageText);
        adaptMessage(category, parameters);
    }
    
    public void log(MsgPriority priority, MsgCategory category, String messageText)
    {
        adaptMessage(category, messageText);
    }
    
    public void log(MsgPriority priority, MsgCategory category, StdMsgType stdMsgType)
    {
        // There is no text to this message, what is the point?!
        adaptMessage(category,"");
    }

    public void log( String messageTxt )
    {
        guiLogger.debug(messageTxt,defaultBusinessProperty );
    }

    public void log( Message message )
    {
        adaptMessage(message.getCategory(), message.getMessageString());
    }

    public void clear( Message message )
    {
        adaptMessage( message.getCategory(), message.getMessageString() );
    }

    protected void adaptMessage(MsgCategory msgCategory, String message)
    {
        adaptMessage(msgCategory,guiLogger.getStdWindowTitle(),message);
    }

    protected void adaptMessage(MsgCategory msgCategory, String windowTitle, String message)
    {
        if (msgCategory.isDebug())
        {
            guiLogger.debug(windowTitle,defaultBusinessProperty,message);
        }
        else if (msgCategory.isInformation())
        {
            guiLogger.information(windowTitle,defaultBusinessProperty,message);
        }
        else if (msgCategory.isSystemAlarm())
        {
            guiLogger.alarm(windowTitle,message);
        }
        else if (msgCategory.isAudit())
        {
            guiLogger.audit(windowTitle,message);
        }
        else if (msgCategory.isNonRepudiation())
        {
            guiLogger.nonRepudiationAudit(windowTitle,message);
        }
        else if (msgCategory.isSystemNotification())
        {
            guiLogger.information(windowTitle,defaultBusinessProperty,message);
        }
    }

    protected void adaptMessage(MsgCategory msgCategory, Object object)
    {
        adaptMessage(msgCategory,guiLogger.getStdWindowTitle(),object);
    }

    protected void adaptMessage(MsgCategory msgCategory, String windowTitle, Object object)
    {
        if (msgCategory.isDebug())
        {
            guiLogger.debug(windowTitle,defaultBusinessProperty,object);
        }
        else if (msgCategory.isInformation())
        {
            guiLogger.information(windowTitle,defaultBusinessProperty,object);
        }
        else if (msgCategory.isSystemAlarm())
        {
            guiLogger.alarm(windowTitle,object);
        }
        else if (msgCategory.isAudit())
        {
            guiLogger.audit(windowTitle,object);
        }
        else if (msgCategory.isNonRepudiation())
        {
            guiLogger.nonRepudiationAudit(windowTitle,object);
        }
        else if (msgCategory.isSystemNotification())
        {
            guiLogger.information(windowTitle,defaultBusinessProperty,object);
        }
    }
}
