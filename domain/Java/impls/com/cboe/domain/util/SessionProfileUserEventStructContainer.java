package com.cboe.domain.util;

import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserSummaryStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

/**
 * Wrapper object for User Event Channel, UserStruct and UserDefinitionStruct.
 * the new class which can replace the odl class   com.cboe.domain.util.UserEventStructContainer
 * @since  Hybrid 1.5 Profile By Session
*/
public class SessionProfileUserEventStructContainer
{
    private SessionProfileUserStruct userStruct;
    private SessionProfileUserDefinitionStruct userDefinitionStruct;
    private UserEnablementStruct userEnablementStruct;
    private String 		userId;
    private String 		userName;

    public SessionProfileUserEventStructContainer(SessionProfileUserStruct userStruct,
                                                  SessionProfileUserDefinitionStruct userDefinitionStruct,
                                                  UserEnablementStruct userEnablementStruct)
    {
    	this.userStruct = userStruct;
    	this.userDefinitionStruct = userDefinitionStruct;
        this.userEnablementStruct = userEnablementStruct;
    }

    public SessionProfileUserEventStructContainer(String userId, String userName)
    {
    	this.userId = userId;
    	this.userName = userName;
    }

    public SessionProfileUserStruct getUserStruct()
    {
        return this.userStruct;
    }

    public SessionProfileUserDefinitionStruct getUserDefinitionStruct()
    {
        return this.userDefinitionStruct;
    }

    public UserEnablementStruct getUserEnablementStruct()
    {
        return this.userEnablementStruct;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public String getUserName()
    {
        return this.userName;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString() {
        StringBuilder buf = new StringBuilder(45);
        buf.append(" UserAcronymExchange: ")
        .append(userStruct.userAcronym.exchange)
        .append(" UserId: ")
        .append(userStruct.userId);
        return buf.toString();
    }
}
