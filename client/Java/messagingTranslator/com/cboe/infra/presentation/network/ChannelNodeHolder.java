/**
 * File Name: ChannelNodeHolder.java
 * 
 * @author Sridhar Nimmagadda
 * 
 * Created on Feb 14, 2003
 * @version
 * 
 * Migrated to diff package on 04/05/05 by jwalton
 */
package com.cboe.infra.presentation.network;

import com.cboe.EventService.ExtentMap.ExtentMapNodeChannelInfo;
import com.cboe.EventService.ExtentMap.NodeChannelInfoDelegate;

/**
 * Created on Feb 14, 2003
 * 
 * @author nimmagad
 *  
 */
public class ChannelNodeHolder
{

    private String _myName;

    private NodeChannelInfoDelegate nodeChannelDelegate;

    private String[] interfaceNames;

    /**
     * Constructor ChannelNodeHolder.
     * 
     * @param string
     * @param nodeChannelInfo
     */
    public ChannelNodeHolder(String channelName,
            ExtentMapNodeChannelInfo nodeChannelInfo)
    {
        _myName = channelName;
        nodeChannelDelegate = new NodeChannelInfoDelegate(nodeChannelInfo);
        interfaceNames = (String[]) nodeChannelDelegate
                .getInterfaceIDsForChannel().toArray(new String[0]);
    }

    /**
     * Constructor for ChannelNodeHolder.
     */
    public ChannelNodeHolder()
    {
    }

    /**
     * Returns the interfaceNames.
     * 
     * @return String[]
     */
    public String[] getInterfaceNames()
    {
        return interfaceNames;
    }

}