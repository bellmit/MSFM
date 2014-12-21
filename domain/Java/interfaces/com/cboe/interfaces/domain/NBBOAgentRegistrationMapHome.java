package com.cboe.interfaces.domain;

import com.cboe.exceptions.*;

/** NBBOAgentRegistrationMapHome will manage NBBOAgentRegistrationMap
 * @auther Ravi Nagayach
 */
public interface NBBOAgentRegistrationMapHome
{
    public static final String HOME_NAME = "NBBOAgentRegistrationMapHome";

    public String findNBBOAgentForClass(int classKey, String sessionName) throws NotFoundException, SystemException, DataValidationException;

    public void registerAgent(int classKey, String agentId, String sessionName, boolean forceUpdate, boolean validateDPMUser)
    throws AuthorizationException, SystemException, CommunicationException, DataValidationException, TransactionFailedException;

    public void unregisterAgent(int classKey, String sessionName, String agentId, boolean validateDPMUser)
            throws AuthorizationException, SystemException, CommunicationException, DataValidationException, TransactionFailedException;

    public void validateAgentForClass(String agentId, String sessionName, int classKey) throws AuthorizationException;

    public void removeAgentFromCache(int classKey, String sessionName);

    public NBBOAgentRegistrationMap[] findAllNBBOAgentMapsForSession(String sessionName);


}