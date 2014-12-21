package com.cboe.util;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * This type was created in VisualAge.
 */
public class ChannelKey implements ChannelType
{
	public int channelType;
	public Object key;
	private int hashcode;
/**
 * ChannelKey constructor comment.
 */
private ChannelKey() {
	super();
	hashcode = (channelType + key.hashCode())>>1;
}
/**
 * This method was created in VisualAge.
 */
public ChannelKey(int channelType, Object key)
{
	this.channelType = channelType;
	this.key = key;
	hashcode = (channelType + key.hashCode())>>1;
}
/**
 * This method was created in VisualAge.
 */
public boolean equals(Object obj)
{
	if ((obj != null) && (obj instanceof ChannelKey))
	{
		return (key.equals(((ChannelKey) obj).key)) && (channelType == ((ChannelKey) obj).channelType);
	}
	return false;
}
/**
 * This method was created in VisualAge.
 */
public int hashCode() {
	return hashcode;
}

public String toString()
{
    String keyString = key.toString();
    StringBuilder result = new StringBuilder(keyString.length()+35);
    result.append("ChannelKey: ").append(keyString).append(" ChannelType: ").append(channelType);
    return result.toString();
}
}
