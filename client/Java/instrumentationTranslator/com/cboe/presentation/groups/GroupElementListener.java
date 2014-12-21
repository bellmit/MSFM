package com.cboe.presentation.groups;

import java.util.EventListener;

public interface GroupElementListener extends EventListener
{
    public void acceptAddElement(GroupElementCacheEventContainer groupElementCacheEventCointainer);
    public void acceptRemoveElement(GroupElementCacheEventContainer groupElementCacheEventCointainer);
    public void acceptUpdateElement(GroupElementModel elementModel);
}
