package com.cboe.interfaces.application;

/**
 * This is the common interface for the Product Definition Home
 * @author Connie Feng
 */
public interface ProductDefinitionHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ProductDefinitionHome";

    /**
    * Creates an instance of the ProductDefinition.
    *
    * @author Connie Feng
    */
    public  ProductDefinition create(SessionManager session);

}
