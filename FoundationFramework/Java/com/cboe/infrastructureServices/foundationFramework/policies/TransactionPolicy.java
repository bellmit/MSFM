package com.cboe.infrastructureServices.foundationFramework.policies;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/TransactionPolicy.java

public class TransactionPolicy extends Policy {
	final public static TransactionPolicy OBJECT_MANAGED = new TransactionPolicy(TransactionPolicyValue.OBJECT_MANAGED);
	private TransactionPolicyValue value;
	
	public TransactionPolicy() {
	}
	/**
	   @roseuid 3659F87203C8
	 */
	public TransactionPolicy(TransactionPolicyValue value) {
	}
}