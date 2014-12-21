//
// -----------------------------------------------------------------------------------
// Source file: GroupImpl.java
//
// PACKAGE: com.cboe.presentation.productConfiguration
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.productConfiguration;

import java.util.regex.*;

import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.presentation.productConfiguration.Group;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Immutable wrapper for the GroupStruct.
 */
public class GroupImpl implements Group
{
    private static final String POST_GROUP_STRING = "Post";
    private static final String STATION_GROUP_STRING = "Station";
    private GroupStruct struct;
    private Group[] subGroups;
    private Group[] superGroups;
    private Pattern groupNameDelimsPattern = Pattern.compile("[\\._]");

    private boolean isPost = false;
    private boolean isStation = false;
    private int postNum = -1;
    private int stationNum = -1;

    public GroupImpl(GroupStruct struct)
    {
        this.struct = struct;
        initialize();
    }

    // parse the groupName to see if this Group is a Post or Station
    protected void initialize()
    {
        String[] nameParts = groupNameDelimsPattern.split(getGroupName());
        for(int i = 0; i < nameParts.length; i++)
        {
            // post nodes' groupName should be of format "Post_1"
            if(nameParts[i].equals(POST_GROUP_STRING))
            {
                // the next part should be the post number
                if(nameParts.length >= i+2)
                {
                    isPost = true;
                    try
                    {
                        postNum = Integer.parseInt(nameParts[++i]);
                    }
                    catch(NumberFormatException e)
                    {
                        GUILoggerHome.find().exception("Error trying to parse the Post number from substring '" +
                                                       nameParts[i - 1] + "' of GroupStruct.groupName '" +
                                                       getGroupName() + "' (groupKey=" + getGroupKey() + ")", e);
                    }
                }
            }
            // station nodes' groupName should be of format "Post_1.Station_2"
            else if(nameParts[i].equals(STATION_GROUP_STRING))
            {
                // the next part should be the station number
                if(nameParts.length >= i + 2)
                {
                    isPost = false;
                    isStation = true;
                    try
                    {
                        stationNum = Integer.parseInt(nameParts[++i]);
                    }
                    catch(NumberFormatException e)
                    {
                        GUILoggerHome.find().exception("Error trying to parse the Station number from substring '" +
                                                       nameParts[i - 1] + "' of GroupStruct.groupName '" +
                                                       getGroupName() + "' (groupKey=" + getGroupKey() + ")", e);
                    }
                }
            }
        }
    }

    /**
     * If this Group is a Post node, this will return the post number.  Otherwise it will return -1.
     */
    public int getPostNumber()
    {
        return postNum;
    }

    /**
     * If this Group is a Station node, this will return the station number.  Otherwise it will return -1.
     */
    public int getStationNumber()
    {
        return stationNum;
    }

    public int getGroupKey()
    {
        return struct.groupKey;
    }

    public String getGroupName()
    {
        return struct.groupName;
    }

    public GroupStruct getGroupStruct()
    {
        return struct;
    }

    public GroupTypeStruct getGroupTypeStruct()
    {
        return struct.groupType;
    }

    /**
     * Returns true if this Group represents a Post.
     */
    public boolean isPostGroup()
    {
        return isPost;
    }

    /**
     * Returns true if this Group represents a Station.
     */
    public boolean isStationGroup()
    {
        return isStation;
    }

    public Group[] getSuperGroups()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(superGroups == null)
        {
            superGroups = GroupFactory.createGroups(APIHome.findProductConfigurationQueryAPI().getSuperGroupsForGroup(getGroupKey()));
        }
        return superGroups;
    }

    public Group[] getSubGroups()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(subGroups == null)
        {
            subGroups = GroupFactory.createGroups(APIHome.findProductConfigurationQueryAPI().getSubGroupsForGroup(getGroupKey()));
        }
        return subGroups;
    }

    /**
     * Sort based on group name.
     */
    public int compareTo(Object obj)
    {
        int retVal = -1;
        if(obj instanceof Group)
        {
            retVal = getGroupName().compareTo(((Group)obj).getGroupName());
        }
        return retVal;
    }

    public int hashCode()
    {
        return getGroupKey();
    }

    public boolean equals(Object obj)
    {
        boolean retVal = false;
        if(obj instanceof Group)
        {
            retVal = getGroupKey() == ((Group)obj).getGroupKey();
        }
        return retVal;
    }
}
