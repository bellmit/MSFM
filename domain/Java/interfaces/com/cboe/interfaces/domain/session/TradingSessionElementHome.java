package com.cboe.interfaces.domain.session;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;

/**
 * A home for trading session elements.
 *
 * @author John Wickberg
 */
public interface TradingSessionElementHome {

    /**
     * Name of the home for foundation framework.
     */
    public static final String HOME_NAME = "TradingSessionElementHome";

    /**
     * Creates a new trading session element from a template.
     *
     * @param template the template to be used to define new element
     * @param businessDay the date when the element will be used
     * @return created trading session element
     * @exception TransactionFailedException if element could not be created
     */
    TradingSessionElement createFromTemplate(
        TradingSessionElementTemplate template,
        DateStruct businessDay)
        throws TransactionFailedException;

    /**
     * Finds element by key.
     *
     * @param elementKey key of the element
     * @return element with requested key
     * @exception NotFoundException if element with key not found
     */
    TradingSessionElement findByKey(int elementKey)
        throws NotFoundException;

    /**
     * Finds all current trading session elements.
     *
     * @return all trading session elements that are active
     */
    TradingSessionElement[] findCurrent();

    /**
     * Finds all current trading session elements for a session.
     *
     * @param sessionName name of trading session
     * @return all active trading session elements for the session.
     */
    TradingSessionElement[] findCurrentForSession(String sessionName);

    /**
     * Finds current trading session element for a template.
     *
     * @param templateName name of template
     * @return active trading session element for the template.
     */
    TradingSessionElement findCurrentForTemplate(String templateName) throws NotFoundException;

    /**
     * Finds all trading session element for a template.
     *
     * @param templateName name of template
     * @return trading session elements for the template.
     */
    TradingSessionElement[] findAllForTemplate(String templateName) throws NotFoundException;


    /**
     * Finds all trading session elements for a session used on a given business
     * day.
     *
     * @param sessionName name of trading session
     * @param businessDay date that session was run or will be active
     * @return all session elements active for a session on a business day
     */
    TradingSessionElement[] findForSession(
        String sessionName,
        DateStruct businessDay);

    /**
     * Gets current trading session element for a template.
     *
     * @param templateName name of template
     * @return active trading session element for the template or null if not found
     */
    TradingSessionElement getCurrentForTemplate(String templateName);

    /**
     * Purges old session elements.
     *
     * @param cutoffDate all sessions with business days earlier than this date
     *                   will be purged.
     *
     */
    void purge(long cutoffDate);

    /**
     * Converts session element to a struct.
     *
     * @param element element to be converted
     * @return struct corresponding to element
     */
    TradingSessionElementStruct toStruct(TradingSessionElement element, boolean includeReportingClasses);
   
    /**
     * Converts array of session elements to a structs.
     *
     * @param elements elements to be converted
     * @return struct corresponding to element
     */
    TradingSessionElementStruct[] toStructs(TradingSessionElement[] elements);
    
    /**
     * Recovers product timers for all elements of the current business day.
     */ 
    void recoverTimers();

    /**
     * Removes element with the given key.
     *
     * @param elementKey key of element to be removed
     * @exception DataValidationException if key is not found
     */
    void removeElement(int elementKey) throws DataValidationException, TransactionFailedException;

    /**
     * Updates a session element from a struct.
     *
     * @param element session element to be updated
     * @param newValues struct containing updated values
     */
    void updateElement(TradingSessionElementStructV2 newValues)
        throws DataValidationException, NotFoundException;
    
    /**
     * Converts session element to a struct.
     *
     * @param element element to be converted
     * @return struct corresponding to element
     */
    TradingSessionElementStructV2 toStructV2(TradingSessionElement element, boolean includeReportingClasses);
   
    /**
     * Converts array of session elements to a structs.
     *
     * @param elements elements to be converted
     * @return struct corresponding to element
     */
    TradingSessionElementStructV2[] toStructsV2(TradingSessionElement[] elements);
    
}
