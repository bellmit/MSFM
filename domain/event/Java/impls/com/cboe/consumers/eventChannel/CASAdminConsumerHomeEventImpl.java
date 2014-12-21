// $Workfile$ com.cboe.consumers.eventChannel.CASAdminConsumerHomeEventImpl.java
// $Revision$
/* $Log$
*   Initial Version                             Keith A. Korecky
*   Revision        Increment 6     12/1/00     desaik
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.consumers.eventChannel;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.interfaces.events.CASAdminConsumer;
import com.cboe.interfaces.events.IECCASAdminConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;

    /**
     * <b> Description </b>
     * <p>
     *      The Text Message Listener class.
     * </p>
     *
     * @author Jeff Illian
     * @author Keval Desai
     */
public class CASAdminConsumerHomeEventImpl extends ClientBOHome implements IECCASAdminConsumerHome
{
    private CASAdminEventConsumerInterceptor                   casAdminEventConsumerInterceptor;
    private CASAdminEventConsumerImpl               casAdminEvent;
    private EventService                            eventService;
    private EventChannelFilterHelper                eventChannelFilterHelper;
    private final String                            CHANNEL_NAME = "CASAdmin";

    /**
     * CASAdminConsumerHomeEventImpl constructor comment.
     */
    public CASAdminConsumerHomeEventImpl()
    {
        super();
    }

    public CASAdminConsumer create()
    {
        return find();
    }

    /**
     * Return the CASAdmin Listener (If first time, create it and bind it to the orb).
     * @return CASAdminConsumer
     */
    public CASAdminConsumer find()
    {
        return casAdminEventConsumerInterceptor;
    }

    public void clientStart()
        throws Exception
    {

        if (eventService == null)
        {
            eventService = eventChannelFilterHelper.connectEventService();
        }

        String interfaceRepId = com.cboe.idl.events.CASAdminEventConsumerHelper.id();
        // connect to the event channel without filter and add the constraint filter later on
        // using addConstraint - Connie Feng
        eventChannelFilterHelper.connectConsumer( CHANNEL_NAME, interfaceRepId, casAdminEvent );
        addConstraints();
    }

    public void clientInitialize()
    {
        eventChannelFilterHelper = new EventChannelFilterHelper();
        CASAdminConsumerIECImpl casAdminConsumer = new CASAdminConsumerIECImpl();
        casAdminConsumer.create(String.valueOf(casAdminConsumer.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(casAdminConsumer);
        casAdminEventConsumerInterceptor = new CASAdminEventConsumerInterceptor(casAdminConsumer);
        if(getInstrumentationEnablementProperty())
        {
            casAdminEventConsumerInterceptor.startInstrumentation(getInstrumentationProperty());
        }
        casAdminEvent = new CASAdminEventConsumerImpl(casAdminEventConsumerInterceptor);
    }

    /**
     * Adds a  Filter to the internal event channel. Constraints based on the
     * ChannelKey will be added as well. Do not make call to addConstraints when this method has
     * already being called.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void addFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // No implementation needed
    }

    /**
     * Adds constraint based on the channel key
     *
     * @param channelKey the event channel key
     *
     * @author Jeff Illian
     */
    private void addConstraints()
        throws SystemException
    {
        ChannelKey channelKey;
        StringBuilder constraintString = new StringBuilder(100);
        String originator;
        String casPairName;
        try {
            originator = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
            casPairName = ApplicationPropertyHelper.getProperty("CASPairName");
        }
        catch (Exception e)
        {
            Log.exception(this, "Exception adding CAS pair constraints", e);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }

        //Inclusion filters
        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_FIRM, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASFirm.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_FIRM, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASFirm.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_RFQ_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASRFQClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_RFQ_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASRFQClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_CURRENTMARKET_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASCurrentMarketClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_CURRENTMARKET_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASCurrentMarketClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_OPENINGPRICE_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASOpeningPriceClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_OPENINGPRICE_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASOpeningPriceClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_TICKER_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASTickerClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_TICKER_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASTickerClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_RECAP_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASRecapClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_RECAP_CLASS_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASRecapClassForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_BOOKDEPTH_PRODUCT_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.addCASBookDepthProductForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_BOOKDEPTH_PRODUCT_FOR_USER, casPairName);
        constraintString.setLength(0);
        constraintString.append("$.removeCASBookDepthProductForUser.casPairName=='").append(casPairName).append("'");
        applyFilter(channelKey, constraintString.toString(), true);

//        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_QUOTE_LOCKED_NOTIFICATION, casPairName);
//        constraintString.setLength(0);
//        constraintString.append("$.addCASQuoteLockedNotification.casPairName=='").append(casPairName).append("'");
//        applyFilter(channelKey, constraintString.toString(), true);

//        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_QUOTE_LOCKED_NOTIFICATION, casPairName);
//        constraintString.setLength(0);
//        constraintString.append("$.removeCASQuoteLockedNotification.casPairName=='").append(casPairName).append("'");
//        applyFilter(channelKey, constraintString.toString(), true);
        //Exclusion filters
        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_FIRM, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASFirm.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_FIRM, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASFirm.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_RFQ_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASRFQClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_RFQ_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASRFQClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_CURRENTMARKET_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASCurrentMarketClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_CURRENTMARKET_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASCurrentMarketClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_OPENINGPRICE_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASOpeningPriceClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_OPENINGPRICE_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASOpeningPriceClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_TICKER_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASTickerClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_TICKER_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASTickerClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_RECAP_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASRecapClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_RECAP_CLASS_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASRecapClassForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_BOOKDEPTH_PRODUCT_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.addCASBookDepthProductForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_BOOKDEPTH_PRODUCT_FOR_USER, originator);
        constraintString.setLength(0);
        constraintString.append("$.removeCASBookDepthProductForUser.originator=='").append(originator).append("'");
        applyFilter(channelKey, constraintString.toString(), false);

//        channelKey = new ChannelKey(ChannelType.CASADMIN_ADD_QUOTE_LOCKED_NOTIFICATION, originator);
//        constraintString.setLength(0);
//        constraintString.append("$.addCASQuoteLockedNotification.originator=='").append(originator).append("'");
//        applyFilter(channelKey, constraintString.toString(), false);

//        channelKey = new ChannelKey(ChannelType.CASADMIN_REMOVE_QUOTE_LOCKED_NOTIFICATION, originator);
//        constraintString.setLength(0);
//        constraintString.append("$.removeCASQuoteLockedNotification.originator=='").append(originator).append("'");
//        applyFilter(channelKey, constraintString.toString(), false);
    }// end of addConstraint

    private void applyFilter(ChannelKey channelKey, String constraintString, boolean creatInclusionFilter)
        throws SystemException
    {
            eventChannelFilterHelper.addEventFilter( casAdminEvent
                                                    , channelKey
                                                    , eventChannelFilterHelper.getChannelName(CHANNEL_NAME)
                                                    , constraintString
                                                    , creatInclusionFilter
                                                    );
    }

    /**
     * Removes the event channel Filter from the CBOE event channel.
     *
     * @param channelKey the event channel key
     *
     * @author Connie Feng
     * @author Keval Desai
     * @version 12/1/00
     */
    public void removeFilter ( ChannelKey channelKey )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // No implementation needed
    }

    // Unused methods declared in home interface for server usage.
    public void addConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer, ChannelKey key) {}
    public void removeConsumer(CASAdminConsumer consumer) {}
}// EOF
