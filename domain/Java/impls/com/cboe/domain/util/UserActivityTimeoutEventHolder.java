package com.cboe.domain.util;

public final class UserActivityTimeoutEventHolder
{
    private int[] groups;
    private String userId;
    private String sessionName;
    private int[] classKeys;
    private short activityReason;
    private String text;
    
    public UserActivityTimeoutEventHolder(int[] groups, String userId, String sessionName, int[] classKeys, short activityReason, String text)
    {
        this.groups = groups;
        this.userId = userId;
        this.sessionName = sessionName;
        this.classKeys = classKeys;
        this.activityReason = activityReason;
        this.text = text;
    }
    
    public int[] getGroups() { return groups; }
    public String getUserId() { return userId; }
    public String getSessionName() { return sessionName; }
    public int[] getClassKeys() { return classKeys; }
    public short getActivityReason() { return activityReason; }
    public String getText() { return text; }
}
