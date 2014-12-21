/*
 * Created by IntelliJ IDEA.
 * User: BRAZHNI
 * Date: Aug 19, 2002
 * Time: 11:19:19 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.FormatterCache;

public class FormatterCacheFactory
{
    public static FormatterCache create()
    {
        return new FormatterCacheImpl();
    }

    public static FormatterCache create(boolean enabled)
    {
        return new FormatterCacheImpl(enabled);
    }

    public static FormatterCache create(int initialCapacity)
    {
        return new FormatterCacheImpl(initialCapacity);
    }

    public static FormatterCache create(int initialCapacity, boolean enabled)
    {
        return new FormatterCacheImpl(initialCapacity, enabled);
    }

    public static FormatterCache create(int initialCapcity, int styleInitialCapacity)
    {
        return new FormatterCacheImpl(initialCapcity, styleInitialCapacity);
    }

    public static FormatterCache create(int initialCapcity, int styleInitialCapacity, boolean enabled)
    {
        return new FormatterCacheImpl(initialCapcity, styleInitialCapacity, enabled);
    }

}
