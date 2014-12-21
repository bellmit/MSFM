package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiAdmin.MessageStruct;

public interface AdministratorAPI
{

    /**
     * Send a message to a user and/or group
     * @usage Send a message to a user and/or group
     * @returns sent message's messageId
     * @raises SystemException, CommunicationException, AuthorizationException, DataValidationException
     */
    public int sendMessage(MessageStruct message)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
