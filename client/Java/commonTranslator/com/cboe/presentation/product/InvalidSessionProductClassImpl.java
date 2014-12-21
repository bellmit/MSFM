package com.cboe.presentation.product;
// -----------------------------------------------------------------------------------
// Source file: InvalidSessionProductClassImpl
//
// PACKAGE: com.cboe.presentation.product
// 
// Created: Mar 10, 2006 8:22:30 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiConstants.ClassStates;

import com.cboe.domain.util.ClientProductStructBuilder;

class InvalidSessionProductClassImpl extends SessionProductClassImpl
{
    private final static String NO_SESSION = "No Session";
    
    private String toStringValue;
    
    /**
     * Constructor
     * @param classStruct to represent
     */
    InvalidSessionProductClassImpl(SessionClassStruct classStruct)
    {
        super(classStruct);
    }
    
    /**
     * Constructor
     * @param sessionName to represent
     * @param classStruct to represent
     */
    InvalidSessionProductClassImpl(String sessionName, ClassStruct classStruct)
    {
        SessionClassStruct sStruct = buildSessionClassStruct();
        
        if (sessionName.length() > 0)
        {
            sStruct.sessionName = sessionName;
            sStruct.underlyingSessionName = sessionName;
        }
        sStruct.classStruct = classStruct;
        
        updateFromStruct(sStruct);
    }
    
    InvalidSessionProductClassImpl(int classKey)
    {
        SessionClassStruct sStruct = buildSessionClassStruct();
        
        sStruct.classStruct = ClientProductStructBuilder.buildClassStruct();
        sStruct.classStruct.classKey    = classKey;
        sStruct.classStruct.classSymbol = "[" + classKey + "]";

        updateFromStruct(sStruct);
    }
    

    /**
     * Default constructor.
     */
    InvalidSessionProductClassImpl()
    {
        super(buildSessionClassStruct());
    }

    /**
     * Determines if this ProductClass is invalid, either it has been marked inactive or has been removed from the
     * system, but some data structures still reference the classkey
     */
    public boolean isValid()
    {
        return false;
    }

    private static SessionClassStruct buildSessionClassStruct()
    {
        SessionClassStruct sc = new SessionClassStruct();
        
        sc.classStruct = ClientProductStructBuilder.buildClassStruct();
        sc.classState  = ClassStates.NOT_IMPLEMENTED;
        sc.classStateTransactionSequenceNumber = 0;
        sc.eligibleSessions = new String[0];
        sc.sessionName = NO_SESSION;
        sc.underlyingSessionName = sc.sessionName;
        
        return sc;
    }

    public String toString()
    {
        if (toStringValue == null)
        {        
            toStringValue = super.toString() + "(Invalid)";
        }
        
        return toStringValue;
    }
}
