package com.cboe.client.util.tourist;

/**
 * TouristIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Smart visitor to the framework
 *
 */

import java.io.*;
import java.util.*;

public interface TouristIF
{
    public void       addParameter(String key, String value);
    public String[]   getMandatoryKeys();
    public boolean    validateParameters(Writer writer) throws Exception;
    public Writer     visit(Writer writer) throws Exception;
    public boolean    waitUntilFinished(long microseconds);
    public void       finished();
    public void       setIsHttp(boolean isHttp);
    public boolean    isHttp();
    public Properties getProperties();
}
