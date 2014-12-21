//
// ------------------------------------------------------------------------
// FILE: StorageManagerImpl.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2007 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.common.storage;

import java.io.IOException;

import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.interfaces.presentation.common.storage.StorageManager;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

public class StorageManagerImpl implements StorageManager
{
    public static final String PROPERTY_SECTION = "Storage";

    public static final String LOCAL_STORAGE_IMPL_CLASS_PROPERTY_KEY = "Local.Class";
    public static final String REMOTE_STORAGE_IMPL_CLASS_PROPERTY_KEY = "Remote.Class";
    public static final String ORBNAME_ALIAS_STORAGE_IMPL_CLASS_PROPERTY_KEY =
            "OrbNameAliasStorage.Class";
    public static final String LOGICAL_ORBNAME_STORAGE_IMPL_CLASS_PROPERTY_KEY =
            "LogicalOrbNameStorage.Class";
    public static final String CAS_INFO_STORAGE_IMPL_CLASS_PROPERTY_KEY = "CASInfoStorage.Class";
    public static final String SQL_QUERY_STORAGE_IMPL_CLASS_PROPERTY_KEY = "SQLQueryStorage.Class";
    public static final String AR_COMM_FAV_STORAGE_IMPL_CLASS_PROPERTY_KEY =
            "ARCommandFavoriteStorage.Class";
    public static final String XTP_GUI_USER_PREF_STORAGE_IMPL_CLASS_PROPERTY_KEY =
            "XtpGuiUserPrefStorage.Class";

    protected Storage localStorage;
    protected Storage remoteStorage;
    protected Storage orbNameAliasStorage;
    protected Storage logicalNameStorage;
    protected Storage casInfoStorage;
    protected Storage sqlQueryStorage;
    protected Storage arCommandFavoriteStorage;
    protected Storage xtpGuiUserPrefStorage;

    protected static final Class<? extends Storage> DEFAULT_LOCAL_STORAGE_CLASS =
            LocalFileStorage.class;
    protected static final Class<? extends Storage> DEFAULT_REMOTE_STORAGE_CLASS =
            LocalFileStorage.class;
    protected static final Class<? extends Storage> DEFAULT_ORBNAMEALIAS_STORAGE_CLASS =
            FtpStorage.class;
    protected static final Class<? extends Storage> DEFAULT_CASINFO_STORAGE_CLASS =
            LocalFileStorage.class;
    protected static final Class<? extends Storage> DEFAULT_SQL_QUERY_STORAGE_CLASS =
            FtpStorage.class;
    protected static final Class<? extends Storage> DEFAULT_AR_COMM_FAV_STORAGE_CLASS =
            FtpStorage.class;
    protected static final Class<? extends Storage> DEFAULT_XTP_GUI_USER_PREF_STORAGE_CLASS =
            FtpStorage.class;
    protected static final Class<? extends Storage> DEFAULT_LOGICAL_ORBNAME_STORAGE_CLASS =
            FtpStorage.class;

    public StorageManagerImpl()
    {
        this(null, null, null, null, null, null, null, null);
    }

    public StorageManagerImpl(String localStorageClassName, String remoteStorageClassName)
    {
        this(localStorageClassName, remoteStorageClassName, null, null, null, null, null, null);
    }

    public StorageManagerImpl(String localStorageClassName, String remoteStorageClassName,
                              String casInfoStorageClassName, String orbNameAliasStorageClassName,
                              String sqlQueryStorageClassName, String arCommFavStorageClassName,
                              String xtpGuiUserPrefStorageClassName, String logicalOrbNameStorageClassName)
    {
        localStorage = initializeStorage(localStorageClassName,
                                         LOCAL_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                         DEFAULT_LOCAL_STORAGE_CLASS);

        remoteStorage = initializeStorage(remoteStorageClassName,
                                          REMOTE_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                          DEFAULT_REMOTE_STORAGE_CLASS);

        orbNameAliasStorage = initializeStorage(orbNameAliasStorageClassName,
                                                ORBNAME_ALIAS_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                                DEFAULT_ORBNAMEALIAS_STORAGE_CLASS);

        casInfoStorage = initializeStorage(casInfoStorageClassName,
                                           CAS_INFO_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                           DEFAULT_CASINFO_STORAGE_CLASS);

        sqlQueryStorage = initializeStorage(sqlQueryStorageClassName,
                                            SQL_QUERY_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                            DEFAULT_SQL_QUERY_STORAGE_CLASS);

        arCommandFavoriteStorage =
                initializeStorage(arCommFavStorageClassName,
                                  AR_COMM_FAV_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                  DEFAULT_AR_COMM_FAV_STORAGE_CLASS);

        xtpGuiUserPrefStorage =
                initializeStorage(xtpGuiUserPrefStorageClassName,
                                  XTP_GUI_USER_PREF_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                  DEFAULT_XTP_GUI_USER_PREF_STORAGE_CLASS);

        logicalNameStorage = initializeStorage(logicalOrbNameStorageClassName,
                                                LOGICAL_ORBNAME_STORAGE_IMPL_CLASS_PROPERTY_KEY,
                                                DEFAULT_LOGICAL_ORBNAME_STORAGE_CLASS);
    }

    public Storage getLocalStorage()
    {
        return localStorage;
    }

    public Storage getRemoteStorage()
    {
        return remoteStorage;
    }

    public Storage getOrbNameAliasStorage()
    {
        return orbNameAliasStorage;
    }

    public Storage getLogicalNameStorage()
    {
        return logicalNameStorage;
    }

    public Storage getCASInfoStorage()
    {
        return casInfoStorage;
    }

    public Storage getSQLQueryStorage()
    {
        return sqlQueryStorage;
    }

    public Storage getARCommandFavoriteStorage()
    {
        return arCommandFavoriteStorage;
    }

    public Storage getXtpGuiUserPrefStorage()
    {
        return xtpGuiUserPrefStorage;
    }

    private Storage initializeStorage(String storageClassName, String keyToFindClassName,
                                      Class<? extends Storage> defaultClass)
    {
        Storage storage = null;

        if(defaultClass == null)
        {
            throw new IllegalArgumentException("defaultClass must not be null.");
        }

        String className = storageClassName;
        if( ( className == null || className.length() == 0 ) &&
            AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            className = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION,
                                                                 keyToFindClassName);
        }

        try
        {
            //noinspection RawUseOfParameterizedType
            Class clazz;

            if(className != null)
            {
                clazz = Class.forName(className);
                if(!Storage.class.isAssignableFrom(clazz))
                {
                    clazz = defaultClass;
                }
            }
            else
            {
                clazz = defaultClass;
            }

            storage = (Storage) clazz.newInstance();
        }
        catch(ClassNotFoundException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch(InstantiationException e)
        {
            GUILoggerHome.find().exception(e);
        }
        catch(IllegalAccessException e)
        {
            GUILoggerHome.find().exception(e);
        }

        if(storage == null)
        {
            GUILoggerHome.find().alarm("Using default Storage Manager for:" + keyToFindClassName + '|' +
                                       storageClassName);

            storage = new LocalFileStorage();
            try
            {
                storage.initializeStorage();
            }
            catch(IOException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }

        return storage;
    }

    /*
    public static void main(String[] args)
    {
        StorageManagerImpl manager =
                new StorageManagerImpl(DEFAULT_LOCAL_STORAGE_CLASS_NAME, FtpStorage.class.getName()); 
        doTest(manager.getLocalStorage());
        System.out.println("************************************************************");
        doTest(manager.getOrbNameAliasStorage());
    }

    public static void doTest(Storage storage)
    {
        System.out.println("Testing storage impl: " + storage.getClass().getName());
        String dirName = "stm/";
        String fileName = dirName + "myFile.txt";
        String copyFileName = dirName + "myFileCopy.txt";
        String renameFileName = dirName + "myFileCopy-renamed.txt";
        String serFileName = dirName + "stest.ser";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        String date = df.format(new Date());
        String contents = "[Test]\r\nDate=" + date +
                "\r\n\r\n[SectionName1]\r\nProperty1=Value1\r\n\r\n[SectionName2]\r\nProperty2=Value2\r\n\r\n";
        try
        {
            storage.delete(fileName);
            storage.delete(copyFileName);
            storage.delete(serFileName);
            storage.delete(renameFileName);
            storage.store(fileName, contents);
            String retrievedText = storage.retrieveString(fileName);
            if (contents.equals(retrievedText) == false)
            {
                System.out.println("FAILED: Store/Retrieve");
                System.out.println("\nSTORED:" + contents);
                System.out.println("\nRETRIEVED:" + retrievedText);
            }
            else
            {
                System.out.println("OK: Store/Retrieve");
            }
            storage.copy(fileName, copyFileName);
            retrievedText = storage.retrieveString(copyFileName);
            if (contents.equals(retrievedText) == false)
            {
                System.out.println("FAILED: Copy/Retrieve");
            }
            else
            {
                System.out.println("OK: Copy/Retrieve");
            }
            storage.rename(copyFileName, renameFileName);
            if (storage.exists(copyFileName))
            {
                System.out.println("FAILED: rename original file exists");
            }
            else if (storage.exists(renameFileName))
            {
                System.out.println("OK: rename renamed file exists");
            }
            else
            {
                System.out.println("FAILED: rename original and renamed dont exist");
            }
            STest sto = new STest(contents);
            storage.store(serFileName, sto);
            if (storage.exists(serFileName) == false)
            {
                System.out.println("FAILED: object file exists");
            }
            else
            {
                System.out.println("OK: object file exists");
            }
            Object retrievedObject = storage.retrieveObject(serFileName);
            if (retrievedObject instanceof STest)
            {
                STest sto2 = (STest) retrievedObject;
                if (sto.compareTo(sto2) != 0)
                {
                    System.out.println("FAILED: retrieved object");
                }
                else
                {
                    System.out.println("OK: retrieved object");
                }
            }
            else
            {
                System.out.println("FAILED: object retrieve instance");
            }
            String[] list = storage.list(dirName);
            if (list.length == 3)
            {
                HashSet set = new HashSet(list.length);
                for (int i = 0; i < list.length; i++)
                {
                    set.add(list[i]);
                }
                set.remove(fileName);
                set.remove(renameFileName);
                set.remove(serFileName);
                // remove the files that should be in the list
                if (set.size() == 0) // all removed
                {
                    System.out.println("FAILED: list expected files are not in the list");
                }
                else
                {
                    System.out.println("OK: list");
                }
            }
            else
            {
                System.out.println("FAILED: list number of files is not 3");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace(System.out);
        }

        System.out.println("\nTesting complete for storage impl: " + storage.getClass().getName());
    }

    static class STest implements Serializable, Comparable
    {
        protected String value;

        public STest()
        {
            value = "";
        }

        public STest(String value)
        {
            this.value = value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public int compareTo(Object o)
        {
            if (o instanceof STest)
            {
                STest sto = (STest) o;
                return value.compareTo(sto.value);
            }
            return 0;
        }
    }
    */
}