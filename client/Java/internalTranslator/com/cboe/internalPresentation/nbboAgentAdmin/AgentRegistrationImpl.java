//
// ------------------------------------------------------------------------
// FILE: AgentRegistrationImpl.java
//
// PACKAGE: com.cboe.internalPresentation.nbboAgentAdmin
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.internalPresentation.nbboAgentAdmin;

import com.cboe.interfaces.internalPresentation.nbboAgentAdmin.AgentRegistration;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.businessServices.NBBOAgentAdminServicePackage.AgentRegistrationStruct;
import com.cboe.presentation.api.APIHome;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

/**
 * @author torresl@cboe.com
 */
class AgentRegistrationImpl implements AgentRegistration
{
    protected AgentRegistrationStruct agentRegistrationStruct;
    protected SessionProductClass sessionProductClass;
    public AgentRegistrationImpl(AgentRegistrationStruct agentRegistrationStruct)
            throws Exception
    {
        super();
        this.agentRegistrationStruct = agentRegistrationStruct;
        initialize();
    }
    public AgentRegistrationImpl(String userId, SessionProductClass sessionProductClass)
            throws Exception
    {
        super();
        this.sessionProductClass = sessionProductClass;
        agentRegistrationStruct = new AgentRegistrationStruct();
        agentRegistrationStruct.agentId = userId;
        agentRegistrationStruct.classKey = sessionProductClass.getClassKey();
        agentRegistrationStruct.sessionName = sessionProductClass.getTradingSessionName();
    }

    private void initialize() throws AuthorizationException, SystemException, CommunicationException, NotFoundException, DataValidationException
    {
        sessionProductClass = APIHome.findProductQueryAPI().getClassByKeyForSession(getSessionName(), getClassKey());
    }

    public String getAgentId()
    {
        return agentRegistrationStruct.agentId;
    }

    public String getSessionName()
    {
        return agentRegistrationStruct.sessionName;
    }

    public int getClassKey()
    {
        return agentRegistrationStruct.classKey;
    }

    public SessionProductClass getSessionProductClass()
    {
        return sessionProductClass;
    }

    public AgentRegistrationStruct getStruct()
    {
        return agentRegistrationStruct;
    }
}
