package com.cboe.infrastructureServices.foundationFramework.policies;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/LoggingPropagationPolicyValue.java

public class LoggingPropagationPolicyValue extends Policy {
	public static int DEBUG = 0;
	public static int INFORMATION = 1;
	public int ABORT = 2;
	public int THROTTLE = 3;
	public int SYSTEM = 4;
	public int RESOURCE = 5;
	public int EXCEPTION = 6;
	
	LoggingPropagationPolicyValue() {
	}
}