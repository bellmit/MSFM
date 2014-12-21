//
// -----------------------------------------------------------------------------------
// Source file: SessionClearingAcronymImpl.java
//
// PACKAGE: com.cboe.internalPresentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import com.cboe.idl.user.SessionClearingAcronymStruct;

import com.cboe.interfaces.internalPresentation.user.SessionClearingAcronym;

public class SessionClearingAcronymImpl implements SessionClearingAcronym
{
    private SessionClearingAcronymStruct struct;

    public SessionClearingAcronymImpl()
    {
        super();
        struct = new SessionClearingAcronymStruct();
        struct.sessionName = "";
        struct.sessionClearingAcronym = "";
    }

    public SessionClearingAcronymImpl(SessionClearingAcronymStruct struct)
    {
        this();
        this.struct = struct;
    }

    public SessionClearingAcronymImpl(String sessionName, String acronym)
    {
        this();
        setSessionName(sessionName);
        setClearingAcronym(acronym);
    }

    public int hashCode()
    {
        return (struct.sessionName + struct.sessionClearingAcronym).hashCode();
    }

    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);

        if(!isEqual)
        {
            if(otherObject instanceof SessionClearingAcronym)
            {
                SessionClearingAcronym castedObj = ( SessionClearingAcronym ) otherObject;

                if(getSessionName().equals(castedObj.getSessionName()) &&
                        getClearingAcronym().equals(castedObj.getClearingAcronym()))
                {
                    isEqual = true;
                }
            }
        }

        return isEqual;
    }

    public int compareTo(Object otherObject)
    {
        int result;

        if( otherObject instanceof SessionClearingAcronym)
        {
            result = getSessionName().compareTo(((SessionClearingAcronym)otherObject).getSessionName());
        }
        else
        {
            result = 1;
        }

        return result;
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionClearingAcronymStruct newStruct = new SessionClearingAcronymStruct(new String(struct.sessionName), new String(struct.sessionClearingAcronym));
        return new SessionClearingAcronymImpl(newStruct);
    }

    public String getClearingAcronym()
    {
        return struct.sessionClearingAcronym;
    }

    public String getSessionName()
    {
        return struct.sessionName;
    }

    public void setClearingAcronym(String acronym)
    {
        if(acronym == null)
        {
            throw new IllegalArgumentException("acronym may not be null");
        }
        struct.sessionClearingAcronym = acronym;
    }

    public void setSessionName(String sessionName)
    {
        if( sessionName == null )
        {
            throw new IllegalArgumentException("sessionName may not be null");
        }
        struct.sessionName = sessionName;
    }

    /**
     * For translator compatibility
     * @return underlying struct
     * @deprecated for translator compatibility only
     */
    public SessionClearingAcronymStruct toStruct()
    {
        return struct;
    }
}
