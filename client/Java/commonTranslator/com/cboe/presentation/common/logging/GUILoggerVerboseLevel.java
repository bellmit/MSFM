/**
 * Created by IntelliJ IDEA.
 * User: Brazhni
 * Date: Jul 28, 2003
 * Time: 2:28:00 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.presentation.common.logging;

import com.cboe.interfaces.presentation.common.logging.IGUILoggerVerboseLevel;
import com.cboe.interfaces.presentation.common.logging.GUILoggerVerboseLevelTypes;

public class GUILoggerVerboseLevel extends GUILoggerProperty implements IGUILoggerVerboseLevel
{
    public static final IGUILoggerVerboseLevel LOW = new GUILoggerVerboseLevel(GUILoggerVerboseLevelTypes.VERBOSE_LEVEL_LOW, "Low");
    public static final IGUILoggerVerboseLevel NORMAL = new GUILoggerVerboseLevel(GUILoggerVerboseLevelTypes.VERBOSE_LEVEL_NORMAL, "Normal");
    public static final IGUILoggerVerboseLevel HIGH = new GUILoggerVerboseLevel(GUILoggerVerboseLevelTypes.VERBOSE_LEVEL_HIGH, "High");

    private static final IGUILoggerVerboseLevel[] verboseLevels = {LOW, NORMAL, HIGH};

    protected GUILoggerVerboseLevel(int key, String name)
    {
        super(key, name);
    }

    public static IGUILoggerVerboseLevel getProperty(int index)
    {
        return verboseLevels[index];
    }

    public static IGUILoggerVerboseLevel[] getVerboseLevels()
    {
        return verboseLevels;
    }

    public static int getMinIndex()
    {
        return GUILoggerVerboseLevelTypes.VERBOSE_LEVEL_MIN;
    }

    public static int getMaxIndex()
    {
        return GUILoggerVerboseLevelTypes.VERBOSE_LEVEL_MAX;
    }

    public String toString()
    {
        return getName();
    }
}
