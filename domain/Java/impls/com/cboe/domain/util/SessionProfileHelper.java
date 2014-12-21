package com.cboe.domain.util;

/**
 * A helper class that uses to convert structs
 * ProfileStruct <=> SessinProfileStruct, the new field of SessionProfileStruct will be ALL_SESSIONS_NAME by default.
 * UserStruct <=> SessionProfileUserStruct
 * UserStructV2 <=> SessionProfileUserStructV2
 * UserDefinitionStruct <=> SessionProfileUserDefinitionStruct
 *
 *
 * Copyright:    Copyright (c) 2003 Chicago Board Options Exchange
 */

import com.cboe.idl.cmiConstants.SessionNameValues;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.cmiUser.ProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.*;
import com.cboe.idl.constants.MarketMakerClassAssignmentTypes;
import com.cboe.idl.constants.UserTypes;
import com.cboe.interfaces.businessServices.UserService;
import com.cboe.interfaces.businessServices.UserServiceHome;
import com.cboe.interfaces.internalBusinessServices.UserMaintenanceService;
import com.cboe.interfaces.internalBusinessServices.UserMaintenanceServiceHome;
import com.cboe.interfaces.domain.user.User;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;

import java.util.ArrayList;


public class SessionProfileHelper
{
    private static UserMaintenanceService userService;

    public static SessionProfileStruct toSessionProfileStruct(ProfileStruct ps)
    {
        SessionProfileStruct result = new SessionProfileStruct();
        result.account = ps.account;
        result.classKey = ps.classKey;
        result.executingGiveupFirm = ps.executingGiveupFirm;
        result.subAccount = ps.subAccount;
        result.sessionName = SessionNameValues.ALL_SESSION_NAME;  // by default
        result.isAccountBlanked = false;  // by default
        result.originCode = ' ';
        return result;
    }
    public static SessionProfileStruct[] toSessionProfileSturcts(ProfileStruct[] ps)
    {
        SessionProfileStruct[] result = new SessionProfileStruct[ps.length];
        for (int i = 0; i < ps.length; i++)
        {
            result[i] = toSessionProfileStruct(ps[i]);
        }
        return result;
    }

    public static ProfileStruct toProfileStruct(SessionProfileStruct ps)
    {
        ProfileStruct result = new ProfileStruct();
        result.account = ps.account;
        result.classKey = ps.classKey;
        result.executingGiveupFirm = ps.executingGiveupFirm;
        result.subAccount = ps.subAccount;
        return result;
    }

    public static ProfileStruct[] toProfileSturcts(SessionProfileStruct[] ps)
    {
        ArrayList resultList = new ArrayList();
        for (int i=0; i< ps.length; i++)
        {
            if  (SessionNameValues.ALL_SESSION_NAME.equals(ps[i].sessionName))
            {
                resultList.add(toProfileStruct(ps[i]));
            }
        }
        ProfileStruct[] result = new ProfileStruct[resultList.size()];
        resultList.toArray(result);
        return result;
    }
    public static SessionProfileUserStruct toSessionProfileUserStruct(UserStruct us)
    {
        SessionProfileUserStruct result = new SessionProfileUserStruct();
        result.userAcronym = us.userAcronym;
        result.userId = us.userId;
        result.firm = us.firm;
        result.fullName = us.fullName;
        result.role = us.role;
        result.executingGiveupFirms = us.executingGiveupFirms;
        result.defaultProfile = toSessionProfileStruct(us.defaultProfile);
        result.accounts = us.accounts;
        result.assignedClasses = us.assignedClasses;
        result.dpms = us.dpms;
        result.sessionProfilesByClass= toSessionProfileSturcts(us.profilesByClass);
        result.defaultSessionProfiles = new SessionProfileStruct[0]; // blank field.
        return result;
    }
    public static SessionProfileUserStruct[] toSessionProfileUserStructs(UserStruct[] us)
    {
        SessionProfileUserStruct[] result = new SessionProfileUserStruct[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toSessionProfileUserStruct(us[i]);
        }
        return result;
    }

    public static UserStruct toUserStruct(SessionProfileUserStruct us)
    {
        UserStruct result = new UserStruct();
        result.accounts = us.accounts;
        result.userAcronym = us.userAcronym;
        result.userId = us.userId;
        result.firm = us.firm;
        result.fullName = us.fullName;
        result.role = us.role;
        result.executingGiveupFirms = us.executingGiveupFirms;
        result.defaultProfile = toProfileStruct(us.defaultProfile);
        result.assignedClasses = us.assignedClasses;
        result.dpms = us.dpms;
        result.profilesByClass = toProfileSturcts(us.sessionProfilesByClass);
        return result;
    }
    public static UserStruct[] toUserStructs(SessionProfileUserStruct[] us)
    {
        UserStruct[] result = new UserStruct[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toUserStruct(us[i]);
        }
        return result;
    }

    public static UserDefinitionStruct toUserDefinitionStruct(SessionProfileUserDefinitionStruct us)
    {
         UserDefinitionStruct result = new  UserDefinitionStruct();
         result.userKey= us.userKey;
         result.userAcronym = us.userAcronym;
         result.userId = us.userId;
         result.userType = us.userType;
         result.firmKey = us.firmKey;
         result.fullName = us.fullName;
         result.role = us.role;
         result.executingGiveupFirms = us.executingGiveupFirms;
         result.defaultProfile = toProfileStruct(us.defaultProfile);
         result.accounts = us.accounts;
         
         result.assignedClasses = new int[us.assignedClasses.length];
         for(int i=0; i< us.assignedClasses.length; i++)
         {
             result.assignedClasses[i] = us.assignedClasses[i].classKey;
         }
        
         result.dpms = us.dpms;
         result.profilesByClass = toProfileSturcts(us.sessionProfilesByClass);
         result.inactivationTime = us.inactivationTime;
         result.lastModifiedTime = us.lastModifiedTime;
         result.membershipKey = us.membershipKey;
         result.isActive = us.isActive;
         result.versionNumber = us.versionNumber;
         return result;
    }

    public static UserDefinitionStruct[] toUserDefinitionStructs(SessionProfileUserDefinitionStruct[] us)
    {
        UserDefinitionStruct[] result = new UserDefinitionStruct[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toUserDefinitionStruct(us[i]);
        }
        return result;
    }
    public static boolean isProfileForAllSessions(SessionProfileStruct profile)
    {
        if(SessionNameValues.ALL_SESSION_NAME.equals(profile.sessionName))
        {
            return true;
        }
        return false;
    }
    public static SessionProfileUserDefinitionStruct toSessionProfileUserDefinitionStruct(UserDefinitionStruct us)
    {
        SessionProfileUserDefinitionStruct result = new SessionProfileUserDefinitionStruct();
        result.userKey = us.userKey;
        result.userAcronym = us.userAcronym;
        result.userId = us.userId;
        result.firmKey = us.firmKey;
        result.fullName = us.fullName;
        result.role = us.role;
        result.executingGiveupFirms = us.executingGiveupFirms;
        result.accounts = us.accounts;
       
        SessionProfileUserDefinitionStruct user = null;
        result.assignedClasses = new MarketMakerClassAssignmentStruct[us.assignedClasses.length];
        try
        {
            user = getUserService().getSessionProfileUserByUserId(us.userId);
        }
        catch(Exception e)
        {
            user = null;
        }
        
        short asgnType;
        if(us.userType == UserTypes.DPM_ACCOUNT)
        {
            asgnType = MarketMakerClassAssignmentTypes.NOT_APPLICABLE;
        }
        else
        {
            asgnType = MarketMakerClassAssignmentTypes.MM;
        }

        if(user == null)
        {
            //This is a new user
            MarketMakerClassAssignmentStruct assignmentStruct;
            for(int i=0; i<us.assignedClasses.length; i++)
            {
                assignmentStruct = new MarketMakerClassAssignmentStruct();
                assignmentStruct.classKey = us.assignedClasses[i];
                assignmentStruct.sessionName = SessionNameValues.ALL_SESSION_NAME;
                assignmentStruct.assignmentType = asgnType;
                
                result.assignedClasses[i] = assignmentStruct;
            }
        }
        else
        {
            //This is existing user so  keep the existing assignments
            MarketMakerClassAssignmentStruct asgnment;
            for(int i=0; i < us.assignedClasses.length; i++)
            {
                asgnment = new MarketMakerClassAssignmentStruct(); 
                asgnment.classKey = us.assignedClasses[i];
                asgnment.sessionName = SessionNameValues.ALL_SESSION_NAME; //In case existing assignment does not have this class.
                asgnment.assignmentType = asgnType; //In case existing assignment does not have this class.
                
                for(int j=0; j< user.assignedClasses.length; j++)
                {
                    if(asgnment.classKey == user.assignedClasses[j].classKey)
                    {
                        asgnment.sessionName = user.assignedClasses[j].sessionName;
                        asgnment.assignmentType = user.assignedClasses[j].assignmentType;
                        break;
                    }
                }
                result.assignedClasses[i] = asgnment;
            }
        }
       
        result.dpms = us.dpms;
        result.inactivationTime = us.inactivationTime;
        result.lastModifiedTime = us.lastModifiedTime;
        result.membershipKey = us.membershipKey;
        result.isActive = us.isActive;
        result.userType = us.userType;
        result.versionNumber = us.versionNumber;
        result.defaultProfile = toSessionProfileStruct(us.defaultProfile);
        result.sessionProfilesByClass = toSessionProfileSturcts(us.profilesByClass);
        result.defaultSessionProfiles = new SessionProfileStruct[0]; // blank field.
        result.sessionClearingAcronyms = new SessionClearingAcronymStruct[0];
        return result;
    }

    public static SessionProfileUserDefinitionStruct[] toSessionProfileUserDefinitionStructs(UserDefinitionStruct[] us)
    {
        SessionProfileUserDefinitionStruct[] result = new SessionProfileUserDefinitionStruct[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toSessionProfileUserDefinitionStruct(us[i]);
        }
        return result;
    }

    public static SessionProfileUserStructV2 toSessionProfileUserStructV2(UserStructV2 us)
    {
        SessionProfileUserStructV2 result = new SessionProfileUserStructV2();
        result.userInfo = toSessionProfileUserStruct(us.userInfo);
        result.userKey = us.userKey;
        return result;
    }

    public static SessionProfileUserStructV2[] toSessionProfileUserStructsV2(UserStructV2[] us)
    {
        SessionProfileUserStructV2[] result = new SessionProfileUserStructV2[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toSessionProfileUserStructV2(us[i]);
        }
        return result;
    }

    public static UserStructV2 toUserStructV2(SessionProfileUserStructV2 us)
    {
        UserStructV2 result = new UserStructV2();
        result.userInfo = toUserStruct(us.userInfo);
        result.userKey = us.userKey;
        return result;
    }

    public static UserStructV2[] toUserStructsV2(SessionProfileUserStructV2[] us)
    {
        UserStructV2[] result = new UserStructV2[us.length];
        for (int i = 0; i < us.length; i++)
        {
            result[i] = toUserStructV2(us[i]);
        }
        return result;
    }
    
    private static UserMaintenanceService getUserService()
    {
        if(userService == null)
        {
            try
            {
                UserMaintenanceServiceHome home = (UserMaintenanceServiceHome) HomeFactory.getInstance().findHome(UserMaintenanceServiceHome.HOME_NAME);
                userService = home.find();
            }
            catch (CBOELoggableException e)
            {
                Log.exception("SessionProfileHelper >>> Could not find UserMaintenanceServiceHome", e);
            }
        }
        return userService;
    }
}