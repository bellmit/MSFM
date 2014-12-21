package com.cboe.cfix.cas.marketData;

/**
 * CfixStrikePriceHelperTourist.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Tourist Parameters:
 *
 */

import java.io.*;
import java.util.*;

import com.cboe.cfix.cas.shared.*;
import com.cboe.client.util.tourist.*;
import com.cboe.interfaces.cfix.*;

public final class CfixStrikePriceHelperTourist extends AbstractTourist
{
    protected String[] mandatoryKeys = new String[]{"what"};

    public String[] getMandatoryKeys()
    {
        return mandatoryKeys;
    }

    public Writer visit(final Writer writer) throws Exception
    {
        String what = getValue("what");

        CfixStrikePriceHelperHome cfixStrikePriceHelperHome = CfixServicesHelper.getCfixStrikePriceHelperHome();
        if (cfixStrikePriceHelperHome == null)
        {
            writer.write("<error type=\"lookup\" text=\"no cfixStrikePriceHelperHome available (yet)\"/>");
            return writer;
        }

        if ("viewStrikePriceTable".equals(what))
        {
            Map map = (Map) cfixStrikePriceHelperHome.find().getStrikePriceTable();
            Map.Entry entry;
            Map sortedMap = new TreeMap();

            writer.write("<strikePriceEntries count=\"" + map.size() + "\">");
            for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); )
            {
                entry = (Map.Entry) iterator.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            for (Iterator iterator = sortedMap.entrySet().iterator(); iterator.hasNext(); )
            {
                entry = (Map.Entry) iterator.next();

                writer.write("<entry symbol=\"" + entry.getKey() + "\" adjustment=\"" + entry.getValue() + "\"/>");
            }
            writer.write("</strikePriceEntries>");

            return writer;
        }

        if ("reloadStrikePriceFile".equals(what))
        {
            cfixStrikePriceHelperHome.loadStrikePriceFile();

            writer.write("<reloadStrikePriceFile action=\"reloaded\"/>");

            return writer;
        }

        return writer;
    }
}
