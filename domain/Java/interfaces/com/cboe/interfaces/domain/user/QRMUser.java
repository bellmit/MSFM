package com.cboe.interfaces.domain.user;

// Source file: d:/sources/com/cboe/interfaces/domain/user/User.java

import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;

import java.util.Vector;
import com.cboe.exceptions.*;

/**
 * QRM-related methods related to a user.
 *
 * @author Steven Sinclair
 */
public interface QRMUser
{
	/**
	 *  Determine if the quote risk management elements of this user are enabled
	 *
	 *  @return boolean - true IIF the quote risk management is enabled
	 *  @author Steven Sinclair
	 */
	public boolean getQuoteRiskManagementEnabled();

	/**
	 *  Get a vector of the QuoteRiskManagementProfiles.
	 *
	 *  @return Vector - a list of QuoteRiskManagementProfile objects
	 *  @author Steven Sinclair
	 */
	public Vector getQuoteRiskManagementProfileVector();

	/**
	 *  Get a vector of the QuoteRiskManagement profiles as an array.
	 *
	 *  @author Steven Sinclair
	 */
	public QuoteRiskManagementProfile[] getQuoteRiskManagementProfiles();

	/**
	 *  Set the status of the QuoteRiskManagement enablement.
	 *
	 *  @param aValue - true/false: set "is QuoteRiskManagement enabled"
	 *  @author Steven Sinclair
	 */
	public void setQuoteRiskManagementEnabled(boolean aValue);

	/**
	 *  Add a profile to the vector of QuoteRiskManagementProfiles based on the values of the
	 *  given struct
	 *
	 *  @param newProfileStruct - the QuoteRiskManagementProfileStruct to use to add another
	 *		QuoteRiskManagementProfile instance to this User object.
	 *  @author Steven Sinclair
	 */
	public void addQuoteRiskManagementProfile(QuoteRiskManagementProfileStruct newProfileStruct);

	/**
	 *  Explicitely set the profile vector.
	 *
	 *  @param Vector - a vector QuoteRiskManagementProfileImpl instances.
	 *  @author Steven Sinclair
	 */
	public void setQuoteRiskManagementProfileVector(Vector newProfiles) throws TransactionFailedException;

	/**
	 * Get a specific QuoteRiskManagementProfile instance for a given class.
	 *
	 *  @return QuoteRiskManagementProfile - null if not found
	 *  @author Steven Sinclair
	 */
	public QuoteRiskManagementProfile getQuoteRiskManagementProfileForClass(int classKey);

	/**
	 * Remove a profile with the given classKey as it's getClassKey() value.
	 * Simply returns if an apropriate profile was not found.
	 *
	 *  @author Steven Sinclair
	 */
	public void removeQuoteRiskManagementProfile(int classKey) throws TransactionFailedException;

	/**
	 * Update the QuoteRiskManagement profile list based on the structs given in the array
	 * parameter.  This update will add/remove/modify existing
	 * QuoteRiskManagementProfile instances to maximize efficiency. This list can include the
	 * default profile.
	 *
	 *  @param profileStructs - the array of structs to use in defining the
	 *    upated QuoteRiskManagementProfile list.
	 *  @author Steven Sinclair
	 */
	public void updateQuoteRiskManagementProfiles(QuoteRiskManagementProfileStruct[] profileStructs) throws TransactionFailedException;

	/**
	 * Return a UserQuoteRiskManagementProfileStruct describing all QuoteRiskManagement-related information
	 * in this User object.
	 *
	 *  @return UserQuoteRiskManagementProfileStruct - the quote risk profile information for
	 *     this user.  The struct may include null values.
	 *  @author Steven Sinclair
	 */
	public UserQuoteRiskManagementProfileStruct toQuoteRiskStruct();
    
    public int getAcronymUserKey();
    
    public String getAcronym();
}
