// $Workfile$ com/cboe/application/quote/QuoteEntryHomeImpl
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial version     3/16/99 Connie Feng
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.tradingSession;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.TradingSession;
import com.cboe.interfaces.application.TradingSessionHome;

/**
 * An implementation of UserQuoteHome for use in the process that implements
 * the user quote service.
 *
 * @author Connie Feng
 */
public class TradingSessionHomeImpl extends ClientBOHome implements TradingSessionHome
{
    /**
     * TradingSessionHomeImpl constructor comment.
     */
    public TradingSessionHomeImpl()
    {
        super();
    }

    /**
    * Creates an instance of Quote for the current session.
    *
    * @author Connie Feng
    */
    public TradingSession create(SessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating TradingSessionImpl for " + theSession);
        }
        TradingSessionImpl bo = new TradingSessionImpl();

        bo.setSessionManager(theSession);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        // Every BObject must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        TradingSessionInterceptor boi = null;
        try {
            boi = (TradingSessionInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        } catch (Exception ex) {
            Log.exception(this, ex);
        }

        return boi;

    }
}// EOF
