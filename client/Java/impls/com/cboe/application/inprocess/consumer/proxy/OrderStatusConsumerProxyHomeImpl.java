package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumer;
import com.cboe.interfaces.application.inprocess.OrderStatusConsumerProxyHome;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelListener;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.application.shared.IOrderAckConstraints;
/**
 * @author Jing Chen
 */
public class OrderStatusConsumerProxyHomeImpl extends BOHome implements OrderStatusConsumerProxyHome

{
    private IOrderAckConstraints iOrderAckConstraints = null;
    private static final String IORDER_ACK_SESSIONS = "IOrderSessionsNoAck";

    /** constructor. **/
    public OrderStatusConsumerProxyHomeImpl()
    {
        super();
        setSmaType("GlobalBaseConsumerProxyHome.BaseConsumerProxyHomeImpl");
    }

    public ChannelListener create(OrderStatusConsumer consumer, BaseSessionManager sessionManager)
        throws DataValidationException
    {
        OrderStatusConsumerProxy bo = new OrderStatusConsumerProxy(consumer, sessionManager, iOrderAckConstraints);
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));
        return bo;
    }

    public void initialize()
    {
        String sessions = null;
        try
        {
            sessions = getProperty(IORDER_ACK_SESSIONS);
        }
        catch (NoSuchPropertyException nspe)
        {
            Log.information(this, "Received NoSuchPropertyException and no suppressed I Order sessions defined in the xml file.");
        }

        iOrderAckConstraints = IOrderAckConstraints.getInstance(sessions);
        Log.information(this, "I Order no ack Sessions= " + iOrderAckConstraints.toString());
    }
}
