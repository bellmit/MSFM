/*
 * Created by IntelliJ IDEA.
 * User: BRAZHNI
 * Date: Aug 19, 2002
 * Time: 9:22:40 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.common.formatters.FormatterCache;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class FormatterCacheImpl implements FormatterCache, PropertyChangeListener
{
    private HashMap objectMap;
    private int styleCapacity;
    private boolean cacheEnabled = true;
    private static int defaultObjectCapacity = 100;
    private static int defaultStyleCapacity = 5;


    public FormatterCacheImpl()
    {
        this(defaultObjectCapacity, defaultStyleCapacity, true);
    }

    public FormatterCacheImpl(boolean enabled)
    {
        this(defaultObjectCapacity, defaultStyleCapacity, enabled);
    }

    public FormatterCacheImpl(int initialCapacity)
    {
        this(initialCapacity, defaultStyleCapacity, true);
    }

    public FormatterCacheImpl(int initialCapacity, boolean enabled)
    {
        this(initialCapacity, defaultStyleCapacity, enabled);
    }

    public FormatterCacheImpl(int initialCapacity, int styleCapacity)
    {
        this(initialCapacity, styleCapacity, true);
    }

    public FormatterCacheImpl(int initialCapacity, int styleCapacity, boolean enabled)
    {
        super();
        objectMap = new HashMap(initialCapacity);
        this.styleCapacity = styleCapacity;
        setCacheEnabled(enabled);
    }

    public synchronized Object put(Object key, Object style, String value)
    {
        Object oldValue = null;
        if (isCacheEnabled())
        {
            HashMap styleMap = (HashMap)getObjectMap().get(key);
            if (styleMap == null)
            {
                styleMap = new HashMap(styleCapacity);
                getObjectMap().put(key, styleMap);
            }
            oldValue = styleMap.put(style, value);
            
            if (key instanceof AbstractMutableBusinessModel)
            {
                ((AbstractMutableBusinessModel)key).addPropertyChangeListener(this);
            }
            
        }
        
        return oldValue;
    }

    public synchronized String get(Object key, Object style)
    {
        String value = null;
        if (isCacheEnabled())
        {
            HashMap styleMap = (HashMap)getObjectMap().get(key);
            if (styleMap != null)
            {
                value = (String)styleMap.get(style);
            }
        }
        return value;
    }

    private HashMap getObjectMap()
    {
        return objectMap;
    }

    public void setCacheEnabled(boolean flag)
    {
        this.cacheEnabled = flag;
    }

    public boolean isCacheEnabled()
    {
        return this.cacheEnabled;
    }
    
    /* (non-Javadoc)
     * @see com.cboe.util.channel.ChannelListener#channelUpdate(com.cboe.util.channel.ChannelEvent)
     */
    public synchronized void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getPropertyName() == MutableBusinessModel.DATA_CHANGE_EVENT)
        {
            getObjectMap().remove(evt.getSource());
        }
    }
}
