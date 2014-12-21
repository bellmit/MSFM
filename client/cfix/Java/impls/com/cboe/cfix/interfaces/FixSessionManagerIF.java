package com.cboe.cfix.interfaces;

/**
 * FixSessionManagerIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.net.*;
import java.util.*;

public interface FixSessionManagerIF extends FixSessionListenerIF
{
    public void                     initialize(String propertyPrefix, Properties sessionProperties) throws Exception;
    public FixSessionIF             createFixSession(Socket socket) throws Exception;
    public FixSessionIF             getFixSessionByName(String name);
    public FixSessionInformationIF  getFixSessionInformationByName(String name);
    public void                     setEngineName(String engineName);
    public String                   getEngineName();
    public void                     copyFixSessionList(List list);
    public void                     copyFixSessionMap(Map map);
    public void                     copyFixSessionInformationMap(Map map);
    public void                     sessionReset(FixSessionIF fixSession);
    public void                     sessionLogonRequest(FixSessionIF fixSession, FixMessageIF fixMessage) throws Exception;
    public FixMessageFactoryIF      getFixMessageFactory();
    public int                      size();
}