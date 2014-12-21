/*
 * Created on Dec 27, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.common.storage;

import java.io.Serializable;
import java.util.Properties;

public class DefaultRemoteStorage extends AbstractStorage
{

    public DefaultRemoteStorage()
    {
        super();
    }

    public void initializeStorage()
    {
    }

    public void store(String name, String content)
    {
        throw new UnsupportedOperationException("store(String name, String content) is not supported by this API.");
    }

    public void store(String name, Serializable content)
    {
        throw new UnsupportedOperationException("store(String name, Serializable content) is not supported by this API.");
    }

    public void store(String name, byte[] content)
    {
        throw new UnsupportedOperationException("store(String name, byte[] content) is not supported by this API.");
    }

    public void store(String name, Properties content)
    {
        throw new UnsupportedOperationException("store(String name, Properties content) is not supported by this API.");
    }

    public String retrieveString(String name)
    {
        throw new UnsupportedOperationException("retrieveString(String name) is not supported by this API.");
    }

    public Object retrieveObject(String name)
    {
        return null;
    }

    public byte[] retrieveBytes(String name)
    {
        throw new UnsupportedOperationException(" retrieveObject(String name) is not supported by this API.");
    }

    public Properties retrieveProperties(String name)
    {
        return new Properties();
    }

    public void rename(String oldName, String newName)
    {
        throw new UnsupportedOperationException("rename is not supported by this API.");
    }

    public void delete(String name)
    {
        throw new UnsupportedOperationException("delete is not supported by this API.");
    }

    public void copy(String name, String copyName)
    {
        throw new UnsupportedOperationException("copy is not supported by this API.");
    }

    public boolean exists(String name)
    {
        throw new UnsupportedOperationException("exist is not supported by this API.");
    }

    public String[] list(String path)
    {
        throw new UnsupportedOperationException("list is not supported by this API.");
    }

    public String[] list()
    {
        throw new UnsupportedOperationException("list is not supported by this API.");
    }

    public boolean isSaveAllowed()
    {
    	return true;
    }
}
