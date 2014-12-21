package com.cboe.application.quote;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserQuoteHome;
import com.cboe.interfaces.application.QuoteV7;
import com.cboe.domain.startup.ClientBOHome;

public class UserQuoteHomeImpl extends ClientBOHome implements UserQuoteHome
{

    public UserQuoteHomeImpl()
    {
        super();
    }

    /**
    * Creates an instance of Quote for the current session.
    */
    public QuoteV7 create(SessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating UserQuoteImpl for " + theSession);
        }
        UserQuoteImpl bo = new UserQuoteImpl();

        bo.setSessionManager(theSession);
        // Every BObject must be added to the container.
        addToContainer(bo);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        UserQuoteInterceptor boi = null;
        try
        {
            boi = (UserQuoteInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
        }
        
        return boi;
    }

}
