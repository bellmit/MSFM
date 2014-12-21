package com.cboe.consumers.eventChannel;

/**
 * Best book listener object listens on the CBOE event channel as an AcceptCASAdminConsumer.
 * There will only be a single best book listener per CAS.
 *
 * @author Keith A. Korecky
 */

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiAdmin.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class CASAdminEventConsumerImpl extends com.cboe.idl.events.POA_CASAdminEventConsumer implements CASAdminConsumer
{

    private CASAdminConsumer delegate;

    /**
     * AcceptCASAdminEventConsumerImpl constructor comment.
     */
    public CASAdminEventConsumerImpl(CASAdminConsumer casAdminConsumer)
    {
        super();
        delegate = casAdminConsumer;
    }

    public void addCASUser( String originator, String casPairName, String userId)
    {
        delegate.addCASUser( originator, casPairName, userId);
    }

    public void removeCASUser( String originator, String casPairName, String userId )
    {
        delegate.removeCASUser( originator, casPairName, userId );
    }

    public void addCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        delegate.addCASFirm( originator, casPairName, userId, firmKey );
    }

    public void removeCASFirm( String originator, String casPairName, String userId, ExchangeFirmStruct firmKey )
    {
        delegate.removeCASFirm( originator, casPairName, userId, firmKey );
    }

    public void addCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
    //  NOT IMPLEMENTED
    }

    public void removeCASClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
    //  NOT IMPLEMENTED
    }

    public void addCASProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
    //  NOT IMPLEMENTED
    }

    public void removeCASProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
    //  NOT IMPLEMENTED
    }

    public void addCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.addCASRFQClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void removeCASRFQClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.removeCASRFQClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void addCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.addCASCurrentMarketClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void removeCASCurrentMarketClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.removeCASCurrentMarketClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void removeCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.removeCASTickerClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void addCASTickerClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.addCASTickerClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void addCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.addCASOpeningPriceClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void removeCASOpeningPriceClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.removeCASOpeningPriceClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void addCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.addCASRecapClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void removeCASRecapClassForUser( String originator, String casPairName, String userId, String sessionName, int classKey )
    {
        delegate.removeCASRecapClassForUser( originator, casPairName, userId, sessionName, classKey );
    }

    public void addCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
        delegate.addCASBookDepthProductForUser( originator, casPairName, userId, sessionName, productKey );
    }

    public void removeCASBookDepthProductForUser( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
        delegate.removeCASBookDepthProductForUser( originator, casPairName, userId, sessionName, productKey );
    }

    public void addCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
        delegate.addCASQuoteLockedNotification( originator, casPairName, userId, sessionName, productKey );
    }

    public void removeCASQuoteLockedNotification( String originator, String casPairName, String userId, String sessionName, int productKey )
    {
        delegate.removeCASQuoteLockedNotification( originator, casPairName, userId, sessionName, productKey );
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
