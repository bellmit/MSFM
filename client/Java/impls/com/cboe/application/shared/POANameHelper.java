/*
 * Created by IntelliJ IDEA.
 * User: HUANGE
 * Date: Sep 12, 2002
 * Time: 10:23:22 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.shared;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public class POANameHelper {
    public static String getPOAName(BOHome home)
    {
        String poaName = "";
        try {
            String fullName = home.getFullName();

            FoundationFramework instance =FoundationFramework.getInstance();
            ConfigurationService config = instance.getConfigService();
            StringBuilder propname = new StringBuilder(60);
            propname.append(home.getFrameworkFullName()).append(".poaName");
            poaName = config.getProperty(propname.toString(), null);
            if(poaName == null)
            {
                propname.setLength(0);
                propname.append(home.getContainer().getFullName()).append(".poaName");
                poaName = config.getProperty(propname.toString(), null);
            }
            if(poaName == null)
            {
                propname.setLength(0);
                propname.append(instance.getFullName()).append(".poaName");
                poaName = config.getProperty(propname.toString());
            }
        } catch (Exception e)
        {
            Log.exception ("could not get poaName for " + home.getFullName(), e);
        }
        return poaName;
    }
}
