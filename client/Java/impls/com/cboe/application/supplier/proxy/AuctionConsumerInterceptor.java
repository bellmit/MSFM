package com.cboe.application.supplier.proxy;

import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;
import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtensionFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;
import com.cboe.domain.supplier.proxy.CallbackInterceptor;
import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.Instrumentor;

/** Send Auction announcement to CMi client; perform any last-minute operations
 * if applicable. Currently the only last-minute operation is to optionally
 * record instrumentation data.
 */
public class AuctionConsumerInterceptor extends CallbackInterceptor implements com.cboe.interfaces.callback.AuctionConsumer
{

    private MethodInstrumentorExtension acceptAuction0;
    //private MethodInstrumentorExtension acceptDirectedAIMAuction0;
    private CMIAuctionConsumer cmiObject;

    /** Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     * @param o IDL Proxy object to send message to CMi user.
     */
    public AuctionConsumerInterceptor(CMIAuctionConsumer o)
    {
        this.cmiObject = o;
    }

    /** Start reporting instrumentation data.
     * @param prefix Unique identifier of the queue (Internal Event Channel)
     *    which delivers events to this object. This value appears in log files
     *    to help trace data traffic to/from one client.
     * @param privateOnly true to log instrumentation data only to disk;
     *    false to publish instrumentation data through an event channel.
     */
    public void startInstrumentation(String prefix, boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(prefix.length()+Instrumentor.NAME_DELIMITER.length()+15);
            name.append(prefix).append(Instrumentor.NAME_DELIMITER).append("acceptAuction0");
            acceptAuction0 = MethodInstrumentorExtensionFactory.createMethodInstrumentor(name.toString(), null, privateOnly);
            //acceptDirectedAIMAuction0 = MethodInstrumentorExtensionFactory.createMethodInstrumentor(prefix + Instrumentor.NAME_DELIMITER + "acceptDirectedAIMAuction0", null, privateOnly);
        }
        catch (InstrumentorAlreadyCreatedException e)
        {
            Log.exception(e);
        }
    }

    /** Remove the instrumentation object from this object, in order to stop
     * instrumenting this object. */
    public void removeInstrumentation()
    {
        if (acceptAuction0 != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptAuction0.getName());
            acceptAuction0 = null;
        }
       /* if (acceptDirectedAIMAuction0 != null)
        {
            MethodInstrumentorExtensionFactory.removeMethodInstrumentor(acceptDirectedAIMAuction0.getName());
            acceptDirectedAIMAuction0 = null;
        }*/
    }

    /** Set up instrumentation for a queue related to the method that we are
     * instrumenting. We may instrument more than one queue in connection with
     * the method instrumentation.
     * @param queueInstrumentorExtension null for no queue instrumentation,
     *     else an object to deal with instrumenting a specific queue.
     */
    public void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentorExtension)
    {
        if (acceptAuction0 != null)
        {
            acceptAuction0.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }
        /*if (acceptDirectedAIMAuction0 != null)
        {
            acceptDirectedAIMAuction0.addQueueInstrumentorRelation(queueInstrumentorExtension);
        }*/
    }

    /** Record times if instrumentation is enabled, and notify client that an
     * auction is starting.
     * @param auctionStruct Details of the auction.
     */
    public void acceptAuction(com.cboe.idl.cmiOrder.AuctionStruct auctionStruct)
    {
        boolean exception = false;
        if (acceptAuction0 != null)
        {
            acceptAuction0.beforeMethodCall();
        }
        try
        {
            cmiObject.acceptAuction(auctionStruct);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptAuction0 != null)
            {
                acceptAuction0.incCalls(1);
                acceptAuction0.afterMethodCall();
                if (exception)
                {
                    acceptAuction0.incExceptions(1);
                }
            }
        }
    }
    
    /*public void acceptDirectedAIMAuction(com.cboe.idl.cmiOrder.AuctionStruct auctionStruct)
    {
        boolean exception = false;
        if (acceptDirectedAIMAuction0 != null)
        {
            acceptDirectedAIMAuction0.beforeMethodCall();
        }
        try
        {
            cmiObject.acceptDirectedAIMAuction(auctionStruct);
        }
        catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        }
        finally
        {
            if (acceptDirectedAIMAuction0 != null)
            {
                acceptDirectedAIMAuction0.incCalls(1);
                acceptDirectedAIMAuction0.afterMethodCall();
                if (exception)
                {
                    acceptDirectedAIMAuction0.incExceptions(1);
                }
            }
        }
    }*/

}
