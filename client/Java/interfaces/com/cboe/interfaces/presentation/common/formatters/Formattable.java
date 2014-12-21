/********************************************************************************
 * FILE:    Formattable.java
 *
 * PACKAGE: com.cboe.interfaces.presentation.common.formatters;
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
 *
 *******************************************************************************/

package com.cboe.interfaces.presentation.common.formatters;


/********************************************************************************
 * Interface for objects that can create string representations of themselves
 * in various formats
 */
public interface Formattable
{
//*** Public Methods

    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the object
     */
    public String toString( String formatSpecifier );

}
