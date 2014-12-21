package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/ClearingAcronym.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.util.ExceptionBuilder;
import com.cboe.idl.user.SessionClearingAcronymStruct;
import com.cboe.exceptions.DataValidationException;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A ClearingAcronym for a Broker User and sessionName
 *
 * @author Mei Wu
 */
public class ClearingAcronym extends PersistentBObject
{

    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "clearing_acr";

    /**
     * Associated user
     */
    private AcronymUserImpl user;

    /**
     * session Name
     */
    private String sessionName;

    /**
     * clearingAcronym
     */
    private String clearingAcr;

    /*
     * Fields for JavaGrinder.
     */
    private static Field _user;
    private static Field _clearingAcr;
    private static Field _sessionName;

    /*
     * JavaGrinder attribute descriptions.
     */
    private static Vector classDescriptor;

    /*
     * Initialize fields
     */
    static
    {
        try
        {
            _user = ClearingAcronym.class.getDeclaredField("user");
            _clearingAcr = ClearingAcronym.class.getDeclaredField("clearingAcr");
            _sessionName = ClearingAcronym.class.getDeclaredField("sessionName");
        }
        catch (Exception e)
        {
            System.out.println("Unable to initialize JavaGrinder fields for ClearingAcronym: " + e);
        }
    }

    /**
     * Constructs a new ClearingAcronym.  This constructor is needed for queries.
     */
    public ClearingAcronym()
    {
    }

    /**
     * Constructs a new ClearingAcronym
     *
     * @param user
     * @param clearingAcr
     * @param sessionName
     */
    public ClearingAcronym( AcronymUserImpl user, String clearingAcr, String sessionName )
    {
        super();
        setUser( user );
        setClearingAcr( clearingAcr );
        setSessionName( sessionName );
    }

    /**
     * Constructs a new ClearingAcronym
     *
     * @param user
     */
    public ClearingAcronym( AcronymUserImpl user )
    {
        super();
        setUser( user );
    }

    /**
     * Gets the user
     */
    public AcronymUserImpl getUser()
    {
        return (AcronymUserImpl) editor.get(_user, user);
    }

    /**
     * Gets sub-account name.
     */
    public String getClearingAcr()
    {
        return (String) editor.get(_clearingAcr, clearingAcr);
    }

    /**
     * Gets session  name.
     */
    public String getSessionName()
    {
        return (String) editor.get(_sessionName, sessionName);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public void initDescriptor()
    {
        synchronized (ClearingAcronym.class)
        {
            if (classDescriptor != null)
                return;
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getForeignRelation(AcronymUserImpl.class, "userKey", _user));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("clearingAcr", _clearingAcr));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("sessionName", _sessionName));
            classDescriptor = tempDescriptor;
        }
    }

    /**
    * Needed to define table name and the description of this class.
    */
    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
        if (classDescriptor == null)
            initDescriptor();
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

    /**
     * Sets the user.
     *
     * @param newUser
     */
    public void setUser(AcronymUserImpl newUser)
    {
        editor.set(_user, newUser, user);
    }

    /**
     * Sets clearing Acronym
     *
     * @param newClearingAcr of the account name
     */
    protected void setClearingAcr(String newClearingAcr)
    {
        editor.set(_clearingAcr, newClearingAcr, clearingAcr);
    }
    /*
    * @param sessionName - the session name of the ClearingAcronym.
    */
    protected void setSessionName(String sessionName)
    {
        editor.set(_sessionName, sessionName, this.sessionName);
    }
    /**
     * Formats this assignment as a string.
     */
    public String toString()
    {
        return getUser().loggableName() + ":" + getSessionName() + ":" + getClearingAcr();
    }


    /**
     * This method allows me to get arounds security problems with updating
     * and object from a generic framework.
     */
    public void update(boolean get, Object[] data, Field[] fields)
    {
        for (int i = 0; i < data.length; i++)
        {
            try
            {
                if (get)
                    data[i] = fields[i].get(this);
                else
                    fields[i].set(this, data[i]);
            }
            catch (IllegalAccessException ex)
            {
                System.out.println(ex);
            }
            catch (IllegalArgumentException ex)
            {
                System.out.println(ex);
            }
        }
    }

    /**
     * Sets all values for the user from the definition struct.
     *
     * @param sessionClearingAcronymStruct
     * @exception DataValidationException if validation checks fail
     */
    public void fromStruct(SessionClearingAcronymStruct sessionClearingAcronymStruct)
            throws DataValidationException
    {

        setClearingAcr( sessionClearingAcronymStruct.sessionClearingAcronym);

        if (!getUser().isValidSessionName(sessionClearingAcronymStruct.sessionName))
        {
            String message = "the sessionName of ClearingAcronym is INVALID: " + sessionClearingAcronymStruct.sessionName +
                             ", defined for UserId: " + getUser().loggableName() +
                             ", with (clearingAcronym: " + sessionClearingAcronymStruct.sessionClearingAcronym + ")";
            Log.alarm(this, message);
            throw ExceptionBuilder.dataValidationException(message, 0);
        }
        setSessionName(sessionClearingAcronymStruct.sessionName);

    }

    /**
     * Creates a SessionClearingAcronymStruct
     *
     */
    public SessionClearingAcronymStruct toStruct()
    {
        SessionClearingAcronymStruct cs = new SessionClearingAcronymStruct();
        cs.sessionClearingAcronym= getClearingAcr();
        cs.sessionName = getSessionName();
        return cs;
    }

}

