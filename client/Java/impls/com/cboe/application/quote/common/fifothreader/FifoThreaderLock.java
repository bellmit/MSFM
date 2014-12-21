package com.cboe.application.quote.common.fifothreader;

/**
 * FifoThreaderLock.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * This lock manages all ByClass and ByProduct acquisitions and cancellations of a specific class
 */

import com.cboe.client.util.queue.*;

public abstract class FifoThreaderLock
{
    protected          int    lockState = LOCK_STATE_IS_UNUSED;
    public    volatile Thread lockOwnerThread;
    public             String lockName;

    public static final int LOCK_STATE_IS_UNUSED   = 1;
    public static final int LOCK_STATE_IS_LOCKED   = 2;
    public static final int LOCK_STATE_IS_HANDOVER = 3;

    public static final int TYPE_CANCEL_CLASS   = 1;
    public static final int TYPE_ACCEPT_CLASS   = 2;
    public static final int TYPE_CANCEL_PRODUCT = 3;
    public static final int TYPE_ACCEPT_PRODUCT = 4;

    public FifoThreaderLock()
    {

    }

    public FifoThreaderLock(String lockName)
    {
        this.lockName = lockName;
    }

    public FifoThreaderLock acquireAcceptClassLock()
    {
        return waitBlockedNode(TYPE_ACCEPT_CLASS);
    }

    public FifoThreaderLock acquireCancelClassLock()
    {
        return waitBlockedNode(TYPE_CANCEL_CLASS);
    }

    public FifoThreaderLock acquireAcceptProductLock()
    {
        return waitBlockedNode(TYPE_ACCEPT_PRODUCT);
    }

    public FifoThreaderLock acquireCancelProductLock()
    {
        return waitBlockedNode(TYPE_CANCEL_PRODUCT);
    }

    /**
     * Try to acquire the lock (or check if was cancelled), blocking, if necessary, until the
     * lock is available
     */
    protected FifoThreaderLock waitBlockedNode(int type)
    {
        synchronized(this)
        {
            if (lockState == FifoThreaderLock.LOCK_STATE_IS_UNUSED)
            {
                lockState       = FifoThreaderLock.LOCK_STATE_IS_LOCKED;
                lockOwnerThread = Thread.currentThread();

                return this;
            }
        }

        return (new BlockedNode()).blockWaitingForLock(this, type);
    }

    /**
     * If could not immediately acquire the lock (or if was not immediately cancelled),
     * place this node into the queue
     */
    protected boolean enqueueToBeProcessedLater(BlockedNode blockedNode, int lockType)
    {
        synchronized(this)
        {
            if (lockState == FifoThreaderLock.LOCK_STATE_IS_UNUSED)
            {
                lockState       = FifoThreaderLock.LOCK_STATE_IS_LOCKED;
                lockOwnerThread = Thread.currentThread();

                return true;
            }

            queueEnqueue(blockedNode, lockType);
        }

        return false;
    }

    /**
     * If the current thread is the owner of the lock, and it is done with the lock, then it HAS to wake up
     * the next thread on the queue
     */
    public void releaseLock()
    {
        synchronized(this)
        {
            // If there IS an owner of the lock, and it is not this thread, then let them drain the queue.
            // If there is NO owner of the lock, we HAVE to take ownership of draining the queue,
            // otherwise we WILL have a situation where there is no one draining the queue
            if (lockOwnerThread != null && lockOwnerThread != Thread.currentThread())
            {
                return;
            }

            // in this case, we drain the queue until we have someone who needs to acquire a lock
            while (!queueIsEmpty())
            {
                BlockedNode blockedNode = null;

                try
                {
                    blockedNode = queueDequeue();
                }
                catch (Exception ex)
                {

                }

                if (blockedNode != null && blockedNode.unblock())  // wake up the other blocked thread if still waiting, else continue
                {
                    return;
                }
            }

            lockState       = FifoThreaderLock.LOCK_STATE_IS_UNUSED;
            lockOwnerThread = null;
        }
    }

    /**
     * A Blocked node represents a Blocked Thread -- waiting to be released
     */
    protected class BlockedNode
    {
        protected boolean waitingToBeNotified = true;
        protected Thread  ownerThread;

        public BlockedNode()
        {
            this.ownerThread = Thread.currentThread();
        }

        public Thread getOwnerThread()        {return ownerThread;}

        /**
         * wake up the thread that is waiting on this BlockedNode sync lock
         */
        public boolean unblock()
        {
            synchronized(this)
            {
                if (waitingToBeNotified)
                {
                    waitingToBeNotified = false;

                    lockState       = FifoThreaderLock.LOCK_STATE_IS_HANDOVER;
                    lockOwnerThread = getOwnerThread();

                    notify();

                    return true;
                }
            }

            return false;
        }

        /**
         * Block waiting for somebody to wake us up
         */
        protected FifoThreaderLock blockWaitingForLock(FifoThreaderLock fifoThreaderLock, int lockType)
        {
            if (fifoThreaderLock.enqueueToBeProcessedLater(this, lockType))
            {
                return fifoThreaderLock;
            }

            synchronized(this)
            {
                while (waitingToBeNotified)
                {
                    try
                    {
                        wait();
                    }
                    catch (InterruptedException ex)
                    {

                    }
                }

                waitingToBeNotified = false;
            }

            synchronized(FifoThreaderLock.this)
            {
                lockState       = FifoThreaderLock.LOCK_STATE_IS_LOCKED;
                lockOwnerThread = Thread.currentThread();
            }

            return fifoThreaderLock;
        }
    }

    public abstract boolean     queueIsEmpty();
    public abstract BlockedNode queueDequeue() throws Exception;
    public abstract void        queueEnqueue(BlockedNode blockedNode, int lockType);

    public static FifoThreaderLock createEqualPriorityFifoThreaderLock()
    {
        return new FifoThreaderLock()
            {
                protected SinglePriorityEventChannelIF waitQueue = new SinglePriorityEventChannel();

                public boolean     queueIsEmpty()      {return waitQueue.size() == 0;}
                public BlockedNode queueDequeue() throws Exception {return (BlockedNode) waitQueue.dequeue();}
                public void        queueEnqueue(BlockedNode blockedNode, int lockType) {waitQueue.enqueue(blockedNode);}
                public String      toString()
                {
                    StringBuilder qsize = new StringBuilder(25);
                    qsize.append("queueSize[").append(waitQueue.size()).append(']');
                    return qsize.toString();
                }
            };
    }

    public static FifoThreaderLock createCancelIsHigherPriorityFifoThreaderLock()
    {
        return new FifoThreaderLock()
            {
                protected DoublePriorityEventChannelIF waitQueue = new DoublePriorityEventChannel();

                public boolean     queueIsEmpty()         {return waitQueue.size() == 0;}
                public BlockedNode queueDequeue() throws Exception {return (BlockedNode) waitQueue.dequeue();}
                public void        queueEnqueue(BlockedNode blockedNode, int lockType)
                {
                    switch (lockType)
                    {
                        case TYPE_CANCEL_CLASS:
                        case TYPE_CANCEL_PRODUCT:
                            waitQueue.enqueueHighPriority(blockedNode);
                            break;
                        case TYPE_ACCEPT_CLASS:
                        case TYPE_ACCEPT_PRODUCT:
                        default:
                            waitQueue.enqueue(blockedNode);
                            break;
                    }
                }
                public String toString()
                {
                    StringBuilder sizes = new StringBuilder(50);
                    sizes.append("queueAcceptSize[").append(waitQueue.normalPrioritySize())
                         .append("] queueCancelSize[").append(waitQueue.highPrioritySize()).append(']');
                    return sizes.toString();
                }
            };
    }
}
