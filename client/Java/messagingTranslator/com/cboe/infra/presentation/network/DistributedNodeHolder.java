/**
 * File Name: DistributedNodeHolder.java
 * 
 * @author Sridhar Nimmagadda
 * 
 * Created on Feb 14, 2003
 * @version
 *  
 */
package com.cboe.infra.presentation.network;

import java.util.Hashtable;
import java.util.Set;

import org.omg.CORBA.SystemException;

import com.cboe.EventService.ExtentMap.ExtentMapAdminV6;
import com.cboe.EventService.ExtentMap.ExtentMapException;
import com.cboe.EventService.ExtentMap.ExtentMapNodeChannelInfo;
import com.cboe.common.log.Logger;
import com.cboe.idl.DistributedFilterNode.DistributedNodeAdmin;
import com.cboe.idl.DistributedFilterNode.DistributedNodeQuery;

/**
 * Created on Feb 14, 2003
 * 
 * @author nimmagad
 *  
 */
public class DistributedNodeHolder
{
    private String compName = "DistributedNodeHolder::";

    private ExtentMapAdminV6 extMapAdminRef;

    private DistributedNodeQuery queryRef;

    private DistributedNodeAdmin adminRef;

    private boolean isIntialized = false;

    private Hashtable channelNodes = new Hashtable();

    private String extentMapNodeName;

    private String myName;

    private String[] channelNames;

    private boolean isActive;

    /**
     * Constructor DistributedNodeHolder.
     * 
     * @param dnQueryRef
     */
    public DistributedNodeHolder(String dnName,
            DistributedNodeQuery dnQueryRef, ExtentMapAdminV6 emAdminRef)
            throws DistributedNodeUtilException
    {
        this.myName = dnName;
        this.queryRef = dnQueryRef;
        this.extMapAdminRef = emAdminRef;
        try
        {
            extentMapNodeName = queryRef.getExtentMapNodeName(myName);
            ExtentMapNodeChannelInfo[] nodeChannels = this.extMapAdminRef
                    .retrieveDNExtentMapInfo(extentMapNodeName);
            for (int i = 0; i < nodeChannels.length; i++)
            {
                ChannelNodeHolder channelHolder = new ChannelNodeHolder(
                        nodeChannels[i].eventChannelID, nodeChannels[i]);
                channelNodes.put(nodeChannels[i].eventChannelID, channelHolder);
            }

            channelNames = (String[]) channelNodes.keySet().toArray(
                    new String[0]);
            isActive = true;
        } catch (ExtentMapException e)
        {
            Logger
                    .sysAlarm(
                            compName
                                    + "DistributedNodeHolder() - Exception while Initializing the Distributed Node <"
                                    + dnName + ">", e);
            throw new DistributedNodeUtilException(
                    "Exception while Initializing the Distributed Node <"
                            + dnName + ">", e);
        } catch (SystemException sysXp)
        {
            Logger
                    .sysAlarm(compName
                            + "DistributedNodeHolder() - SystemException. Ignoring Node <"
                            + dnName + ">.");
            // System Exception . The Node for the Reference might not be
            // running. Set the Node
            isActive = false;
        }

    }

    /**
     * Constructor for DistributedNodeHolder.
     */
    public DistributedNodeHolder()
    {

    }

    /**
     * Returns the queryRef.
     * 
     * @return DistributedNodeQuery
     */
    public DistributedNodeQuery getQueryRef()
    {
        return queryRef;
    }

    /**
     * Sets the queryRef.
     * 
     * @param queryRef
     *            The queryRef to set
     */
    public void setQueryRef(DistributedNodeQuery queryRef)
    {
        this.queryRef = queryRef;
    }

    /**
     * Method getChannelNames.
     */
    public String[] getChannelNames()
    {
        return channelNames;
    }

    public Set getChannelSet()
    {
        return channelNodes.keySet();
    }

    /**
     * Method getChannelNodeHolder.
     * 
     * @param channelName
     * @return ChannelNodeHolder
     */
    public ChannelNodeHolder getChannelNodeHolder(String channelName)
    {
        return (ChannelNodeHolder) channelNodes.get(channelName);
    }

    /**
     * Method getName.
     * 
     * @return String
     */
    public String getName()
    {
        return myName;
    }

    /**
     * Method setAdminRef.
     * 
     * @param dnAdminRef
     */
    public void setAdminRef(DistributedNodeAdmin dnAdminRef)
    {
        adminRef = dnAdminRef;
    }

    /**
     * Method getAdminRef.
     * 
     * @return DistributedNodeAdmin
     */
    public DistributedNodeAdmin getAdminRef()
    {
        return adminRef;
    }

    /**
     * Returns the isActive.
     * 
     * @return boolean
     */
    public boolean isActive()
    {
        return isActive;
    }

    /**
     * Sets the isActive.
     * 
     * @param isActive
     *            The isActive to set
     */
    public void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
    }

}