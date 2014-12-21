/**
 * 
 */
package com.cboe.delegates.application;

import com.cboe.interfaces.application.TradingClassStatusQueryService;;
/**
 * @author Arun Ramachandran Nov 15, 2009
 *
 */
public class TradingClassStatusQueryServiceDelegate extends com.cboe.idl.cmiV8.POA_TradingClassStatusQuery_tie{
	
    public TradingClassStatusQueryServiceDelegate(TradingClassStatusQueryService delegate) {
        super(delegate);
    }
}
