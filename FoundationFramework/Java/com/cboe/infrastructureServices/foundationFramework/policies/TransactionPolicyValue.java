package com.cboe.infrastructureServices.foundationFramework.policies;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/TransactionPolicyValue.java

public class TransactionPolicyValue {
	static TransactionPolicyValue  OBJECT_MANAGED = new TransactionPolicyValue();
	static TransactionPolicyValue INTERCEPTOR_MANAGED = new TransactionPolicyValue();
	
	TransactionPolicyValue() {
	}
}