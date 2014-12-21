package com.cboe.presentation.fix.appia;

import com.javtech.appia.ExecutionReport;
import com.javtech.appia.MessageObject;
import com.cboe.idl.cmiCallback.CMIOrderStatusConsumer;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.domain.util.fixUtil.FixUtilConstants;

/**
 * Author: beniwalv
 * Date: Jul 20, 2004
 * Time: 3:00:19 PM
 */
public abstract class ExecutionReportPublisher {
    /**
     *  This method will publish the execution report  
     */
    public abstract void publishExecutionReport(ExecutionReport er, FixSessionImpl session, CMIOrderStatusConsumer ordStatusConsumer);


	/**
	 * Return from a synchronous operation. Some execution reports are solicited
	 * while others are unsolicited.
	 * 
	 * @param execReport
	 *            a FIX execution report
	 * @param session
	 *            a FIX session
	 * @return a FIX request message, such as an Order
	 */
	protected MessageObject responseReceived(ExecutionReport execReport, FixSessionImpl session) {
		MessageObject request = null;
		if (execReport.ClOrdID != null) {
            /*  Add a check for Tag 50 = SenderSubID; if populated with a value of type "L_ClOrdID",
                it implies that the execution reports are responses for an Internalization Order.
                In such cases we need to indicate to the ResponseSynchronizer if we have received all the reports.
                Both the execution reports are needed to build the InternalizationOrderResultStruct.
                ***NOTE*** This should be only for ExecType = New
            */

            if (execReport.ExecType.equals(FixUtilConstants.ExecType.NEW) && execReport.header.TargetSubID != null && execReport.header.TargetSubID != "")
            {
                if ((execReport.header.TargetSubID.trim().substring(0, 2).equals("L_")))
                {
                    request = session.responseReceived(execReport.header.TargetSubID, execReport, 2, 1);
                }

            }else
            {
                request = session.responseReceived(execReport.ClOrdID, execReport);
            }

		} else {
			GUILoggerHome.find().alarm("Execution report missing ClOrdID (ExecType=" + 
					execReport.ExecType + ")",
					GUILoggerBusinessProperty.ORDER_ENTRY);
		}
		return request;
	}
    
}
