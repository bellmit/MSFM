package com.cboe.domain.util;

import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.user.UserDefinitionStruct;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.user.UserSummaryStruct;

/**
* Wrapper object for User Event Channel, UserStruct and UserDefinitionStruct.
* @author William Wei
*/
public class UserEventStructContainer
{
    private UserStruct   	userStruct;
    private UserDefinitionStruct userDefinitionStruct;
    private UserEnablementStruct userEnablementStruct;
    private UserSummaryStruct    userSummaryStruct;
    private String 		userId;
    private String 		userName;

    public UserEventStructContainer(UserStruct userStruct,
                                    UserDefinitionStruct userDefinitionStruct,
                                    UserEnablementStruct userEnablementStruct)
    {
    	this.userStruct = userStruct;
    	this.userDefinitionStruct = userDefinitionStruct;
        this.userEnablementStruct = userEnablementStruct;
    }

    public UserEventStructContainer(UserSummaryStruct summary)
    {
    	this.userSummaryStruct = summary;
    }

    public UserEventStructContainer(String userId, String userName)
    {
    	this.userId = userId;
    	this.userName = userName;
    }

    public UserStruct getUserStruct()
    {
        return this.userStruct;
    }

    public UserDefinitionStruct getUserDefinitionStruct()
    {
        return this.userDefinitionStruct;
    }

    public UserEnablementStruct getUserEnablementStruct()
    {
        return this.userEnablementStruct;
    }

    public UserSummaryStruct getUserSummaryStruct()
    {
        return this.userSummaryStruct;
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
        StringBuilder buf = new StringBuilder(15);
        buf.append(" UserId: ")
        .append(userStruct.userId);
        return buf.toString();
    }
}// end UserEventStructContainer class
