/*
 * Created by IntelliJ IDEA.
 * User: BRAZHNI
 * Date: Aug 19, 2002
 * Time: 9:17:07 AM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.presentation.common.formatters;

public interface FormatterCache
{
    public Object put(Object key, Object style, String value);
    public String get(Object key, Object style);
    public void setCacheEnabled(boolean flag);
    public boolean isCacheEnabled();
}
