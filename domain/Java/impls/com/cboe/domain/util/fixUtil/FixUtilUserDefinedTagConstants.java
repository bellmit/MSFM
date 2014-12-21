package com.cboe.domain.util.fixUtil;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Sundares
 * Date: Jul 28, 2004
 * Time: 9:50:56 AM
 * To change this template use Options | File Templates.
 */
public class FixUtilUserDefinedTagConstants {

	public final static Pattern patternUserAssignedID = Pattern.compile(FixUtilUserDefinedTagConstants.SECONDARY_CLORDID + "=(\\w{1,8})");
	public final static Pattern patternCxlReqTradingSession = Pattern.compile(FixUtilUserDefinedTagConstants.LO_CXL_REQ_TRADING_SESSION + "=(\\w{1,})");
	public final static Pattern patternCxlReqType = Pattern.compile(FixUtilUserDefinedTagConstants.LO_CXL_REQ_TYPE + "=(\\w{1,})");
	public final static Pattern priceProtectionScopePattern = Pattern.compile(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE + "=(\\w{1,})");
	public final static String QUOTE_TEXT = "9008";
    public final static String CANCELOPENQTY = "9310";  //Introduced in FIX/ORS Supported in FIX 4.2
    public final static String TLCQTY = "9311"; // Introduced in FIX/ORS required by FIX 4.2 proposed for standardization
    public final static String QUOTE_STATUS = "9312";
    public final static String OPEN_INTEREST = "9314";
    public final static String MD_SCOPE = "9315";
    public final static String LEGAL_MARKET = "9316";
    public final static String LIGHT_ORDER  = "9317";
    public final static String ORDER_TYPES = "9318";
    public final static String SECONDARY_CLORDID = "9321";
    public final static String MULTILEG_PRICE_INCREMENT = "9322";
    public final static String MULTILEG_MONTH_INCREMENT = "9323";
    public final static String CLEARING_OPTIONAL_DATA = "9324";
    public final static String EXECUTION_INFORMATION = "9433";
    public final static String PUBLISH_ORDER_STATUS="9363"; //Introduced in SBT V 2.0 to specify whether to publish order status at Logon
    public final static String PREM_PRICE_TICK_BREAK_POINT = "9365";
    public final static String PREM_PRICE_TICK_ABOVE = "9366";
    public final static String PREM_PRICE_TICK_BELOW = "9367";
    public final static String LAST_BUST_SHARES = "9368";
    public final static String PRICE_PROTECTION_SCOPE = "9369";
    public final static String MULTI_LEG_POSITION_EFFECTS = "9370";
    public final static String MULTI_LEG_COVERED_OR_UNCOVERED="9371";
    public final static String MULTI_LEG_STOCK_CLEARING_FIRM="9372";
    public final static String MULTI_LEG_PRICE_PER_LEG="9379";
    public final static String SUBSCRIPTION_REQUEST_TYPE="9463";
    public final static String ORDER_ORIGINATOR="9465"; // for entry of ExchangeAcronymStruct
    public final static String EQUITY_SESSION="9467"; // for entry of Equity Session (for Buy-Writes)
    public final static String USERASSIGNED_CANCELID="9468"; // work-around to distinguish multiple CXL request
    public final static String EXTENDED_PRICE_TYPE="9469"; // for accessing FIX 4.4 Tag 423 values (e.g. cabinet trades)
    public final static String APPLICATION_QUEUE_DEPTH="6699";// Introduced in HYBRID
    public final static String NO_OF_LEGS="555";
    public final static String LEG_SECURITY_TYPE="609";
    public final static String LEG_SYMBOL="600";
    public final static String LEG_SECURITY_ID="602";
    public final static String LEG_MATURITY_MONTH_YEAR="610";
    public final static String LEG_MATURITY_DATE="611"; // OSI
    public final static String LEG_SIDE="624";
    public final static String LEG_STRIKE_PRICE="612";
    public final static String LEG_OPT_ATTRIBUTE="613";
    public final static String LEG_RATIO_QTY="623";
    public final static String LEG_COVERED_UNCOVERED="565";
    public final static String LEG_POSITION_EFFECT="564";
    public final static String LEG_PRICE="566";
    public final static String NESTED_PARTY_ID="524";
    public final static String NO_OF_LEGS_LIST="6706";
    public final static String LEG_SECURITY_TYPE_LIST="6711";
    public final static String LEG_SYMBOL_LIST="6712";
    public final static String LEG_SECURITY_ID_LIST="6713";
    public final static String LEG_SIDE_LIST="6714";
    public final static String LEG_RATIO_QTY_LIST="6715";
    public final static String LEG_PRICE_LIST="6716";
    public final static String LEG_MATURITY_MONTH_YEAR_LIST="6717";
    public final static String LEG_MATURITY_DAY_LIST="6708";
    public final static String LEG_STRIKE_PRICE_LIST="6718";
    public final static String LEG_OPT_ATTRIBUTE_LIST="6719";
    public final static String LEG_COVERED_UNCOVERED_LIST="6720";
    public final static String LEG_POSITION_EFFECT_LIST="6721";
    public final static String LEG_REF_ID_LIST="6722";
    public final static String NESTED_PARTY_ID_LIST="6707";
    public final static String BROKER_ROUTING_ID="6818";
    public final static String STOCK_FIRM_NAME="9380";    // Used in Buy writes
    public final static String STOCK_FIRM_NAME_KEY="9381"; // Used in Buy writes
    public final static String QUOTE_STATUS_REQUEST_TYPE ="5349";// Introduced in API Enhancements
    public final static String UDF_SUPPORT_INDICATOR ="9003";// Introduced in API Enhancements
    public final static String CUSTOMER_SIZE ="9004";// Introduced in API Enhancements
    public final static String PROFESSIONAL_SIZE ="9005";// Introduced in API Enhancements
    public final static String QUOTE_UPDATE_CONTROL_ID ="9006";// Introduced in API Enhancements
    public final static String USER_PROFILE="6600";		// FIX GUI logon response
    public final static String ORDER_PRICE = "9302"; // for starting price in an Automated Auction
    public final static String PIP_MANAGEMENT_TYPE = "9743"; // to signal an Automated Auction solicitation
    public final static String MATCH_TYPE = "9382"; // cmiConstants::MatchTypes
    public final static String AUCTION_TYPE = "9383"; // cmiConstants::AuctionTypes
    public final static String AUCTION_CONTINGENCY = "9384"; // cmiConstants::ContingencyTypes
    public final static String AUCTION_ID = "9385"; // ID number of an Automated Auction
    public static final String TT_EXCHANGE_SPECIFIC_STRING2 = "16102"; // Introduced for TT
    public static final String TT_EXCHANGE_SPECIFIC_STRING3 = "16103"; // Introduced for TT
    public static final String TT_PRICE_DISPLAY_TYPE = "16451";// Introduced for TT
    public static final String TT_TICKSIZE= "16452";// Introduced for TT
    public static final String SECONDARY_EXEC_ID = "9769"; // Introduced for TT
    public static final String LEG_REFID = "654"; // Introduced for Supporting Leg Id for One step
    public static final String SUPPRESS_ORDER_STATUS = "9191"; // Introduced for suppressing OrdStatus at logon.
    public static final String CLORDID_FORMAT = "5039"; // used on logon to turn on 2 char branch id processing
    public static final String PARENT_CLORDID = "5254"  ; // Used internally only to map the 2 char clordid onto userassigned id in cmi struct
    public static final String ENHANCED_CXL_RE_IND = "7025"; // Used to determine whether the user wants the enhanced cxl re functionality
    public static final String TRADE_LIQUIDITY_INDICATOR = "9730"  ; // A.k.a. - BillingType
    public static final String AUX_AUCTION_INFO = "9221"  ; // for COB Auction Enhancements
    public static final String ENHANCED_QUOTE_MODEL = "9192"; // Used to determine whether the user wants the enhanced quote model
    public static final String DIRECTED_FIRM = "5941"; // for input to extensions
    public static final String SHORT_SALE_INDICATOR = "20101"; // for indicating the short sale values
    public static final String MASS_ORDER_CANCEL_REQUEST_TYPE = "530";
    public static final String MASS_ORDER_CANCEL_RESPONSE = "531";
    public static final String LO_PENDING_FILLED_QTY = "20102"; // returned to indicate the amount which will be sent as a FILL report later
    public static final String LO_PENDING_CANCELLED_QTY = "20103"; // returned to indicate the amount which will be sent as a FILL report later
    public static final String LO_CXL_REQ_TRADING_SESSION = "20104"; // session name for light order cancel requests
    public static final String LO_CXL_REQ_TYPE = "20105"; // 3 means cancel by class, 1 means cancel by series

    
    //Trading Class Status related User Defined Tags
    public static final String TRADING_GROUP_INFO = "20010"; // for indicating the trading group(BC or trade server) name
    public static final String TRADING_GROUP_STATUS_SUBSCRIPTION_REQUEST_TYPE = "9263"; //indicates the type of subscription class or group based
    //--end changes for Trading class status....... 
    
    
}
