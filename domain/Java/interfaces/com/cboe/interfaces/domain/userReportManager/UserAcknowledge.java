package com.cboe.interfaces.domain.userReportManager;

import java.util.Date;

/**
 *  Interface to define user ackknowledge information for user reports.
 *
 *  @author Steven Sinclair
 */
public interface UserAcknowledge
{
    public String getUserId();
    public String getReportKey();
    public Date getTimeStamp();
    public String getTransClockPoints();

    public void setUserId(String userId);
    public void setReportKey(String reportKey);
    public void setTimeStamp(Date date);
    public void setTransClockPoints(String points);
}
