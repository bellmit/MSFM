package com.cboe.interfaces.domain.user;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

/**
 * 
 */
public interface UserSmartCacheHome {

	public final static String HOME_NAME = "UserSmartCacheHome";
    
    public UserSmartCache create(String userId, int userKey) throws AlreadyExistsException, DataValidationException;
	public UserSmartCache[] findAll();
	public UserSmartCache findByUserKey(int userKey) throws NotFoundException;
	public UserSmartCache findByUserId(String userId) throws NotFoundException;
    public boolean shouldUpdateActivityTime(String userId) throws NotFoundException;
    public void updateActivityTime(String userId) throws NotFoundException;
    public void delete(String userId) throws SystemException;
    public long getUserLocalCacheCount() throws SystemException;
}
