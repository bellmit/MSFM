package com.cboe.interfaces.domain.userReportManager;

import java.util.Date;

/**
 *  Described values to maintain a user registered for a certain type of report.
 *
 *  @author Steven Sinclair
 */
public interface RegisteredUser
{
    public String getUserId();
    public int getReportType();
    public Date getTimeStamp();
    public int getClassKey();

    public void setUserId(String userId);
    public void setReportType(int reportType);
    public void setTimeStamp(Date date);
    public void setClassKey(int classKey);
}


