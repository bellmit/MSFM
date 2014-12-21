//
// -----------------------------------------------------------------------------------
// Source file: UserQuoteRiskManagementProfile.java
//
// PACKAGE: com.cboe.interfaces.presentation.qrm;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.qrm;

import java.util.Observer;
import java.beans.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.exceptions.*;

/**
 * Defines a contract that a UserQuoteRiskManagementProfile that represents
 * a UserQuoteRiskManagementProfileStruct should provide.
 */
public interface UserQuoteRiskManagementProfile extends Observer
{
    public final static String USER_QRM_PROFILE_ENABLED_EVENT = "USER_QRM_PROFILE_ENABLED_EVENT";
    public final static String QRM_PROFILE_REMOVED_EVENT = "QRM_PROFILE_REMOVED_EVENT";
    public final static String QRM_PROFILE_ADDED_UPDATED_EVENT = "QRM_PROFILE_ADDED_UPDATED_EVENT";

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public QuoteRiskManagementProfile getDefaultProfile();

    public QuoteRiskManagementProfile[] getQRMProfiles();

    public QuoteRiskManagementProfileStruct[] getQRMProfileStructs();

    public String getUserId();

    public boolean isModified();

    public void setModified(boolean newModified);

    public boolean isQRMEnabled();

    public void put(ProductClass element);

    public void remove(ProductClass element);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public void save() throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;

    public void setDefaultProfile(QuoteRiskManagementProfile newDefaultProfile);

    public void setQRMEnabled(boolean newQRMEnabled);

    public void setUserId(String newUserId);

    public void clearProfileUpdates();

    public void setQRMProfile(QuoteRiskManagementProfile profile);

    public void setQRMProfiles(QuoteRiskManagementProfile[] profiles);

    public void replaceQRMProfiles(QuoteRiskManagementProfile[] profiles);

    public void mergeQRMProfiles(QuoteRiskManagementProfile[] profiles);

    public void initialize();

    public boolean isInitialized();
}