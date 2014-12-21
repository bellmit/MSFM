package com.cboe.infrastructureServices.foundationFramework.policies;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/ThreadPolicy.java

public class ThreadPolicy extends Policy {
	private ThreadPolicyValue value;
	public static ThreadPolicy SHARED_THREAD = new ThreadPolicy(ThreadPolicyValue.SHARED_THREAD);
	public static ThreadPolicy THREAD_PER_REQUEST = new ThreadPolicy(ThreadPolicyValue.THREAD_PER_REQUEST );
	public static ThreadPolicy REUSE_THREAD = new ThreadPolicy(ThreadPolicyValue.REUSE_THREAD );
	
	ThreadPolicy() {
	}
	/**
	   @roseuid 3659F83F0111
	 */
	public ThreadPolicy(ThreadPolicyValue value) {
		
	}
}