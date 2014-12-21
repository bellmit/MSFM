package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.exceptions.*;

/**
 * This is the common interface for the Current Market Home
 * @author John Wickberg
 */
public interface LinkageAdminMessageTextServiceHome {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "LinkageAdminMessageTextServiceHome";
	/**
	 * Returns a reference to the LinkageAdminMessageQueue.
	 *
	 *    @return reference to LinkageAdminMessageQueue service
	 *
	 */
	public LinkageAdminMessageTextService find();
  
}

