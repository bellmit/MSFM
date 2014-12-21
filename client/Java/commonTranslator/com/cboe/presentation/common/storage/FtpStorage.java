//
// ------------------------------------------------------------------------
// FILE: FtpFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter;
import com.cboe.presentation.common.storage.adapters.ftp.FtpAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.InputStream;




import java.util.Properties;

/**
 * Responsible for representing a remote file storage implemented in the FTP protocol. This object communicates using
 * the FTP implementation provided by {@link FtpAdapterFactory}. The configuration information to connect to the FTP
 * server are pulled from {@link AppPropertiesFileFactory}.
 * @author torresl@cboe.com
 */
public class FtpStorage extends AbstractFileStorage
{

    public static final String USER_CLASS_PROPERTY_KEY = "Remote.User";
    public static final String PSWD_CLASS_PROPERTY_KEY = "Remote.Password";
    public static final String HOST_CLASS_PROPERTY_KEY = "Remote.Host";
    public static final String PORT_CLASS_PROPERTY_KEY = "Remote.Port";

    protected String user;
    protected String password;
    protected String host;
    protected int port;
    protected FtpAdapter ftpAdapter;

    public FtpStorage()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            user = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, USER_CLASS_PROPERTY_KEY);
            password = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, PSWD_CLASS_PROPERTY_KEY);
            host = AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, HOST_CLASS_PROPERTY_KEY);
            port = Integer.parseInt(AppPropertiesFileFactory.find().getValue(StorageManagerImpl.PROPERTY_SECTION, PORT_CLASS_PROPERTY_KEY));
        }
        if (user == null)
        {
            user = System.getProperty(USER_CLASS_PROPERTY_KEY, "infra2");
        }
        if (password == null)
        {
            password = System.getProperty(PSWD_CLASS_PROPERTY_KEY, "infra2");
        }
        if (host == null)
        {
            host = System.getProperty(HOST_CLASS_PROPERTY_KEY, "devsvr6");
        }
        if (port == 0)
        {
            port = Integer.parseInt(System.getProperty(HOST_CLASS_PROPERTY_KEY, "21"));
        }

        ftpAdapter = FtpAdapterFactory.createFtpAdapter();
    }

    public synchronized void initializeStorage()
            throws IOException
    {
        ftpAdapter.openServer(host, port);
        ftpAdapter.login(user, password);
        ftpAdapter.ascii();

    }

    protected synchronized void checkConnection()
            throws IOException
    {
        if (ftpAdapter.isConnected() == false)
        {
            initializeStorage();
        }
    }

    protected void mkdirs(String name)
            throws IOException
    {
        checkConnection();

        String ftpPath = stripLastPathComponent(name);
        if(ftpPath != null && ftpPath.length()>0)
        {
            ftpAdapter.mkdirs(ftpPath);
        }
    }
    protected String stripLastPathComponent(String pathName)
    {
        String ftpPath = pathName.replace(File.separatorChar, '/');
        int lastSeparator = ftpPath.lastIndexOf("/");
        // strip the last path component
        if(lastSeparator != -1)
        {
            ftpPath = ftpPath.substring(0, lastSeparator);
        }
        else
        {
            ftpPath = "";
        }
        return ftpPath;
    }
    protected void mkdir(String name)
            throws IOException
    {
        checkConnection();

        String ftpPath = stripLastPathComponent(name);
        if(ftpPath != null && ftpPath.length()>0)
        {
            ftpAdapter.mkdir(ftpPath);
        }
    }

    /**
     * Copies the inputstream into the remote storage location
     * 
     * @param name
     *            The remote file name
     * @param inputStream
     *            The stream to copy to that location.
     * @throws IOException
     *             Thrown if there was a problem transmitting the data
     */
    public void store(String name, InputStream inputStream) throws IOException {
        long beforeMkdir = System.currentTimeMillis();
        mkdirs(name);
        long afterMkdir = System.currentTimeMillis();
        checkConnection();
        long beforeStream = System.currentTimeMillis();
        OutputStream outputStream = ftpAdapter.put(name);
        long afterStream = System.currentTimeMillis();
        
        long beforeCopy = System.currentTimeMillis();
        // copy in bursts
        copyStreams(inputStream, outputStream);
        outputStream.close();
        long afterCopy = System.currentTimeMillis();
        
        long mkdir = (afterMkdir - beforeMkdir);
        long stream = (afterStream - beforeStream);
        long copy = (afterCopy - beforeCopy);
        
        
    }


    
    
    public void store(String name, String content)
            throws IOException
    {
        store(name, content.getBytes());
    }

    public void store(String name, Serializable content)
            throws IOException
    {
        mkdirs(name);
        checkConnection();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(ftpAdapter.put(name));
        writeObject(objectOutputStream, content);
    }

    public void store(String name, byte[] content)
            throws IOException
    {
        mkdirs(name);
        checkConnection();
        writeStream(ftpAdapter.put(name), content);
    }
    public void store(String name, Properties content)
            throws IOException
    {
        mkdirs(name);
        checkConnection();
        OutputStream outputStream = ftpAdapter.put(name);
        content.store(outputStream,"");
    }

    public String retrieveString(String name)
            throws IOException
    {
        checkConnection();
        return readStream(ftpAdapter.get(name));
    }

    public Object retrieveObject(String name)
            throws IOException
    {
        checkConnection();
        return readObject(new ObjectInputStream(ftpAdapter.get(name)));
    }

    public byte[] retrieveBytes(String name)
            throws IOException
    {
        return retrieveString(name).getBytes();
    }

    public Properties retrieveProperties(String name)
            throws IOException
    {
        Properties content = new Properties();

        checkConnection();
        content.load(ftpAdapter.get(name));
        return content;
    }

    public void rename(String oldName, String newName)
            throws IOException
    {
        checkConnection();
        mkdirs(newName);
        ftpAdapter.rename(oldName, newName);
    }

    public void delete(String name)
            throws IOException
    {
        checkConnection();
        ftpAdapter.delete(name);
    }

    public void copy(String name, String copyName)
            throws IOException
    {
        store(copyName, retrieveBytes(name));
    }

    public boolean exists(String name)
            throws IOException
    {
        checkConnection();
        return ftpAdapter.exists(name);
    }

    public String[] list(String path)
            throws IOException
    {
        checkConnection();
        return ftpAdapter.list(path);
    }

    public String[] list()
            throws IOException
    {
        checkConnection();
        return ftpAdapter.list();
    }
}
