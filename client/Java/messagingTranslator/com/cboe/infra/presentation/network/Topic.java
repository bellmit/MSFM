package com.cboe.infra.presentation.network;

/**
 * A Topic is a combination of a channel and an extent
 */
public class Topic
{
    private String extentName;
    private String channelName;
    private boolean filterFlag = false;
    private boolean isLocal = false;
    private int hash = 0;

    public String toString()
    {
        return channelName + ":" + extentName + ":" + Boolean.toString(isLocal);
    }

    public String getExtentName()
    {
        return extentName;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public boolean isLocal()
    {
        return isLocal;
    }

    public boolean isFilterFlagSet()
    {
        return filterFlag;
    }

    public boolean equals( Object o )
    {
        if (! (o instanceof Topic) )
        {
            return false;
        }
        Topic casted = (Topic) o;
        boolean sameChannel = channelName.equals(casted.channelName);
        boolean sameExtent = extentName.equals(casted.extentName);
        boolean sameScope = isLocal == casted.isLocal;
        return sameChannel && sameExtent && sameScope;
    }

    public int hashCode()
    {
        return hash;
    }

    public Topic( String channel, String extent, boolean filter )
    {
        channelName = channel;
        extentName = extent;
        filterFlag = filter;
        isLocal = extent.toUpperCase().indexOf("LOCAL") != -1;
        hash = ( channelName + extentName + Boolean.toString(isLocal)).hashCode();
    }
}
