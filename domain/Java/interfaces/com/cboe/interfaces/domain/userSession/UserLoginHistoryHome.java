package com.cboe.interfaces.domain.userSession;

import com.cboe.exceptions.*;
import java.util.*;

/**
 *  @author Steven Sinclair
 */
public interface UserLoginHistoryHome
{
	public static String HOME_NAME="UserLoginHistoryHome";

	public Collection findAll()
		throws SystemException;

	public UserLoginHistory[] findUserLoginHistory(Date fromDate, Date toDate)
		throws SystemException;

	public UserLoginHistory[] findUserLoginHistory(int sessionKey, Date fromDate, Date toDate)
		throws SystemException;

	public UserLoginHistory[] findUserLoginHistory(String userId, Date fromDate, Date toDate)
		throws SystemException;

	public UserLoginHistory create(int sessionKey, String userId, String sourceComponent, int action, Date time, String description)
		throws TransactionFailedException, SystemException;
}

