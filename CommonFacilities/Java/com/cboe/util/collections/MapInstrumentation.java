package com.cboe.util.collections;

/**
 * MapInstrumentation.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class MapInstrumentation
{
    protected int added;
    protected int replaced;
    protected int removed;
    protected int hit;
    protected int miss;

    public static final MapInstrumentation NopMapInstrumentation = new MapInstrumentation()
    {
        public int getAdded() {return 0;}
        public void setAdded(int added) {}
        public void incAdded() {}
        public void incAdded(int inc) {}
        public int getReplaced() {return 0;}
        public void setReplaced(int replaced) {}
        public void incReplaced() {}
        public void incReplaced(int inc) {}
        public int getRemoved() {return 0;}
        public void setRemoved(int removed) {}
        public void incRemoved() {}
        public void incRemoved(int inc) {}
        public int getHit() {return 0;}
        public void setHit(int hit) {}
        public void incHit() {}
        public void incHit(int inc) {}
        public int getMiss() {return 0;}
        public void setMiss(int miss) {}
        public void incMiss() {}
        public void incMiss(int inc) {}
    };

    public int getAdded()
    {
		return added;
	}

    public void setAdded(int added)
    {
		this.added = added;
	}

    public void incAdded()
    {
		added++;
	}

    public void incAdded(int inc)
    {
		added += inc;
	}

    public int getReplaced()
    {
		return replaced;
	}

    public void setReplaced(int replaced)
    {
		this.replaced = replaced;
	}

    public void incReplaced()
    {
		replaced++;
	}

    public void incReplaced(int inc)
    {
		replaced += inc;
	}

    public int getRemoved()
    {
		return removed;
	}

    public void setRemoved(int removed)
    {
		this.removed = removed;
	}

    public void incRemoved()
    {
		removed++;
	}

    public void incRemoved(int inc)
    {
		removed += inc;
	}

    public int getHit()
    {
		return hit;
	}

    public void setHit(int hit)
    {
		this.hit = hit;
	}

    public void incHit()
    {
		hit++;
	}

    public void incHit(int inc)
    {
		hit += inc;
	}

    public int getMiss()
    {
		return miss;
	}

    public void setMiss(int miss)
	{
		this.miss = miss;
	}

    public void incMiss()
	{
		miss++;
	}

    public void incMiss(int inc)
	{
		miss += inc;
	}
}
