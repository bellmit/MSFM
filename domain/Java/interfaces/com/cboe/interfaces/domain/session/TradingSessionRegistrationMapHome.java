package com.cboe.interfaces.domain.session;

import com.cboe.exceptions.*;
import com.cboe.idl.session.TradingSessionServerEventStateStruct;
import com.cboe.idl.session.TradingSessionGroupStruct;
import com.cboe.idl.session.TradingSessionRegistrationStruct;
import com.cboe.idl.internalBusinessServices.TradingSessionClient;
import com.cboe.idl.cmiUtil.KeyValueStruct;

import java.util.Collection;
import java.util.Map;

/**
 * This home is to manage the TradingSessionRegistration - map of session and servers.
 */
public interface TradingSessionRegistrationMapHome
{
    public final static String HOME_NAME = "TradingSessionRegistrationMapHome";

    //This will be called by TS when registering itself. This will also change the server state to be active.
    public void registerClient(String sessionName, String clientName, TradingSessionClient client, KeyValueStruct[] registrationDetails)
        throws DataValidationException;

    /**
     * This is session less registration.
     * If entry alreay exists for this client, no other entry will be created, only state of the client will be made active.
     * If there is no entry for this client then Entry will be created for all the available sessions.
     * @param clientName
     * @param client
     */
    public  void registerClient(String clientName, TradingSessionClient client);

    /**
     * This is used by TSS to update the server's active status.
     * No need to have session name because trader does not have the session name.
     * If server is not already registered Log the message and do not put the server into the map.
     * If server is supporting more than one session than change to active state for all the session of this server.
     * @param clientName
     * @param client
     * @param isActive
     * @exception NotFoundException
     */
    public void updateClientState(String clientName, TradingSessionClient client, boolean isActive)
        throws NotFoundException;

    /**
     * Return all clients for the given session.
     */
    public String[] getActiveClientsForSession(String sessionName);

    public String[] getInactiveClientsForSession(String sessionName);

    /**
     * Validate if the clientName has registered with the given session.
     * Return true if session-client combination is valid, otherwise false.
     * @param sessionName
     * @param clientName
     * @return true/false
     */
    public boolean validateClientRegistration(String sessionName, String clientName);

    /**
     * Validate if the clientName has registered with the given session.
     * Return true if the client is registered for any session, otherwise false.
     * @param clientName
     * @return true/false
     */
    public boolean validateClientRegistration(String clientName);

    /**
     * Validate if a client using the given groupName has registered with the given session.
     * Return true if session-group combination is valid, otherwise false.
     * @param sessionName
     * @param groupName
     * @param groupType
     * @return true/false
     */
    public boolean validateGroupRegistration(String sessionName, String groupName, int groupType);

    /**
     * Update the last event sent and its status for the given client-session.
     * @param sessionName
     * @param clientName
     * @param historyDetail
     * @exception DataValidationException
     */
    public void updateHistoryDetailForClient(String sessionName, String clientName, TradingSessionEventHistoryDetail historyDetail)
        throws DataValidationException;


    /**
     *  Same as <code>updateSequenceNumberGenerators(Collection, true)</code>.
     *
     *  @see updateSequenceNumberGenerators(Collection, boolean)
     */
    public int updateSequenceNumberGenerators(Collection theDetails);

    /**
     * For each TradingSessionEventHistoryDetail in detailsList, if getClientName() is a registered
     * client, then set that detail object's sequence number generator reference to be the same
     * as the one for the registered client.  This will ensure that all detail objects for a
     * registered client use mutually incremental sequence numbers.
     *
     * @param theDetails - Collection of TradingSessionEventHistoryDetail's.
     * @param setToNextNumber - if true, then the current sequence number for each entry will
     *        be set to 'nextNumber' to be sure that their values are corrent (ie, get rid of
     *        whatever 'stale' value they may current have).
     * @return int - number of details in list that had their seq# gen reference updated.
     */
    public int updateSequenceNumberGenerators(Collection theDetails, boolean setToNextNumber);

    /**
     * It gives the current state of trading session event and its state on the servers.
     * <br><b>Note:</b> Supports ALL_SESSIONS
     * @param sessionName
     */
    public TradingSessionServerEventStateStruct[] getClientEventStates(String sessionName);

    /**
     * Find out all the active clients and the registered 'trading session client'
     * It should return 0 size table if there are no active clients.
     * <br><b>Note:</b> Supports ALL_SESSIONS
     * @param sessionName
     * @return table of clientId->TradingSessionClient
     */
    public Map getActiveClientMapForSession(String sessionName);

    /**
     * Find out all the inactive clients and the registered 'trading session client'
     * It should return 0 size table if there are no inactive clients.
     * <br><b>Note:</b> Supports ALL_SESSIONS
     * @param sessionName
     * @return List of  clientIds
     */
    public Map getInactiveClientMapForSession(String sessionName);

    /**
     * TradingSessionClient registered for the client.
     * If 'all sessions' is specified, then the first non-null TSC for clientName in any session is returned.
     * <br><b>Note:</b> Supports ALL_SESSIONS
     * @param sessionName
     * @param clientName
     * @return The client
     */
    public TradingSessionClient getTradingSessionClientForClient(String sessionName, String clientName);

    /**
     * Return the registration object for the given session/client combination.
     */
    public TradingSessionRegistration findRegistration(String sessionName, String clientName) throws NotFoundException;

    /**
     * Return a struct array representing the registered clients.  If sessionName is not an 'all sessions' constant,
     * then the resulting array will be of size 1.
     * <br><b>Note:</b> Supports ALL_SESSIONS
     */
    public TradingSessionRegistrationStruct[] getRegisteredServerNames(String sessionName);

    /**
     * Return a struct representing all groups for all sessions.
     */
    public TradingSessionGroupStruct[] getRegisteredGroups();

    /**
     * Return a struct representing all groups for all sessions that have the given group type.
     */
    public TradingSessionGroupStruct[] getRegisteredGroups(int groupType);
}
