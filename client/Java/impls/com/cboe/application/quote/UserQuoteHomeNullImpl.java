// $Workfile$ com/cboe/application/quote/QuoteEntryHomeImpl
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial version     3/16/99 Connie Feng
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.quote;


import com.cboe.interfaces.application.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.loggingService.*;

/**
 * An implementation of UserQuoteHome for use in the process that implements
 * the user quote service.
 *
 * @author Connie Feng
 */
public class UserQuoteHomeNullImpl extends BOHome implements UserQuoteHome
{
    /**
     * QuoteEntryHomeImpl constructor comment.
     */
    public UserQuoteHomeNullImpl()
    {
        super();
    }

    /**
    * Creates an instance of Quote for the current session.
    *
    * @author Connie Feng
    */
    public QuoteV7 create(SessionManager theSession)
    {
        UserQuoteImpl bo = new UserQuoteImpl();

        bo.setSessionManager(theSession);

        //Every bo object must be added to the container.
        addToContainer(bo);

        //Every BOObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        UserQuoteInterceptor boi = null;
        try {
          boi = (UserQuoteInterceptor) this.createInterceptor( bo );
        } catch (Throwable ex) {
          FoundationFramework.getInstance().getLogService(getComponentName()).log(
            MsgPriority.high, MsgCategory.systemNotification, "UserQuoteHomeNullImpl.create", "Failed to create interceptor", new  MsgParameter[0]);
        }

        return boi;

    }
}// EOF
