package com.cboe.infrastructureServices.foundationFramework.policies;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/ThreadPolicyValue.java

public class ThreadPolicyValue {
	static ThreadPolicyValue SHARED_THREAD = new ThreadPolicyValue();
	static ThreadPolicyValue THREAD_PER_REQUEST = new ThreadPolicyValue();
	static ThreadPolicyValue REUSE_THREAD = new ThreadPolicyValue();
	
	ThreadPolicyValue() {
	}
}