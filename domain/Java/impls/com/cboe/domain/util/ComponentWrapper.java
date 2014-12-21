package com.cboe.domain.util;

import java.util.Date;

public class ComponentWrapper
{
    private int componentInt;
    private double componentDouble;
    
    public ComponentWrapper()
    {
        super();
        componentInt = 0;
        componentDouble = 0.0d;
    }
    
    public ComponentWrapper(int component)
    {
        super();
        setComponent(component);
    }
    
    public ComponentWrapper(double component)
    {
        super();
        setComponent(component);
    }
    
    public void setComponent(int component)
    {
        componentInt = component;
        componentDouble = (double)componentInt;
    }
    
    public void setComponent(double component)
    {
        componentInt = (int)component;
        componentDouble = component;
    }
    
    public double getComponentAsDouble()
    {
        return componentDouble;
    }
    
    public int getComponentAsInt()
    {
        return componentInt;
    }
}
