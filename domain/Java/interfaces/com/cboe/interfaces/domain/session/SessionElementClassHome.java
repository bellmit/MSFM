package com.cboe.interfaces.domain.session;

import com.cboe.idl.session.ClassStateDetailStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionClassDetailStruct;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;

import java.util.HashMap;

/**
 * A home for session element classes.
 *
 * @author John Wickberg
 */
public interface SessionElementClassHome {

    /**
     * Name of this home for foundation framework.
     */
    public static final String HOME_NAME = "SessionElementClassHome";

    /**
     * Creates a session class from a template class.
     *
     * @param element trading session element to own session class
     * @param definition template class used to define session class
     * @return created session class
     * @exception TransactionFailedException if session class cannot be created
     */
    SessionElementClass create(TradingSessionElement element, TemplateClass definition)
        throws TransactionFailedException;
        
    /**
     * Find all assignments for a class.
     *
     * @param classKey key of requested class
     * @return all assignments for the class
     */
    SessionElementClass[] findByClass(int classKey);
    
    /**
     * Sets strategy tick size map
     * 
     * @param newMap new map that contains strategy tick sizes by session name
     */
    void setStrategyTickSizeMap(HashMap newMap);
    
    /**
     * Converts class to a detailed state struct.
     * 
     * @param elementClass class to be converted
     * @return converted struct
     */
    ClassStateDetailStruct toDetailStateStruct(SessionElementClass elementClass);
    
    /**
     * Converts class to a struct.
     * 
     * @param elementClass class to be converted
     * @return converted struct
     */
    SessionClassDetailStruct toStruct(SessionElementClass elementClass);
    
    /**
     * Converts classes to a struct.
     * 
     * @param elementClasses classes to be converted
     * @return converted structs
     */
    SessionClassDetailStruct[] toStructs(SessionElementClass[] elementClasses);

    /**
     * Converts class to a session struct.
     * 
     * @param elementClass class to be converted
     * @return converted struct
     */
    SessionClassStruct toSessionClassStruct(SessionElementClass elementClass);

    /**
     * Converts classes to a session struct.
     * 
     * @param elementClasses classes to be converted
     * @return converted structs
     */
    SessionClassStruct[] toSessionClassStructs(SessionElementClass[] elementClasses, boolean includeReportingClasses);
}
