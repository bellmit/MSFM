/*
 * Created on Jun 8, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.processes.OrbProcessCache;
import com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory;

/**
 * @author I Nyoman Mahartayasa
 */
public class OrbProcessCacheFactoryInstrumentationImpl implements OrbProcessCacheFactory
{


    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCacheFactory#getProcessCache()
     */
    public OrbProcessCache getProcessCache()
    {
        return (OrbProcessCache)InstrumentationTranslatorFactory.find();
    }

}
