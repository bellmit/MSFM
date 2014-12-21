/**
 * 
 */
package com.cboe.consumers.callback;

import org.omg.CORBA.Object;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumerHelper;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.delegates.internalCallback.CurrentMarketManualQuoteDelegate;

/**
 * 
 * @author Eric Maheo
 * 
 */
public class CurrentMarketManualQuoteConsumerFactory extends AbstractV4ConsumerFactory
{

	public CurrentMarketManualQuoteConsumerFactory(){
		super();
	}
	
	public static CurrentMarketManualQuoteConsumer create(EventChannelAdapter eventProcessor)
    {

        try
        {
        	com.cboe.interfaces.internalCallback.CurrentMarketManualQuoteConsumer consumer = new CurrentMarketManualQuoteConsumerImpl(eventProcessor);
        	
        	CurrentMarketManualQuoteDelegate delegate = new CurrentMarketManualQuoteDelegate(consumer);

            Object corbaObject = (Object) RemoteConnectionFactory.find().register_object(delegate, getPOAName());
            
            return CurrentMarketManualQuoteConsumerHelper.narrow(corbaObject);
        }
        catch(Exception e)
        {
            GUILoggerHome.find().exception(e, "CurrentMarketManualQuoteConsumerFactory.create");
            return null;
        }
    }
}