package com.cboe.cfix.interfaces;

/**
 * FixSessionWriterIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;
import com.cboe.client.util.queue.*;

public interface FixSessionWriterIF
{
    public DoublePriorityEventChannelIF getFixEventChannel();
}