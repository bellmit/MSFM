
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cboe.interfaces.domain.session;

import java.util.Date;
import com.cboe.idl.cmiUtil.*;

public interface BusinessDayHome {

     /**
     * Name of this home for foundation framework.
     */
    public static final String HOME_NAME = "BusinessDayHome";

    /**
     * Utility method to find out wherether or not the specified Date is a BusinessDay.
     * 
     * @param DateStruct Specified the date.
     * @return boolean   True if is a business day otherwise false.
     */
    public boolean isBusinessDay(DateStruct aDate);
    
    /**
    * Finds a Business Day for a specified Date.
    * 
    * @param DateStruct Specify the Date for the Business Day.
    */
    public BusinessDay find(DateStruct aDate);
    
    /**
    * Finds a Business Day for a specified Date.
    * 
    * @param Date Specify the Date for the Business Day.
    */   
    public BusinessDay find (Date aDate);
    
    /**
     * Finds the Business day for Today date. If there is not a Business day , a new one is created.
     * 
     */
    public BusinessDay findCurrent ();
       
    /**
     * Creates the Business Day for the current Day.
     */
    public BusinessDay createCurrentDay();

    /**
     * Creates the Business Day for the next Day.
     */
    public BusinessDay createNext();
    
    /**
     * Creates a Business Day for a specified date.
     * 
     * @param DateStruct specify the Date for the Business Day
     */
    public BusinessDay create(DateStruct aDate);
    
    /**
     * Creates a Business Day for a specified date.
     * 
     * @param Date specify the Date for the Business Day
     */
    public BusinessDay create(Date aDate);

}