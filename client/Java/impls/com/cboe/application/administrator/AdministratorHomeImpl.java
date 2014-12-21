package com.cboe.application.administrator;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.Administrator;
import com.cboe.interfaces.application.AdministratorHome;
import com.cboe.interfaces.application.SessionManager;

/**
 * The User Order Home Impl.  This time for Orders.
 * @author Thomas Lynch
 */
public class AdministratorHomeImpl extends ClientBOHome implements AdministratorHome
{
    private String sacasUserId;

    /** AdministratorHomeImpl constructor. **/
    public AdministratorHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns AdministratorInterceptor
      */
    public Administrator create(SessionManager theSession)
    {
        AdministratorInterceptor boi = null;
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating AdministratorImpl for " + theSession);
        }
        AdministratorImpl bo = new AdministratorImpl(sacasUserId);
        bo.setSessionManager(theSession);

        // Every BObject must be added to the container BEFORE anything else.
        addToContainer(bo);

        //Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        try
        {
            boi = (AdministratorInterceptor) this.createInterceptor(bo);
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch (Exception ex)
        {
            Log.exception(this, "session : " + theSession, ex);
        }
        return boi;
    }

    public void clientInitialize()
        throws Exception
    {
//        sacasUserId = getProperty( "SACASUserId" );
        sacasUserId = "" + UserRoles.HELP_DESK;
    }

}
