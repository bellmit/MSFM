package com.cboe.domain.util;

import com.cboe.idl.cmiUser.*;

/**
 * Used to build default valued CORBA structs in cmiUser.IDL.
 *
 * @author John Wickberg
 */
public class ClientUserStructBuilder
{
/**
 * All methods are static, no instance needed.
 *
 * @author John Wickberg
 */
protected ClientUserStructBuilder()
{
	super();
}
/**
 * Creates a default valued user struct.
 *
 * @return struct with default values
 *
 * @author John Wickberg
 */
public static SessionProfileUserStruct buildSessionProfileUserStruct()
{
    SessionProfileUserStruct aStruct = new SessionProfileUserStruct();
    aStruct.firm = StructBuilder.buildExchangeFirmStruct("", "");
    aStruct.fullName = "";
    aStruct.role = ' ';
    aStruct.userId = "";
    aStruct.userAcronym = StructBuilder.buildExchangeAcronymStruct("", "");
    aStruct.assignedClasses = new int[0];
    aStruct.defaultProfile = new SessionProfileStruct();
    aStruct.defaultProfile.classKey =0;
    aStruct.defaultProfile.subAccount = "";
    aStruct.defaultProfile.account = "";
    aStruct.defaultProfile.executingGiveupFirm = StructBuilder.buildExchangeFirmStruct("", "");
    aStruct.defaultProfile.sessionName =  "ALL_SESSIONS";
    aStruct.defaultProfile.isAccountBlanked = false;
    aStruct.defaultProfile.originCode = ' ';
    aStruct.executingGiveupFirms = new ExchangeFirmStruct[0];
    aStruct.accounts = new AccountStruct[0];
    aStruct.defaultSessionProfiles = new SessionProfileStruct[0];
    aStruct.sessionProfilesByClass = new SessionProfileStruct[0];
    aStruct.dpms = new DpmStruct[0];

	return aStruct;
}

public static UserStruct buildUserStruct()
{
	UserStruct aStruct = new UserStruct();
    aStruct.firm = StructBuilder.buildExchangeFirmStruct("", "");
    aStruct.fullName = "";
    aStruct.role = ' ';
    aStruct.userId = "";
    aStruct.userAcronym = StructBuilder.buildExchangeAcronymStruct("", "");
    aStruct.assignedClasses = new int[0];
    aStruct.defaultProfile = new ProfileStruct(0,
                                               "",
                                               "",
                                               StructBuilder.buildExchangeFirmStruct("", ""));
    aStruct.executingGiveupFirms = new ExchangeFirmStruct[0];
    aStruct.accounts = new AccountStruct[0];
    aStruct.profilesByClass = new ProfileStruct[0];

    aStruct.dpms = new DpmStruct[0];

	return aStruct;
}
/**
 * Creates a default valued user PreferenceStruct.
 *
 * @return struct with default values
 *
 * @author Connie Feng
 */
public static PreferenceStruct buildPreferenceStruct()
{
	PreferenceStruct aStruct = new PreferenceStruct();
	aStruct.name = "";
	aStruct.value = "";
	return aStruct;
}

/**
 * converts the PreferenceStruct into string format for each field
 *
 * @return String
 *
 * @author Connie Feng
 */
public static String toString(PreferenceStruct prefStruct)
{
    StringBuilder buf = new StringBuilder(20);

    buf.append("Name: ")
    .append(prefStruct.name)
    .append(" Value: ")
    .append(prefStruct.value);

    return buf.toString();
}
}
