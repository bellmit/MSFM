package com.cboe.domain.util;

import java.util.HashMap;
import java.util.Iterator;

public class StructFactoryList
{
    private static ThreadLocal<HashMap<StructFactoryIF, StructFactoryIF>> factoryList = new ThreadLocal<HashMap<StructFactoryIF, StructFactoryIF>> ()
    {
        protected HashMap<StructFactoryIF, StructFactoryIF> initialValue()
        {
            return new HashMap<StructFactoryIF, StructFactoryIF>();
        }
    };
    
    public static void enter()
    {
    }
    
    public static void addToFactoryList(StructFactoryIF factory)
    {
        factoryList.get().put(factory,factory);
    }
    
    public static void releaseAll ()
    {
        try
        {
            HashMap<StructFactoryIF, StructFactoryIF> list = factoryList.get();
            
            if (list.size() > 0)
            {
                Iterator<StructFactoryIF> iterator = list.values().iterator();
                
                while (iterator.hasNext())
                {
                    try
                    {
                        iterator.next().releaseAll();
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }
        finally
        {
            factoryList.get().clear();
        }
    }
}

