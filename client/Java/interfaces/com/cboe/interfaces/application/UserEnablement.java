

package com.cboe.interfaces.application;

import java.util.Set;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.user.UserEnablementStruct;

public interface UserEnablement {
    // FIXME - KAK  after full single acr rollout, this will no longer be needed
    void acceptUserEnablementUpdate(PropertyGroupStruct propertyGroupStruct, UserEnablementStruct userEnablementStruct);

    void acceptUserEnablementUpdate(PropertyGroupStruct propertyGroupStruct);

    public void verifyUserEnablement(String sessionName, int classKey, int operationType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserEnablementForSession(String sessionName, int operationType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserEnablementForProduct(String sessionName, int productKey, short operationType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserMDXEnabled()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserTradingFirmEnabled()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public String getUserMDXEnablmentKey();
    public String getUserTradingFirmEnablementKey();
    public String getUserEnablementKey();
    public String getUserTestClassesKey();
    // FIXME - KAK required for single acr rollout
    public boolean setPropertyUpdatesOnly(boolean propertyOnly);

    /**
     * Returns the session names for which the user has some enablements.
     *
     * @return Set of session names
     *
     * @author Gijo Joseph
     */
    public Set<String> getSessionsWithAnyEnablements();
}
