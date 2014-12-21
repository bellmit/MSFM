//
// -----------------------------------------------------------------------------------
// Source file: LinkageStateCacheFactory.java
//
// PACKAGE: com.cboe.internalPresentation.product.models
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.presentation.common.logging.GUILoggerHome;


public class LinkageStateCacheFactory
{
    private static LinkageStateCache linkageStateCache = null;

    protected LinkageStateCacheFactory()
    {
    }
    
    public static LinkageStateCache find()
    {
        if (linkageStateCache == null)
        {
            try {
                linkageStateCache = new LinkageStateCache();
            } catch (Exception e) {
                GUILoggerHome.find().exception(e);
            }
        }
        return linkageStateCache;
    }

}
