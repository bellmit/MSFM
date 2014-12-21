package com.cboe.cfix.cas.product;

/**
 * CfixProductConfigurationServiceImpl.java
 *
 * @author Dmitry Volpyansky
 * @author Jing Chen
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import com.cboe.application.shared.*;
import com.cboe.client.util.*;
import com.cboe.client.util.collections.*;
import com.cboe.idl.product.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.cfix.*;

public final class CfixProductConfigurationServiceImpl extends BObject implements CfixProductConfigurationService
{
    private static final String GROUP_KEY_NAME = "MDCASSET20";
    protected Map           postToTargetCompIDMap = new HashMap(16);
    protected boolean       handleAllPosts        = false;
    protected IntObjectMap  pcsGroupCacheMap;

    public CfixProductConfigurationServiceImpl() throws Exception
    {
        super();

        clearPcsGroupCache();

        initializeConfigurations();
    }

    public void clearPcsGroupCache()
    {
        pcsGroupCacheMap = new IntObjectMap.IntObjectMapMT(1024);
    }

    public boolean getHandleAllPosts()
    {
        return handleAllPosts;
    }

    public boolean setHandleAllPosts(boolean handleAllPosts)
    {
        boolean oldHandleAllPosts = this.handleAllPosts;
        this.handleAllPosts = handleAllPosts;
        return oldHandleAllPosts;
    }

    public void initializeConfigurations() throws Exception
    {
        BufferedReader reader = null;

        try
        {
            String postToTargetCompIDMapFile = System.getProperty("postToEngineMapFile");
            if (postToTargetCompIDMapFile == null)
            {
                throw new Exception("CfixProductConfigurationServiceImpl:: System property 'postToEngineMapFile' not set");
            }

            if (Log.isDebugOn())
            {
                Log.debug("CfixProductConfigurationServiceImpl:: Post File is '" + postToTargetCompIDMapFile + "'");
            }

            String          line;
            String          post;
            String          targetEngine;
            StringTokenizer tokenizer;

            URLConnection urlConnection = null;

            if (postToTargetCompIDMapFile.indexOf(":") >= 0)
            {
                urlConnection = new URL(postToTargetCompIDMapFile).openConnection();
            }
            else
            {
                try
                {
                    urlConnection = new URL(postToTargetCompIDMapFile).openConnection();
                }
                catch (Exception ex)
                {
                    try
                    {
                        StringBuilder url = new StringBuilder(postToTargetCompIDMapFile.length()+5);
                        url.append("file:").append(postToTargetCompIDMapFile);
                        urlConnection = new URL(url.toString()).openConnection();
                    }
                    catch (Exception ex2)
                    {
                        throw ex;
                    }
                }
            }

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            Map tempPostToTargetCompIDMap = new HashMap(16);
            StringBuilder postMsg = new StringBuilder(postToTargetCompIDMapFile.length()+80);

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#"))
                {
                    continue;
                }

                if (line.startsWith("*"))
                {
                    handleAllPosts = true;
                    Log.information("CfixProductConfigurationServiceImpl:: Handling All Posts");
                    return;
                }

                tokenizer = new StringTokenizer(line);
                if (tokenizer.countTokens() < 2)
                {
                    continue;
                }

                post         = tokenizer.nextToken();
                targetEngine = tokenizer.nextToken();

                tempPostToTargetCompIDMap.put(post, targetEngine);

                postMsg.append("CfixProductConfigurationServiceImpl:: Adding Post=").append(post)
                        .append(" TargetEngine=").append(targetEngine);
                Log.information(postMsg.toString());
                postMsg.setLength(0);
            }

            postMsg.append("CfixProductConfigurationServiceImpl:: Number of Posts read from '")
                   .append(postToTargetCompIDMapFile).append("' = ")
                   .append(tempPostToTargetCompIDMap.size());
            Log.information(postMsg.toString());

            postToTargetCompIDMap = tempPostToTargetCompIDMap;
        }
        catch (Exception ex)
        {
            Log.exception(ex);

            throw ex;
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch(Exception ex)
            {

            }
        }
    }

    public String getConfiguredTargetCompID(int classKey, String askingTargetCompID)
    {
        if (handleAllPosts)
        {
            return askingTargetCompID;
        }

        try
        {
            GroupStruct[] groupData = (GroupStruct[]) pcsGroupCacheMap.getValueForKey(classKey);

            if (groupData == null)
            {
                groupData = ServicesHelper.getProductConfigurationService().getGroupsForProductClass(classKey);
                if (groupData != null && groupData.length > 0)
                {
                    pcsGroupCacheMap.putKeyValue(classKey, groupData);
                }
            }

            StringBuilder msg = new StringBuilder(150);
            for (int j = 0; j < groupData.length; j++)
            {
                // trim leading and trailing whitespace first
                String groupName = groupData[j].groupName.trim().toUpperCase();
                if (groupName.length() > GROUP_KEY_NAME.length() && groupName.startsWith(GROUP_KEY_NAME))
                {
                    msg.append("CfixProductConfigurationServiceImpl:: classKey = ").append(classKey)
                       .append(" groupName = ").append(groupName).append(" continue!!!");
                    Log.information(msg.toString());
                    msg.setLength(0);
                    continue;
                }
                String alternateEngine = (String) postToTargetCompIDMap.get(groupData[j].groupName);
                msg.append("CfixProductConfigurationServiceImpl:: classKey = ").append(classKey)
                   .append(" groupName = ").append(groupName)
                   .append(" alternateEngine = ").append(alternateEngine);
                Log.information(msg.toString());
                msg.setLength(0);

                if (alternateEngine != null)
                {
                    return alternateEngine;
                }
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }

        return null;
    }

    public Writer debugConfiguredTargetCompID(int classKey, Writer writer) throws Exception
    {
        writer.write("<classKey key=\"");
        writer.write(StringHelper.intToString(classKey));
        writer.write("\">");

        GroupStruct[] groupData = ServicesHelper.getProductConfigurationService().getGroupsForProductClass(classKey);

        writer.write("<configuredGroupKey>");

        for (int j = 0; j < groupData.length; j++)
        {
            String alternateEngine = (String) postToTargetCompIDMap.get(groupData[j].groupName);

            if (alternateEngine != null)
            {
                writer.write("<alternate name=\"" + groupData[j].groupName + "\" target=\"" + alternateEngine + "\"/>");
                break;
            }

            writer.write("<notalternate name=\"" + groupData[j].groupName + "\"/>");
        }

        writer.write("</configuredGroupKey>");

        writer.write("</classKey>");

        return writer;
    }

    public Map getPostToTargetCompIDMap()
    {
        return postToTargetCompIDMap;
    }
}
