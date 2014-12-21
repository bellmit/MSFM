package com.cboe.presentation.api;

import java.util.*;

import com.cboe.exceptions.*;


public class TradingSessionFinder {

    private static Map sessionsByClass = null;

    public TradingSessionFinder()
    {
        super();
        sessionsByClass = new HashMap();
    }

    public synchronized void addClassToSession(int classKey, String sessionName)
    {
        Integer classTemp = new Integer(classKey);
        if(sessionsByClass.containsKey(classTemp)) {
            LinkedList list = (LinkedList)sessionsByClass.get(classTemp);
            if (!list.contains(sessionName))
            {
                LinkedList clonedNewList = (LinkedList)list.clone();
                clonedNewList.add(sessionName);
                sessionsByClass.put(classTemp, clonedNewList);
            }

        }
    }

    public synchronized LinkedList getTradingSessions(int classKey)
    {
        LinkedList list = null;
        Integer classTemp = new Integer(classKey);
        list = (LinkedList)sessionsByClass.get(classTemp);
        return list;
    }
}