package com.cboe.cfix.startup;

/**
 * CfixStrikePriceHelperHomeImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.client.util.*;
import com.cboe.domain.startup.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

/**
 *  A home class that is configured with the mapping between the time in force
 *  codes used by TPF and the session and time in force code used in SBT.
 */
public class CfixStrikePriceHelperHomeImpl extends ClientBOHome implements CfixStrikePriceHelperHome
{
    protected Map                   strikePriceTable = new HashMap(128);
    protected int                   strikePriceMultiplier = 1000;
    protected String                strikePriceHelperClass;
    protected String                strikePriceInputFileName;
    protected CfixStrikePriceHelper cfixStrikePriceHelper;

    /**
     *  Default Strike Price Multiplier - The default strike price values are
     *  assumed to be in thousands - this means they would need to be multiplied by
     *  1000 before adding to the truncated strike price. This can be overidden in
     *  the configuration file by setting the strikePriceMultiplier property.
     */
    protected final static String DEFAULT_STRIKE_PRICE_MULTIPLIER = "1000";

    public void clientInitialize() throws Exception
    {
        strikePriceMultiplier    = Integer.parseInt(getProperty("strikePriceMultiplier", DEFAULT_STRIKE_PRICE_MULTIPLIER));
        strikePriceHelperClass   = getProperty("strikePriceHelperClass", "com.cboe.cfix.cas.marketData.CfixStrikePriceHelperImpl");
        strikePriceInputFileName = getProperty("strikePriceInputFileName");

        loadStrikePriceFile();

        find();
    }

    public CfixStrikePriceHelper find()
    {
        if (cfixStrikePriceHelper == null)
        {
            synchronized(this)
            {
                if (cfixStrikePriceHelper == null)
                {
                    cfixStrikePriceHelper = (CfixStrikePriceHelper) ClassHelper.loadClass(strikePriceHelperClass);
                    cfixStrikePriceHelper.setStrikePriceTable(strikePriceTable);
                }
            }
        }

        return cfixStrikePriceHelper;
    }

    public void loadStrikePriceFile()
    {
        BufferedReader  bufferedReader = null;
        String          line;
        StringTokenizer parser;
        String          classSymbol;
        Integer         strikeAdjustment;
        Map             map       = new HashMap(128);
        boolean         replacing = strikePriceTable.size() > 0;

        if (replacing)
        {
            Log.information(this, "Replacing Strike Price Adjustments from file: " + strikePriceInputFileName);
        }
        else
        {
            Log.information(this, "Reading Strike Price Adjustments from file: " + strikePriceInputFileName);
        }

        try
        {
            bufferedReader = new BufferedReader(new FileReader(strikePriceInputFileName));

            while ((line = bufferedReader.readLine()) != null)
            {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#"))
                {
                    continue;
                }

                parser = new StringTokenizer(line);
                if (parser.countTokens() != 2)
                {
                    continue;
                }

                classSymbol = parser.nextToken();

                strikeAdjustment = IntegerHelper.createInteger(Integer.parseInt(parser.nextToken()) * strikePriceMultiplier);

                map.put(classSymbol, strikeAdjustment);

                if (Log.isDebugOn())
                {
                    if (replacing)
                    {
                        Log.debug(this, "Replacing Adding ClassSymbol=" + classSymbol + " StrikeAdjuster=" + strikeAdjustment.intValue());
                    }
                    else
                    {
                        Log.debug(this, "Adding ClassSymbol=" + classSymbol + " StrikeAdjuster=" + strikeAdjustment.intValue());
                    }
                }
            }

            if (replacing)
            {
                Log.information(this, "Replacing Number of Strike Price Adjustments read from: " + strikePriceInputFileName + " New #" + map.size() + " Previous #" + strikePriceTable.size());
            }
            else
            {
                Log.information(this, "Number of Strike Price Adjustments read from: " + strikePriceInputFileName + " New #" + map.size());
            }

            strikePriceTable = map;

            if (cfixStrikePriceHelper != null)
            {
                cfixStrikePriceHelper.setStrikePriceTable(strikePriceTable);
            }
        }
        catch (Exception e)
        {
            Log.exception(this, e);
        }
        finally
        {
            try
            {
                if (bufferedReader != null)
                {
                    bufferedReader.close();
                }
            }
            catch (Exception e)
            {

            }
        }
    }
}
