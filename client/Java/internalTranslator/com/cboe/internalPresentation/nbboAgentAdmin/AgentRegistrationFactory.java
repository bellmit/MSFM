//
// ------------------------------------------------------------------------
// FILE: AgentRegistrationFactory.java
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

public class AgentRegistrationFactory
{
    public static AgentRegistration createAgentRegistration(AgentRegistrationStruct agentRegistrationStruct)
                throws Exception
    {
        return new AgentRegistrationImpl(agentRegistrationStruct);
    }
    public static AgentRegistration createAgentRegistration(String userId, SessionProductClass sessionProductClass)
                throws Exception
    {
        return new AgentRegistrationImpl(userId, sessionProductClass);
    }
}