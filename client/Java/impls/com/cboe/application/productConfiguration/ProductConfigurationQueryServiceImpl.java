package com.cboe.application.productConfiguration;

import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.exceptions.*;

import com.cboe.interfaces.application.ProductConfigurationQueryService;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.internalBusinessServices.ClientProductConfigurationService;

/**
* Implemetation of the ProductConfigurationQueryService interface
*/
public class ProductConfigurationQueryServiceImpl
        extends BObject implements ProductConfigurationQueryService
{
    protected SessionManager currentSession;
    protected ClientProductConfigurationService pcs;

    public ProductConfigurationQueryServiceImpl(SessionManager theSession)
    {
        super();
        currentSession = theSession;
    }

    public void create(String name)
    {
        super.create(name);
        pcs = ServicesHelper.getProductConfigurationService();
    }

    /////////////// IDL methods ////////////////////////////////////

    // just delegate to the ClientProductConfigurationService
    public int getGroupKey(String group)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+group.length()+55);
        calling.append("calling getGroupKey groupName:").append(group).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getGroupKey(group);
    }

    public int[] getGroupKeysForProductClass(int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+80);
        calling.append("calling getGroupKeysForProductClass classKey:").append(classKey).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getGroupKeysForProductClass(classKey);
    }

    public GroupStruct[] getGroupsByType(int type)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+65);
        calling.append("calling getGroupsByType groupType:").append(type).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getGroupsByType(type);
    }

    public GroupStruct[] getGroupsForProductClass(int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+75);
        calling.append("calling getGroupsForProductClass classKey:").append(classKey).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getGroupsForProductClass(classKey);
    }

    public GroupTypeStruct[] getGroupTypes()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+45);
        calling.append("calling getGroupTypes for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getGroupTypes();
    }

    public int[] getProductClassesForGroup(String group)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+75);
        calling.append("calling getProductClassesForGroup groupName:").append(group).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getProductClassesForGroup(group);
    }

    public int[] getProductClassesForGroupByKey(int groupKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+80);
        calling.append("calling getProductClassesForGroupByKey groupKey:").append(groupKey).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getProductClassesForGroupByKey(groupKey);
    }

    public GroupStruct[] getSubGroupsForGroup(int groupKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+70);
        calling.append("calling getSubGroupsForGroup groupKey:").append(groupKey).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getSubGroupsForGroup(groupKey);
    }

    public GroupStruct[] getSuperGroupsForGroup(int groupKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        String smgr = currentSession.toString();
        StringBuilder calling = new StringBuilder(smgr.length()+75);
        calling.append("calling getSuperGroupsForGroup groupKey:").append(groupKey).append(" for sessionManager:").append(smgr);
        Log.information(this, calling.toString());
        return pcs.getSuperGroupsForGroup(groupKey);
    }
}
