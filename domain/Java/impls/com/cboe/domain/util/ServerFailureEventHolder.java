package com.cboe.domain.util;

public final class ServerFailureEventHolder
{
    private int[] groups;
    private int groupKeyFailed;
    private short serverType;
    private String sessionName;
    private int[] classKeys;
    private short activityReason;
    private String text;
    
    public ServerFailureEventHolder(int[] groups, int groupKeyFailed, short serverType, String sessionName, int[] classKeys, short activityReason, String text)
    {
        this.groups = groups;
        this.groupKeyFailed = groupKeyFailed;
        this.serverType = serverType;
        this.sessionName = sessionName;
        this.classKeys = classKeys;
        this.activityReason = activityReason;
        this.text = text;
    }
    
    public int[] getGroups() { return groups; }
    public int getGroupKeyFailed() { return groupKeyFailed; }
    public short getServerType() { return serverType; }
    public String getSessionName() { return sessionName; }
    public int[] getClassKeys() { return classKeys; }
    public short getActivityReason() { return activityReason; }
    public String getText() { return text; }
}
