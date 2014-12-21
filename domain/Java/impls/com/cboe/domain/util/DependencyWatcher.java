package com.cboe.domain.util;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.DependencyObserver;
import com.cboe.interfaces.domain.DependencyCondition;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * A generic dependency watcher.  This class accepts observers and conditions, and notifies the
 * observers when the conditions have been met.  This implementation is intended for situations where
 * polling is required because the external framework where the condition disposition is governed does not
 * support an observer pattern directly.  An instance of this class may be re-used, but the instance can only
 * run in one thread at a time.  For this reason a subsequent use require all conditions to be met during the
 * previous run.
 * 
 * @author Eric Fredericks
 */

public final class DependencyWatcher implements Runnable
{
    private final String name;
    private final long sleepTime;
    private final Set conditions;
    private final Set observers;
    private Thread t;

    /**
     * Construct a new instance of DependencyWatcher.
     * 
     * @param name the name of the watcher
     * @param sleepTime the number of milliseconds to wait in before checking conditions again
     */
    public DependencyWatcher(final String name, final long sleepTime)
    {
        this.name = name;
        this.sleepTime = sleepTime;
        conditions = new HashSet();
        observers = new HashSet();
    }

    /**
     * Add an observer to be notified when conditions are met.
     * 
     * @param observer
     * @throws IllegalArgumentException if the observer to be added is null
     * @throws IllegalStateException if the dependency watcher is currently running
     */
    public synchronized void addObserver(final DependencyObserver observer)
    {
        if(observer == null)
        {
            throw new IllegalArgumentException("Cannot add null observer");
        }
        
        if(t != null)
        {
            throw new IllegalStateException("Cannot add obsever when dependency watcher has been started");
        }
        
        observers.add(observer);
    }

    /**
     * Remove an observer from the set of observers to be notified when conditions are met.
     * 
     * @param observer
     * @throws IllegalStateException if the dependency watcher is currently running
     */
    public synchronized void removeObserver(final DependencyObserver observer)
    {
        if(t != null)
        {
            throw new IllegalStateException("Cannot remove obsverver when dependency watcher is running"); 
        }
        
        observers.remove(observer);
    }

    /**
     * Add a condition to be checked and returned to the observers when it has been met
     * during a run of this class.
     * 
     * @param condition
     * @throws IllegalArgumentException if the condition to be added is null
     * @throws IllegalStateException if the dependency watcher is currently running
     */
    public synchronized void addCondition(final DependencyCondition condition)
    {
        if(condition == null)
        {
            throw new IllegalArgumentException("Cannot add null condition");
        }

        if(t != null)
        {
            throw new IllegalStateException("Cannot add condition when dependency watcher has been started");
        }

        conditions.add(condition);
    }

    /**
     * Remove a condition from the set of conditions to be checked.
     * 
     * @param condition
     */
    public synchronized void removeCondition(final DependencyCondition condition)
    {
        if(t != null)
        {
            throw new IllegalStateException("Cannot remove condition when dependency watcher is running"); 
        }
        
        conditions.remove(condition);
    }
    
    /**
     * Begin watching conditions.  This will put the dependency watcher in a state where neither conditions
     * nor observers can be removed.  May only be called if the watcher is not currently watching the conditions
     * in the condition set.
     * 
     * @throws IllegalStateException if the dependency watcher is already watching the conditions
     */
    public synchronized void start()
    {
        if(t != null)
        {
            throw new IllegalStateException("Already started");
        }
        
        if(observers.size() == 0)
        {
            Log.alarm("Dependency Watcher with name=" + name + " has no observers. Will not start.");
            return;
        }

        t = new Thread(this);
        t.start();
    }

    /**
     * Run method, which executes the watch cycle and calls the observers back when conditions
     * have been met.
     */
    public void run()
    {
        try
        {
            Log.information("Starting run of dependency watcher with name=" + name);
        
            while(conditions.size() != 0)
            {
                Thread.sleep(sleepTime);
                Iterator conditionIt = conditions.iterator();
                while(conditionIt.hasNext())
                {
                    DependencyCondition condition = (DependencyCondition) conditionIt.next();
                    if(condition.conditionMet())
                    {
                        Iterator observerIt = observers.iterator();
                        while(observerIt.hasNext())
                        {
                            DependencyObserver observer = (DependencyObserver) observerIt.next();
                            observer.notifyConditionMet(condition);
                        }
                        
                        conditionIt.remove();
                    }
                }
            }

            Log.information("Ending run of dependency watcher with name=" + name);
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            Log.exception("Dependency Watcher with name=" + name + " interrupted", e);
        }
        finally
        {
            t = null;
        }
    }
}
