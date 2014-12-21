package com.cboe.infra.presentation.filter;

//------------------------------------------------------------------------------------------------------------------
// FILE:    Filter.java
//
// PACKAGE: com.cboe.presentation.common
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------

/**
 *
 *  @author C Goodacre
 *  Creation date:
 *  @version
 */

public interface Filter
{

    /**
     * This methods tests the supplied element against
     * some set of pre-configured criteria and either
     * accepts or denies the candidate.
     * @return true If candidate passes tests, false otherwise
     * or if candidate is null.
     */
    public boolean accept( Object candidate );
}
