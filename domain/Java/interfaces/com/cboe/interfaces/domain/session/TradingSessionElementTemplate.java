package com.cboe.interfaces.domain.session;

import com.cboe.idl.session.TemplateClassStruct;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.TransactionFailedException;

/**
 * A template for trading session elements.  A template contains static
 * definitions to make it easier to create elements on a daily basis.
 *
 * @author John Wickberg
 */
public interface TradingSessionElementTemplate {

    /**
     * Adds a class to this template.
     *
     * @param newClass struct defining class to be added.
     * @return new template class
     */
    TemplateClass addClass(TemplateClassStruct newClass) throws DataValidationException, TransactionFailedException;

    /**
     * Finds a class assigned to this template.
     *
     * @param classKey key of class
     * @return found class
     * @exception NotFoundException if class is not assigned to template
     */
    TemplateClass findClass(int classKey) throws NotFoundException;

    /**
     * Gets all of the classes assigned to this template.
     *
     * @return all assigned classes
     */
    TemplateClass[] getClasses();

    /**
     * Removes assigned class from this template.
     *
     * @param classKey key of class to be removed
     */
    void removeClass(int classKey) throws DataValidationException;
    
    // Getters used by session element impl
    boolean autoPreOpenProducts();
    boolean autoOpenProducts();
    boolean autoCloseProducts();
    boolean isActive();
    String getTemplateName();
    int getProductPreOpenTime();
    int getProductOpenTime();
    int getProductCloseTime();
    int getSequenceNumber();
    TradingSession getSession();

}
