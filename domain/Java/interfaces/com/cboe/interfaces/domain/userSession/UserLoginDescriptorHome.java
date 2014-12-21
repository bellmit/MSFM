package com.cboe.interfaces.domain.userSession;

import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.userSession.*;
import java.util.*;

/**
 *  @author Steven Sinclair
 */
public interface UserLoginDescriptorHome
{
	public static String HOME_NAME="UserLoginDescriptorHome";

	public Collection findAllDescriptors()
		throws SystemException;

	public UserLoginDescriptor findUserLoginDescriptor(UserSessionDescriptor userSession, String sourceComponent)
		throws NotFoundException, SystemException;

	public UserLoginDescriptor findUserLoginDescriptorForKey(int userLoginKey)
		throws NotFoundException, SystemException;

	public UserLoginDescriptor create(UserSessionDescriptor userSession, String sourceComponent)
		throws AlreadyExistsException, TransactionFailedException, SystemException;

	public void remove(UserLoginDescriptor userLogin)
		throws SystemException, TransactionFailedException;
}

