//
// -----------------------------------------------------------------------------------
// Source file: SessionManagementComponent.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

import java.util.*;

/**
 * SessionManagementComponent represents an abstract system component
 */
public class SessionManagementComponent
{
    protected boolean isRunning = false;
    protected boolean isMaster = false;
    protected String name = "";
    protected HashMap masterServices = new HashMap();

    /**
     * Constructor
     * @param name of component
     * @param isRunning True if running, false otherwise
     * @param isMaster True if master, false otherwise
     */
    public SessionManagementComponent(String name, boolean isRunning, boolean isMaster)
    {
        this.isRunning = isRunning;
        this.isMaster = isMaster;
        this.name = name;
    }

    /**
     * Returns the hashcode of the name
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /**
     * Determines if this object is equal to another
     * @param otherObject to compare with
     * @return True if equal, false otherwise.
     */
    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);

        if(!isEqual)
        {
            if(otherObject == null)
            {
                isEqual = false;
            }
            else if(getClass() == otherObject.getClass())
            {
                SessionManagementComponent castedObj = (SessionManagementComponent)otherObject;

                if(getName().equals(castedObj.getName()))
                {
                    isEqual = true;
                }
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Determines whether this component is running.
     * @return True if running, false otherwise
     */
    public boolean isRunning()
    {
        return this.isRunning;
    }

    /**
     * Determines whether this component is master.
     * @return True if master, false otherwise
     */
    public boolean isMaster()
    {
        return this.isMaster;
    }

    /**
     * Gets the name of this component.
     * @return name of component
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Determines whether this component has a "Master" component for the given
     * component type.
     * @param sessionBrowserConstant the constant that defines the component type.
     * @return True if has a master, false otherwise
     */
    public boolean hasMasterComponent(String sessionBrowserConstant)
    {
        ArrayList masterList = (ArrayList)this.masterServices.get(sessionBrowserConstant);
        return((masterList != null) && (masterList.size() > 0));
    }

    /**
     * Sets the given component as "Master" to this front end
     * @param sessionBrowserConstant the constant that defines the component type.
     * @param masterComponent
     */
    public void addMasterComponent(String sessionBrowserConstant, SessionManagementComponent masterComponent)
    {
        ArrayList masterList = (ArrayList)this.masterServices.get(sessionBrowserConstant);

        if(masterList == null)
        {
            masterList = new ArrayList();
            this.masterServices.put(sessionBrowserConstant, masterList);
        }
        masterList.add(masterComponent);
    }

    /**
     * Gets a list of "Master" components of the given type to this component.
     * @param sessionBrowserConstant the constant that defines the component type.
     * @return array of connected master components
     */
    public SessionManagementComponent[] getConnectedMasterComponents(String sessionBrowserConstant)
    {
        SessionManagementComponent[] components = new SessionManagementComponent[0];
        ArrayList masterList = (ArrayList)this.masterServices.get(sessionBrowserConstant);
        if(masterList != null)
        {
            components = (SessionManagementComponent[])masterList.toArray(components);
        }
        return components;
    }
}
