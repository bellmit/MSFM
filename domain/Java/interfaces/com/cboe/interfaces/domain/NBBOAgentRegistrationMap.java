package com.cboe.interfaces.domain;

/** NBBOAgentRegistrationMap - TO register the NBBO Agent for given class 
 * @auther Ravi Nagayach
 */
public interface NBBOAgentRegistrationMap
{
    /**
     * Populates the neccessary fields for NBBOAgentRegistrationmap.
     * @param classKey
     * @param sessiionName
     * @param agentId
     */ 
    public void create(int classKey, String sessiionName, String agentId);

    /**
     * Deletes the map, will mark the entry to unregistered.
     */ 
    public void remove();

    /**
     * Gets the agent id for the map/
     * @return String
     */ 
    public String getAgentId();

    /**
     * Gets the classKey for the map.
     * @return int classKey
     */ 
    public int getClassKey();
    
}