package com.cboe.interfaces.domain.session;

// cboe classes
import com.cboe.idl.session.*;
import com.cboe.exceptions.*;
import com.cboe.infrastructureServices.persistenceService.*;

/**
 * Home interface for the Trading Session.
 * @author Ravi Vazirani
 */
public interface TradingSessionHome
{
	// CLASS CONSTANTS
   	/**
   	 * Name of the home
   	 */
   	 public static final String HOME_NAME = "TradingSessionHome";
/**
 * Creates and returns the new session.
 * @return com.cboe.interfaces.businessServices.TradingSession
 * @param aNewSession com.cboe.idl.session.TradingSessionStruct
 * @exception com.cboe.idl.cmiExceptions.DataValidationException 
 */
public TradingSession create(TradingSessionStruct aNewSession) throws DataValidationException;
/**
 * Returns the session with specified name.
 * @return TradingSession
 * @exception NotFoundException if the session is not found.
 */
public TradingSession find(String aSessionName) throws NotFoundException;
/**
 * Returns all the Trading sessions defined.
 * @return TradingSession[]
 */
public TradingSession[] findAll();
/**
 * Initializes all sessions for a new business day.  Called after a new business
 * day is created to have the sessions create their timers for the new day.
 */
void initializeSessionsForNewDay(boolean multiThreaded);
/**
 * Recovers start/end timers for all sessions.
 */ 
void recoverTimers();
/**
 * Deletes the session with specified name.
 * @param TradingSession
 * @exception SystemException if the session is not found.
 */
public void remove(TradingSession aSession) throws SystemException;
/**
 * Creates and returns the structure given the Trading session object.
 * @return com.cboe.idl.session.TradingSessionStruct
 * @param aSession com.cboe.interfaces.businessServices.TradingSession
 */
TradingSessionStruct toStruct(TradingSession aSession);
/**
 * Creates and returns the structure given the Trading session objects.
 * @return converted structs
 * @param sessions sesions to be converted
 */
TradingSessionStruct[] toStructs(TradingSession[] sessions);
/**
 * Updates and returns the modified session.
 * @param TradingSession the existing session.
 * @param aNewSession com.cboe.idl.session.TradingSessionStruct
 * @exception com.cboe.idl.cmiExceptions.DataValidationException 
 */
public void updateSession(TradingSessionStruct aNewSession) throws NotFoundException, DataValidationException;
}
