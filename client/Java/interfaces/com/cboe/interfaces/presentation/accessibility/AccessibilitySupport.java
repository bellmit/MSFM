// -----------------------------------------------------------------------------------
// Source file: AccessibilitySupport.java
//
// PACKAGE: com.cboe.interfaces.presentation.accessibility;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.accessibility;

/**
 * Defines a contract for a class that provides AccessibilitySupport.
 */
public interface AccessibilitySupport
{
    /**
     * Determines if component is allowed for Edit
     * @return true if allowed
     */
    public boolean isAllowedForEdit();
    /**
     * Sets the flag determining if component is allowed for Edit
     */
    public void setAllowedForEdit(boolean flag);
    /**
     * Determines if component is allowed for view
     * @return true if allowed
     */
    public boolean isAllowedForView();
    /**
     * Sets the flag determining if component is allowed for View
     */
    public void setAllowedForView(boolean flag);
}
