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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @author torresl@cboe.com
 */
public class FtpFileStorage extends AbstractFileStorage
{

    public static final String USER_CLASS_PROPERTY_KEY = "Remote.User";
    public static final String PSWD_CLASS_PROPERTY_KEY = "Remote.Password";
    public static final String HOST_CLASS_PROPERTY_KEY = "Remote.Host";
    public static final String PORT_CLASS_PROPERTY_KEY = "Remote.Port";

    protected String user;
    protected String password;
    protected String host;
    protected int port;

    public FtpFileStorage()
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

    }

    public void initializeStorage()
            throws IOException
    {
        // nothing to do
    }

    protected String buildUrlString(String fileName)
    {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append("ftp://").append(user).append(":").append(password);
        buffer.append("@").append(host).append(":").append(port);
        buffer.append("/").append(fileName);
        return buffer.toString();
    }

    protected URL buildUrl(String fileName)
            throws MalformedURLException
    {
        return new URL(buildUrlString(fileName));
    }

    protected void mkdirs(String name)
            throws IOException
    {
        File file = new File(name);
        File dir = file.getParentFile();
        while (dir != null)
        {
            String path = dir.getPath();
            String ftpPath = path.replace(File.separatorChar, '/');
            if (!exists(ftpPath))
            {
                mkdirs(ftpPath);
                mkdir(name);
            }
        }
        mkdir(name);
    }

    protected void mkdir(String name)
            throws IOException
    {
        String ftpPath = name.replace(File.separatorChar, '/');
        URL url = buildUrl(ftpPath + "/");
        URLConnection connection = getURLConnection(url);
        connection.connect();
        OutputStream os = connection.getOutputStream();
        os.flush();
        os.close();
    }

    private URLConnection getURLConnection(URL url) throws IOException
    {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(30000);
        urlConnection.setReadTimeout(30000);
        return urlConnection;
    }

    public void store(String name, String content)
            throws IOException
    {
        mkdirs(name);
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        store(name, content.getBytes());
    }

    public void store(String name, Serializable content)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(connection.getOutputStream());
        writeObject(objectOutputStream, content);
    }

    public void store(String name, byte[] content)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        writeStream(connection.getOutputStream(), content);
    }
    public void store(String name, Properties content)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        content.store(connection.getOutputStream(),"");
    }

    public String retrieveString(String name)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        return readStream(connection.getInputStream());
    }

    public Object retrieveObject(String name)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        return readObject(new ObjectInputStream(connection.getInputStream()));
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

        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
 
        content.load(connection.getInputStream());

        return content;
    }

    public void rename(String oldName, String newName)
            throws IOException
    {
        delete(newName);
        copy(oldName, newName);
        delete(oldName);
    }

    public void delete(String name)
            throws IOException
    {
        if (exists(name))
        {
            URL url = buildUrl(name);
            URLConnection connection = getURLConnection(url);
            connection.connect();
            writeStream(connection.getOutputStream(), new byte[0]);
        }
    }

    public void copy(String name, String copyName)
            throws IOException
    {
        URL originalUrl = buildUrl(name);
        URLConnection originalConnection = getURLConnection(originalUrl);
        originalConnection.connect();
        String originalData = readStream(originalConnection.getInputStream());
        URL copyUrl = buildUrl(copyName);
        URLConnection copyConnection = getURLConnection(copyUrl);
        copyConnection.connect();
        writeStream(copyConnection.getOutputStream(), originalData.getBytes());
    }

    public boolean exists(String name)
            throws IOException
    {
        URL url = buildUrl(name);
        URLConnection connection = getURLConnection(url);
        connection.connect();
        return connection.getContentLength() > 0;
    }

    public String[] list(String path)
            throws IOException
    {
        return new String[0];
    }

    public String[] list()
            throws IOException
    {
        return new String[0];
    }
}
