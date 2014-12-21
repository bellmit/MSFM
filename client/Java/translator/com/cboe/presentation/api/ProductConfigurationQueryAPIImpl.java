//
// -----------------------------------------------------------------------------------
// Source file: ProductConfigurationQueryAPIImpl.java
//
// PACKAGE: com.cboe.pcqsPresentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.regex.*;
import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.idl.internalBusinessServices.ProductConfigurationQueryService;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;

public class ProductConfigurationQueryAPIImpl implements ProductConfigurationQueryAPI
{
    private ProductConfigurationQueryService pcqs;

    private HashMap<Integer, GroupStruct[]> cachedGroupsByType;
    private HashMap<Integer, GroupStruct> postsForStations;
    private HashMap<Integer, GroupStruct[]> groupsForProductClass;

    private Pattern postPattern = Pattern.compile("^Post_\\d+$");
    private Pattern stationPattern = Pattern.compile(".*Station_\\d+$");

    public ProductConfigurationQueryAPIImpl(ProductConfigurationQueryService pcqs)
    {
        initialize(pcqs);
    }

    protected void initialize(ProductConfigurationQueryService pcqs)
    {
        this.pcqs = pcqs;
        cachedGroupsByType = new HashMap<Integer, GroupStruct[]>();
        postsForStations = new HashMap<Integer, GroupStruct>();
        groupsForProductClass = new HashMap<Integer, GroupStruct[]>();
    }

    public boolean isPostGroup(GroupStruct group)
    {
        boolean isPost = postPattern.matcher(group.groupName).matches();
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":isPostGroup returning " + isPost + " for GroupStruct:",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, group);
        }
        return isPost;
    }

    public boolean isStationGroup(GroupStruct group)
    {
        boolean isStation = stationPattern.matcher(group.groupName).matches();
        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":isStationGroup returning " + isStation + " for GroupStruct:",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, group);
        }
        return isStation;
    }

    public GroupStruct getGroup(int groupKey, int groupType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            Object[] args = new Object[2];
            args[0] = groupKey;
            args[1] = groupType;
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroup",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, args);
        }

        GroupStruct retVal = null;
        GroupStruct[] groupsByType = getGroupsByType(groupType);
        for(GroupStruct struct : groupsByType)
        {
            if(struct.groupKey == groupKey)
            {
                retVal = struct;
                break;
            }
        }
        return retVal;
    }

    /**
     * This iterates through all GroupTypeStructs to find the one with description
     * ProductConfigurationQueryAPI.POST_ASSIGNMENT_GROUP_DESC.  If one isn't found with that
     * description, it returns ProductConfigurationQueryAPI.DEFAULT_POST_ASSIGNMENT_GROUP_TYPE.
     * @return int groupType for the GroupTypeStruct whose description is POST_ASSIGNMENT_GROUP_DESC
     */
    public int getPostStationGroupType()
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getPostStationGroupType", GUILoggerBusinessProperty.PRODUCT_QUERY);
        }
        int retVal = DEFAULT_POST_ASSIGNMENT_GROUP_TYPE;
        try
        {
            GroupTypeStruct[] allTypes = getGroupTypes();
            for(GroupTypeStruct type : allTypes)
            {
                if(type.groupTypeDescription.equalsIgnoreCase(DEFAULT_POST_ASSIGNMENT_GROUP_TYPE_DESC))
                {
                    retVal = type.groupType;
                    if(GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug(TRANSLATOR_NAME +
                                                   ":getPostStationGroupType -- found GroupTypeStruct with description '" +
                                                   DEFAULT_POST_ASSIGNMENT_GROUP_TYPE_DESC + "' -- type=" + retVal,
                                                   GUILoggerBusinessProperty.PRODUCT_QUERY);
                    }
                    break;
                }
            }
        }
        catch(UserException e)
        {
            retVal = DEFAULT_POST_ASSIGNMENT_GROUP_TYPE;
            GUILoggerHome.find().exception("Error getting all group types when trying to find the group type for Post/Station assignments; will use DEFAULT_POST_ASSIGNMENT_GROUP_TYPE "+
                                           DEFAULT_POST_ASSIGNMENT_GROUP_TYPE, e);
        }
        return retVal;
    }

    public int getGroupKey(String groupName)
            throws SystemException, CommunicationException, DataValidationException, NotFoundException,
                   AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroupKey",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, groupName);
        }

        return pcqs.getGroupKey(groupName);
    }

    public int[] getGroupKeysForProductClass(int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroupKeysForProductClass",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }

        return pcqs.getGroupKeysForProductClass(classKey);
    }

    public GroupTypeStruct[] getGroupTypes()
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroupTypes",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, "");
        }

        return pcqs.getGroupTypes();
    }

    public GroupStruct getPostGroupForStation(GroupStruct stationGroup)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        GroupStruct retVal = postsForStations.get(stationGroup.groupKey);
        if(retVal == null)
        {
            GroupStruct[] superGroups = getSuperGroupsForGroup(stationGroup.groupKey);
            for(int i = 0; i < superGroups.length && retVal == null; i++)
            {
                if(isPostGroup(superGroups[i]))
                {
                    retVal = superGroups[i];
                    postsForStations.put(stationGroup.groupKey, retVal);
                }
            }
        }
        return retVal;
    }

    public GroupStruct[] getGroupsByType(int type)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroupsByType",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(type));
        }

        GroupStruct[] groups = cachedGroupsByType.get(type);
        if(groups == null || groups.length == 0)
        {
            groups = pcqs.getGroupsByType(type);
            if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY))
            {
                GUILoggerHome.find().debug(getClass().getName() + ".getGroupsByType("+type+')', GUILoggerBusinessProperty.PRODUCT_QUERY, groups);
            }
            cachedGroupsByType.put(type, groups);
        }
        return groups;
    }

    public GroupStruct[] getGroupsForProductClass(int classKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getGroupsForProductClass",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(classKey));
        }

        GroupStruct[] groups = groupsForProductClass.get(classKey);
        if(groups == null || groups.length == 0)
        {
            groups = pcqs.getGroupsForProductClass(classKey);
            groupsForProductClass.put(classKey, groups);
        }
        return groups;
    }

    public int[] getProductClassesForGroup(String groupName)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesForGroup",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, groupName);
        }

        return pcqs.getProductClassesForGroup(groupName);
    }

    /**
     * This will get all the classKeys for the groupKey, and then try to find a
     * ProductClass for each classKey.
     * @param groupKey - the groupKey to get all ProductClasses for
     * @return array of ProductClasses
     * @throws SystemException
     * @throws CommunicationException
     * @throws DataValidationException
     * @throws AuthorizationException
     */
    public ProductClass[] getProductClassesByGroupKey(int groupKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        List<ProductClass> foundProductClasses;
        int[] classKeys = getProductClassesForGroupByKey(groupKey);
        foundProductClasses = new ArrayList<ProductClass>(classKeys.length);
        for(int classKey : classKeys)
        {
            try
            {
                ProductClass pc = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
                foundProductClasses.add(pc);
            }
            catch(DataValidationException e)
            {
                GUILoggerHome.find().exception(getClass().getName() + ".getProductClassesByGroupKey()",
                                               "DataValidationException from ProductQueryAPI().getProductClassByKey() classKey=" +
                                               classKey, e);
            }
            catch(NotFoundException e)
            {
                GUILoggerHome.find().exception(getClass().getName()+".getProductClassesByGroupKey()",
                                               "NotFoundException from ProductQueryAPI().getProductClassByKey() classKey=" +
                                               classKey, e);
            }
        }

        return foundProductClasses.toArray(new ProductClass[foundProductClasses.size()]);
    }

    public int[] getProductClassesForGroupByKey(int groupKey)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProductClassesForGroupByKey",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(groupKey));
        }

        return pcqs.getProductClassesForGroupByKey(groupKey);
    }

    public GroupStruct[] getSuperGroupsForGroup(int groupKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSuperGroupsForGroup",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(groupKey));
        }

        return pcqs.getSuperGroupsForGroup(groupKey);
    }

    public GroupStruct[] getSubGroupsForGroup(int group)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getSubGroupsForGroup",
                                       GUILoggerBusinessProperty.PRODUCT_QUERY, new Integer(group));
        }

        return pcqs.getSubGroupsForGroup(group);
    }

	@Override
    public int getProcessGroupType()
    {
	        if(GUILoggerHome.find().isDebugOn())
	        {
	            GUILoggerHome.find().debug(TRANSLATOR_NAME + ":getProcessGroupType", GUILoggerBusinessProperty.PRODUCT_QUERY);
	        }
	        int retVal = DEFAULT_PROCESS_GROUP_TYPE;
	        try
	        {
	            GroupTypeStruct[] allTypes = getGroupTypes();
	            for(GroupTypeStruct type : allTypes)
	            {
	                if(type.groupTypeDescription.equalsIgnoreCase(DEFAULT_PROCESS_GROUP_TYPE_DESC))
	                {
	                    retVal = type.groupType;
	                    if(GUILoggerHome.find().isDebugOn())
	                    {
	                        GUILoggerHome.find().debug(TRANSLATOR_NAME +
	                                                   ":getProcessGroupType -- found GroupTypeStruct with description '" +
	                                                   DEFAULT_PROCESS_GROUP_TYPE_DESC + "' -- type=" + retVal,
	                                                   GUILoggerBusinessProperty.PRODUCT_QUERY);
	                    }
	                    break;
	                }
	            }
	        }
	        catch(UserException e)
	        {
	            retVal = DEFAULT_PROCESS_GROUP_TYPE;
	            GUILoggerHome.find().exception("Error getting all group types when trying to find the group type for Process Groups; will use DEFAULT_PROCESS_GROUP_TYPE "+
	                                           DEFAULT_PROCESS_GROUP_TYPE, e);
	        }
	        return retVal;
	    }
}
