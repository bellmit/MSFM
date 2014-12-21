//------------------------------------------------------------------------------------------------------------------
// FILE:    ValidationResult.java
//
// PACKAGE: com.cboe.interfaces.presentation.validation
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
//-------------------------------------------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.validation;

import java.util.List;

public interface ValidationResult
{
    public void setErrorCode(int newErrorCode);
    public int getErrorCode();
    public void setErrorMessage(String newErrorMessage);
    public String getErrorMessage();
    public boolean isValid();

    /**
     * Return true if the data is valid, but requires additional user confirmation (e.g., "are you
     * REALLY REALLY sure you mean to do that??").
     *
     * If true, the messages returned by getConfirmationMessages() should be displayed to the user
     * before proceeding.
     */
    boolean requiresAdditionalConfirmation();

    /**
     * Add a confirmation message to be displayed back to the user when asking them if they really
     * want to proceed.
     * @param newConfirmationMessage
     */
    void addConfirmationMessage(String newConfirmationMessage);

    /**
     * Get all confirmation messages that should be displayed back to the user when asking them if
     * they really want to proceed.
     */
    List<String> getConfirmationMessages();
}