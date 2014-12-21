package com.cboe.application.inprocess.quoteQuery;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteQuery;
import com.cboe.interfaces.application.inprocess.QuoteQueryHome;

/**
 * @author Jing Chen
 */
public class QuoteQueryHomeImpl extends ClientBOHome implements QuoteQueryHome
{
    public QuoteQuery create(InProcessSessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating QuoteEntryImpl for " + theSession);
        }
        QuoteQueryImpl bo = new QuoteQueryImpl();
        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setInProcessSessionManager(theSession);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        QuoteQueryInterceptor boi = null;
        try
        {
            boi = (QuoteQueryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
        }
        return boi;
    }
}
