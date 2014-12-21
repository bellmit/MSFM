//
// ------------------------------------------------------------------------
// FILE: LocalFileStorage.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * @author torresl@cboe.com
 */
public class LocalFileStorage extends AbstractFileStorage
{
    public LocalFileStorage()
    {
        super();
    }

    public void initializeStorage()
            throws IOException
    {
        // nothing to do
    }

    protected void mkdirs(String name)
    {
        String pathName = stripLastPathComponent(name);
        if(pathName != null && pathName.length()>0)
        {
            File dir = new File(pathName);
            if(dir.exists() == false)
            {
                dir.mkdirs();
            }
        }
        else
        {
            File dir = new File(name).getParentFile();
            if (dir != null)
            {
                if (dir.exists() == false)
                {
                    dir.mkdirs();
                }
            }
        }
    }

    protected String stripLastPathComponent(String dirPath)
    {
        int lastSeparator = dirPath.lastIndexOf(File.separatorChar);
        // strip the last path component
        if(lastSeparator != -1)
        {
            dirPath = dirPath.substring(0, lastSeparator);
        }
        else
        {
            dirPath = "";
        }
        return dirPath;
    }

    public void store(String name, String content)
            throws IOException
    {
        name = name.replace('/', File.separatorChar);
        mkdirs(name);
        BufferedWriter bw = new BufferedWriter(new FileWriter(name));
        bw.write(content);
        bw.close();
    }

    public void store(String name, Serializable content)
            throws IOException
    {
        name = name.replace('/', File.separatorChar);
        mkdirs(name);
        writeObject(new ObjectOutputStream(new FileOutputStream(name)), content);
    }

    public void store(String name, byte[] content)
            throws IOException
    {
        name = name.replace('/', File.separatorChar);
        mkdirs(name);
        writeStream(new FileOutputStream(name), content);
    }
    public void store(String name, Properties content)
            throws IOException
    {
        mkdirs(name);
        content.store(new FileOutputStream(name),"");
    }

    public String retrieveString(String name)
            throws IOException
    {
        name = name.replace('/', File.separatorChar);
        return readStream(new FileInputStream(name));
    }

    public Object retrieveObject(String name)
            throws IOException
    {
        name = name.replace('/', File.separatorChar);
        return readObject(new ObjectInputStream(new FileInputStream(name)));
    }

    public byte[] retrieveBytes(String name) throws IOException
    {
        name = name.replace('/', File.separatorChar);
        return readStream(new FileInputStream(name)).getBytes();
    }

    public Properties retrieveProperties(String name) throws IOException
    {
        Properties content = new Properties();
        content.load(new FileInputStream(name));
        return content;
    }

    public void rename(String oldName, String newName)
            throws IOException
    {
        File oldFile = new File(oldName);
        if (exists(newName))
        {
            delete(newName);
        }
        if (oldFile.renameTo(new File(newName)) == false)
        {
            throw new IOException("Rename failed.");
        }
    }

    public void delete(String name)
            throws IOException
    {
        File file = new File(name);
        file.delete();
    }

    public boolean exists(String name)
            throws IOException
    {
        File file = new File(name);
        return file.exists();
    }

    public void copy(String name, String copyName)
            throws IOException
    {
        mkdirs(copyName);
        File currentFile = new File(name);
        File copyFile = new File(copyName);
        if (copyFile.exists())
        {
            copyFile.delete();
        }
        copyFile.createNewFile();
        FileInputStream reader = new FileInputStream(currentFile);
        FileOutputStream writer = new FileOutputStream(copyFile);
        int bytesToRead = reader.available();
        while (bytesToRead > 0)
        {
            byte[] bytes = new byte[bytesToRead];
            reader.read(bytes);
            writer.write(bytes);
            bytesToRead = reader.available();
        }
        reader.close();
        writer.close();
    }

    public String[] list(String path)
            throws IOException
    {
        File dir = new File(path);
        if (dir.isDirectory())
        {
            return dir.list();
        }
        return new String[0];
    }

    public String[] list()
            throws IOException
    {
        // get current dir
        return list(".");
    }
}
