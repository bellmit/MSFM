package com.cboe.infra.presentation.network;

public abstract class AbstractSBTNode implements SBTNode, Comparable, Cloneable
{
    protected String hostName = "Unknown";
    protected String userOwner = "Unknown";
    protected SBTNodeType nodeType = SBTNodeType.UNDEFINED_TYPE;
    protected String nodeName = "Unknown";
    protected boolean ignoreAdmin = true;
    //*************************************************************************
    // CTORS
    //*************************************************************************
    public AbstractSBTNode(String name, SBTNodeType type) {
        nodeName = name;
        nodeType = type;
    }

    public AbstractSBTNode(String name) {
        nodeName = name;
        nodeType = deriveTypeFromName(name);
    }
    //*************************************************************************
    // Simple properties
    //*************************************************************************
    public String getUser() {
        return userOwner;
    }

    public boolean isIgnoringAdmin() {
        return ignoreAdmin;
    }

    public void setIgnoringAdmin(boolean ignore) {
        ignoreAdmin = ignore;
    }

    public SBTNodeType getType() {
        return nodeType;
    }

    public String getHost() {
        return hostName;
    }

    public String getName() {
        return nodeName;
    }

    public boolean isDN() {
        return getType() == SBTNodeType.DN_NODE;
    }

    //*************************************************************************
    // java.lang.Object overrides
    //*************************************************************************
//    public boolean equals(Object o) {
//        boolean rv = false;
//        if (!(o instanceof SBTNode)) {
//            // immediate failure
//            return rv;
//        }
//        SBTNode other = (SBTNode) o;
//        rv = this.getClass().getName().equals(o.getClass().getName()) && this.getName().equals(other.getName()) && this.getHost().equals(other.getHost());
//
//        return rv;
//    }

    public int hashCode() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        sb.append(nodeName);
        sb.append(hostName);
        return sb.toString().hashCode();
    }

    public String toString() {
        return nodeName;
    }

    //*************************************************************************
    // Topics
    //*************************************************************************
    public abstract Topic[] getLocalPublishList();
    public abstract Topic[] getLocalSubscribeList();
    public abstract Topic[] getGlobalPublishList();
    public abstract Topic[] getGlobalSubscribeList();
    //*************************************************************************
    // Subjects and channels
    //*************************************************************************
    public abstract boolean listensOnSubject(String subjectName);
    public abstract boolean belongsToChannel( String channelName );
    //*************************************************************************
    // Miscellaneous
    //*************************************************************************
    public int compareTo( Object o )
    {
        SBTNode other = (SBTNode)o;
        return ( this.nodeName + this.hostName + Integer.toString( getType().intValue() ) ).compareTo( other.getName() + other.getHost() + Integer.toString( other.getType().intValue() ) );
    }

    protected abstract SBTNodeType deriveTypeFromName( String name );
}