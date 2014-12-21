package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECProductStatusConsumerHome;
import com.cboe.interfaces.events.ProductStatusConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

/**
 * @author Jeff Illian
 */


public class ProductStatusConsumerHomeEventImpl extends ClientBOHome implements IECProductStatusConsumerHome {
    private ProductStatusEventConsumerInterceptor productStatusEventConsumerInterceptor;
    private ProductStatusEventConsumerImpl productStatusEvent;
    private EventService eventService;
    private EventChannelFilterHelper eventChannelFilterHelper;
    private static final String CHANNEL_NAME = "ProductStatus";

    /**
     * ProductStatusListenerFactory constructor comment.
     */
    public ProductStatusConsumerHomeEventImpl() {
        super();
    }

    public ProductStatusConsumer create() {
        return find();
    }

    /**
     * Return the OrderStatus Listener (If first time, create it and bind it to the orb).
     * @author Jeff Illian
     * @return ProductStatusListener
     */
    public ProductStatusConsumer find() {
        return productStatusEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null){
            eventService = eventChannelFilterHelper.connectEventService();
        }
        String interfaceRepId = com.cboe.idl.events.ProductStatusEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, productStatusEvent );
    }

    public void clientInitialize() {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        ProductStatusConsumerIECImpl productStatusConsumer = new ProductStatusConsumerIECImpl();
        productStatusConsumer.create(String.valueOf(productStatusConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(productStatusConsumer);

        productStatusEventConsumerInterceptor = new ProductStatusEventConsumerInterceptor(productStatusConsumer);
        if(getInstrumentationEnablementProperty())
        {
            productStatusEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        productStatusEvent = new ProductStatusEventConsumerImpl(productStatusEventConsumerInterceptor);
    }

    /**
     * Adds a filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Keith A. Korecky
     */
    public void addFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        addConstraint(channelKey);
    }

    /**
     * Adds constraint based on the channel key
     * Event Channel name and the constraint string will be created
     * specifically for the current listener.
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void addConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null )
        {
            String constraintString = getConstraintString(channelKey);

            eventChannelFilterHelper.addEventFilter( productStatusEvent, channelKey,
                    eventChannelFilterHelper.getChannelName(CHANNEL_NAME), constraintString);
        }
    }// end of addConstraint


    /**
     * Removes the event channel filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Keith A. Korecky
     */
    public void removeFilter (ChannelKey channelKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException {

        removeConstraint(channelKey);
    }

    /**
     * Removes constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    private void removeConstraint(ChannelKey channelKey)
        throws SystemException
    {
        if ( find() != null ) {
            String constraintString = getConstraintString(channelKey);
            eventChannelFilterHelper.removeEventFilter( channelKey, constraintString);
        }
    }// end of addConstraint
    /**
     * Returns the constraint string based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getConstraintString(ChannelKey channelKey)
    {
        String parm = getParmName(channelKey);

        if(parm.equals(EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT) ||
           parm.equals(EventChannelFilterHelper.NO_EVENTS_CONSTRAINT))
        {
            return parm;
        }

        StringBuilder buf = new StringBuilder(50);
        buf.append("$.").append(parm);
        return buf.toString();
    }

    /**
     * Returns the constraint parameter string based on the channel key
     * @fixMe Due to the face that the current Architecture can not handle
     * a sequence of filters in the filter string, will filter on everything for now
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     */
    protected String getParmName(ChannelKey channelKey)
    {
        switch (channelKey.channelType)
        {
            case ChannelType.PQS_PRICE_ADJUSTMENT_UPDATED_NOTICE :
            case ChannelType.PQS_PRICE_ADJUSTMENT_APPLIED_NOTICE :
            case ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE :
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS :
            case ChannelType.PQS_UPDATE_REPORTING_CLASS :
            case ChannelType.PS_UPDATE_LINKAGE_INDICATOR :
            case ChannelType.PQS_SET_CLASS_STATE :
                return EventChannelFilterHelper.ALL_EVENTS_CONSTRAINT;
            case ChannelType.PQS_UPDATE_PRODUCT :
                return new StringBuilder(65)
                          .append("updateProduct.updatedProduct.productKeys.classKey==").append(channelKey.key)
                          .toString();
            case ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS :
                return new StringBuilder(65)
                          .append("updateProduct.updatedProduct.productKeys.classKey==").append(channelKey.key)
                          .toString();
            case ChannelType.PQS_STRATEGY_UPDATE :
                return new StringBuilder(80)
                          .append("updateProductStrategy.updatedStrategy.product.productKeys.classKey==").append(channelKey.key)
                          .toString();
            default :
                Log.alarm(this, "Invalid Channel Type for filtering for context: " + channelKey.channelType);
                return EventChannelFilterHelper.NO_EVENTS_CONSTRAINT;
        }
    } // end of getParmName

    // Unused methods declared in home interface for server usage.
    public void addConsumer(ProductStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ProductStatusConsumer consumer, ChannelKey key) {}
    public void removeConsumer(ProductStatusConsumer consumer) {}
}// EOF
