package com.cboe.client.util;

/**
 * Latch.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * A Latch is a condition starting out false, but once set true N times, remains true forever
 *
 * <CODE>
 * <PRE>
 *
 * class FOO
 * {
 *     protected final Latch latch = new Latch(10);
 *
 *     public run()
 *     {
 *         Object o;
 *
 *         while (true)
 *         {
 *             //block until something happens -- but once it happens, never block again
 *             latch.acquire();
 *
 *             //continue
 *         }
 *     }
 * }
 *
 * another thread:
 *
 *     FOO.getLatch().release();
 *
 *
 * </PRE>
 * </CODE>
 *
 */

public class Latch
{
   final private int target_count;
   private       int current_count = 0;

    /**
     * Create a latch requiring a single release
     *
     */
    public Latch()
    {
        this(1, false);
    }

    /**
     * Create a latch requiring a single release
     *
     */
    public Latch(boolean released)
    {
        this(1, released);
    }

    /**
     * Create a latch requiring a <i>num</i> releases
     *
     */
    public Latch(int num)
    {
        this(num, false);
    }

    /**
     * Create a latch requiring a <i>num</i> releases
     *
     */
    public Latch(int num, boolean released)
    {
        target_count = num;
        if (released)
        {
            current_count = target_count;
        }
    }
   /**
	* Try to acquire this latch, waiting until this latch is fully released
	*
	*/
   public synchronized boolean acquire()
   {
	   final int prev_current_count = current_count;

	   while (current_count != target_count)
	   {
		   try
		   {
			   wait();
		   }
		   catch (InterruptedException ex)
		   {

		   }
	   }

	   return prev_current_count == current_count;
   }
   /**
	* Try to acquire this latch, waiting until this latch is fully released, or <i>millis</i> milliseconds
	*
	*/
   public synchronized boolean acquire(long millis)
   {
	   final int prev_current_count = current_count;

	   if (current_count != target_count)
	   {
		   try
		   {
			   wait(millis);
		   }
		   catch (InterruptedException ex)
		   {

		   }
	   }

	   return prev_current_count == current_count;
   }
   /**
	* Try to acquire this latch, waiting until this latch is fully released
	*
	*/
   public synchronized boolean acquireAndReset()
   {
	   final int prev_current_count = current_count;

	   while (current_count != target_count)
	   {
		   try
		   {
			   wait();
		   }
		   catch (InterruptedException ex)
		   {

		   }
	   }

	   boolean b = prev_current_count == current_count;

      reset();

      return b;
   }
   /**
	* Try to acquire this latch, waiting until this latch is fully released, or <i>millis</i> milliseconds
	*
	*/
   public synchronized boolean acquireAndReset(long millis)
   {
	   final int prev_current_count = current_count;

	   if (current_count != target_count)
	   {
		   try
		   {
			   wait(millis);
		   }
		   catch (InterruptedException ex)
		   {

		   }
	   }

	   boolean b = prev_current_count == current_count;

      reset();

      return b;
   }

   public boolean acquireIf(boolean doAcquire)
   {
       if (doAcquire)
       {
           return acquire();
       }

       return false;
   }

   /**
	* Reset count to allow this latch to be used more than once (ie this is no longer a latch)
	*
	*/
   public void reset()
   {
       current_count = 0;
   }
   /**
	* Retrieve the latch current count
	*
	*/
   public int getCurrentCount()
   {
	   return current_count;
   }
   /**
	* Retrieve the latch count
	*
	*/
   public int getTargetCount()
   {
	   return target_count;
   }
   /**
	* Release one
	*
	*/
   public void release()
   {
	   release(1);
   }
   /**
	* Release <i>many</i>
	*
	*/
   public synchronized void release(int many)
   {
	   if (current_count == target_count)
	   {
		   return;
	   }

	   if (current_count + many > target_count)
	   {
		   current_count = target_count;
	   }
	   else
	   {
		   current_count += many;
	   }

	   notifyAll();
   }
}
