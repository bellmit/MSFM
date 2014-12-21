package com.cboe.client.util;

/**
 * RunnableInitializableIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Runnable that can be initialized from a property file and a prefix
 *
 */

import java.util.*;

public interface RunnableInitializableIF extends Runnable
{
    public void initialize(String propertyPrefix, Properties properties) throws Exception;
}
