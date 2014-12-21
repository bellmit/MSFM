package com.cboe.common.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Define contextual information in the code. This is used to document specific topics that must be
 * understood when using code, or maintaining the code.
 */
public @interface Usage
{

	public @interface Precondition
	{
		String[] value() default
		{""};
	}
	
	/**
	 * Describes information about concurrency issues
	 */
	public @interface Concurrency
	{
		/**
		 * Indicates the call does not involve a lock that blocks for a significant amount of time
		 */
		@Target(
		{ElementType.CONSTRUCTOR, ElementType.METHOD})
		public @interface NonBlocking
		{
			String[] value() default
			{""};
		}

		/**
		 * Indicates the call involves a lock that blocks for a significant amount of time
		 */
		@Target(
		{ElementType.CONSTRUCTOR, ElementType.METHOD})
		public @interface Blocking
		{
			String[] value() default
			{""};
		}

		/**
		 * Conveys some issues relating to threading, such as requiring the use of a system thread
		 * or indicating that execution may block
		 */
		public @interface Threading
		{

			String[] value() default{""};
		}
		
		public @interface ThreadSafe {
			String[] value() default{""};
		};
		
		public @interface ThreadUnsafe 
		{
			String[] value() default{""};
		};

		/**
		 * Conveys specific locking requirements
		 */
		public @interface Locking
		{
			String[] value() default
			{""};
		}

		String[] value() default
		{""};
	}

	/**
	 * Conveys to the client that there is some performance issue with using this method. For
	 * example the method takes an excessive amount of time to complete.
	 */
	public @interface Performance
	{
		/**
		 * Conveys specific locking requirements
		 */
		public @interface Slow
		{
			String[] value() default
			{""};
		}
		
		String[] value() default
		{""};
	}

	/**
	 * Alerts to the user to some non-obvious side effect associated with invoking this method
	 */
	public @interface SideEffect
	{
		String[] value() default
		{""};
	}

	/**
	 * Conveys some invocation sequence. For example "don't invoke this before you invoke X"
	 */
	public @interface Protocol
	{
		String[] value() default
		{""};
	}

	/**
	 * Conveys to the user some requirement due to legacy constraints.
	 */
	public @interface Legacy
	{
		String[] value() default
		{""};
	}

}
