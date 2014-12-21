//
// -----------------------------------------------------------------------------------
// Source file: FirmValidator.java
//
// PACKAGE: com.cboe.internalPresentation.firm
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.firm;

import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.interfaces.internalPresentation.firm.FirmModel;

import java.util.Map;
import java.util.HashMap;

/**
 * Provide simple true/false validation to find out if a Firm exists in CBOEdirect for the specified
 * firmAcronym and firmNumber (when the PAR client stores firm data for a PARBrokerProfile, it only
 * includes acronym/number -- no exchange data).
 */
//todo: just add this functionality to the FirmCache
public class FirmValidator
{
    private static Map<String, FirmModel> allFirms;

    private FirmValidator()
    {
    }

    public static boolean firmExists(String firmAcronym, String firmNumber)
    {
        FirmModel cachedFirm = getAllFirms().get(firmAcronym + '.' + firmNumber);
        boolean retVal = cachedFirm != null;
        return retVal;
    }

    private synchronized static Map<String, FirmModel> getAllFirms()
    {
        if (allFirms == null)
        {
            FirmModel[] tmpFirms = SystemAdminAPIFactory.find().getFirms();
            if (tmpFirms != null && tmpFirms.length > 0)
            {
                allFirms = new HashMap<String, FirmModel>();
                for (FirmModel fm : tmpFirms)
                {
                    allFirms.put(fm.getAcronym() + '.' + fm.getFirmNumber(), fm);
                }
            }
        }
        return allFirms;
    }
}
