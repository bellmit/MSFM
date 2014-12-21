package com.cboe.interfaces.domain;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;

public interface OrderQueryHome {

	public  int getAllOrderCount() throws TransactionFailedException;
	public  int getAllOrderCountForSync() throws SystemException;
	public  long getTotalOrderRemainingQuantity() throws TransactionFailedException;
}
