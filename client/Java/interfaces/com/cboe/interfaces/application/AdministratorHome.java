package com.cboe.interfaces.application;

import com.cboe.idl.cmi.*;
/**
 * This is the common interface for the Administrator Home
 * @author Connie Feng
 */
public interface AdministratorHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "AdministratorHome";

    /**
    * Creates an instance of the Administrator.
    *
    * @author Connie Feng
    */
    public  Administrator create(SessionManager sessionManager);

}
