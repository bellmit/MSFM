
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cboe.interfaces.domain.session;

public interface BusinessDayListener
{

    
    /**
     * Constant that defines a Timer for Bussiness Day.
     */
    public static int BUSINESS_DAY_TIMER_TYPE = 0;
    
    /**
     * Constant that defines a Timer for Pre Open Product.
     */
    public static int PRODUCT_PRE_OPEN_TIMER_TYPE = 1;
    
    /**
     * Constant that defines a Timer for Open Product.
     */
    public static int PRODUCT_OPEN_TIMER_TYPE = 2; 
    
    /**
     * Constant that defines a Timer for Close Product.
     */
    public static int PRODUCT_CLOSE_TIMER_TYPE = 3;
    
    /**
     * Constant that defines a Timer for Start Session.
     */
    public static int START_SESSION_TIMER_TYPE = 4;
    
    /**
     * Constant that defines a Timer for End Session.
     */
    public static int END_SESSION_TIMER_TYPE = 5;
    
    /**
     * Constant that defines a Timer for early close.
     */
    public static int PRODUCT_EARLY_CLOSE_TIMER_TYPE = 6;

    /**
     * Invoke when the BusinessDay timer is expired.
     */
    public void handleBusinessDayTimerExpired ();
    
    /**
     * Invoke when the Preopen Product timer is expired.
     */
    public void handleProductPreOpenTimerExpired();
    
    /**
     * Invoke when the Open Product timer is expired.
     */
    public void handleProductOpenTimerExpired();
    
    /**
     * Invoke when the Close Product timer is expired.
     */
    public void handleProductCloseTimerExpired();
    
    /**
     * Invoke when the StartSession Product timer is expired.
     */
    public void handleStartSessionTimerExpired ();
    
    /**
     * Invoke when the EndSession Product timer is expired.
     */
    public void handleEndSessionTimerExpired();
    
    /**
     * Invoke when the Early Close Product timer is expired.
     */
    public void handleProductEarlyCloseTimerExpired();
}