package com.cboe.infra.presentation.network;

import java.io.Serializable;

public interface SBTNode extends Serializable, Comparable
{
//	/** Node type = Distribution Node */
//    public static final int DN_NODE = 1;
//    /** NOde type = General Cluster or Business Cluster */
//    public static final int OR_NODE = 2;
//    /** Node type = Front End */
//    public static final int FE_NODE = 4;
//    /** Node type = Client Application Server */
//    public static final int CAS_NODE = 8;
//    /** Node type = UNDEFINED (Uninitialized) */
//    public static final int UNDEFINED_TYPE = -1;
//    /** Node type = UNKNOWN (Error) */
//    public static final int UNKNOWN_TYPE = 32;
//    /** Node type = Talarian RT Server */
//    public static final int RTSERVER = 16;

    /**
     * Get the name of this node.  Note that node naming conventions differ between Talarian
     * and the extent map.
     */
    public String getName();

	/**
	 * The name of the user owner of the process
	 * @return null For static nodes
	 */
    public String getUser();

    /**
     * Get the type of node.  Valid values are:
     * DN_NODE
     * OR_NODE
     * FE_NODE
     * CAS_NODE
     * RTSERVER
     * UNDEFINED_TYPE
     * UNKNOWN_TYPE
     */
    public SBTNodeType getType();

    /**
     * Convenience method to test whether this is a distribution node
     * @return getType() == DN_NODE
     */
    public boolean isDN();

	/**
	 * Get the hostname of the machine this process is executing on
	 */
    public String getHost();

    /**
     * Test to see whether this node either publishes or subscribes to to subjects
     * on the supplied channel name.
     */
    public boolean belongsToChannel( String channelName );

	/**
	 * Check to see if this node subscribes to the supplied Talarian subject.
	 */
    public  boolean listensOnSubject( String subjectName );


    /**
     * Get a list of local Topics that this node publishes to
     */
    public Topic[] getLocalPublishList();

    /**
     * Get a list of local Topics that this node subscribes to
     */
    public Topic[] getLocalSubscribeList();

    /**
     * Get a list of global Topics that this node publishes to
     */
    public  Topic[] getGlobalPublishList();

    /**
     * Get a list of global Topics that this node subscribes to
     */
    public  Topic[] getGlobalSubscribeList();

    /**
     * Is this node ignoring admin subjects when it provides results
     * for local/global subscribe/publish lists?
     */
    public boolean isIgnoringAdmin();

	/**
	 * Instruct the node to ignore or not ignore admin channel subjects
	 * when constructing lists of Topics (global/local, publish/subscribe)
	 */
    public void setIgnoringAdmin( boolean ignore );

    /**
     * Specified by java.lang.Comparable
     * Compares nodes based on host, name, and type
     */
    public int compareTo( Object o );


}
