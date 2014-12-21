package com.cboe.application.cache;

// Java classes
import java.util.*;

/**
 * Implements LRU (Least Recently Used), LFU (Least Frequently Used) and LRFU (Least Recent Frequently Used) counters.
 * <p>
 * This class tracks the usage of any object registered with it. Whenever an object is used, a call to
 * <code>incrementCount</code> will cause the counter to update access times and frequency counts on that
 * object. A utility class <code>Counter</code> is used to track these counts.
 * <p>
 * LRU and LFU calculations are extremely simple - just determine which object has a Counter with the
 * lowest access time (LRU) or frequency of increments (LFU). These objects can be then retrieved and,
 * if needed, removed from the list of tracked objects.
 * <p>
 * LRFU is a more complicated concept. LRU and LFU both have degenerate cases that can cause a "bad" aging
 * out. LRU fails when there have been a bunch of low-volume hits on some objects very recently, so that
 * a heavily used object that falls just outside the "time window" gets aged out by a bunch of relatively
 * unused objects. LFU fails when objects are used very heavily early in a lifecycle (say, during initialization)
 * but are then rarely used throughout the rest of the lifecycle. These "heavy objects" can keep killing off
 * more immediately useful objects by never allowing them to gain the kind of frequency that would keep them
 * from being aged out.
 * <p>
 * LRFU deals with these faults by employing a "weighted frequency". After a set number of counter increments,
 * the list of tracked objects is scanned and the weighted frequency is divided by a set amount. As time goes by,
 * any old increments gradually decrease in importance as they are divided down. Newer increments count more as
 * they have been divided less often. The object with the lowest weighted frequency will be aged out. In cases
 * where more than one object has the "low" weighted frequency, the object with the highest "absolute" (unweighted)
 * frequency will be aged out, as it can be assumed to be older (it had to take longer to get <b>down</b> to this
 * low number).
 * <p>
 * Currently, we default to reweight after 250 increments and divide by 2, so after each 250 increments,
 * existing increments are worth only half as much as they used to be. While this is "unfair" to increments
 * that occurred just prior to reweighting, making the process truly fair makes the algorithm overly complex.
 * <p>
 * @author Brian Erst
 * @version 2001.8.09
 */

public class LRFUCounter
{
    Map countMap;
    int lastAccess;
    int reweightAfterNIncrements = 250;
    int reweightFactor = 2;

    private void init()
    {
        countMap = new HashMap();
        lastAccess = 0;
    }

    public LRFUCounter()
    {
        init();
    }

    public LRFUCounter(int _reweightAfterNIncrements, int _reweightFactor)
    {
        init();
        reweightAfterNIncrements = _reweightAfterNIncrements;
        reweightFactor = _reweightFactor;
    }

    private Map getMap()
    {
        return countMap;
    }

    private void reweight()
    {
        Iterator it = getMap().values().iterator();
        while (it.hasNext())
        {
            Counter counter = (Counter) it.next();
            counter.weightedFrequency /= reweightFactor;
        }
    }

    public synchronized void incrementCount(Object countee, int frequencyIncrement)
    {
        // It's time to reweight the existing frequencies if we've gone
        // thru another set of increments
        if (++lastAccess % reweightAfterNIncrements == 0)
        {
            reweight();
        }

        Counter counter = (Counter) getMap().get(countee);
        if (counter == null)
        {
            counter = new Counter(countee, lastAccess, frequencyIncrement);
        }
        else
        {
            counter.increment(lastAccess, frequencyIncrement);
        }

        getMap().put(countee, counter);
    }

    public synchronized void incrementCount(Object countee)
    {
        incrementCount(countee, 1);
    }

    public synchronized Object retrieveLRUObject(boolean removeObject)
    {
        Iterator it           = getMap().values().iterator();
        Counter  bestEntry    = null;
        int      oldestAccess = java.lang.Integer.MAX_VALUE;

        while (it.hasNext())
        {
            Counter counter = (Counter) it.next();

            if (counter.lastAccess < oldestAccess)
            {
                oldestAccess = counter.lastAccess;
                bestEntry = counter;
            }
        }

        if (removeObject)
        {
            getMap().remove(bestEntry.countee);
        }

        return bestEntry.countee;
    }

    public synchronized Object retrieveLFUObject(boolean removeObject)
    {
        Iterator it            = getMap().values().iterator();
        Counter  bestEntry     = null;
        int      leastFrequent = java.lang.Integer.MAX_VALUE;

        while (it.hasNext())
        {
            Counter counter = (Counter) it.next();

            if (counter.frequency < leastFrequent)
            {
                leastFrequent = counter.frequency;
                bestEntry = counter;
            }
        }

        if (removeObject)
        {
            getMap().remove(bestEntry.countee);
        }

        return bestEntry.countee;
    }

    public synchronized Object retrieveLRFUObject(boolean removeObject)
    {
        Iterator it            = getMap().values().iterator();
        Counter  bestEntry     = null;
        int      leastFrequent = java.lang.Integer.MAX_VALUE;
        int      frequency     = 0;

        while (it.hasNext())
        {
            Counter counter = (Counter) it.next();

            // Weighted frequency diminishes by time - so huge hits a long time ago are not as important
            // as fewer hits recently. When the weighted frequencies don't differ (really old vs. hit
            // only sporadically but recently), kill the "older" one by using its high absolute frequency
            // as an indicator of its extreme age.
            if ( (counter.weightedFrequency < leastFrequent) ||
                 ( (counter.weightedFrequency == leastFrequent) && (counter.frequency > frequency) ) )
            {
                leastFrequent = counter.weightedFrequency;
                frequency     = counter.frequency;
                bestEntry     = counter;
            }
        }

        if (removeObject)
        {
            getMap().remove(bestEntry.countee);
        }

        return bestEntry.countee;
    }

    public synchronized void cleanup()
    {
        getMap().clear();
    }

/*
    public static void main(String[] args)
    {
        String earlyHeavy = new String("Heavy");
        String frequentUse1 = new String("Frequent 1");
        String frequentUse2 = new String("Frequent 2");
        String frequentUse3 = new String("Frequent 3");
        String lateLight = new String("Light");
        LRFUCounter counter = new LRFUCounter();

        for (int i=0; i<5000; i++)
        {
            // Forced LFU degenerate case: earlyHeavy gets slammed to start, but is only hit once again
            if (i < 2000)
                counter.incrementCount(earlyHeavy);

            if ((i > 2000) && (i < 4995))
            {
                if (i%3 == 0)
                    counter.incrementCount(frequentUse1);
                if (i%3 == 1)
                    counter.incrementCount(frequentUse2);
                if (i%3 == 2)
                    counter.incrementCount(frequentUse3);
            }

            // LRFU proof: The early heavy is ONCE after the frequent users, but their more recent
            // heavier use weighs more than its early heaviness
            if (i==4995)
                counter.incrementCount(earlyHeavy);

            // Forced LRU degenerate case: A heretofore untouched object gets hit a few times
            // just before the end...
            if (i > 4996)
            {
                counter.incrementCount(lateLight);
            }
        }

        // LRFU picks the "best" object to remove - one that is least likely to be used again
        System.out.println("LRFU picks = "+counter.retrieveLRFUObject(false).toString());

        // Prove the LFU degenerate case, then remove it to show the LRU degenerate case
        System.out.println("LFU picks  = "+counter.retrieveLFUObject(true).toString());

        // Prove the LRU degenerate case - a much more heavily used object will die because a
        // rarely-used object was hit recently
        System.out.println("LRU picks  = "+counter.retrieveLRUObject(false).toString());

    }
*/
}

class Counter
{
    Object countee;
    int    frequency, weightedFrequency;
    int    lastAccess;

    public Counter(Object _countee, int _lastAccess, int initialFrequency)
    {
        countee            = _countee;
        frequency          = initialFrequency;
        weightedFrequency  = initialFrequency;
        lastAccess         = _lastAccess;
    }

    public void increment(int _lastAccess,  int frequencyIncrement)
    {
        lastAccess = _lastAccess;
        weightedFrequency += frequencyIncrement;
        frequency += frequencyIncrement;
    }
}