package com.cboe.interfaces.presentation.qrm;

import org.omg.CORBA.UserException;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.exceptions.*;

/**
 * Defines a contract that a QuoteRiskManagementProfile that represents
 * a QuoteRiskManagementProfileStruct should provide.
 */
public interface QuoteRiskManagementProfile extends ProductClass
{
    /**
     * Gets the profile struct this profile is for.
     * @return QuoteRiskManagementProfileStruct
     */
    public QuoteRiskManagementProfileStruct getProfileStruct();

    /**
     * Gets the product class that this profile is for.
     */
    public ProductClass getProductClass();

    /**
     * Gets the class key from the profile
     * @return class key of class profile is for
     */
    public int getClassKey();

    /**
     * Gets the class symbol of the product class for this profile formatted as a string.
     * @return formatted string
     */
    public String getClassSymbol();

    /**
     * return true if the ProductClass is designated as a Test Class
     */
    public boolean isTestClass();

    /**
     * Gets the product type of the ProductClass.
     */
    public short getProductType();

    /**
     * Gets the underlying product from the ProductClass.
     */
    public Product getUnderlyingProduct();

    /**
     * Gets the listing state of the ProductClass.
     */
    public short getListingState();

    /**
     * Gets the created time of the ProductClass.
     */
    public DateTimeStruct getCreatedTime();

    /**
     * Gets the last modified time of the ProductClass.
     */
    public DateTimeStruct getLastModifiedTime();

    /**
     * Gets the activation time of the ProductClass.
     */
    public DateStruct getActivationDate();

    /**
     * Gets the inactivation time of the ProductClass.
     */
    public DateStruct getInactivationDate();

    /**
     * Gets the primary exchange of the ProductClass.
     */
    public String getPrimaryExchange();

    /**
     * Gets the EPW structs of the ProductClass.
     */
    public EPWStruct[] getEPWValues();

    /**
     * Gets the EPW fast market multiplier of the ProductClass.
     */
    public double getEPWFastMarketMultiplier();

    /**
     * Gets the ProductDescriptionStruct of the ProductClass.
     */
    public ProductDescriptionStruct getProductDescription();

    /**
     * Determines if this profile represents the default profile
     * @return boolean True if default, false otherwise
     */
    public boolean isDefaultProfile();

    /**
     * Gets the volume threshold of this profile
     * @return volume threshold
     */
    public int getVolumeThreshold();

    /**
     * Sets the volume threshold of this profile
     * @param newThreshold to set
     */
    public void setVolumeThreshold(int newThreshold);

    /**
     * Sets the time window for this profile
     * @param newTimeWindow to set
     */
    public void setTimeWindow(int newTimeWindow);

    /**
     * Gets the time window for this profile
     * @return time window
     */
    public int getTimeWindow();

    /**
     * Determines if the profile is quote risk management enabled
     * @return boolean
     */
    public boolean isQuoteRiskManagementEnabled();

    /**
     * Sets whether quote risk management is enabled for this profile
     * @param enabled
     */
    public void setQuoteRiskManagementEnabled(boolean enabled);

    /**
     * Determines if this profile has been modified.
     * @return True if modified, false otherwise.
     */
    public boolean isModified();

    /**
     * Sets whether this profile has been modified.
     * @param modified True if modified, false if NOT.
     */
    public void setModified(boolean modified);

    /**
     * Gets the user id of this profile
     * @return user id
     */
    public String getUserId();

    /**
     * Sets the user id of this profile
     * @param userId
     */
    public void setUserId(String userId);

    /**
     * Returns the string representation of this profile
     */
    public String toString();

    /**
     * Removes this profile from the collection of profiles.
     */
    public void remove() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;

    /**
     * Saves this profile to the collection of profiles.
     */
    public void save() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;

    public boolean isAllSelected();

    public QuoteRiskManagementProfile cloneForAnotherUser(UserQuoteRiskManagementProfile newUserQrmProfile, String userId);
}