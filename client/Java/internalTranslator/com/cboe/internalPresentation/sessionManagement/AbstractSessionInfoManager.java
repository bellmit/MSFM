//
// -----------------------------------------------------------------------------------
// Source file: AbstractSessionInfoManager.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import com.cboe.idl.infrastructureServices.sessionManagementService.Components;
import com.cboe.idl.infrastructureServices.sessionManagementService.ComponentStruct;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;
import com.cboe.idl.infrastructureServices.sessionManagementService.SessionManagementAdminServiceOperations;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.internalPresentation.common.comparators.SMComponentNameComparator;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 * Implements an abstract version of the SessionInfoManager
 */
public abstract class AbstractSessionInfoManager implements SessionInfoManager
{
    private Comparator nameComparator = null;

    /**
     * AbstractSessionInfoManager constructor comment.
     */
    public AbstractSessionInfoManager()
    {
      super();
    }

    /**
     * Returns a reference to the SessionManagementAdminService.
     * @return SessionManagementAdminServiceOperations
     */
    protected abstract SessionManagementAdminServiceOperations getSessionManagementAdminService() throws SessionQueryException;

    /**
     * Accesses the current user sessions from the Session Management Service.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public UserSession[] getUserSessions() throws SessionQueryException
    {
        UserSession[] userArray = null;

        
            // Get all of the users from the SMS
        UserLoginStruct[] userStructs;
        try {
            userStructs = getSessionManagementAdminService().getUsers(false);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.USER_SESSION,
                    userStructs);
            if (GUILoggerHome.find().isInformationOn()) {
                GUILoggerHome.find().information("AbstractSessionInfoManager",
                        GUILoggerSABusinessProperty.USER_SESSION, "Found " + userStructs.length + " User Sessions");
            }

            userArray = new UserSession[userStructs.length];
            UserSession userSession = null;

            for (int i = 0; i < userStructs.length; i++) {
                if (userStructs[i].sourceComponents != null && userStructs[i].sourceComponents.length == 1
                        && userStructs[i].sourceComponents[0].length() == 0) {
                    userSession = new UserSession(new User(userStructs[i].userId, userStructs[i].loggedIn),
                            new String[0]);
                } else {
                    userSession = new UserSession(new User(userStructs[i].userId, userStructs[i].loggedIn),
                            userStructs[i].sourceComponents);
                }
                userArray[i] = userSession;
            }

            // Sort the list of users in alphabetical order by Name
            Arrays.sort(userArray, getNameComparator());

        } catch (Exception e) {
            throw new SessionQueryException("Could not acquire user sessions information." ,e);
        }
        
        return userArray;
    }

    /**
     * Accesses the current user sessions from the Session Management Service.
     * @param name user name for which the user sessions to be obtained.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public UserSession[] getUserSessions(String name) throws SessionQueryException
    {
        UserSession[] userArray = null;

        try
        {
            int sessionId = getSessionManagementAdminService().getSessionByUserId(name);
            //Get all of the users from the SMS
            UserLoginStruct[] userStructs = getSessionManagementAdminService().getUsersForSession(sessionId);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.USER_SESSION, userStructs);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.USER_SESSION,
                                                 "Found " + userStructs.length + " User Sessions");
            }

            userArray = new UserSession[userStructs.length];
            UserSession userSession = null;

            for(int i = 0; i < userStructs.length; i++)
            {
                if(userStructs[i].sourceComponents != null && userStructs[i].sourceComponents.length == 1 &&
                   userStructs[i].sourceComponents[0].length() == 0)
                {
                    userSession = new UserSession(new User(userStructs[i].userId, userStructs[i].loggedIn), new String[0]);
                }
                else
                {
                    userSession = new UserSession(new User(userStructs[i].userId, userStructs[i].loggedIn), userStructs[i].sourceComponents);
                }
                userArray[i] = userSession;
            }

            //Sort the list of users in alphabetical order by Name
            Arrays.sort(userArray, getNameComparator());
        }
        catch(NotFoundException e)
        {
            //We are not throwing this back because, finding sessionId for a logged out user always 
            //throws this exception. This is handled by the calling method accordingly.
            GUILoggerHome.find().debug("Session Not found for user-id ", GUILoggerSABusinessProperty.USER_SESSION, name);
        }
        catch(Exception e)
        {
            // catch the rest and attach the original exception that was the problem.
            throw new SessionQueryException(String.format("Could not get session for user name %s.",name), e);
        }
       
        return userArray;
    }
    
    /**
     * Returns a list of all registered CAS instances.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public CAS[] getCASes() throws SessionQueryException
    {
        CAS[] cases = null;

        ComponentStruct[] components = getCASComponents();

        if(components != null)
        {
            cases = new CAS[components.length];

            for(int i = 0; i < components.length; i++)
            {
                cases[i] = buildCASComponent(components[i]);
            }

            Arrays.sort(cases, getNameComparator());
        }
        else
        {
            cases = new CAS[0];
        }

        return cases;
    }

    /**
     * Returns a list of the names of all registered CAS instances.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public String[] getCASNames() throws SessionQueryException
    {
        String[] casNames = null;

        ComponentStruct[] components = getCASComponents();

        if(components != null)
        {
            casNames = new String[components.length];

            for(int i = 0; i < components.length; i++)
            {
                casNames[i] = components[i].componentId;
            }

            Arrays.sort(casNames);
        }
        else
        {
            casNames = new String[0];
        }

        return casNames;
    }

    /**
     * Returns a list of all registered front ends.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public FrontEnd[] getFrontEnds() throws SessionQueryException
    {
        FrontEnd[] frontEnds = null;

        ComponentStruct[] components = getFrontEndComponents();

        if(components != null)
        {
            frontEnds = new FrontEnd[components.length];

            for(int i = 0; i < components.length; i++)
            {
                frontEnds[i] = buildFrontEndComponent(components[i]);
            }

            Arrays.sort(frontEnds, getNameComparator());
        }
        else
        {
            frontEnds = new FrontEnd[0];
        }

        return frontEnds;
    }

    /**
     * Returns a list of the names of all registered front ends.
     * @exception SessionQueryException if any SMS service methods fail
     */
    public String[] getFrontEndNames() throws SessionQueryException
    {
        String[] frontEndNames = null;

        ComponentStruct[] components = getFrontEndComponents();

        if(components != null)
        {
            frontEndNames = new String[components.length];

            for(int i = 0; i < components.length; i++)
            {
                frontEndNames[i] = components[i].componentId;
            }

            Arrays.sort(frontEndNames);
        }
        else
        {
            frontEndNames = new String[0];
        }

        return frontEndNames;
    }

    /**
     * Returns a UserSession instance whose name matches the given parameter
     * @param name of user to get
     * @exception SessionQueryException if any SMS service methods fail
     */
    public UserSession getUserForName(String name) throws SessionQueryException
    {
        UserSession returnedSession = null;

        SessionManagementComponent sampleComponent = new SessionManagementComponent(name, false, false);

        UserSession[] allSessions = getUserSessions(name);

        if(allSessions != null)
        {
            int index = Arrays.binarySearch(allSessions, sampleComponent, getNameComparator());

            if(index >= 0)
            {
                returnedSession = allSessions[index];
            }
        }

        return returnedSession;
    }

    /**
     * Returns a CAS instance whose name matches the given parameter
     * @param name of CAS to get
     * @exception SessionQueryException if any SMS service methods fail
     */
    public CAS getCASForName(String name) throws SessionQueryException
    {
        CAS returnedCAS = null;
        ComponentStruct component = null;

        try {
            component = getSessionManagementAdminService().getComponent(name);
        } catch (Exception e) {
            throw new SessionQueryException(String.format("Could not retrieve info for CAS %s.",name), e);
        }
        
        if(component != null)
        {
            returnedCAS = buildCASComponent(component);
        }

        return returnedCAS;
    }

    /**
     * Returns a FrontEnd instance whose name matches the given parameter
     * @param name of FrontEnd to get
     * @exception SessionQueryException if any SMS service methods fail
     */
    public FrontEnd getFrontEndForName(String name) throws SessionQueryException
    {
        FrontEnd returnedFrontEnd = null;
        ComponentStruct component = null;

        try {
            component = getSessionManagementAdminService().getComponent(name);
        } catch (Exception e) {
            throw new SessionQueryException(String.format("Could not retrieve front end name for CAS %s.",name),e);
        }     

        if(component != null)
        {
            returnedFrontEnd = buildFrontEndComponent(component);
        }

        return returnedFrontEnd;
    }

    /**
     * Returns a Service instance whose name matches the given parameter
     * @param name of service to get
     * @exception SessionQueryException if any SMS service methods fail
     */
    public Service getServiceForName(String name) throws SessionQueryException
    {
        Service returnedService = null;

        try
        {
            ComponentStruct component = getSessionManagementAdminService().getComponent(name);
            returnedService = new Service(name, component.isRunning, component.isMaster);
        }
        catch(Exception e)
        {
            throw new SessionQueryException(String.format("Could not receive service for %s.", name), e);
        }

        return returnedService;
    }

    /**
     * Creates and obtains a singleton comparator for SessionManagementComponent's
     */
    protected Comparator getNameComparator()
    {
        if(nameComparator == null)
        {
            nameComparator = new SMComponentNameComparator();
        }

        return nameComparator;
    }

    /**
     * Builds a CAS component from a ComponentStruct
     * @param component struct to build CAS from
     * @return CAS
     * @exception SessionQueryException if any SMS service methods fail
     */
    protected CAS buildCASComponent(ComponentStruct component) throws SessionQueryException
    {
        CAS returnedCAS = null;

        User[] connectedUsers = null;
        String[] connectedFrontEnds = null;
        ArrayList masterFrontEnds = new ArrayList();

        try
        {
            UserLoginStruct[] connectedUsersStructSeq = getSessionManagementAdminService().getUsersForSourceComponent(component.componentId, false);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, connectedUsersStructSeq);
            if(GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + connectedUsersStructSeq.length + " User Sessions for CAS name=" + component.componentId + ".");
            }

            connectedUsers = new User[connectedUsersStructSeq.length];

            for(int i = 0; i < connectedUsers.length; i++)
            {
                connectedUsers[i] = new User(connectedUsersStructSeq[i].userId, connectedUsersStructSeq[i].loggedIn);
            }
            Arrays.sort(connectedUsers, getNameComparator());
        }
        catch (Exception e) {
            throw new SessionQueryException(
                    String.format("Could not build CAS component because of user problem for componentID %s.",
                            component.componentId), e);
        }

        try
        {
            ComponentStruct[] connectedFrontEndsStructSeq = getSessionManagementAdminService().getConnectedComponents(component.componentId, Components.CONNECTION_COMPONENT);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, connectedFrontEndsStructSeq);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + connectedFrontEndsStructSeq.length + " Front End Components for CAS name=" + component.componentId + ".");
            }

            connectedFrontEnds = new String[connectedFrontEndsStructSeq.length];

            for(int i = 0; i < connectedFrontEnds.length; i++)
            {
                connectedFrontEnds[i] = connectedFrontEndsStructSeq[i].componentId;

                if(connectedFrontEndsStructSeq[i].isMaster)
                {
                    SessionManagementComponent masterComp = new SessionManagementComponent(
                            connectedFrontEndsStructSeq[i].componentId,
                            connectedFrontEndsStructSeq[i].isRunning, connectedFrontEndsStructSeq[i].isMaster);

                    masterFrontEnds.add(masterComp);
                }
            }

            Arrays.sort(connectedFrontEnds);
        }
        catch(Exception e)
        {
            throw new SessionQueryException(String.format("Could not retrieve Front Ends for CAS=%s.",component.componentId), e);
        }

        returnedCAS = new CAS(component.componentId, connectedUsers, connectedFrontEnds, component.isRunning, component.isMaster);

        for(Iterator i = masterFrontEnds.iterator(); i.hasNext();)
        {
            returnedCAS.addMasterComponent(SessionBrowserConstants.FRONTEND, (SessionManagementComponent)i.next());
        }

        return returnedCAS;
    }

    /**
     * Gets the ComponentStructs for CAS's
     * @return ComponentStruct array that represents all CAS's
     * @exception SessionQueryException if any SMS service methods fail
     */
    protected ComponentStruct[] getCASComponents() throws SessionQueryException
    {
        ComponentStruct[] cases = null;

        try
        {
            cases = getSessionManagementAdminService().getComponentsForType(Components.SOURCE_COMPONENT);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, cases);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager",  GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + cases.length + " CAS Components");
            }
        }
        catch(Exception e)
        {
            throw new SessionQueryException("Could not retrieve CAS Components.", e);
        }

        return cases;
    }

    /**
     * Builds a FrontEnd component from a ComponentStruct
     * @param component struct to build FrontEnd from
     * @return FrontEnd
     * @exception SessionQueryException if any SMS service methods fail
     */
    protected FrontEnd buildFrontEndComponent(ComponentStruct component) throws SessionQueryException
    {
        FrontEnd returnedFrontEnd = null;

        String[] connectedCASList = null;
        String[] connectedServices = null;
        ArrayList masterServices = new ArrayList();

        try
        {
            ComponentStruct[] connectedCASStructSeq = getSessionManagementAdminService().getConnectedComponents(component.componentId, Components.SOURCE_COMPONENT);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, connectedCASStructSeq);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + connectedCASStructSeq.length + " CAS Components for Front End name=" + component.componentId + ".");
            }

            connectedCASList = new String[connectedCASStructSeq.length];

            for(int i = 0; i < connectedCASList.length; i++)
            {
                connectedCASList[i] = connectedCASStructSeq[i].componentId;
            }
            Arrays.sort(connectedCASList);
        }
        catch(Exception e)
        {
            throw new SessionQueryException(String.format("Could not retrieve CAS's for Front End named %s.",component.componentId), e);
        }

        try
        {
            ComponentStruct[] connectedServicesStructSeq = getSessionManagementAdminService().getConnectedComponents(component.componentId, Components.CRITICAL_COMPONENT);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, connectedServicesStructSeq);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + connectedServicesStructSeq.length + " Service Components for Front End name=" + component.componentId + ".");
            }

            connectedServices = new String[connectedServicesStructSeq.length];

            for(int i = 0; i < connectedServicesStructSeq.length; i++)
            {
                String serviceName = connectedServicesStructSeq[i].componentId;
                connectedServices[i] = serviceName;

                if(connectedServicesStructSeq[i].isMaster)
                {
                    SessionManagementComponent masterComp = new SessionManagementComponent(
                            connectedServicesStructSeq[i].componentId,
                            connectedServicesStructSeq[i].isRunning, connectedServicesStructSeq[i].isMaster);

                    masterServices.add(masterComp);
                }
            }

            Arrays.sort(connectedServices);
        }
        catch(Exception e)
        {
            throw new SessionQueryException(String.format("Could not retrieve Services for Front End CAS named %s.",component.componentId),e);
        }

        returnedFrontEnd = new FrontEnd(component.componentId, connectedCASList, connectedServices, component.isRunning, component.isMaster);

        for(Iterator i = masterServices.iterator(); i.hasNext();)
        {
            returnedFrontEnd.addMasterComponent(SessionBrowserConstants.SERVICE, (SessionManagementComponent)i.next());
        }

        return returnedFrontEnd;
    }                                         

    /**
     * Gets the ComponentStructs for Front End's
     * @return ComponentStruct array that represents all Front End's
     * @exception SessionQueryException if any SMS service methods fail
     */
    protected ComponentStruct[] getFrontEndComponents() throws SessionQueryException
    {
        ComponentStruct[] frontEnds = null;

        try
        {
            frontEnds = getSessionManagementAdminService().getComponentsForType(Components.CRITICAL_COMPONENT);

            GUILoggerHome.find().debug("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT, frontEnds);
            if (GUILoggerHome.find().isInformationOn())
            {
                GUILoggerHome.find().information("AbstractSessionInfoManager", GUILoggerSABusinessProperty.SESSION_MANAGEMENT,
                                                 "Found " + frontEnds.length + " Front End Components");
            }
        }
        catch(Exception e)
        {
            throw new SessionQueryException("Could not retrieve Front End Components.", e);
        }

        return frontEnds;
    }
}
