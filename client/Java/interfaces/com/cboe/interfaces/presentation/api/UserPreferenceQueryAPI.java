package com.cboe.interfaces.presentation.api;

import com.cboe.idl.cmiUser.*;
import com.cboe.exceptions.*;

public interface UserPreferenceQueryAPI
{
    /**
     * setUserPreferences sets the user preferences contained within the given
     * sequence of preference structs.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param preferenceSequence the user preferences to set.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void setUserPreferences(PreferenceStruct[] preferenceSequence)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Removes the user preferences contained within the given sequence of
     * preference structs.
     *
     * @param preferenceSequence the user preferences to remove.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreference(PreferenceStruct[] preferenceSequence)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all user preferences for this user.
     *
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllUserPreferences()
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all user preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getUserPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Removes all user preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void removeUserPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all system preferences for this user.
     *
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getAllSystemPreferences()
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
     * Gets all system preferences for this user that begin with the given prefix.
     *
     * @param prefix a string containing the requested preferences name prefix.
     * @return a sequence of preference structs.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public PreferenceStruct[] getSystemPreferencesByPrefix(String prefix)
           throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
