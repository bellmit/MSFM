package com.cboe.domain.util.fixUtil;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Convience methods to get collections of FixTags.
 *
 * Date: Aug 27, 2004
 */
public class FixTagHelper
{
    private static FixTag[] allFixTags;
    private static FixTagMap allFixTagsMap;
    private static FixTag[] userAssignedIDTags;

    public static FixTag[] getAllTags()
    {
        if(allFixTags == null)
        {
//            allFixTags = (FixTag[])getAllTagsMap().values().toArray(new FixTag[getAllTagsMap().size()]);
            TreeMap sortedMap = new TreeMap(getAllTagsMap());
            allFixTags = (FixTag[])sortedMap.values().toArray(new FixTag[sortedMap.size()]);
        }
        return allFixTags;
    }

    // if any more tags are allowed in UserAssignedID, they should be added here
    public static FixTag[] getUserAssignedIDTags()
    {
        if(userAssignedIDTags == null)
        {
            FixTagMap map = getAllTagsMap();
            userAssignedIDTags = new FixTag[9];
            userAssignedIDTags[0] = map.get(FixUtilConstants.SenderSubID.TAGNUMBER);
            userAssignedIDTags[1] = map.get(FixUtilConstants.SenderLocationID.TAGNUMBER);
            userAssignedIDTags[2] = map.get(FixUtilConstants.OnBehalfOfCompID.TAGNUMBER);
            userAssignedIDTags[3] = map.get(FixUtilConstants.OnBehalfOfSubID.TAGNUMBER);
            userAssignedIDTags[4] = map.get(FixUtilConstants.OnBehalfOfLocationID.TAGNUMBER);
            userAssignedIDTags[5] = map.get(FixUtilConstants.TargetLocationID.TAGNUMBER);
            userAssignedIDTags[6] = map.get(FixUtilConstants.STOCK_FIRM_NAME.TAGNUMBER);
            userAssignedIDTags[7] = map.get(FixUtilConstants.STOCK_FIRM_NAME_KEY.TAGNUMBER);
            userAssignedIDTags[8] = map.get(FixUtilConstants.ExDestination.TAGNUMBER);
        }
        return userAssignedIDTags;
    }

    // if a new FIX tag class is added to FixUtilConstants, it should also be added here.
    public static FixTagMap getAllTagsMap()
    {
        if(allFixTagsMap == null)
        {
            allFixTagsMap = new FixTagMap(210);
            FixTag aTag;
            // use reflection to create a FixTag for every class in FixUtilConstants that
            //    has TAGNAME and TAGNUMBER fields
            Class[] fixConstantClasses = FixUtilConstants.class.getClasses();
            for(int i=0; i<fixConstantClasses.length; i++)
            {
                try
                {
                    aTag = new FixTag(fixConstantClasses[i]);
                    allFixTagsMap.put(aTag);
                }
                catch(IllegalArgumentException e)
                {
                    // do nothing -- this exception is ok because not all classes in FixUtilConstants represent a FixTag (with TAGNAME and TAGNUMBER fields)
                }
            }
        }
        return allFixTagsMap;
    }
}
