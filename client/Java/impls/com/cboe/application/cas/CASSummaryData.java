package com.cboe.application.cas;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.VersionQuery;
import com.cboe.util.UserDataTypes;
import com.cboe.util.version.NoSuchRuntimeJarException;
import com.cboe.util.version.NoSuchVersionResourceException;

/**
 * @author Jing Chen
 */
public class CASSummaryData
{
    String version;
    public CASSummaryData(VersionQuery versionQuery)
    {
        try
        {
            version = versionQuery.getApplicationImplVersion();
        }
        catch(NoSuchRuntimeJarException e)
        {
            Log.exception(e);
        }
        catch(NoSuchVersionResourceException e)
        {
            Log.exception(e);
        }
    }

    public String toString()
    {
        InstrumentorUserData userData = new InstrumentorUserData();
        int numberOfUsers = ServicesHelper.getUserSessionQueryHome().getActiveSessions().size();

        userData.addValue(UserDataTypes.NUMBER_OF_USERS, Integer.toString(numberOfUsers));
        userData.addValue(UserDataTypes.CAS_VERSION, version);

        return userData.toString();
    }
}
