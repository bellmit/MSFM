package com.cboe.interfaces.domain.user;

import com.cboe.exceptions.*;
import com.cboe.idl.user.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.interfaces.domain.user.*;
import java.util.*;

/**
 * Home for converting UserEnablementElement's to/from the corresponding struct.
 *
 *  @author Steven Sinclair
 */
public interface UserEnablementHome
{
	public static final String HOME_NAME = "UserEnablementHome";

	/**
	 * Get the user enablement
	 */
	public UserEnablementStruct getUserEnablement(String userId) throws SystemException;

	/**
	 * Get the user enablement
	 */
	public UserEnablementStruct[] getAllUserEnablements() throws SystemException;

	/**
	 *  Set the user enablement 
	 */
	public void setUserEnablement(UserEnablementStruct enablementStruct) throws TransactionFailedException;
}
