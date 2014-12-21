/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 3, 2002
 * Time: 9:55:52 AM
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiIntermarket.IntermarketUserAccess;
import com.cboe.idl.cmiIntermarket.IntermarketUserAccessHelper;
import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager;
import com.cboe.presentation.common.logging.GUILoggerHome;
import org.omg.CORBA.OBJECT_NOT_EXIST;

public class IntermarketUserAccessFactory
{
    private static IntermarketUserAccess intermarketUserAccess;
    public IntermarketUserAccessFactory()
    {
        super();
    }

    public static IntermarketUserAccess find()
    {
        if (intermarketUserAccess == null)
        {
            try
            {
                Object obj = RemoteConnectionFactory.find().find_initial_intermarket_object();
                intermarketUserAccess = IntermarketUserAccessHelper.narrow((org.omg.CORBA.Object) obj);
            }
            catch(OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch (Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.find()","IntermarketUserAccess remote object connection exception",e);
            }
        }
        return intermarketUserAccess;
    }
    public static IntermarketUserSessionManager getIntermarketUserSessionManager(
        UserSessionManager userSessionManager)
        throws AuthorizationException, CommunicationException, SystemException, NotFoundException
    {
        IntermarketUserAccess userAccess = find();
        IntermarketUserSessionManager intermarketUserSessionManager= null;
        if(userAccess != null)
        {
            intermarketUserSessionManager = userAccess.getIntermarketUserSessionManager(userSessionManager);
        }
        return intermarketUserSessionManager;
    }

}
