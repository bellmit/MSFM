//Source file: T:\fixola\Java\com\cboe\integrationServices\linkage\OLAFixConstants.java
package com.cboe.domain.util.fixUtil;

/**
 * @author
 */
public class FixUtilLinkageFixConstants // extends FixConstants
{

	/**
	 * @author
	 */
	public static class AutoexSize
	{
		public static final int TAGNUMBER = 5201;
		public static final String TAGNAME = "AutoexSize";
	}

	/**
	 * @author
	 */
	public static class TradeThruTime
	{
		public static final int TAGNUMBER = 5202;
		public static final String TAGNAME = "TradeThruTime";
	}

	/**
	 * @author
	 */
	public static class TradeThruSize
	{
		public static final int TAGNUMBER = 5203;
		public static final String TAGNAME = "TradeThruSize";
	}

	/**
	 * @author
	 */
	public static class TradeThruPrice
	{
		public static final int TAGNUMBER = 5204;
		public static final String TAGNAME = "TradeThruPrice";
	}

	/**
	 * @author
	 */
	public static class AdjustedPriceInd
	{
		public static final int TAGNUMBER = 5205;
		public static final String TAGNAME = "AdjustedPriceInd";
	}

	/**
	 * @author
	 */
	public static class SatisfactionOrdDisp
	{
		public static final int TAGNUMBER = 5206;
		public static final String TAGNAME = "SatisfactionOrdDisp";
	}

	/**
	 * @author
	 */
	public static class ExecReceiptTime
	{
		public static final int TAGNUMBER = 5207;
		public static final String TAGNAME = "ExecReceiptTime";
	}

	/**
	 * @author
	 */
	public static class OriginalOrderTime
	{
		public static final int TAGNUMBER = 5208;
		public static final String TAGNAME = "OriginalOrderTime";
	}

	/**
	 * @author
	 */
	public static class OLAOrdRejReason
	{
		public static final int TAGNUMBER = 5209;
		public static final String TAGNAME = "OLAOrdRejReason";

		public static final int BROKER_OR_EXCHANGE_OPTION = 0;
		public static final int UNKNOWN_SYMBOL = 1;
		public static final int EXCHANGE_OR_TRADING_SESSION_CLOSED = 2;
		public static final int ORDER_EXCEEDS_LIMIT = 3;
		public static final int TOO_LATE_TO_ENTER = 4;
		public static final int UNKNOWN_ORDER = 5;
		public static final int DUPLICATE_ORDER = 6;
		public static final int STALE_ORDER = 8;
		public static final int INVALID_INSTRUMENT_STATE_ROTATION = 31;
		public static final int INVALID_INSTRUMENT_STATE_NON_FIRM = 32;
		public static final int INVALID_INSTRUMENT_STATE_HALTED = 33;
		public static final int NOT_AT_NBBO = 34;
		public static final int PRICE_OUT_OF_BOUNDS = 35;
		public static final int UNKNOWN_CLEARING_FIRM = 36;
		public static final int SUB_ACCOUNT_ID_MISSING = 37;
		public static final int INVALID_AUTO_EX = 38;
		public static final int ACCOUNT_MISSING = 39;
		public static final int INVALID_TIME_IN_FORCE = 40;
		public static final int INVALID_OPEN_CLOSE = 41;
		public static final int MISSING_EXEC_BROKER = 42;
		public static final int MISSING_CLEARING_ACCOUNT = 43;
		public static final int MISSING_EXEC_INFO = 44;
		public static final int ORDER_RECEIVED_TOO_SOON = 45;
		public static final int INVALID_ORDER_CAPACITY = 46;
		public static final int LATE_PRINT_TO_OPRA_TAPE = 61;
		public static final int COMMUNICATION_DELAYS_TO_OPRA = 62;
		public static final int MANUAL_TRADE = 63;
		public static final int PROCESSING_PROBLEMS_AT_MARKET_CENTER = 64;
		public static final int COMPLEX_ORDER = 65;
		public static final int TRADE_REJECTED = 66;
		public static final int TRADE_BUSTED_CORRECTED = 67;
		public static final int ORIGINAL_ORDER_REJECTED = 68;
        public static final int CANCEL_DUE_TO_NON_BLOCK_TRADE = 69;
	}
}
