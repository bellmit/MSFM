package com.cboe.application.shared;

import com.cboe.interfaces.application.*;
import com.cboe.util.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.domain.startup.ClientBOHome;

/**
 * IORMakerHomeImpl.
 * @author Jimmy Wang
 */
public class IORMakerPOAHomeImpl extends ClientBOHome implements IORMakerHome
{
    private IORMakerPOAImpl bo = null;

    /** constructor. **/
    public IORMakerPOAHomeImpl()
    {
        super();
    }

    /**
      * Follows the proscribed method for creating and generating a impl class.
      * Sets the Session Manager parent class and initializes the Order Query.
      * @param theSession com.cboe.application.session.SessionManager
      * @returns UserOrderQueryInterceptor
      */
    public IORMaker create()
    {
        if (bo == null)
        {
            bo = new IORMakerPOAImpl();
            //Every bo object must be added to the container BEFORE anything else.
            addToContainer(bo);

            //Every BOObject create MUST have a name...if the object is to be a managed object.
            bo.create(String.valueOf(bo.hashCode()));
        }
        return bo;
    }
}
