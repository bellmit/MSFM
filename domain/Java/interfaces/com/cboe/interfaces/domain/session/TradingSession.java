package com.cboe.interfaces.domain.session;

import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;

/**
 * Home interface for the Trading Session.
 * @author Ravi Vazirani
 */
public interface TradingSession
{
/**
 *
 * @return boolean
 */
boolean abortEndOfSession();
/**
 * Adds a product to a session.  Used for intraday series adds and strategies.
 *
 * @param newProduct product being added
 */
SessionElementProduct addProduct(ProductStruct newProduct) throws DataValidationException, TransactionFailedException;
/**
 *
 * @return boolean
 */
boolean allProductsClosed();
/**
 *
 * @return boolean
 */
boolean autoStartEndOfSession();
/**
 * Completes end of session processing.
 */
void completeEndOfSession();
/**
 * Finds current session class by key.
 */
SessionElementClass findClassByKey(int classKey) throws NotFoundException;
/**
 * Finds current session product by key.
 */
SessionElementProduct findProductByKey(int productKey) throws NotFoundException;

/**
 * Gets current session product by key.
 *
 * @return SessionElementProduct; returns null if not found.
 */
public SessionElementProduct getProductByKey(int productKey);

/**
 * Get the exchange associated with the session
 */
public String getExchange();

/**
 * Returns the stragey name associated with the end of session.
 * @return java.lang.String
 */
String getEndOfSessionStrategy();
/**
 * Gets end time of this session.
 */
long getEndTime();
/**
 * Gets the name of this session.
 */
String getSessionName();
/**
 * Gets the state of this session.
 */
short getSessionState();
/**
 * Gets start time of this session.
 */
long getStartTime();
/**
 * Returns the session destinationCode.
 * @return detination code
 */
short getSessionDestinationCode();
/**
 * Gets default underlying session name
 */
String getDefaultUnderlyingSessionName();
/**
 * Checks given classes to see which ones are not assigned to the session.
 *
 * @param classes classses to be checked
 * @return structs for classes not assigned in this session
 */
ClassStruct[] getUnassignedClasses(ProductClassStruct[] classes);
/**
 * Determines if this is the last session of the business day.
 */
boolean isLastSessionOfDay();
/**
 * Determines if this product is trabable in this session.
 *
 * @param productKey key of product to be checked
 */
boolean isTradableInSession(int productKey);
/**
 * Sets flag to abort end of session processing.
 */
void setAbortEndOfSession(boolean newValue);
/**
 * Sets the code for the event in progress.
 */
void setCurrentEndOfSessionEvent(short newValue);
/**
 * Sets the state of this session.
 */
void setSessionState(short newState);
 /**
 * Starts this session.
 */
void startSession(boolean quickStart);
}
