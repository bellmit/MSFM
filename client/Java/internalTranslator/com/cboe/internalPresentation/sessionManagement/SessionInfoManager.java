//
// -----------------------------------------------------------------------------------
// Source file: SessionInfoManager.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * Defines a common definition for an APIFactory.
 */
public interface SessionInfoManager
{
    /**
     * Accesses the current user sessions from the Session Management Service.
     */
    public UserSession[] getUserSessions() throws SessionQueryException;
    
    /**
     * Accesses the current user sessions from the Session Management Service.
     * @param name : username for which the sessions to be retrieved
     */
    public UserSession[] getUserSessions(String name) throws SessionQueryException;    

    /**
     * Returns a list of all registered CAS instances.
     */
    public CAS[] getCASes() throws SessionQueryException;

    /**
     * Returns a list of the names of all registered CAS instances.
     */
    public String[] getCASNames() throws SessionQueryException;

    /**
     * Returns a list of all registered front ends.
     */
    public FrontEnd[] getFrontEnds() throws SessionQueryException;

    /**
     * Returns a list of the names of all registered front ends.
     */
    public String[] getFrontEndNames() throws SessionQueryException;

    /**
     * Returns a UserSession instance whose name matches the given parameter
     * @param name of user to get
     */
    public UserSession getUserForName(String name) throws SessionQueryException;

    /**
     * Returns a CAS instance whose name matches the given parameter
     * @param name of CAS to get
     */
    public CAS getCASForName(String name) throws SessionQueryException;

    /**
     * Returns a FrontEnd instance whose name matches the given parameter
     * @param name of FrontEnd to get
     */
    public FrontEnd getFrontEndForName(String name) throws SessionQueryException;

    /**
     * Returns a Service instance whose name matches the given parameter
     * @param name of service to get
     */
    public Service getServiceForName(String name) throws SessionQueryException;
}
