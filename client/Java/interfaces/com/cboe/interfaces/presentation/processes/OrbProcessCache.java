/*
 * Created on Jun 8, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.interfaces.presentation.processes;

import com.cboe.util.event.EventChannelListener;

/**
 * @author I Nyoman Mahartayasa
 */
public interface OrbProcessCache
{

    public CBOEProcess[] getAllProcesses(EventChannelListener listener);
    
    public void unsubscribeAllProcesses(EventChannelListener listener);
    
    public void subscribeAllProcesses(EventChannelListener listener);
    
    public void cleanUp();
}
