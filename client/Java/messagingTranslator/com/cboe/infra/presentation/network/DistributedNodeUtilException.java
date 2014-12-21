/**
 * File Name: DistributedNodeUtilException.java
 * 
 * @author Sridhar Nimmagadda
 * 
 * Created on Feb 7, 2003
 * @version
 * 
 * Migrated to diff package on 04/04/05 by jwalton
 */
package com.cboe.infra.presentation.network;

/**
 * Created on Feb 7, 2003
 * 
 * @author nimmagad
 *  
 */
public class DistributedNodeUtilException extends Exception
{
    /**
     * Constructor for DistributedNodeUtilException.
     */
    public DistributedNodeUtilException()
    {
        super();
    }

    /**
     * Constructor for DistributedNodeUtilException.
     * 
     * @param message
     */
    public DistributedNodeUtilException(String message)
    {
        super(message);
    }

    /**
     * Constructor for DistributedNodeUtilException.
     * 
     * @param message
     * @param cause
     */
    public DistributedNodeUtilException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor for DistributedNodeUtilException.
     * 
     * @param cause
     */
    public DistributedNodeUtilException(Throwable cause)
    {
        super(cause);
    }
}