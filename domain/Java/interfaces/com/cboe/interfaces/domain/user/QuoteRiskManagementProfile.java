
package com.cboe.interfaces.domain.user;

import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;

/**
 *  A profile for a user with a MM-like role (ie, a user who submits quotes).
 *  The profile describes a quoting preference for a particular product class
 *  for a given user.  These profiles are accessed via the User interface.
 *
 *  @author Steven Sinclair
 */
public interface QuoteRiskManagementProfile
{
	/**
	 *  Use the data elements of the profile to create & populate a
	 *  struct.
	 */
	public QuoteRiskManagementProfileStruct toStruct();

	/**
	 *  Set the values (except for User) using the struct
	 */
	public void fromStruct(QuoteRiskManagementProfileStruct struct);

	/**
	 *  Get the user associated with this profile
	 */
	public QRMUser getUser();

	/**
	 *  Set the user assoicated with this profile
	 */
	public void setUser(QRMUser user);

	/**
	 *  Get the class key for the product class that this profile is targeting.
	 */
	public int getClassKey();

	/**
	 *  Set the class key for the product class that this profile is targeting.
	 */
	public void setClassKey(int classKey);

	/**
	 *  Get the volume threshold value for this profile.
	 */
	public int getVolumeThreshold();

	/**
	 *  Set the volume threshold value for this profile.
	 */
	public void setVolumeThreshold(int volumeThreshold);

	/**
	 *  Get the time window in milliseconds.
	 */
	public int getTimeWindowMillis();

	/**
	 *  Set the time window in milliseconds.
	 */
	public void setTimeWindowMillis(int timeWindowMillis);

	/**
	 *  Get the enabled value for this profile (ie, "is this profile enabled?")
	 */
	public boolean getProfileEnabled();

	/**
	 *  Set this profile to be enabled or disabled.
	 */
	public void setProfileEnabled(boolean isEnabled);
	
	/**
	 *  This method was created to pass information to be printed in log for QRM report
	 */
	public void createQRMLogMessage(String status);
}
