package com.cboe.interfaces.cfix;

/**
 * CfixProductConfigurationService.java
 *
 * @author Dmitry Volpyansky
 * @author Jing Chen
 *
 */

import java.io.*;
import java.util.*;

public interface CfixProductConfigurationService
{
    public String  getConfiguredTargetCompID(int classKey, String askingTargetCompID);
    public Map     getPostToTargetCompIDMap();
    public Writer  debugConfiguredTargetCompID(int classKey, Writer writer) throws Exception;
    public void    clearPcsGroupCache();
    public boolean getHandleAllPosts();
    public boolean setHandleAllPosts(boolean handleAllPosts);
}
