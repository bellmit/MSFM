package com.cboe.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/domain/user/Profile.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

import java.lang.reflect.Field;
import java.util.Vector;

import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import com.cboe.interfaces.domain.user.UserIdentifier;

/**
 * Represents a userid, which overlays a UserImpl. This is to allow a single
 * acronym+exchange to have multiple logins.
 * 
 * @author Steven Sinclair
 */
public class UserIdImpl extends PersistentBObject implements UserIdentifier
{

    /**
     * Table name used for object mapping.
     */
    public static final String TABLE_NAME = "login_userid";

    private int userKey;
    private String userId;
    private boolean active;

    /*
     * Fields for JavaGrinder.
     */
    private static Field _userKey;
    private static Field _userId;
    private static Field _active;

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
            _userKey = UserIdImpl.class.getDeclaredField("userKey");
            _userId = UserIdImpl.class.getDeclaredField("userId");
            _active = UserIdImpl.class.getDeclaredField("active");
            _userKey.setAccessible(true);
            _userId.setAccessible(true);
            _active.setAccessible(true);
        }
        catch (Exception e)
        {
            System.out.println("Unable to initialize JavaGrinder fields for Profile: " + e);
        }
    }

    /**
     * Constructs a new Profile. This constructor is needed for queries.
     */
    public UserIdImpl()
    {
        setUsing32bitId(true);
    }

    public int getAcronymUserKey()
    {
        return editor.get(_userKey, userKey);
    }

    public int getUserIdKey()
    {
        return this.getObjectIdentifierAsInt();
    }

    public String getUserId()
    {
        return (String) editor.get(_userId, userId);
    }

    public boolean isActive()
    {
        return editor.get(_active, active);
    }

    /**
     * Describe how this class relates to the relational database.
     */
    public void initDescriptor()
    {
        synchronized (UserIdImpl.class)
        {
            if (classDescriptor != null)
                return;
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("user_key", _userKey));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("userid", _userId));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("active", _active));
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

    public void setAcronymUserKey(int value)
    {
        editor.set(_userKey, value, this.userKey);
    }

    public void setUserIdKey(int value)
    {
        this.setObjectIdentifierFromInt(value);
    }

    public void setUserId(String value)
    {
        editor.set(_userId, value, this.userId);
    }

    public void setIsActive(boolean value)
    {
        editor.set(_active, value, this.active);
    }

    /**
     * Formats this assignment as a string.
     * <p>Example: <pre> ['ABC1', #4837275, acr#8742761, active=T]
     */
    public String toString()
    {
        final String userIdKey = (super.objectIdentifier==null) 
            ? "{newUserid}" 
            : Integer.toString(getUserIdKey());

        return "['" + getUserId() + "' #" + userIdKey + ", acr#" + getAcronymUserKey() 
            + ", active=" + (isActive() ? "T" : "F") + ']';
    }
}

