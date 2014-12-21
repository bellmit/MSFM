/**
 * <CODE>ChannelListener</CODE> is the marker interface that must be extended by any listener
 * interface that will be implemented for use with the an extended implementation
 * of <CODE>ChannelAdapter</CODE>.  Your channel listener interface will probably require a
 * listener callback method that pertains to the type of your <CODE>ChannelAdapter</CODE>.
 *
 * <B>>>>NOTE<<<</B>
 * <CODE>equals</CODE> and <CODE>hashCode</CODE> are included here as a subtle
 * reminder that these methods <U>need</U> to be overridden from the
 * <CODE>java.lang.Object</CODE> default implementations.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/19/1999
 * @see com.cboe.util.channel.ChannelAdapter
 */
package com.cboe.util.channel;

public interface ChannelListener extends java.util.EventListener
{
    /**
     * This method must be implemented to check the equality of two objects.  This is used
     * by the Hashtable class to check the equality of key objects.  Since in some instances
     * the implemented ChannelListener is a CORBA object it is necessary to implement an
     * appropriate equals method that will guarantee the correct response is returned.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return true - if the objects are the same; false - otherwise.
     * @param obj the object to perform the comparison against.
     */
    public boolean equals(Object obj);

    /**
     * This method must be implemented by the implementer of the interface to generate
     * a hashcode for the listener object.  This is used by the Hashtable class to
     * generate the key for storing and retrieving objects in the Hashtable.  Since in some
     * instances the implemented ChannelListener is a CORBA object it is necessary to implement
     * an appropriate hashcode method that will guarantee the correct response is returned.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return int value representing the hashkey for the object implementing the interface.
     */
    public int hashCode();

    /**
     * channelUpdate is called by the event channel adapter when it dispatches an
     * event to the registered listeners.
     *
     * @author Derek T. Chambers-Boucher
     */
    public void channelUpdate(ChannelEvent event);
}
