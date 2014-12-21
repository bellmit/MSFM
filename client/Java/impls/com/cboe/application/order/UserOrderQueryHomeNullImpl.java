package com.cboe.application.order;

import com.cboe.interfaces.application.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

/**
 * The User Order Home Null Impl.  Used for testing.
 * @author Thomas Lynch
 */
public class UserOrderQueryHomeNullImpl extends BOHome implements UserOrderQueryHome {
    /** UserOrderHomeNullImpl constructor. **/
    public UserOrderQueryHomeNullImpl() {
        super();
    }

    /**
      * UserOrderHomeNullImpl create method.  Follows the proscribed method for
      * creating and generating a impl class.  Sets the Session Manager parent
      * class and initializes the Order Query for the null testing mode.
     *
      * @param theSession com.cboe.application.session.SessionManager
      */
    public OrderQueryV6 create(SessionManager theSession) {
        UserOrderQueryImpl bo = new UserOrderQueryImpl();
        //Every bo object must be added to the container BEFORE anything else.
        addToContainer(bo);
        bo.setSessionManager(theSession);
        bo.initialize(true);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        UserOrderQueryInterceptor boi = null;
        try {
            boi = (UserOrderQueryInterceptor) this.createInterceptor(bo);
        }
        catch (Throwable ex) {
            Log.alarm(this, "UserOrderHomeNullImpl.create() failed to create interceptor." );
        }

        return boi;
    }
}
