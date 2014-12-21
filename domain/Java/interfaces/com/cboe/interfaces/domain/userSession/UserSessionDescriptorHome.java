package com.cboe.interfaces.domain.userSession;

import com.cboe.exceptions.*;
import java.util.*;

/**
 *  @author Steven Sinclair
 */
public interface UserSessionDescriptorHome
{
	public static String HOME_NAME="UserSessionDescriptorHome";

	public Collection findAllDescriptors()
		throws SystemException;

	public UserSessionDescriptor findUserSessionDescriptor(String userId)
		throws NotFoundException, SystemException;

	public UserSessionDescriptor findUserSessionDescriptorForKey(int userSessionKey)
		throws NotFoundException, SystemException;

	public UserSessionDescriptor create(String userId)
		throws AlreadyExistsException, TransactionFailedException, SystemException;

	public void remove(UserSessionDescriptor userSession)
		throws SystemException, TransactionFailedException;
}

