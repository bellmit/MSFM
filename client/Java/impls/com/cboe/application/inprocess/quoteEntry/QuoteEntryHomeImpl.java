package com.cboe.application.inprocess.quoteEntry;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.QuoteEntry;
import com.cboe.interfaces.application.inprocess.QuoteEntryHome;

/**
 * @author Jing Chen
 */
public class QuoteEntryHomeImpl extends ClientBOHome implements QuoteEntryHome
{
    public QuoteEntry create(InProcessSessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating QuoteEntryImpl for " + theSession);
        }
        QuoteEntryImpl bo = new QuoteEntryImpl();
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setInProcessSessionManager(theSession);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        QuoteEntryInterceptor boi = null;
        try
        {
            boi = (QuoteEntryInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
        }
        return boi;
    }
}
