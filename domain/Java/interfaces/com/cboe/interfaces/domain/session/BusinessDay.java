
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author    Silvia Torres
 * @version 1.0
 */
package com.cboe.interfaces.domain.session;

import java.util.Date;

public interface BusinessDay
{

    public static final int CURRENT   = 1;
    public static final int COMPLETE  = 2;
    public static final int FUTURE    = 3;

    /**
     * Adds the specified listener to receive events from the BusinessDayObject.
     * Events occur when the timer register by the listener is expired.
     *
     * @param aListener Bussines Day Listener that need to be notified when the business day starts and ends.
     * @param timerType Specifie the timer to be queue.
     * @param time      Specifie the time to be waiting.
     * @return boolean  This method will return true if the Listener was register, otherwise false.
     */
    public boolean registerListener (BusinessDayListener aListener, int timerType, long time);
    
    /**
     * Remove a Listener from the queue. 
     * @param BusinessDayListener Listener to be remove.
     * @param int  Defines the type of timer this listener was resgister to. 
     */
    public void removeListener (BusinessDayListener aListener, int timerType);

    /**
     * Method use to know if there are listeners register.
     * 
     * @return boolean  Returns true if there are listeners otherwise false.
     */
    public boolean hasListenersRegister();
    
    public Date getDate();
    
    /**
     * Gets business state of this day.
     */
    public short getState();
    
    /**
     * Gets status of day.  This is a control flag and not the exposed state.  Status is mainly used
     * to control which date is the current business day.  Allows a business day to be opened before
     * the actual calendar day.
     */
    public int getStatus();
    
    /**
     * Sets business state of this day.
     */
    public void setState(short newState);
}