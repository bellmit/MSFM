/**
 * This class acts as a proxy to the actual listener object.  The
 * proxy object works in conjunction with a <CODE>ChannelThreadCommand</CODE>
 * object to send the received events to the listener.  The
 * thread command gets scheduled with a thread pool to perform the
 * work of the command - calling <CODE>channelUpdate()</CODE> on the listener
 * object in this case.
 *
 * @author Derek T. Chambers-Boucher
 * @version 04/18/1999
 */
package com.cboe.util.channel;


public interface ChannelListenerProxy extends ChannelListener, ListenerProxyQueueControl
{
    public ChannelListener getDelegateListener();

    public void addEvent(ChannelEvent event) ;

    public void cleanUp();

    /**
     * This method sets the given <CODE>ChannelAdapter</CODE> reference.  This
     * is used for deregistration of a listener in the adapter if the proxies
     * underlying listener fails.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param adapter the <CODE>ChannelAdapter</CODE> reference.
     */

    /**
     * This method returns the reference of the <CODE>ChannelAdapter</CODE>
     * to the caller.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return a reference to the proxies <CODE>ChannelAdapter</CODE>.
     */
    public ChannelAdapter getChannelAdapter() ;

    /**
     * This method returns the set status of the ThreadPool property.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return true if the <CODE>ThreadPool</CODE> has been set; false if it is null.
     */
    public boolean isThreadPool() ;

    /**
     * channelUpdate is called by the event channel adapter when it dispatches an
     * event to the registered listeners.
     *
     * @author Derek T. Chambers-Boucher
     */
    public void channelUpdate(ChannelEvent event);

}
