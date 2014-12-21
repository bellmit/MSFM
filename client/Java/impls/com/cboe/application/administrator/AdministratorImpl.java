package com.cboe.application.administrator;

import com.cboe.idl.cmiAdmin.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiConstants.*;

import com.cboe.idl.textMessage.*;
import com.cboe.idl.constants.*;

import com.cboe.application.shared.*;
import com.cboe.application.shared.consumer.*;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.domain.logout.LogoutServiceFactory;

import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.domain.util.*;

public class AdministratorImpl extends BObject implements Administrator, UserSessionLogoutCollector
{
    private static final String                 DEFAULT_SACAS_USERID    = "" + UserRoles.HELP_DESK;
    private static final int                    DEFAULT_SACAS_ROLE      = TextMessageTypes.ROLE;
    private static final int                    DEFAULT_TIME_TO_LIVE    = TextMessageConstants.INFINITE_TIME_TO_LIVE;

    private String sacasUserId;
    protected SessionManager                    currentSession;

    private TextMessagingService textMessagingService    = null;
    private UserSessionLogoutProcessor userLogoutProcessor = null;

    public AdministratorImpl(String sacasUser)
    {
        super();
        sacasUserId = sacasUser;
    }

    protected void setSessionManager(SessionManager session)
    {
        currentSession = session;
        userLogoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, userLogoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
    }

//TextMessagingService
//
    /**
     * Send a message to a user and/or group
     * @usage Send a message to a user and/or group
     * @returns sent message's messageId
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public int sendMessage(MessageStruct message)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling sendMessage for " + currentSession);
        }
        DestinationStruct[]     receipients = new DestinationStruct[ 1 ];
        MessageTransportStruct  messageTransport;
        MessageResultStruct     messageResults;

        message.sender = currentSession.getValidSessionProfileUser().userId;
        message.timeStamp = TimeServiceWrapper.toDateTimeStruct();
        receipients[ 0 ]    = new DestinationStruct( getSACASUserId(), getSACASRole() );
        messageTransport    = new MessageTransportStruct(   TextMessageStates.UNDELIVERED
                                                            , getTimeToLive()
                                                            , message
                                                            );

        messageResults = getTextMessagingService().sendMessage(receipients, messageTransport);
        if ( messageResults.status.length == 0 )
        {
            return messageResults.messageKey;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Error sending mesage to System Administrator " + "session : " + currentSession, messageResults.status[ 0 ].error);
        }
    }

    private TextMessagingService getTextMessagingService()
    {
        if ( textMessagingService == null )
        {
            textMessagingService = ServicesHelper.getTextMessagingService();
        }

        return textMessagingService;
    }


    private String getSACASUserId()
    {
        return sacasUserId;
    }

    private int getTimeToLive()
    {
        int         timeToLive = 0;

        ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
        String fullName = getBOHome().getFullName();
        StringBuilder parameter = new StringBuilder(fullName.length()+11);
        parameter.append(fullName).append(".TimeToLive");
        timeToLive = configService.getInt( parameter.toString(), DEFAULT_TIME_TO_LIVE );

        return timeToLive;
    }

    private int getSACASRole()
    {
        int         sacasRole = 0;

        ConfigurationService configService = FoundationFramework.getInstance().getConfigService();
        String fullName = getBOHome().getFullName();
        StringBuilder parameter = new StringBuilder(fullName.length()+10);
        parameter.append(fullName).append(".SACASRole");
        sacasRole = configService.getInt( parameter.toString(), DEFAULT_SACAS_ROLE );

        return sacasRole;
    }

    public void acceptUserSessionLogout() {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(currentSession,this);
        userLogoutProcessor.setParent(null);
        userLogoutProcessor = null;

        // Do any individual service clean up needed for logout
        currentSession = null;
        textMessagingService = null;
        userLogoutProcessor = null;

    }
}//EOC
