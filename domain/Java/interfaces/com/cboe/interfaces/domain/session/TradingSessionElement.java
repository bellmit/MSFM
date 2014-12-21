package com.cboe.interfaces.domain.session;

import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementInfoStruct;

/**
 * A trading session element is a control point for a subset of classes
 * of a trading session.  The PreOpen, Open and Closing times for each can
 * be different for each element of a trading session.
 *
 * @author John Wickberg
 */
public interface TradingSessionElement {

    /**
     * Finds class by product class key.
     *
     * @param classKey product class key of desired class.
     * @return session class matching key
     * @exception NotFoundException if class isn't found
     */
    SessionElementClass findClass(int classKey)
        throws NotFoundException;

    /**
     * Gets the business day of this element.
     * 
     * @return business day as a time in millis
     */
    long getBusinessDay();

    /**
     * Gets classe by key from the session element.
     * 
     * @param classKey product class key of desired class
     *
     * @return found class or null if class isn't found
     */
    SessionElementClass getClassByKey(int classKey);

    /**
     * Gets all classes for the session element.
     *
     * @return all session element classes
     */
    SessionElementClass[] getClasses();

    /**
     * Gets key of this element.
     * 
     * @return key of element
     */
    int getElementKey();

    /**
     * Gets name of this element.
     * 
     * @return name of element
     */
    String getElementName();

    /**
     * Gets state of this element.
     * 
     * @return state of element
     */
    short getElementState();

    /**
     * Gets the time that the products close.
     */
    long getProductCloseTime();

    /**
     * Gets the time that the products open.
     */
    long getProductOpenTime();

    /**
     * Gets the time that the products pre-open.
     */
    long getProductPreOpenTime();

    /**
     * Gets the session of this element
     * 
     * @return template of this element
     */
    TradingSession getSession();

    /**
     * Gets the template of this element
     * 
     * @return template of this element
     */
    TradingSessionElementTemplate getTemplate();

    /**
     * Updates the classes and products of this session element based on
     * its template.
     */
    void updateFromTemplate();

    TradingSessionElementInfoStruct getTradingSessionElementInfoStruct();
}
