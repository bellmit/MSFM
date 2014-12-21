package com.cboe.interfaces.application;

import com.cboe.idl.cmi.*;
/**
 * This is the common interface for the Product Query Manager Home
 * @author Connie Feng
 */
public interface ProductQueryManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductQueryManagerHome";
    
    /**
    * Creates an instance of the product query manager.
    *
    * @author Connie Feng
    */
    public  ProductQueryManager create(SessionManager sessionManager);
        
}
