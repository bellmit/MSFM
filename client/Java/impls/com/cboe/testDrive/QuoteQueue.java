package com.cboe.testDrive;

/**
 * This class implements a fifo queue for ThreadCommands
 * Note that it has package access.
 *
 * This version makes use of a fifo. By using a ring buffer
 * insertions and deletions are O(1), however, it does not support
 * priorities.
 *
 * @author Craig Murphy
 */

import java.util.Enumeration;

public class QuoteQueue extends Object
{
    private QuoteBlock queue[];
    private int capacity;
    private int size;
    private int maxCount;
    private int head; // Next place to retrieve a command from
    private int tail; // Next place to put a command into
    // If head == tail, the queue is either full or empty. That is
    // why we keep the currentCount.


    /**
     * Constructor
     */
    public QuoteQueue(int capacity)
    {
        this.capacity = (capacity > 0) ? capacity :1;
        queue = new QuoteBlock[this.capacity];
        head = 0;
        tail =0;
        size = 0;
    }

    public synchronized void waitWhileFull()
    {
        try
        {
            while (isFull())
            {
                wait();
            }
        } catch (Exception e){}
    }

    public synchronized void waitWhileEmpty()
    {
        try
        {
            while (isEmpty())
            {
                wait();
            }
        } catch (Exception e) {}
    }

    public synchronized boolean isFull()
    {
        return (size == this.capacity);
    }

    public synchronized boolean isEmpty()
    {
        return 0 == size;
    }

    public synchronized int getQueueSize()
    {
        return size;
    }

    public synchronized int getMaxQueueSize()
    {
        return maxCount;
    }

    /**
     * Adds a new command to the queue
     *
     * @param QuoteBlock  -- the BlockQuote object to place in the queue
     */
    public synchronized void insertQuoteBlock( QuoteBlock quoteBlock )
    {
        waitWhileFull();

        queue[ head ] = quoteBlock;
        head = (head + 1) % capacity;
        size++;
        if (size > maxCount)
        {
            maxCount = size;
        }
        notify();
    }
    /**
     * This method will get the next available command.
     *  If there are no commands available, wait for one.
     *  If we are shutting down, return null
     * @author Craig Murphy
     * @return ThreadCommand the command to be executed.
     */
    public synchronized QuoteBlock getNextQuoteBlock()
    {
        waitWhileEmpty();

        QuoteBlock retVal = queue[tail];

        queue[tail] = null;
        tail = (tail + 1 ) % capacity;

        size --;

        notify();
        return retVal;
    }
}
