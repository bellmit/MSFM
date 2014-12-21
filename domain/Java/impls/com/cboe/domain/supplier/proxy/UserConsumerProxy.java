package com.cboe.domain.supplier.proxy;

import com.cboe.util.channel.ChannelEvent;
/**
 * This interface is implemented by all user consumer proxy objects.  It provides
 * for the user-based cleanup of all bad/failed references to consumer objects.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/27/1999
 */

public interface UserConsumerProxy
{
    /**
     * This method is called when a connection is lost between a consumer object
     * and an associated proxy for that listener.  This method is intended to invoke
     * any cleanup required to remove the dead consumer reference.
     *
     * @author Derek T. Chambers-Boucher
     */
    public void lostConnection(ChannelEvent event);
}
