package com.cboe.interfaces.domain.session;

import com.cboe.idl.session.TemplateClassStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;


/**
 * A home for template classes.
 *
 * @author John Wickberg
 */
public interface TemplateClassHome {

    /**
     * Name of this home for foundation framework.
     */
    public static final String HOME_NAME="TemplateClassHome";

    /**
     * Creates a new template class.
     *
     * @param template template being assigned new class
     * @param newClass struct defining template class
     * @return created template class
     */
    TemplateClass create(TradingSessionElementTemplate template, TemplateClassStruct newClass)
        throws DataValidationException, TransactionFailedException;

    /**
     * Finds assigned class by session and class key.
     * 
     * @param sessionName name of session
     * @param classKey class key
     * @exception NotFoundException if combination is not found
     */
    TemplateClass findBySession(String sessionName, int classKey)
        throws NotFoundException;
    
    /**
     * Finds all assigned instances of a class.
     *
     * @param classKey key of requested class
     * @return all assigned instances of the class
     */
    TemplateClass[] findByClass(int classKey);

    /**
     * Converts template class object to a struct.
     * 
     * @param templateClass class to be converted
     * @return converted class
     */
     TemplateClassStruct toStruct(TemplateClass templateClass);

    /**
     * Converts template class objects to a structs.
     * 
     * @param templateClasses classes to be converted
     * @return converted classes
     */
     TemplateClassStruct[] toStructs(TemplateClass[] templateClasses);

}
