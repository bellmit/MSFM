package com.cboe.domain.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class QueryThrottler
{
    private final Set<Long> set;
    private final Semaphore semaphore;
    
    public QueryThrottler(int maxNumberAccesses)
    {
        this.set = Collections.synchronizedSet(new HashSet<Long>());
        this.semaphore = new Semaphore(maxNumberAccesses, true);
    }
    
    public boolean acquire() throws InterruptedException
    {
        semaphore.acquire();
        boolean isSuccess = false;
        try {
            isSuccess = set.add(new Long(Thread.currentThread().getId()));
            return isSuccess;
        }
        finally {
            if (!isSuccess) {
                semaphore.release();
            }
        }
    }
    
    public boolean release() {
        boolean isReleased = set.remove(new Long(Thread.currentThread().getId()));
        if (isReleased) {
            semaphore.release();
        }
        return isReleased;
    }
}
