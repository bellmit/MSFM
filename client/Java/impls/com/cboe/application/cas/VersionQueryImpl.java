package com.cboe.application.cas;

import com.cboe.util.version.NoSuchRuntimeJarException;
import com.cboe.util.version.NoSuchVersionResourceException;
import com.cboe.util.version.VersionInfo;
import com.cboe.interfaces.application.VersionQuery;

/**
 * @author Jing Chen
 */
public class VersionQueryImpl implements VersionQuery
{
    public String getApplicationImplVersion() throws NoSuchRuntimeJarException, NoSuchVersionResourceException
    {
        String version = (String)VersionInfo.getVersionInfo(CLIENT_IMPL_JAR_NAME).get(VersionInfo.IMPLEMENTATION_VERSION_PROPERTY);
        return version;
    }
}
