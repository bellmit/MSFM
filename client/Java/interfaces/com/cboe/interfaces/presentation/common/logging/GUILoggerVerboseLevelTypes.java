/**
 * Created by IntelliJ IDEA.
 * User: Brazhni
 * Date: Jul 28, 2003
 * Time: 2:35:17 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.presentation.common.logging;

public interface GUILoggerVerboseLevelTypes
{
    public static final int VERBOSE_LEVEL_MIN = 0;

    public static final int VERBOSE_LEVEL_LOW = VERBOSE_LEVEL_MIN;
    public static final int VERBOSE_LEVEL_NORMAL = VERBOSE_LEVEL_LOW + 1;
    public static final int VERBOSE_LEVEL_HIGH = VERBOSE_LEVEL_NORMAL + 1;

    public static final int VERBOSE_LEVEL_MAX = VERBOSE_LEVEL_HIGH;
}
