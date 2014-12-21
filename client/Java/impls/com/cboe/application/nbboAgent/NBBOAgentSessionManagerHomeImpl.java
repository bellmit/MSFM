package com.cboe.application.nbboAgent;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.NBBOAgentSessionManager;
import com.cboe.interfaces.application.NBBOAgentSessionManagerHome;
import com.cboe.interfaces.application.SessionManager;
import java.util.HashMap;

public class NBBOAgentSessionManagerHomeImpl extends ClientBOHome
        implements NBBOAgentSessionManagerHome
{
    // Collection of NBBOAgentSessionManagers
    private HashMap                            nbboAgentSessionManagers;
    private NBBOAgentSessionManagerImpl        nbboAgentSessionManagerImpl = null;

    public NBBOAgentSessionManagerHomeImpl() {
        super();
        nbboAgentSessionManagers = new HashMap();
    }

    public synchronized NBBOAgentSessionManager create(SessionManager sessionManager)
    {

        nbboAgentSessionManagerImpl = (NBBOAgentSessionManagerImpl)(getNBBOAgentSessionManagers().get(sessionManager));

        if (nbboAgentSessionManagerImpl == null)
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Creating nbboAgentSessionManagerImpl for " + sessionManager);
            }
            nbboAgentSessionManagerImpl = new NBBOAgentSessionManagerImpl();

            // Every BObject must be added to the container, ?
            addToContainer(nbboAgentSessionManagerImpl);

            // Every BObject create MUST have a name...if the object is to be a managed object, ?
            nbboAgentSessionManagerImpl.create(String.valueOf(nbboAgentSessionManagerImpl.hashCode()));

            nbboAgentSessionManagerImpl.setSessionManager(sessionManager);

            getNBBOAgentSessionManagers().put(sessionManager, nbboAgentSessionManagerImpl);
        }

        NBBOAgentSessionManagerInterceptor boi = null;
        try {
                nbboAgentSessionManagerImpl.initialize();
                boi = (NBBOAgentSessionManagerInterceptor) this.createInterceptor(nbboAgentSessionManagerImpl);
                boi.setSessionManager(sessionManager);
                if(getInstrumentationEnablementProperty())
                {
                    boi.startInstrumentation(getInstrumentationProperty());
                }
                return boi;

        } catch (Exception ex) {

             remove(sessionManager);
             Log.exception(this, ex);

             return null;
        }

    }

   public NBBOAgentSessionManager find(SessionManager sessionManager)
   {
       return create(sessionManager);
   }

    private HashMap getNBBOAgentSessionManagers()
    {
        if (nbboAgentSessionManagers == null )
        {
            nbboAgentSessionManagers = new HashMap();
        }
        return nbboAgentSessionManagers;
    }

    public void clientStart()
        throws Exception
    {

    }

    /**
    * Removes an instance of the NBBOAgentSessionManager
    */
   public synchronized void remove (SessionManager sessionManager)
     {
       nbboAgentSessionManagerImpl = (NBBOAgentSessionManagerImpl)getNBBOAgentSessionManagers().get(sessionManager);
        if (nbboAgentSessionManagerImpl!= null)
        {
            getNBBOAgentSessionManagers().remove(sessionManager);
        }
    }

} //EOF
