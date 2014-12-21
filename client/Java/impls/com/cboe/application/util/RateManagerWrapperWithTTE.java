/**
 * 
 */
package com.cboe.application.util;

import java.util.Map;

import com.cboe.application.shared.TransactionTimingRegistration;
import com.cboe.application.shared.TransactionTimingUtil;
import com.cboe.domain.rateMonitor.RateManager;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.transactionTimingCommon.TransactionTimer;

/**
 * 
 * A wrapper class to emit TTE points.
 * @author Arun Ramachandran Mar 14, 2011
 *
 */
public class RateManagerWrapperWithTTE extends RateManager {

	/**
	 * @param sessionConstraints
	 * @param userId
	 * @param exchange
	 * @param acronym
	 * @param monitorTypes
	 */
	public RateManagerWrapperWithTTE(Map sessionConstraints, String userId,
			String exchange, String acronym, Short[] monitorTypes) {
		super(sessionConstraints, userId, exchange, acronym, monitorTypes);
	}
	
	public void monitorRate(String sessionName, long currentTime, String methodName, short monitorType) throws NotAcceptedException{
		long entityId = 0L;
		boolean exceptionThrown = true;
		try {
			entityId = TransactionTimingUtil.getEntityID();
			TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRateMonitorEmitPoint(), entityId,	TransactionTimer.Enter);
		}catch (Exception e) {
			Log.information("[RateManagerWrapperWithTTE]--> Unable to get EntityID! Exception details: "+ e.getMessage());
		}
		try {
			super.monitorRate(sessionName, currentTime, methodName, monitorType);
			exceptionThrown = false;
			
		}finally {
			TransactionTimingUtil.resetEntityID(entityId);
			TransactionTimingUtil.generateOrderEvent(TransactionTimingRegistration.getRateMonitorEmitPoint(),entityId,
					exceptionThrown ? TransactionTimer.LeaveWithException:TransactionTimer.Leave);
		}
    }
}
