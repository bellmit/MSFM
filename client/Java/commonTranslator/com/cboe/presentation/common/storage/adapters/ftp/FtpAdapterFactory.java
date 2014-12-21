//
// ------------------------------------------------------------------------
// FILE: FtpAdapterFactory.java
//
// PACKAGE: com.cboe.presentation.common.storage
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.storage.adapters.ftp;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.interfaces.presentation.common.storage.Storage;
import com.cboe.interfaces.presentation.common.storage.adapters.ftp.FtpAdapter;


/**
 * @author torresl@cboe.com
 */
public class FtpAdapterFactory
{
    public static final String PROPERTY_SECTION = "Adapters";
    public static final String FTP_ADAPTER_IMPL_CLASS_PROPERTY_KEY = "Ftp.Class";

    private static FtpAdapter createDefaultFtpAdapter()
    {
        return new SunClientFtpAdapter();
    }

    public static FtpAdapter createFtpAdapter()
    {
        FtpAdapter ftpAdapter = null;
        String className = null;
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            className = AppPropertiesFileFactory.find().getValue(PROPERTY_SECTION,
                                                                        FTP_ADAPTER_IMPL_CLASS_PROPERTY_KEY);
        }
        if(className == null)
        {
            className = System.getProperty(PROPERTY_SECTION + "." + FTP_ADAPTER_IMPL_CLASS_PROPERTY_KEY,
                                           SunClientFtpAdapter.class.getName());
        }

        if (className != null)
        {
            try
            {
                Class implClass = Class.forName(className);
                if (FtpAdapter.class.isAssignableFrom(implClass)) // implements correct interface?
                {
                    ftpAdapter = (FtpAdapter) implClass.newInstance();
                }
            }
            catch (ClassNotFoundException e)
            {
                GUILoggerHome.find().exception(e, "Cannot find FtpAdapter Class " + className);
            }
            catch (InstantiationException e)
            {
                GUILoggerHome.find().exception(e);
            }
            catch (IllegalAccessException e)
            {
                GUILoggerHome.find().exception(e);
            }
        }
        if (ftpAdapter == null)
        {
            GUILoggerHome.find().alarm("Could not create FtpAdapter, or missing FtpAdapter specification.  Using default FtpAdapter");
            ftpAdapter = createDefaultFtpAdapter();
        }
        return ftpAdapter;
    }
}
