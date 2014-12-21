package com.cboe.interfaces.application;

import com.cboe.util.version.NoSuchRuntimeJarException;
import com.cboe.util.version.NoSuchVersionResourceException;

/**
 * @author Jing Chen
 */
public interface VersionQuery
{
    public static final String CLIENT_IMPL_JAR_NAME = "client_impls";
    public static final String CFIX_IMPL_JAR_NAME = "cfix_impls";
    public static final String SA_CLIENT_IMPL_JAR_NAME = "sysAdminClient_impls";
    public static final String FIX_CLIENT_IMPL_JAR_NAME = "fixclient";
    public static final String IC_IMPL_JAR_NAME = "ics_impls";

    public String getApplicationImplVersion() throws NoSuchRuntimeJarException, NoSuchVersionResourceException;
}
