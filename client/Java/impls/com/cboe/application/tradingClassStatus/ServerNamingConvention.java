/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * @author Arun Ramachandran Dec 10, 2010
 *
 */
public class  ServerNamingConvention{
	String ohServerFormat = null;
	String tradeServerFormat = null;
	String spreadtradeServerFormat = "SpreadTradeServer";
	ServerNamingConvention(String sessionName) {
		if(sessionName.equals("ONE_MAIN")) {
			ohServerFormat = "OHServerONE";
			tradeServerFormat = "TradeServer";
			
		}else if(sessionName.equals("CFE_MAIN")) {
			ohServerFormat = "OHServerCFE";
			tradeServerFormat = "TradeServer";
			
		}else if(sessionName.equals("COF_MAIN")) {
			ohServerFormat = "OHServerCOF";
			tradeServerFormat = "TradeServer";
		}else if(sessionName.equals("W_MAIN")) {
			ohServerFormat = "OHServerHybrid";
			tradeServerFormat = "HybridTradeServer";
			
		}else if(sessionName.equals("W_STOCK")) {
			ohServerFormat = "OHServerEquity";
			tradeServerFormat = "EquityTradeServer";
		}else {
			Log.information("ServerNamingConvention >>Ignoring Session :<"+sessionName+">"); 
		}
		
	}
	
	protected String getFullOHServerName(String tradeServerName) {
		String format = tradeServerFormat;
		if(tradeServerName.contains(spreadtradeServerFormat)) {
			format = spreadtradeServerFormat;
		}
		return tradeServerName.substring(0, tradeServerName.indexOf(format))+ohServerFormat;
	}
		
}
	

