// $Workfile$ com.cboe.application.product.ProductQueryManagerHomeImpl.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial Version         3/15/99      fengc
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.productDefinition;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductDefinition;
import com.cboe.interfaces.application.ProductDefinitionHome;
import com.cboe.interfaces.application.SessionManager;

/**
 * An implementation of ProductDefinitionHome
 *
 * @author Connie Feng
 */
public class ProductDefinitionHomeImpl extends ClientBOHome implements ProductDefinitionHome
{
    /**
     * ProductDefinitionHomeImpl constructor.
     */
    public ProductDefinitionHomeImpl()
    {
        super();
    }

    /**
    * Creates an instance of the product query manager.
    *
    * @author Connie Feng
    */
    public ProductDefinition create(SessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating ProductDefinitionImpl for " + theSession);
        }
        ProductDefinitionImpl bo = new ProductDefinitionImpl();
        bo.setSessionManager(theSession);

        bo.create(String.valueOf(bo.hashCode()));
        //Every bo object must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        ProductDefinitionInterceptor boi = null;

        try {
            boi = (ProductDefinitionInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        } catch (Exception ex) {
            Log.exception(this, ex);
        }

        return boi;
    }// end of create

}// EOF
