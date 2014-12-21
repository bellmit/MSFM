package com.cboe.infra.presentation.network;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * This is a cache for the Topic objects.  The objects are readonly and used a lot,
 * so it is best to cache them with a simple cache.
 */
public class TopicCache
{
    private static TopicCache instance;
    private Map    topicCache;

    private TopicCache()
    {
        topicCache = new HashMap(100);    
    }

    public Topic getTopic(String subject, String localGlobal)
    {
        Map topicMap = (Map) topicCache.get(localGlobal);
        if (topicMap == null)
        {
            topicMap = new HashMap();
            topicCache.put(localGlobal,topicMap);
        }
        Topic topic = (Topic) topicMap.get(subject);
        if (topic == null)
        {
            topic = createTopic(subject,localGlobal);
            topicMap.put(subject,topic);
        }
        return topic;
    }

    protected Topic createTopic(String subject, String localGlobal) 
    {
        Topic rv = null;
        StringTokenizer parts = new StringTokenizer(subject,"\\/");
        if (parts.countTokens() > 1)
        {
            try {
                String channelName = parts.nextToken();
                // for now, weed out all admin subjects
                if (channelName.indexOf("Admin") != -1) {
                    return rv;
                }

                String extentName = null;
                while (parts.hasMoreTokens())
                {
                    extentName = parts.nextToken();
                }
                StringTokenizer extentSegs = new StringTokenizer(extentName,"-");
                // also: test for Admin conditionally (not like above)
                if (extentSegs.countTokens() < 3) {
                    // this is not one we're looking for
                    extentName = "ERR:" + extentName;
                } else {
                    extentSegs.nextToken();
                    extentName = extentSegs.nextToken() + "---" + extentSegs.nextToken();
                }

                // local subjects only!
                if (extentName.indexOf(localGlobal) == -1) {
                    return rv;
                }

                // create the Topic:
                rv = new Topic(channelName, extentName, false);
            } catch (Exception e)
            {
                GUILoggerHome.find().exception("Channel/extent extraction failed for subject " + subject + ": " + e.getMessage(),e);
            }
        }
        return rv;

    }

    public static TopicCache getInstance()
    {
        if (instance == null)
        {
            instance = new TopicCache();
        }
        return instance;
    }
}
