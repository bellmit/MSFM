/**
 * 
 */
package com.cboe.interfaces.domain;

/**
 * @author misbahud
 *
 */
public interface AutoLinkedOrderExpireTimerTask
{
    public static final long NO_TASK_ID = -1;
    public boolean cancel();
    public long getTaskId();
    public boolean isInProgress();
    public void setInProgress(boolean p_inProgress);
    public boolean isFillInProgress();
    public void setFillInProgress(boolean p_fillInProgress);
}
