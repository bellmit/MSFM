package com.cboe.ffConsumers;

import com.cboe.infrastructureServices.eventService.ConsumerFilter;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.ffidl.ffEvents.TradeReportEventConsumerHelper;
import com.cboe.ffidl.ffEvents.TradeReportEventConsumer;
import com.cboe.ffInterfaces.TradeReportConsumer;
import com.cboe.ffInterfaces.TradeReportConsumerHome;
import com.cboe.ffUtil.ExceptionBuilder;
import com.cboe.ffidl.ffExceptions.*;
import java.util.ArrayList;

public class TradeReportConsumerHomeImpl 
    extends BOHome
    implements TradeReportConsumerHome
{
    protected static final String SESSION_FILTER_SYS_PROP = "SessionFilter";

    /**
     * Constraint string which will evaluate to true for all parameters for a method's <code>ConsumerFilter</code>.
     */
    protected static final String ALL_PARAMETERS_CONSTRAINT = "1";

    protected static final String CHANNEL_NAME_PROP="eventChannelName";

    protected TradeReportPublisher publisher;
    protected String eventChannelName;

    public void TradeReportConsumerHomeImpl()
    {
        setSmaType("GlobalTradeReportConsumer.TradeReportConsumerHomeImpl");
    }

    protected String getRepositoryId()
    {
        return TradeReportEventConsumerHelper.id();
    }

    public void initialize()
    {
        try
        {
            System.out.println("full name = \"" + getFullName() + "\"");
            eventChannelName = getProperty(CHANNEL_NAME_PROP);
        }
        catch (NoSuchPropertyException ex)
        {
            Log.exception(this, "Failed to find channel name property.", ex);
        }
        Log.debug(this, "Event channel name = \"" + eventChannelName + "\"");
    }

    public void goSlave()
    {
    }

    public void goMaster(boolean failover)
    {
    }

    /**
     * Returns the publisher
     */
    public TradeReportConsumer find()
    {
        return create();
    }

    /**
     * Returns the publisher
     */
    public TradeReportConsumer create()
    {
        if (publisher == null)
        {
            synchronized (this)
            {
                if (publisher == null)
                {
                    TradeReportEventConsumer stub = TradeReportEventConsumerHelper.narrow(getEventChannelStub());
                    publisher = new TradeReportPublisher(stub);
                }
            }
        }
        return publisher;
    }

    /**
     * Sets consumerImpl to be a consumer of this channel
     */
    public void addConsumer(TradeReportConsumer consumer)
        throws SystemException
    {
        TradeReportConsumerImpl consumerImpl = new TradeReportConsumerImpl(consumer);

	String sessionFilter = System.getProperty(SESSION_FILTER_SYS_PROP);
	if (sessionFilter != null && sessionFilter.trim().length() > 0)
	{
		String filterStr = "$.acceptTradeReport.session == '" + sessionFilter + "'";
		Log.information(this, "Adding consumer with filter '" + filterStr + "'");
		try
		{
			ConsumerFilter filter = createInclusionFilter(consumerImpl, "acceptTradeReport", filterStr);
			FoundationFramework.getInstance().getEventService().applyFilter(filter);
		}
		catch (org.omg.CosNotifyFilter.InvalidConstraint ex)
		{
			throw ExceptionBuilder.systemException("Failed to apply filter (invalid constraint): " + ex, 0);
		}
	}
	else
	{
		Log.information(this, "Adding consumer without filters");
	}

        connectToChannel(consumerImpl);
    }

    protected void connectToChannel(TradeReportConsumerImpl consumerImpl) throws SystemException
    {
        try
        {
            EventService es = FoundationFramework.getInstance().getEventService();
            es.connectTypedNotifyChannelConsumer(eventChannelName, getRepositoryId(), consumerImpl);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception e)
        {
            String msg = "Unable to connect consumer to the " + eventChannelName + " channel";
            Log.exception(this, msg, e);
            throw ExceptionBuilder.systemException(msg + ": " + e, 0);
        }
    }

    /**
     * Gets stub for the event channel.
     */
    protected org.omg.CORBA.Object getEventChannelStub()
    {
        EventService es = FoundationFramework.getInstance().getEventService();
        try
        {
            org.omg.CORBA.Object supplier = es.getTypedEventChannelSupplierStub(eventChannelName, getRepositoryId());
            return supplier;
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            String msg = "Unable to get supplier stub for channel \"" + eventChannelName + "\"";
            Log.exception(this, msg, ex);
            throw new UnsupportedOperationException(msg + ": " + ex);
        }
    }

    protected ConsumerFilter createInclusionFilter(TradeReportConsumerImpl eventConsumer, String methodName, String constraint) throws SystemException
    {
        try
        {
            EventService es = FoundationFramework.getInstance().getEventService();
            return es.createNewInclusionFilter(eventConsumer, getRepositoryId(), methodName, constraint, eventChannelName);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw ExceptionBuilder.systemException("Exception creating inclusion filter: " + ex, 0);
        }
    }
}
