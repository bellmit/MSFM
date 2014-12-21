//------------------------------------------------------------------------------------------------------------------
// FILE:    ValidationResultImpl.java
//
// PACKAGE: com.cboe.presentation.validation
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
//-------------------------------------------------------------------------------------------------------------------

package com.cboe.presentation.validation;

import com.cboe.interfaces.presentation.validation.ValidationResult;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;

import java.util.List;
import java.util.ArrayList;

public class ValidationResultImpl implements ValidationResult
{
    private int errorCode = ValidationErrorCodes.NO_ERROR;
    private String errorMessage = null;
    private List<String> confirmationMessages = new ArrayList<String>();

   /**
    * Default Constructor ...
    * @author Alex Brazhnichenko
    */
    public ValidationResultImpl()
    {
        super();
    }

   /**
    * Constructor ...
    * @param errorCode int
    * @param errorMessage String
    * @author Alex Brazhnichenko
    */
    public ValidationResultImpl(int errorCode, String errorMessage)
    {
        super();
        setErrorCode(errorCode);
        setErrorMessage(errorMessage);
    }

    /**
     * Setter method for errorCode member variable
     * @author Alex Brazhnichenko
     * @param newErrorCode int
     */
     public void setErrorCode(int newErrorCode)
     {
         errorCode = newErrorCode;
     }

     /**
      * Getter method for errorCode member variable
      * @author Alex Brazhnichenko
      * @return int
      */
      public int  getErrorCode()
      {
          return errorCode;
      }

    /**
     * Setter method for errorMessage member variable
     * @author Alex Brazhnichenko
     * @param newErrorMessage String
     */
     public void setErrorMessage(String newErrorMessage)
     {
         errorMessage = newErrorMessage;
     }

     /**
      * Getter method for errorCode member variable
      * @author Alex Brazhnichenko
      * @return String
      */
      public String  getErrorMessage()
      {
          return errorMessage;
      }

     /**
      * Convinience method to check if ValidationResult is positive.
      *
      * @author Alex Brazhnichenko
      * @return boolean
      */
      public boolean isValid()
      {
          return (errorCode == ValidationErrorCodes.VALID);
      }

    /**
     * Return true if the order is valid, but requires additional user confirmation (e.g., "are you
     * REALLY REALLY sure you mean to do that??")
     */
    public boolean requiresAdditionalConfirmation()
    {
        return getConfirmationMessages().size() > 0;
    }

    /**
     * Add a confirmation message that can be displayed back to the user when asking them if they
     * really want to proceed.
     * @param newConfirmationMessage
     */
    public void addConfirmationMessage(String newConfirmationMessage)
    {
        confirmationMessages.add(newConfirmationMessage);
    }

    /**
     * Get all confirmation message that have been added, which should be displayed back to the
     * user when asking them if they really want to proceed.
     */
    public List<String> getConfirmationMessages()
    {
        return confirmationMessages;
    }
}