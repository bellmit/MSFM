//
// ------------------------------------------------------------------------
// FILE: Storage.java
// 
// PACKAGE: com.cboe.interfaces.presentation.common.storage
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.storage;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.NotSupportedException;
import com.cboe.exceptions.SystemException;
import com.cboe.interfaces.presentation.api.TimedOutException;

public interface Storage
{
    void initializeStorage() throws IOException;
    void store(String name, String content) throws IOException;
    void store(String name, Serializable content) throws IOException;
    void store(String name, byte[] content) throws IOException;
    void store(String name, Properties content) throws IOException;
    void addEntry(Object entry) throws SystemException, CommunicationException,NotFoundException,DataValidationException, NotAcceptedException, AlreadyExistsException, TimedOutException, AuthorizationException, AuthenticationException, NotSupportedException, UserException;
    void updateEntry(Object entry) throws SystemException, CommunicationException, AlreadyExistsException, DataValidationException, NotAcceptedException, TimedOutException, UserException;
    void removeEntry(Object entry) throws SystemException, CommunicationException, NotFoundException, DataValidationException, NotAcceptedException, TimedOutException, UserException;
    
    String retrieveString(String name) throws IOException;
    Object retrieveObject(String name) throws IOException;
    byte[] retrieveBytes(String name) throws IOException;
    Properties retrieveProperties(String name) throws IOException;

    void rename(String oldName, String newName) throws IOException;
    void delete(String name) throws IOException;
    void copy(String name, String copyName) throws IOException;

    boolean exists(String name) throws IOException;
    boolean isSaveAllowed();

    String[] list(String path) throws IOException;
    String[] list() throws IOException;
}
