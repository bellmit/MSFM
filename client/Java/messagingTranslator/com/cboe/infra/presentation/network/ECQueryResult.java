//
//-----------------------------------------------------------------------------------
//Source file: NonECQueryResult.java
//
//PACKAGE: package com.cboe.infra.presentation.traderService;
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

public class ECQueryResult
{
    public String eventChannelName;
    public boolean isNotifyChannel;
    public boolean isChannelActive;
    public String qualityOfService;
    public String[] interfaceIds;

    public ECQueryResult()
    {
    }

    public ECQueryResult(
            String eventChannelName
            ,boolean isNotifyChannel
            ,boolean isChannelActive
            ,String qualityOfService
            ,String[] interfaceIds
            )
    {
        this.eventChannelName = eventChannelName;
        this.isNotifyChannel = isNotifyChannel;
        this.isChannelActive = isChannelActive;
        this.qualityOfService = qualityOfService;
        this.interfaceIds = interfaceIds;
    }

    /**
     * @return Returns the eventChannelName.
     */
    public String getEventChannelName()
    {
        return this.eventChannelName;
    }

    /**
     * @param eventChannelName The eventChannelName to set.
     */
    public void setEventChannelName(String eventChannelName)
    {
        this.eventChannelName = eventChannelName;
    }

    /**
     * @return Returns the isChannelActive.
     */
    public boolean isChannelActive()
    {
        return this.isChannelActive;
    }

    /**
     * @param isChannelActive The isChannelActive to set.
     */
    public void setChannelActive(boolean isChannelActive)
    {
        this.isChannelActive = isChannelActive;
    }

    /**
     * @return Returns the isNotifyChannel.
     */
    public boolean isNotifyChannel()
    {
        return this.isNotifyChannel;
    }

    /**
     * @param isNotifyChannel The isNotifyChannel to set.
     */
    public void setNotifyChannel(boolean isNotifyChannel)
    {
        this.isNotifyChannel = isNotifyChannel;
    }

    /**
     * @return Returns the qualityOfService.
     */
    public String getQualityOfService()
    {
        return this.qualityOfService;
    }

    /**
     * @param qualityOfService The qualityOfService to set.
     */
    public void setQualityOfService(String qualityOfService)
    {
        this.qualityOfService = qualityOfService;
    }

    /**
     * @return Returns the interfaceIds.
     */
    public String[] getInterfaceIds()
    {
        return this.interfaceIds;
    }

    /**
     * @param interfaceIds The interfaceIds to set.
     */
    public void setInterfaceIds(String[] interfaceIds)
    {
        this.interfaceIds = interfaceIds;
    }

}
