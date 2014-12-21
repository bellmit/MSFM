package com.cboe.cfix.cas.product;

/**
 * CfixProductConfigurationServiceTourist.java
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

import com.cboe.application.tradingSession.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.cfix.cas.shared.*;
import com.cboe.cfix.startup.*;
import com.cboe.client.util.tourist.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiSession.*;

public final class CfixProductConfigurationServiceTourist extends AbstractTourist
{
    protected String[] mandatoryKeys = new String[]{"what"};

    public String[] getMandatoryKeys()
    {
        return mandatoryKeys;
    }

    public Writer visit(final Writer writer) throws Exception
    {
        String what = getValue("what");

        CfixProductConfigurationServiceImpl cfixProductConfigurationService = (CfixProductConfigurationServiceImpl) CfixServicesHelper.getCfixProductConfigurationService();
        if (cfixProductConfigurationService == null)
        {
            writer.write("<error type=\"lookup\" text=\"no cfixProductConfigurationService available (yet)\"/>");
            return writer;
        }

        if ("viewPostToTargetTable".equals(what))
        {
            Map map = cfixProductConfigurationService.getPostToTargetCompIDMap();
            Map.Entry entry;

            writer.write("<postToTargetEntries count=\"" + map.size() + "\">");
            for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); )
            {
                entry = (Map.Entry) iterator.next();

                writer.write("<entry set=\"" + entry.getKey() + "\" target=\"" + entry.getValue() + "\"/>");
            }
            writer.write("</postToTargetEntries>");

            return writer;
        }

        if ("reloadPostFile".equals(what))
        {
            cfixProductConfigurationService.initializeConfigurations();

            writer.write("<reloadPostFile action=\"reloaded\"/>");

            return writer;
        }

        if ("clearCache".equals(what))
        {
            cfixProductConfigurationService.clearPcsGroupCache();

            writer.write("<reloadPostFile action=\"cleared\"/>");

            return writer;
        }

        if ("handleAllPosts".equals(what))
        {
            String handle = getValue("handle");
            if (handle == null || !("true".equals(handle) || "false".equals(handle)))
            {
                writer.write("<handleAllPosts action=\"unspecified\" oldValue=\"" + cfixProductConfigurationService.getHandleAllPosts() + "\"/>");
            }
            else
            {
                boolean newHandleAllPosts = "true".equals(handle);
                boolean oldHandleAllPosts = cfixProductConfigurationService.getHandleAllPosts();
                if (newHandleAllPosts == oldHandleAllPosts)
                {
                    writer.write("<handleAllPosts action=\"same\" oldValue=\"" + cfixProductConfigurationService.getHandleAllPosts() + "\"/>");
                }
                else
                {
                    oldHandleAllPosts = cfixProductConfigurationService.setHandleAllPosts(newHandleAllPosts);

                    writer.write("<handleAllPosts action=\"set\" oldValue=\"" + oldHandleAllPosts + "\" newValue=\"" + newHandleAllPosts + "\"/>");
                }
            }
            return writer;
        }

        if ("checkClassKey".equals(what))
        {
            String classKey = getValue("classKey");
            if (classKey != null)
            {
                String t;
                String s;
                
                t = CfixHomeImpl.cfixProperties.getProperty("connection.21536.cfix.fixNetworkAcceptor.targetCompID");
                if (t == null)
                {
                    t = CfixHomeImpl.cfixProperties.getProperty("connection.21537.cfix.fixNetworkAcceptor.targetCompID");
                    if (t == null)
                    {
                        for (Enumeration propertiesEnum = CfixHomeImpl.cfixProperties.propertyNames(); propertiesEnum.hasMoreElements(); )
                        {
                            s = (String) propertiesEnum.nextElement();
                            if (s != null && 
                                s.startsWith("connection.") &&
                                s.endsWith(".cfix.fixNetworkAcceptor.targetCompID"))
                            {
                                t = CfixHomeImpl.cfixProperties.getProperty(s);
                                break;
                            }
                        }
                    }
                }
            
                writer.write("<pcs target=\"" + t + "\">");
                cfixProductConfigurationService.debugConfiguredTargetCompID(Integer.parseInt(classKey), writer);
                writer.write("</pcs>");
            }
            else
            {
                writer.write("<error type=\"conversion\" text=\"you did not specify the classKey to check\"/>");
            }

            return writer;
        }

        if ("checkSymbol".equals(what))
        {
            String session = getValue("session");
            String symbol  = getValue("symbol");
            String type    = getValue("type");

            if (type == null)
            {
                type = Integer.toString(ProductTypes.OPTION);
            }

            if (session == null)
            {
                session = "W_MAIN";
            }

            SessionClassStruct sessionClassStruct = null;

            try
            {
                sessionClassStruct = ServicesHelper.getTradingSessionServiceAdapter().getClassBySessionForSymbol(session, Short.parseShort(type), symbol);
            }
            catch (Exception ex)
            {

            }

            if (sessionClassStruct == null)
            {
                writer.write("<error type=\"find\" text=\"can't find session(" + session + ") symbol(" + symbol + ") type(" + type + ")\"/>");
                return writer;
            }

            writer.write("<pcs>");
            cfixProductConfigurationService.debugConfiguredTargetCompID(sessionClassStruct.classStruct.classKey, writer);
            writer.write("</pcs>");

            return writer;
        }

        return writer;
    }
}
