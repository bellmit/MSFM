package com.cboe.internalPresentation.api;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;

import org.omg.CORBA.IntHolder;

import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUser.ProfileStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.constants.TextMessageStates;
import com.cboe.idl.constants.TextMessageTypes;
import com.cboe.idl.infrastructureServices.securityService.securityAdmin.MemberAccountStruct;
import com.cboe.idl.product.GroupStruct;
import com.cboe.idl.product.GroupTypeStruct;
import com.cboe.idl.quote.InternalQuoteStruct;
import com.cboe.idl.session.TradingSessionElementTemplateStruct;
import com.cboe.idl.session.TradingSessionElementTemplateStructV2;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.textMessage.DestinationStruct;
import com.cboe.idl.textMessage.MessageResultStruct;
import com.cboe.idl.textMessage.MessageTransportStruct;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.tradingProperty.ClassSpreadStruct;
import com.cboe.idl.tradingProperty.SpreadClassStruct;
import com.cboe.idl.tradingProperty.TimeRangeStruct;
import com.cboe.idl.user.AccountDefinitionStruct;
import com.cboe.idl.user.SessionProfileUserDefinitionStruct;
import com.cboe.idl.user.UserDefinitionStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.internalPresentation.SystemAdminAPI;
import com.cboe.interfaces.internalPresentation.bookDepth.Tradable;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.presentation.product.ProductAdjustmentContainer;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.api.TestCASCallback;

import com.cboe.application.shared.UnitTestHelper;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.ReflectiveStructBuilder;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;

/**
 * @author Keith A. Korecky
 */
public class TestSACAS
{

    private static final String     TEST_CASE                       = "TestCase";

    // UserMaintenanceService
    private static final String     GET_ALL_USERS                   = "getAllUsers";
    private static final String     GET_USER_BY_KEY                 = "getUserByKey";
    private static final String     GET_USER_BY_KEY_PARMS           = "(int userKey)";
    private static final String     GET_SYSTEM_PREFERENCES          = "getSystemPreferences";
    private static final String     SET_SYSTEM_PREFERENCES          = "setSystemPreferences";
    private static final String     SET_SYSTEM_PREFERENCES_PARMS    = "(String tag, String value)";
    private static final String     GET_DPM_PARTICIPANTS_FOR_CLASS  ="getDpmParticipantsForClass";
    private static final String     GET_DPM_PARTICIPANTS_FOR_CLASS_PARMS = "(int classKey)";
    private static final String     GET_DPM_JOINT_ACCT_FOR_CLASS    = "getDpmJointAccountForClass";
    private static final String     GET_DPM_JOINT_ACCT_FOR_CLASS_PARMS = "(String userId, int classKey)";
    private static final String     GET_DPMS_FOR_JOINT_ACCT         = "getDpmsForJointAccount";
    private static final String     GET_DPMS_FOR_JOINT_ACCT_PARMS   = "(String jointAccountUserId)";
    private static final String     GET_DPMS_FOR_CLASS    = "getDpmsForClass";
    private static final String     GET_DPMS_FOR_CLASS_PARMS = "(String userId, int classKey)";

    // SessionManagmentAdminService
    private static final String     CREATE_SESSION                  = "createSession";
    private static final String     FORCE_CLOSE_SESSION             = "forceCloseSession";
    private static final String     FORCE_LEAVE_SESSION             = "forceLeaveSession";
    private static final String     CLOSE_SESSION                   = "closeSession";
    private static final String     GET_SESSION_BY_USERID           = "getSessionByUserId";
    private static final String     CREATE_SESSION_PARMS            = "(String userId)";
    private static final String     FORCE_CLOSE_SESSION_PARMS       = "(String userName)";
    private static final String     FORCE_LEAVE_SESSION_PARMS       = "(String userName)";
    private static final String     CLOSE_SESSION_PARMS             = "(int sessionKey)";
    private static final String     GET_SESSION_BY_USERID_PARMS     = "(String userId)";

    // TradingPropertyService
    private static final String     GET_OPENING_PRICE_RATE          = "getOpeningPriceRate";
    private static final String     GET_OPENING_PRICE_DELAY         = "getOpeningPriceDelay";
    private static final String     GET_CONT_QUOTE_PERIOD_CREDIT    = "getContinuousQuotePeriodForCredit";
    private static final String     GET_MIN_QUOTE_CREDIT_DEFAULT    = "getMinQuoteCreditDefaultSize";
    private static final String     GET_OPENING_PERIOD_TIME_RANGE   = "getOpeningPeriodTimeRange";
    private static final String     SET_OPENING_PRICE_RATE          = "setOpeningPriceRate";
    private static final String     GET_PRESCRIBE_WIDTH_ALL_CLASSES = "getExchangePrescribedWidthForAllClasses";
    private static final String     GET_PRESCRIBE_WIDTH             = "getExchangePrescribedWidth";
    private static final String     GET_PRESCRIBE_WIDTH_PARMS       = "(int exchangeKey, int classKey)";
    private static final String     GET_OPENING_PRICE_RATE_PARMS    = "(int exchangeKey)";
    private static final String     SET_OPENING_PRICE_RATE_PARMS    = "(int exchangeKey)";
    private static final String     GET_OPENING_PRICE_DELAY_PARMS   = "(int exchangeKey)";
    private static final String     GET_CONT_QUOTE_PERIOD_CREDIT_PARMS= "(int exchangeKey)";
    private static final String     GET_MIN_QUOTE_CREDIT_DEFAULT_PARMS= "(int exchangeKey)";
    private static final String     GET_OPENING_PERIOD_TIME_RANGE_PARMS= "(int exchangeKey)";
    private static final String     GET_PRESCRIBE_WIDTH_ALL_CLASSES_PARMS= "(int exchangeKey)";

    // TradingSessionService
    private static final String     GET_ALL_TEMPLATES               = "getAllTemplates";
    private static final String     GET_SESSIONS_FOR_TEMPLATE       = "getSessionsForTemplate";
    private static final String     GET_CURRENT_TRADING_SESSIONS    = "getCurrentTradingSessions";
    private static final String     START_SESSION                   = "startSession";
    private static final String     GET_SESSIONS_FOR_TEMPLATE_PARMS = "(String templateName)";
    private static final String     START_SESSION_PARMS             = "";

    // ProductStateService
    private static final String     SET_ALL_PRODUCT_STATES          = "setAllProductStates";
    private static final String     SET_PRODUCT_STATE_BY_PRODUCT    = "setProductStateByProduct";
    private static final String     SET_PRODUCT_STATE_BY_CLASS      = "setProductStateByClass";
    private static final String     SET_ALL_PRODUCT_STATES_PARMS    = "( short productState )";
    private static final String     SET_PRODUCT_STATE_BY_PRODUCT_PARMS  = "(int productKey, short productState)";
    private static final String     SET_PRODUCT_STATE_BY_CLASS_PARMS    = "(int classKey, short productState)";

    // ProductConfigurationService
    private static final String     GET_GROUP_TYPES                 = "getGroupTypes";
    private static final String     GET_GROUPS_BY_TYPE              = "getGroupsByType";
    private static final String     GET_GROUPS_BY_TYPE_PARMS        = "(int groupType)";
    private static final String     GET_PRODUCT_CLASSES_GROUP_BY_KEY= "getProductClassesForGroupByKey";
    private static final String     GET_PRODUCT_CLASSES_GROUP_BY_KEY_PARMS = "(int groupKey)";
    private static final String     CREATE_GROUP                    = "createGroup";
    private static final String     CREATE_GROUP_PARMS              = "(String group, int groupType)";
    private static final String     ADD_PRODUCT_CLASS_TO_GROUP      = "addProductClassToGroup";
    private static final String     ADD_PRODUCT_CLASS_TO_GROUP_PARMS = "(int classKey, String group)";

    // ProductMaintenanceService
    private static final String     PRICE_ADJ_UPDATE_COMPLETE       = "priceAdjustmentUpdateComplete";

    // ProductQuery
    private static final String     GET_PRODUCT_TYPES               = "getProductTypes";
    private static final String     GET_PRODUCT_TYPES_PARMS         = "(boolean isActiveForTrading)";

    // SecurityAdminService
    private static final String     GET_GROUPS                      = "getGroups";
    private static final String     GET_SERVICES                    = "getServices";
    private static final String     GET_ACCOUNT                     = "getAccount";
    private static final String     GET_ACCOUNT_PARMS               = "(String memberId)";

    // TraderAPI
    private static final String     GET_PRODUCT_CLASSES             = "getProductClasses";
    private static final String     GET_PRODUCTS                    = "getProducts";
    private static final String     GET_PRODUCT_CLASSES_PARMS       = "(short productType, boolean active)";
    private static final String     GET_PRODUCTS_PARMS              = "(int classKey, boolean active)";
    private static final String     GET_FIRMS                       = "getFirms";
    private static final String     GET_FIRMS_PARMS                 = "(boolean activeOnly, boolean clearingFirmOnly)";

    // UserTradingParameterService
    private static final String     SET_QRM_PROFILE                 = "setQuoteRiskProfile";
    private static final String     SET_QRM_PROFILE_PARAMS          = "( String userId, int classKey, int volume, int time, boolean status )";
    private static final String     GET_QRM_PROFILE                 = "getQuoteRiskProfileByClass";
    private static final String     GET_QRM_PROFILE_PARAMS          = "( String userId, int classKey )";
    private static final String     CHANGE_QRM_GLOBAL_STATUS        = "changeQRMGlobalStatus";
    private static final String     CHANGE_QRM_GLOBAL_STATUS_PARAMS = "( String userId, boolean status )";
    private static final String     GET_ALL_QRM_PROFILES            = "getAllQuoteRiskProfiles";
    private static final String     GET_ALL_QRM_PROFILES_PARAMS     = "( String userId )";

    // TextMessagingAPI
    private static final String     SEND_MESSAGE_FOR_USER           = "sendMessageForUser";
    private static final String     SEND_MESSAGE_FOR_PRODUCT_CLASS  = "sendMessageForProductClass";
    private static final String     SEND_MESSAGE_TO_HELP_DESK       = "sendMessageToHelpDesk";
    private static final String     PUBLISH_MESSAGES_FOR_USER       = "publishMessagesForUser";
    private static final String     CREATE_TEMPLATE                 = "createTemplate";
    private static final String     UPDATE_TEMPLATE                 = "updateTemplate";
    private static final String     DELETE_TEMPLATE                 = "deleteTemplate";
    private static final String     GET_AVAILABLE_TEMPLATES         = "getAvailableTemplates";
    private static final String     GET_TEMPLATE_TEXT               = "getTemplateText";
    private static final String     SEND_MESSAGE_FOR_USER_PARMS     = "(String userId, String subject, String text)";
    private static final String     SEND_MESSAGE_FOR_PRODUCT_CLASS_PARMS= "(String classKey, String subject, String text)";
    private static final String     SEND_MESSAGE_TO_HELP_DESK_PARMS = "(String subject, String text)";
    private static final String     PUBLISH_MESSAGES_FOR_USER_PARMS = "(String userId)";
    private static final String     CREATE_TEMPLATE_PARMS           = "(String templateName, String templateText)";
    private static final String     UPDATE_TEMPLATE_PARMS           = "(String templateName, String templateText)";
    private static final String     DELETE_TEMPLATE_PARMS           = "(String templateName)";
    private static final String     GET_AVAILABLE_TEMPLATES_PARMS   = "";
    private static final String     GET_TEMPLATE_TEXT_PARMS         = "(String templateName)";

    // Quote
    private static final String     SUBSCRIBE_RFQ                   = "subscribeRFQ";
    private static final String     UNSUBSCRIBE_RFQ                 = "unsubscribeRFQ";
    private static final String     SUBSCRIBE_RFQ_PARMS             = "(int classKey)";
    private static final String     UNSUBSCRIBE_RFQ_PARMS           = "(int classKey)";

    // MarketMakerAPI
    private static final String     GET_USER_MARKET_DATA_BY_PRODUCT = "getUserMarketDataByProduct";
    private static final String     GET_USER_MARKET_DATA_BY_PRODUCT_PARMS = "(int productKey)";

    // TradeMaintenanceService
    private static final String     GET_TRADE_REPORT                = "getTradeReportByTradeId";
    private static final String     GET_TRADE_REPORT_PARMS          = "( int highTradeId, int lowTradeId )";
    private static final String     ACCEPT_TRADE_BUST               = "acceptTradeBust";

    // OrderBookService
    private static final String     GET_BOOK                        = "getBook";
    private static final String     GET_BOOK_PARMS                  = "(int productKey, double price)";

    // MarketMakerQuoteService
    private static final String     GET_QUOTE_FOR_PRODUCT           = "getQuoteForProduct";
    private static final String     GET_QUOTE_FOR_PRODUCT_PARMS     = "(int productKey, int highId, int lowId)";

    // OrderHandlingService
    private static final String     GET_ORDERS_FOR_PRODUCT          = "getOrdersForProduct";
    private static final String     GET_ORDERS_FOR_PRODUCT_PARMS    = "(String userId, int productKey)";

    // TradingSessionMaintenanceEventService
    private static final String     SUBSCRIBE_TRADING_SESSION_EVENT_STATE = "subscribeTradingSessionEventState";
    private static final String     UNSUBSCRIBE_TRADING_SESSION_EVENT_STATE = "unsubscribeTradingSessionEventState";

    private static final String     DISPLAY_TRADING_SESSIONS        = "displayTradingSessions";
    private static final String     SET_TRADING_SESSION             = "setTradingSession";
    private static final String     SET_TRADING_SESSION_PARMS       = "( String sessionName )";
    private static final String     LOGOUT                          = "logout";
    private static final String     HELP                            = "help";

    private static String[]         generalMenu                     = { "@@ GENERAL"
                                                                        , "______________________"
                                                                        , TEST_CASE
                                                                        , DISPLAY_TRADING_SESSIONS
                                                                        , SET_TRADING_SESSION
                                                                        , LOGOUT
                                                                        , HELP
                                                                        };
    private static String[]         traderAPIMenu                   = { "@@ TRADER.API"
                                                                        , "______________________"
                                                                        , GET_PRODUCT_CLASSES + GET_PRODUCT_CLASSES_PARMS
                                                                        , GET_PRODUCTS + GET_PRODUCTS_PARMS
                                                                        };
    private static String[]         userMaintainMenu                = { "@@ USER.MAINTENANCE.SERVICE"
                                                                        , "______________________"
                                                                        , GET_ALL_USERS
                                                                        , GET_USER_BY_KEY + GET_USER_BY_KEY_PARMS
                                                                        , GET_SYSTEM_PREFERENCES
                                                                        , SET_SYSTEM_PREFERENCES + SET_SYSTEM_PREFERENCES_PARMS
                                                                        , GET_DPM_PARTICIPANTS_FOR_CLASS + GET_DPM_PARTICIPANTS_FOR_CLASS_PARMS
                                                                        , GET_DPM_JOINT_ACCT_FOR_CLASS + GET_DPM_JOINT_ACCT_FOR_CLASS_PARMS
                                                                        , GET_DPMS_FOR_JOINT_ACCT + GET_DPMS_FOR_JOINT_ACCT_PARMS
                                                                        , GET_DPMS_FOR_CLASS + GET_DPMS_FOR_CLASS_PARMS
                                                                        };
    private static String[]         sessionManagementMenu           = { "@@ SESSION.MANAGEMENT.ADMIN.SERVICE"
                                                                        , "______________________"
                                                                        , CREATE_SESSION + CREATE_SESSION_PARMS
                                                                        , FORCE_CLOSE_SESSION + FORCE_CLOSE_SESSION_PARMS
                                                                        , FORCE_LEAVE_SESSION + FORCE_LEAVE_SESSION_PARMS
                                                                        , CLOSE_SESSION + CLOSE_SESSION_PARMS
                                                                        , GET_SESSION_BY_USERID + GET_SESSION_BY_USERID_PARMS
                                                                        };
    private static String[]         tradingPropertyMenu             = { "@@ TRADING.PROPERTY.SERVICE"
                                                                        , "______________________"
                                                                        , GET_OPENING_PRICE_RATE + GET_OPENING_PRICE_RATE_PARMS
                                                                        , SET_OPENING_PRICE_RATE + SET_OPENING_PRICE_RATE_PARMS
                                                                        , GET_OPENING_PRICE_DELAY + GET_OPENING_PRICE_DELAY_PARMS
                                                                        , GET_CONT_QUOTE_PERIOD_CREDIT + GET_CONT_QUOTE_PERIOD_CREDIT_PARMS
                                                                        , GET_MIN_QUOTE_CREDIT_DEFAULT + GET_MIN_QUOTE_CREDIT_DEFAULT_PARMS
                                                                        , GET_OPENING_PERIOD_TIME_RANGE + GET_OPENING_PERIOD_TIME_RANGE_PARMS
                                                                        , GET_PRESCRIBE_WIDTH_ALL_CLASSES + GET_PRESCRIBE_WIDTH_ALL_CLASSES_PARMS
                                                                        , GET_PRESCRIBE_WIDTH + GET_PRESCRIBE_WIDTH_PARMS
                                                                        };
    private static String[]         tradingSessionMenu              = { "@@ TRADING.SESSION.SERVICE"
                                                                        , "______________________"
                                                                        , GET_CURRENT_TRADING_SESSIONS
                                                                        , START_SESSION + START_SESSION_PARMS
                                                                        , GET_ALL_TEMPLATES
                                                                        , GET_SESSIONS_FOR_TEMPLATE + GET_SESSIONS_FOR_TEMPLATE_PARMS
                                                                        };
    private static String[]         productStateMenu                = { "@@ PRODUCT.STATE.SERVICE"
                                                                        , "______________________"
                                                                        , SET_ALL_PRODUCT_STATES + SET_ALL_PRODUCT_STATES_PARMS
                                                                        , SET_PRODUCT_STATE_BY_PRODUCT + SET_PRODUCT_STATE_BY_PRODUCT_PARMS
                                                                        , SET_PRODUCT_STATE_BY_CLASS + SET_PRODUCT_STATE_BY_CLASS_PARMS
                                                                        };
    private static String[]         productConfigMenu               = { "@@ PRODUCT.CONFIGURATION.SERVICE"
                                                                        , "______________________"
                                                                        , GET_GROUP_TYPES
                                                                        , GET_GROUPS_BY_TYPE + GET_GROUPS_BY_TYPE_PARMS
                                                                        , GET_PRODUCT_CLASSES_GROUP_BY_KEY + GET_PRODUCT_CLASSES_GROUP_BY_KEY_PARMS
                                                                        , CREATE_GROUP + CREATE_GROUP_PARMS
                                                                        , ADD_PRODUCT_CLASS_TO_GROUP + ADD_PRODUCT_CLASS_TO_GROUP_PARMS
                                                                        };
    private static String[]         productMaintainMenu             = { "@@ PRODUCT.MAINTENANCE.SERVICE"
                                                                        , "______________________"
                                                                        , PRICE_ADJ_UPDATE_COMPLETE
                                                                        };
    private static String[]         productQueryMenu                = { "@@ PRODUCT.QUERY"
                                                                        , "______________________"
                                                                        , GET_PRODUCT_TYPES + GET_PRODUCT_TYPES_PARMS
                                                                        };
    private static String[]         securityAdminMenu               = { "@@ SECURITY.ADMIN.SERVICE"
                                                                        , "______________________"
                                                                        , GET_GROUPS
                                                                        , GET_SERVICES
                                                                        , GET_ACCOUNT + GET_ACCOUNT_PARMS
                                                                        };
    private static String[]         textMessageMenu                 = { "@@ TEXT.MESSAGING.SERVICE"
                                                                        , "______________________"
                                                                        , SEND_MESSAGE_FOR_USER + SEND_MESSAGE_FOR_USER_PARMS
                                                                        , SEND_MESSAGE_FOR_PRODUCT_CLASS + SEND_MESSAGE_FOR_PRODUCT_CLASS_PARMS
                                                                        , SEND_MESSAGE_TO_HELP_DESK + SEND_MESSAGE_TO_HELP_DESK_PARMS
                                                                        , PUBLISH_MESSAGES_FOR_USER + PUBLISH_MESSAGES_FOR_USER_PARMS
                                                                        , CREATE_TEMPLATE + CREATE_TEMPLATE_PARMS
                                                                        , UPDATE_TEMPLATE + UPDATE_TEMPLATE_PARMS
                                                                        , DELETE_TEMPLATE + DELETE_TEMPLATE_PARMS
                                                                        , GET_AVAILABLE_TEMPLATES + GET_AVAILABLE_TEMPLATES_PARMS
                                                                        , GET_TEMPLATE_TEXT + GET_TEMPLATE_TEXT_PARMS
                                                                        };
    private static String[]         firmServiceMenu                 = { "@@ FIRM.SERVICE"
                                                                        , "______________________"
                                                                        , GET_FIRMS + GET_FIRMS_PARMS
                                                                        };

    private static String[]         orderBookServiceMenu            = { "@@ ORDER.BOOK.SERVICE"
                                                                        , "______________________"
                                                                        , GET_BOOK + GET_BOOK_PARMS
                                                                        };

    private static String[]         marketMakerQuoteServiceMenu     = { "@@ MARKET.MAKER.QUOTE.SERVICE"
                                                                        , "______________________"
                                                                        , GET_QUOTE_FOR_PRODUCT + GET_QUOTE_FOR_PRODUCT_PARMS
                                                                        };


    private static String[]         quoteMenu                       = { "@@ QUOTE"
                                                                        , "______________________"
                                                                        , SUBSCRIBE_RFQ + SUBSCRIBE_RFQ_PARMS
                                                                        , UNSUBSCRIBE_RFQ + UNSUBSCRIBE_RFQ_PARMS
                                                                        };

    private static String[]         marketMakerMenu                 = { "@@ MARKET.MAKER.API"
                                                                        , "______________________"
                                                                        , GET_USER_MARKET_DATA_BY_PRODUCT + GET_USER_MARKET_DATA_BY_PRODUCT_PARMS
                                                                        };

    private static String[]         orderHandlingServiceMenu        = { "@@ ORDER.HANDLING.SERVICE"
                                                                        , "______________________"
                                                                        , GET_ORDERS_FOR_PRODUCT + GET_ORDERS_FOR_PRODUCT_PARMS
                                                                        };

    private static String[]         tradeMaintenanceServiceMenu     = { "@@ TRADE.MAINTENACNE.SERVICE"
                                                                        , "______________________"
                                                                        , GET_TRADE_REPORT + GET_TRADE_REPORT_PARMS
                                                                        , ACCEPT_TRADE_BUST
                                                                        };

    private static String[]         tradingSessionMaintenanceEventServiceMenu = { "@@ TRADING.SESSION.MAINTENANCE.EVENT.SERVICE"
                                                                        , "______________________"
                                                                        , SUBSCRIBE_TRADING_SESSION_EVENT_STATE
                                                                        , UNSUBSCRIBE_TRADING_SESSION_EVENT_STATE
                                                                        };


    private static final int                DEFAULT_TTL                     = 999;

    private static String                   memberKey                       = "sbtUser";

    static SystemAdminAPI                   sacasAPI                        = null;

//    static String                          userId                          = memberKey;
    static String                           userId                          = "CCC";
//    static String                          userId                          = "InvalidUserId";

    static String                           password                        = "CCC";
    static String                           version                         = "version";
    static UserLogonStruct                  logon                           = new UserLogonStruct( userId, password, version, 'L' );
    static TestCASCallback                  logoffListener                  = new TestCASCallback("logoffListener");
    static TestCASCallback                  productClassesListener          = new TestCASCallback("productClassesListener");
    static TestCASCallback                  productsListener                = new TestCASCallback("productsListener");
    static TestCASCallback                  rfqListener                     = new TestCASCallback("rfqListener");
    static TestCASCallback                  eventStateListener              = new TestCASCallback("eventStateListener");
    static IntHolder                        lIntHolder                      = new IntHolder(0);
    static int                              lInt                            = 0;
    static boolean                          lBoolean                        = false;
    static short                            lShort                          = (short)0;
    static double                           lDouble                         = (double)0.0;
    static PreferenceStruct[]               preferencesSequence             = null;
    static UserAccountModel[]               userSequence                    = null;
    static ProductTypeStruct[]              productTypeSequence             = null;
    static TradingSessionElementTemplateStructV2[]   tradingSessionTemplateSequence  = null;
    static GroupTypeStruct[]                groupTypeSequence               = null;
    static GroupStruct[]                    groupSequence                   = null;
    static TradingSessionStruct[]           tradingSessionSequence          = null;
    static TimeRangeStruct                  timeRangeStruct                 = null;
    static ClassSpreadStruct[]              classSpreadSequence             = null;
    static SpreadClassStruct[]              spreadClassSequence             = null;
    static ClassStruct[]                    classSequence                   = null;
    static ProductStruct[]                  productSequence                 = null;
    static int[]                            productClassSequence            = null;
    static EPWStruct[]                      epwSequence                     = null;
    static MemberAccountStruct              memberAccountStruct             = null;
    static int                              sessionKey                      = 0;
    static String                           sessionName                     = "W_AM1";
    static com.cboe.idl.cmiSession.TradingSessionStruct[] tradingSessions   = null;
    static int                              i;
    static int                              j;
    static int                              messageId                       = 0;
    static int                              userKey                         = 2164635;
    static TradeReportStruct                lTradeReport                    = null;


    public TestSACAS()
    {
        super();
    }


    public static void main(String[] args)
    {
        boolean             testing = true;
        String[]            largs;
        Vector              mainMenu = new Vector();

        loadMenu( mainMenu );

        try
        {
            ////////// MUST BE CALLED /////////
            String [] fileName = {  "TestSACAS.properties"  } ;
            FoundationFramework ff = FoundationFramework.getInstance();
            ConfigurationService configService = new ConfigurationServiceFileImpl();
            configService.initialize(args, 0);
            ff.initialize("SystemAdminServer", configService);

            System.out.println( "Logging on..." );
            sacasAPI = SystemAdminUserAccessFactory.logon( logon, logoffListener );
            System.out.println( "Logged on as:" + logon.userId );
            tradingSessions = sacasAPI.getCurrentTradingSessions( logoffListener );


//            System.out.println( "\n:: Comnand list ::" );
//            System.out.println( COMMAND_LIST );
            printMenu( mainMenu );

            while( testing )
            {
                largs = getCommands();

                if ( largs[ 0 ].compareToIgnoreCase( TEST_CASE ) == 0 )                             TestCase();

                else if ( largs[ 0 ].compareToIgnoreCase( GET_USER_BY_KEY ) == 0 )                  getUserByKey( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_ALL_USERS ) == 0 )                    getAllUsers( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_DPM_PARTICIPANTS_FOR_CLASS ) == 0 )   getDpmParticipantsForClass( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_DPM_JOINT_ACCT_FOR_CLASS ) == 0 )     getDpmJointAccountForClass( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_DPMS_FOR_JOINT_ACCT ) == 0 )          getDpmsForJointAccount( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_DPMS_FOR_CLASS ) == 0 )               getDpmsForClass( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( CREATE_SESSION ) == 0 )                   createSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( FORCE_CLOSE_SESSION ) == 0 )              forceCloseSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( FORCE_LEAVE_SESSION ) == 0 )              forceLeaveSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_SESSION_BY_USERID ) == 0 )            getSessionByUserId( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_OPENING_PRICE_RATE ) == 0 )           getOpeningPriceRate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_OPENING_PRICE_RATE ) == 0 )           setOpeningPriceRate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRESCRIBE_WIDTH_ALL_CLASSES ) == 0 )  getExchangePrescribedWidthForAllClasses( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRESCRIBE_WIDTH ) == 0 )              getExchangePrescribedWidth( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( SET_ALL_PRODUCT_STATES ) == 0 )           setAllProductStates( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_PRODUCT_STATE_BY_PRODUCT ) == 0 )     setProductStateByProduct( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_PRODUCT_STATE_BY_CLASS ) == 0 )       setProductStateByClass( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRODUCT_TYPES ) == 0 )                getProductTypes( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_GROUPS ) == 0 )                       getGroups( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_SERVICES ) == 0 )                     getServices( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_ALL_TEMPLATES ) == 0 )                getAllTemplates( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_SESSIONS_FOR_TEMPLATE ) == 0 )        notImplemented( largs );    //getSessionsForTemplate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_CURRENT_TRADING_SESSIONS ) == 0 )     getCurrentTradingSessions( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( START_SESSION ) == 0 )                    startSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_SYSTEM_PREFERENCES ) == 0 )           getSystemPreferences( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_GROUP_TYPES ) == 0 )                  getGroupTypes( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_GROUPS_BY_TYPE ) == 0 )               getGroupsByType( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( PRICE_ADJ_UPDATE_COMPLETE ) == 0 )        priceAdjustmentUpdateComplete( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( CLOSE_SESSION ) == 0 )                    closeSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_OPENING_PRICE_DELAY ) == 0 )          getOpeningPriceDelay( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_CONT_QUOTE_PERIOD_CREDIT ) == 0 )     getContinuousQuotePeriodForCredit( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_MIN_QUOTE_CREDIT_DEFAULT ) == 0 )     getMinQuoteCreditDefaultSize( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_OPENING_PERIOD_TIME_RANGE ) == 0 )    getOpeningPeriodTimeRange( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_SYSTEM_PREFERENCES ) == 0 )           setSystemPreferences( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_ACCOUNT ) == 0 )                      getAccount( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRODUCT_CLASSES ) == 0 )              notImplemented( largs );    //getProductClasses( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRODUCTS ) == 0 )                     notImplemented( largs );    //getProducts( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( CREATE_GROUP ) == 0 )                     createGroup( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( ADD_PRODUCT_CLASS_TO_GROUP ) == 0 )       addProductClassToGroup( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_PRODUCT_CLASSES_GROUP_BY_KEY ) == 0 ) getProductClassesForGroupByKey( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_TRADE_REPORT ) == 0 )                 getTradeReportByTradeId( largs );
                else if ( largs[ 0 ].compareToIgnoreCase(ACCEPT_TRADE_BUST ) == 0 )                 acceptTradeBust(largs);
                else if ( largs[ 0 ].compareToIgnoreCase( SEND_MESSAGE_FOR_USER ) == 0 )            sendMessageForUser( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SEND_MESSAGE_FOR_PRODUCT_CLASS ) == 0 )   sendMessageForProductClass( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SEND_MESSAGE_TO_HELP_DESK ) == 0 )        sendMessageToHelpDesk( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( PUBLISH_MESSAGES_FOR_USER ) == 0 )        publishMessagesForUser( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( CREATE_TEMPLATE ) == 0 )                  createTemplate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( UPDATE_TEMPLATE ) == 0 )                  updateTemplate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( DELETE_TEMPLATE ) == 0 )                  deleteTemplate( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_AVAILABLE_TEMPLATES ) == 0 )          getAvailableTemplates( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_TEMPLATE_TEXT ) == 0 )                getTemplateText( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( GET_BOOK ) == 0 )                         getBook( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( GET_QUOTE_FOR_PRODUCT ) == 0 )            getQuoteForProduct( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( GET_QRM_PROFILE ) == 0 )                  getQRMProfile( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_QRM_PROFILE ) == 0 )                  setQRMProfile( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( CHANGE_QRM_GLOBAL_STATUS ) == 0 )         changeQRMGlobalStatus( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_ALL_QRM_PROFILES ) == 0 )             getAllQuoteRiskProfiles( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( GET_FIRMS ) == 0 )                        getFirms( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( SUBSCRIBE_RFQ ) == 0 )                    notImplemented( largs );    //subscribeRFQ( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( UNSUBSCRIBE_RFQ ) == 0 )                  notImplemented( largs );    //unsubscribeRFQ( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( GET_USER_MARKET_DATA_BY_PRODUCT ) == 0 )  notImplemented( largs );    //getUserMarketDataByProduct( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( GET_ORDERS_FOR_PRODUCT ) == 0 )           notImplemented( largs );    //getOrdersForProduct( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( SUBSCRIBE_TRADING_SESSION_EVENT_STATE ) == 0 ) subscribeTradingSessionEventState( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( UNSUBSCRIBE_TRADING_SESSION_EVENT_STATE ) == 0 ) unsubscribeTradingSessionEventState( largs );

                else if ( largs[ 0 ].compareToIgnoreCase( DISPLAY_TRADING_SESSIONS ) == 0 )         displayTradingSessions( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( SET_TRADING_SESSION ) == 0 )              setTradingSession( largs );
                else if ( largs[ 0 ].compareToIgnoreCase( LOGOUT ) == 0 )                           testing = false;

                else
                {
//                    System.out.println( "\n:: Comnand list ::" );
//                    System.out.println( COMMAND_LIST );
                    printMenu( mainMenu );
                }
            }

/*
                System.out.println( "****************************************************************" );
                System.out.println();
                System.out.println( "            Waiting 30 seconds before logging out." );
                System.out.println();
                System.out.println( "****************************************************************" );
                Thread waiter = new Thread();
                waiter.sleep(30000);
*/

            System.out.println( "Logging out..." );
            sacasAPI.logout();

            System.out.println();
            System.out.println();
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );
            System.out.println();
            System.out.println( "                           DONE" );
            System.out.println();
            System.out.println( "****************************************************************" );
            System.out.println( "****************************************************************" );

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        System.exit( 0 );
    }

    public static void loadMenu( Vector menu )
    {
        menu.add(generalMenu);
        menu.add(traderAPIMenu);
        menu.add(userMaintainMenu);
        menu.add(sessionManagementMenu);
        menu.add(tradingPropertyMenu);
        menu.add(tradingSessionMenu);
        menu.add(productStateMenu);
        menu.add(productConfigMenu);
        menu.add(productMaintainMenu);
        menu.add(productQueryMenu);
        menu.add(securityAdminMenu);
        menu.add(textMessageMenu);
        menu.add(firmServiceMenu);
        menu.add(orderBookServiceMenu);
        menu.add(marketMakerQuoteServiceMenu);
        menu.add(quoteMenu);
        menu.add(marketMakerMenu);
        menu.add(orderHandlingServiceMenu);
        menu.add(tradeMaintenanceServiceMenu);
        menu.add(tradingSessionMaintenanceEventServiceMenu);
    }

    public static void printMenu( Vector    menu )
    {
        int             menuCount       = 0;
        boolean         printingLeft    = false;
        boolean         printingRight   = false;
        String[]        leftMenu        = null;
        String[]        rightMenu       = null;
        int             leftCount       = 0;
        int             rightCount      = 0;
        String          leftItem        = "";
        String          rightItem       = "";

        if ( menuCount < menu.size() )
        {
            leftMenu = (String[])menu.get(menuCount++);
            printingLeft = true;
            leftCount = 0;
        }
        if ( menuCount < menu.size() )
        {
            rightMenu = (String[])menu.get(menuCount++);
            printingRight = true;
            rightCount = 0;
        }

        while( printingLeft || printingRight )
        {
            // handle right side menu
            if ( leftCount < leftMenu.length )
            {
                leftItem = leftMenu[ leftCount++ ];
            }
            else
            {
                if ( menuCount < menu.size() )
                {
                    leftMenu = (String[])menu.get(menuCount++);
                    printingLeft = true;
                    leftCount = 0;
//                    leftItem = leftMenu[ leftCount++ ];
                    leftItem = "";
                }
                else
                {
                    printingLeft = false;
                    leftItem = "";
                }
            }
            // handle right side menu
            if ( rightCount < rightMenu.length )
            {
                rightItem = rightMenu[ rightCount++ ];
            }
            else
            {
                if ( menuCount < menu.size() )
                {
                    rightMenu = (String[])menu.get(menuCount++);
                    printingRight = true;
                    rightCount = 0;
//                    rightItem = rightMenu[ rightCount++ ];
                    rightItem = "";
                }
                else
                {
                    printingRight = false;
                    rightItem = "";
                }
            }
        printMenuLine( leftItem, rightItem );
        }
    }

    public static void printMenuLine( String leftLine, String rightLine )
    {
        String          stringOfSpaces  = "                                                            ";
        int             spaceLen        = 0;
        String          spaces          = "";

        spaces = ( leftLine.length() > 60 ) ? "" : stringOfSpaces.substring( leftLine.length() );
        System.out.println( leftLine + spaces + " | " + rightLine );
    }


    public static String[] getCommands()
    {
        String[]            commands    = null;
        int                 commlen     = 0;
        LineNumberReader    in          = new LineNumberReader(new InputStreamReader(System.in));

        try
        {
            System.out.println( "\n:: Current global variables ::" );
            System.out.println( "userId             :: " + userId );
            System.out.println( "sessionKey         :: " + sessionKey );
            System.out.println( "sessionName        :: " + sessionName );
            System.out.print( "\nCommand: " );
            String commStr = in.readLine();
            StringTokenizer st = new StringTokenizer( commStr );
            if ( ( commlen = st.countTokens() ) < 1 ) commlen = 1;
            commands = new String[ commlen ];
            commands[ 0 ] = "";
            i = 0;
            while( st.hasMoreTokens() )
            {
                commands[ i++ ] = st.nextToken();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return commands;
    }

    public static void displayTradingSessions(String args[])
    {
        System.out.println( "\ncalling displayTradingSessions()");
        System.out.println( "\ttradingSessions count = " + tradingSessions.length );
        if ( tradingSessions != null )
        {
            for( int i = 0; i < tradingSessions.length; i++ )
            {
                System.out.println( "\t" + i + ") Session name:" + tradingSessions[ i ].sessionName );
            }
        }
        System.out.println( "\ncalling displayTradingSessions(" + ") done." );
    }

    public static void setTradingSession(String args[])
    {
        System.out.println( "\ncalling setTradingSession(sessionName)");
        if ( args.length > 0 )
        {
            sessionName = args[ 0 ];
        }
        System.out.println( "\ncalling setTradingSession(" + sessionName + ") done." );
    }

    public static void notImplemented( String args[] )
    {
        System.out.println( "\n!!! Method Currently Not Implemented !!!" );
    }

// *******************************************************************************************************************************
//  command line tests
// *******************************************************************************************************************************
//

// TraderAPI
//
    public static void getFirms(String args[])
    {
        try
        {
            boolean lBooleanActive    = true;
            boolean lBooleanClearingFirm = true;
            if( args[ 1 ].compareToIgnoreCase( "FALSE" ) == 0 )
            {
                lBooleanActive = false;
            }
            if( args[ 2 ].compareToIgnoreCase( "FALSE" ) == 0 )
            {
                lBooleanClearingFirm = false;
            }
            System.out.println( "\ncalling getFirms");
            ExchangeFirm[] firms = sacasAPI.getFirms( lBooleanActive, lBooleanClearingFirm);
            System.out.println( "\tfirms count = " + firms.length );
            for( i = 0; i < firms.length; i++ )
            {
                System.out.println( "\tFirm[" + i + "] = " + firms[i] );
            }
            System.out.println( "\ncalling getFirms(" + lBooleanActive + "," + lBooleanClearingFirm + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getProductClasses( String args[] )
    {
/*
        try
        {
            lBoolean    = false;
            lShort      = Short.parseShort(args[ 1 ]);
            if( args[ 2 ].compareToIgnoreCase( "TRUE" ) == 0 )
            {
                lBoolean = true;
            }
            System.out.println( "\ncalling getProductClasses(" + lShort + "," + lBoolean + ", productClassesListener)" );
//KAK            classSequence = sacasAPI.getProductClasses( lShort, lBoolean, productClassesListener );
            System.out.println( "\tclasses count = " + classSequence.length );
            for( i = 0; i < classSequence.length; i++ )
            {
                System.out.println( "\tclassSequence[" + i + "] = " + classSequence[ i ].classKey );
            }
            System.out.println( "\ncalling getProductClasses(" + lShort + "," + lBoolean + ", productClassesListener) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }

    public static void getProducts( String args[] )
    {
/*
        try
        {
            lBoolean    = false;
            lInt        = Integer.parseInt(args[ 1 ]);
            if( args[ 2 ].compareToIgnoreCase( "TRUE" ) == 0 )
            {
                lBoolean = true;
            }
            System.out.println( "\ncalling getProducts(" + lInt + "," + lBoolean + ", productsListener)" );
//KAK            productSequence = sacasAPI.getProducts( lInt, lBoolean, productsListener );
            System.out.println( "\tproducts count = " + productSequence.length );
            for( i = 0; i < productSequence.length; i++ )
            {
                System.out.println( "\tproductSequence[" + i + "] = " + productSequence[ i ].productKeys.productKey );
            }
            System.out.println( "\ncalling getProducts(" + lInt + "," + lBoolean + ", productsListener) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }

// ProductQuery
//
    public static void getProductTypes( String args[] )
    {
/*
        try
        {
            lBoolean = false;
            if( args[ 1 ].compareToIgnoreCase( "TRUE" ) == 0 )
            {
                lBoolean = true;
            }
            System.out.println( "\ncalling getProductTypes(" + lBoolean + ")" );
//KAK            productTypeSequence = sacasAPI.getProductTypes( lBoolean );
            System.out.println( "\tproduct types count = " + productTypeSequence.length );
            for( i = 0; i < productTypeSequence.length; i++ )
            {
                System.out.println( "\tproductTypeSequence[" + i + "] = " + productTypeSequence[ i ].name + "::" + productTypeSequence[ i ].description );
            }
            System.out.println( "\ngetProductTypes(" + lBoolean + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }

    public static void setProductStateByProduct( String args[] )
    {
        try
        {
            System.out.println( "\ncalling setProductStateByProduct(" + sessionName + "," + Integer.parseInt(args[ 1 ])+ "," + Short.parseShort(args[ 2 ]) + ")" );
//KAK            sacasAPI.setProductStateByProduct(Integer.parseInt(args[ 1 ]), Short.parseShort(args[ 2 ]));
            sacasAPI.setProductStateByProduct(sessionName, Integer.parseInt(args[ 1 ]), Short.parseShort(args[ 2 ]) );
            System.out.println( "\nsetProductStateByProduct(" + sessionName + "," + Integer.parseInt(args[ 1 ])+ "," + Short.parseShort(args[ 2 ]) + ") done::" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void setProductStateByClass( String args[] )
    {
        try
        {
            System.out.println( "\ncalling setProductStateByClass(" + sessionName + "," + Integer.parseInt(args[ 1 ])+ "," + Short.parseShort(args[ 2 ]) + ")" );
//KAK            sacasAPI.setProductStateByClass(Integer.parseInt(args[ 1 ]), Short.parseShort(args[ 2 ]));
            sacasAPI.setProductStateByClass(sessionName, Integer.parseInt(args[ 1 ]), Short.parseShort(args[ 2 ]) );
            System.out.println( "\nsetProductStateByClass(" + sessionName + "," + Integer.parseInt(args[ 1 ])+ "," + Short.parseShort(args[ 2 ]) + ") done::" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void setAllProductStates( String args[] )
    {
        try
        {
            System.out.println( "\ncalling setAllProductStates(" + sessionName + "," + Short.parseShort(args[ 1 ]) + ")" );
//KAK            sacasAPI.setAllProductStates(Short.parseShort(args[ 1 ]));
            sacasAPI.setAllProductStates(sessionName, Short.parseShort(args[ 1 ]));
            System.out.println( "\nsetAllProductStates(" + sessionName + "," + Short.parseShort(args[ 1 ]) + ") done::" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getExchangePrescribedWidth( String args[] )
    {
        try
        {
            lIntHolder.value    = 0;
            String lSessionName = args[ 1 ];
            int lClassKey       = Integer.parseInt(args[ 2 ]);
            System.out.println( "\ngetExchangePrescribedWidth(" + lSessionName + "," + lClassKey + "," + lIntHolder.value + ")" );
            epwSequence = sacasAPI.getExchangePrescribedWidth( lSessionName, lClassKey, lIntHolder );
            System.out.println( "\tepw count = " + epwSequence.length );
            for( i = 0; i < epwSequence.length; i++ )
            {
                System.out.println( "\tepwSequence[" + i + "] = " + epwSequence[ i ].maximumAllowableSpread );
            }
            System.out.println( "\ngetExchangePrescribedWidth(" + lSessionName + "," + lClassKey + "," + lIntHolder.value + ")" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getExchangePrescribedWidthForAllClasses( String args[] )
    {
      try
   {
       String lSessionName = args[ 1 ];
       lIntHolder.value = 0;
       System.out.println( "\ngetExchangePrescribedWidthForAllClasses(String " + lSessionName);
       spreadClassSequence = sacasAPI.getExchangePrescribedWidthForAllClasses(lSessionName);
       System.out.println( "\tspread  count = " + spreadClassSequence.length );
       for( i = 0; i < spreadClassSequence.length; i++ )
       {
           System.out.println( "\tspreadClassSequence[" + i + "] = " + spreadClassSequence[ i ].classKey );
       }
   }
   catch (Throwable e)
   {
       e.printStackTrace();
   }

//TODO - remove
  /*    try
      {
          String lSessionName = args[ 1 ];
          lIntHolder.value = 0;
          System.out.println( "\ngetExchangePrescribedWidthForAllClasses(String " + lSessionName);
          classSpreadSequence = sacasAPI.getExchangePrescribedWidthForAllClasses(lSessionName);
          System.out.println( "\tspread  count = " + classSpreadSequence.length );
          for( i = 0; i < classSpreadSequence.length; i++ )
          {
              System.out.println( "\tclassSpreadSequence[" + i + "] = " + classSpreadSequence[ i ].classKey );
          }
      }
      catch (Throwable e)
      {
          e.printStackTrace();
      } */
  }
//TODO remove
/*        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ngetExchangePrescribedWidthForAllClasses(String " + lSessionName + ", IntHolder lIntHolder)::" + lIntHolder.value );
            classSpreadSequence = sacasAPI.getExchangePrescribedWidthForAllClasses(lSessionName, lIntHolder);
            System.out.println( "\tspread  count = " + classSpreadSequence.length );
            for( i = 0; i < classSpreadSequence.length; i++ )
            {
                System.out.println( "\tclassSpreadSequence[" + i + "] = " + classSpreadSequence[ i ].classKey );
            }
            System.out.println( "\ngetExchangePrescribedWidthForAllClasses(IntHolder lIntHolder) done.::" + lIntHolder.value );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
*/
    public static void getOpeningPriceRate( String args[] )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ncalling getOpeningPriceRate(String " + lSessionName + ", IntHolder lIntHolder)::" + lIntHolder.value );
            lInt = sacasAPI.getOpeningPriceRate(lSessionName, lIntHolder);
            System.out.println( "\n" + lInt + " = getOpeningPriceRate(String " + lSessionName + ", IntHolder lIntHolder) done.::" + lIntHolder.value );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void setOpeningPriceRate( String args[] )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lInt                = Integer.parseInt( args[ 2 ] );
            lIntHolder.value    = Integer.parseInt( args[ 3 ] );
            System.out.println( "\ncalling setOpeningPriceRate(String " + lSessionName + ", int" + lInt + ", int " + lIntHolder.value + ")");
            sacasAPI.setOpeningPriceRate(lSessionName, lInt, lIntHolder.value);
            System.out.println( "\nsetOpeningPriceRate(String " + lSessionName + ", int " + lInt + ", int " + lIntHolder.value + ")" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void createSession( String args[] )
    {
        try
        {
            System.out.println( "\ncalling createSession(" + args[ 1 ] + ", RouteNameHelper.getRouteName())" );
            sessionKey = sacasAPI.createSession(args[ 1 ], RouteNameHelper.getRouteName(), RouteNameHelper.getRouteName());
            System.out.println( "\ncreateSession(" + args[ 1 ] + ", RouteNameHelper.getRouteName()) done::" + sessionKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void forceCloseSession( String args[] )
    {
        try
        {
                int sessionKey = sacasAPI.getSessionByUserId(args[1]);
            System.out.println( "\ncalling forceCloseSession( " + args[1]  + ")");
            sacasAPI.forceCloseSession( sessionKey, "Force Close Session for user - " + args[1] );
            System.out.println( "\nforceCloseSession( " + args[1] + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void forceLeaveSession( String args[] )
    {
        try
        {
            int sessionKey = sacasAPI.getSessionByUserId(args[1]);
            System.out.println( "\ncalling forceLeaveSession( " + args[1] + ")" );
            sacasAPI.forceLeaveSession( sessionKey, RouteNameHelper.getRouteName(), "Force Leave Session for user - " + args[1] );
            System.out.println( "\nforceLeaveSession( " + args[ 1 ] + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getSessionByUserId( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getSessionByUserId(" + args[ 1 ] + ")" );
            sessionKey = sacasAPI.getSessionByUserId( args[ 1 ] );
            System.out.println( "\ngetSessionByUserId(" + args[ 1 ] + ") done::" + sessionKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getUserByKey(String args[] )
    {
         try
        {
            System.out.println( "\ncalling getUserByKey()" );
            userKey               = Integer.parseInt(args[ 1 ]);
            SessionProfileUserDefinitionStruct userDefinition = sacasAPI.getUserStructByKey(userKey);
            ReflectiveStructBuilder.printStruct(userDefinition, "UserDefinition");
            System.out.println( "getUserByKey() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getAllUsers( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getAllUsers()" );
            userSequence = sacasAPI.getAllUsers();
            System.out.println( "\tuser definition count = " + userSequence.length );
            for( i = 0; i < userSequence.length; i++ )
            {
                System.out.println( "\tuserDefinitionSequence[" + i + "] = " + userSequence[i].getFullName() +
                                    "::" + userSequence[i].getUserKey());
            }
            System.out.println( "\ngetAllUsers() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getDpmParticipantsForClass( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getDPMParticipantForClass()" );
            int classKey               = Integer.parseInt(args[ 1 ]);
            UserAccountModel[] userDefinitions = sacasAPI.getDPMParticipantForClass(classKey);
            for( int i = 0; i < userDefinitions.length; i++ )
            {
                ReflectiveStructBuilder.printStruct(userDefinitions[i], "UserDefinitions");
            }
            System.out.println( "getDPMParticipantForClass() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    public static void getDpmsForJointAccount( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getDpmsForJointAccount()" );
            String jointAccountUserId = args[ 1 ];
            UserAccountModel[] userDefinitions = sacasAPI.getDPMForJointAccount(jointAccountUserId);
            for( int i = 0; i < userDefinitions.length; i++ )
            {
                ReflectiveStructBuilder.printStruct(userDefinitions[i], "userDefinitions");
            }
            System.out.println( "getDpmsForJointAccount() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    public static void getDpmJointAccountForClass( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getDpmJointAccountForClass()" );
            String userId              = args[1];
            int classKey               = Integer.parseInt(args[ 2 ]);
            UserAccountModel userDefinition =
                    sacasAPI.getDPMJointAccountForClass(userId, classKey);
            ReflectiveStructBuilder.printStruct(userDefinition.getUserDefinitionStruct(), "UserDefinition");
            System.out.println( "getDpmJointAccountForClass() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getDpmsForClass( String args[] )
    {
        try
        {
            System.out.println( "\ncalling getDpmsForClass()" );
            String userId              = args[1];
            int classKey               = Integer.parseInt(args[ 2 ]);
            UserAccountModel[] userDefinitions = sacasAPI.getDPMForClass(userId, classKey);
            for( int i = 0; i < userDefinitions.length; i++ )
            {
                ReflectiveStructBuilder.printStruct(userDefinitions[i], "UserDefinition");
            }
            System.out.println( "getDpmsForClass() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    public static void getAllPendingAdjustments( String[] args )
    {
        try
        {
            System.out.println( "\ncalling getAllPendingAdjustments()" );
            ProductAdjustmentContainer[] adjustmentInfo = sacasAPI.getAllPendingAdjustments();
            System.out.println( "\tadjustmentInfo count = " + adjustmentInfo.length );
            for( i = 0; i < adjustmentInfo.length; i++ )
            {
                System.out.println( "\tadjustmentInfo[" + i + "] = " + adjustmentInfo[ i ].getClassKey() );
            }
            System.out.println( "\ngetAllPendingAdjustments() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// TradingSessionService
//
    public static void getCurrentTradingSessions( String[] args )
    {
        try
        {
            System.out.println( "\ncalling getCurrentTradingSessions()" );
            com.cboe.idl.cmiSession.TradingSessionStruct[]  tradingSessions = sacasAPI.getCurrentTradingSessions( logoffListener );
            for( i = 0; i < tradingSessions.length; i++ )
            {
                System.out.println( "\ttradingSessions[" + i + "] = " + tradingSessions[ i ].sessionName + "::" + tradingSessions[ i ].sequenceNumber );
            }
            System.out.println( "\ngetCurrentTradingSessions() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void startSession( String[] args )
    {
        try
        {
            System.out.println( "\ncalling startSession( " + sessionName + " )" );
//KAK            sacasAPI.startSession(sessionKey);
            sacasAPI.startSession(sessionName);
            System.out.println( "\ncalling startSession( " + sessionName + " ) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// UserMaintenanceService
//
    public static void setSystemPreferences( String[] args )
    {
        try
        {
            System.out.println( "\ncalling setSystemPreferences(" + args[ 1 ] + "," + args[ 2 ] + ")" );
            preferencesSequence = new PreferenceStruct[ 1 ];
            preferencesSequence[ 0 ] = new PreferenceStruct(args[ 1 ], args[ 2 ]);
            sacasAPI.setSystemPreferences(preferencesSequence);
            System.out.println( "\ncalling setSystemPreferences(" + args[ 1 ] + "," + args[ 2 ] + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getSystemPreferences( String[] args )
    {
        try
        {
            System.out.println( "\ncalling getSystemPreferences()" );
            preferencesSequence = sacasAPI.getSystemPreferences();
            System.out.println( "\tpreferences count = " + preferencesSequence.length );
            for( i = 0; i < preferencesSequence.length; i++ )
            {
                System.out.println( "\tpreferencesSequence[" + i + "] = " + preferencesSequence[ i ].name + "::" + preferencesSequence[ i ].value );
            }
            System.out.println( "\ngetSystemPreferences() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// ProductConfigurationService
//
    public static void getProductClassesForGroupByKey( String[] args )
    {
        int lGroupType = Integer.parseInt( args[ 1 ] );
        try
        {
            System.out.println( "\ncalling getProductClassesForGroupByKey(" + lGroupType + ")" );
            productClassSequence = sacasAPI.getProductClassesForGroupByKey( lGroupType );

            System.out.println( "\tproduct classes count = " + productClassSequence.length );
            for( i = 0; i < productClassSequence.length; i++ )
            {
                System.out.println( "\tproductClassSequence[" + i + "] = " + productClassSequence[ i ] );
            }
            System.out.println( "\ngetProductClassesForGroupByKey(" + lGroupType + ") done." );

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void createGroup( String[] args )
    {
        String lGroup = args[ 1 ];
        int lType = Integer.parseInt( args[ 2 ] );
        try
        {
            System.out.println( "\ncalling createGroup(" + lGroup + ", " + lType + ")" );
            sacasAPI.createGroup( lGroup, lType );
            System.out.println( "\ncreateGroup(" + lGroup + ", " + lType + ") done." );

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void addProductClassToGroup( String[] args )
    {
        int lClassKey = Integer.parseInt( args[ 1 ] );
        String lGroup = args[ 2 ];
        try
        {
            System.out.println( "\ncalling addProductClassToGroup(" + lClassKey + ", " + lGroup + ")" );
            sacasAPI.addProductClassToGroup( lClassKey, lGroup );
            System.out.println( "\naddProductClassToGroup(" + lClassKey + ", " + lGroup + ") done." );

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getGroupTypes( String[] args )
    {
        String[]    largs = new String[ 2 ];
        largs[ 0 ] = "";
        try
        {
            System.out.println( "\ncalling getGroupTypes()" );
            groupTypeSequence = sacasAPI.getGroupTypes();
            for( i = 0; i < groupTypeSequence.length; i++ )
            {
                System.out.println( "\tgroupTypeSequence[" + i + "] = " + groupTypeSequence[ i ].groupType + "::" + groupTypeSequence[ i ].groupTypeDescription );
                largs[ 1 ] = Integer.toString(groupTypeSequence[ 0 ].groupType);
//                getGroupsByType( largs );
            }
            System.out.println( "\ngetGroupTypes() done." );

        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getGroupsByType( String[] args )
    {
        try
        {
            lInt = Integer.parseInt( args[ 1 ] );
            System.out.println( "\ncalling getGroupsByType(" + lInt + ")" );
            groupSequence = sacasAPI.getGroupsByType( lInt );
            for( i = 0; i < groupSequence.length; i++ )
            {
                System.out.println( "\tgroupSequence[" + i + "] = " + groupSequence[ i ].groupKey + "::" + groupSequence[ i ].groupName );
            }
            System.out.println( "\ncalling getGroupsByType(" + lInt + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// ProductMaintenanceService
// ProductState
//
    public static void priceAdjustmentUpdateComplete( String[] args )
    {
        try
        {
            System.out.println( "\ncalling priceAdjustmentUpdateComplete()" );
            sacasAPI.priceAdjustmentUpdateComplete();
            System.out.println( "\npriceAdjustmentUpdateComplete() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// TradingPropertyService
//
    public static void getOpeningPriceDelay( String[] args )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ncalling getOpeningPriceDelay(String " + lSessionName + ", IntHolder lIntHolder)::" + lIntHolder.value );
            int result = sacasAPI.getOpeningPriceDelay(lSessionName, lIntHolder);
            System.out.println( "\ngetOpeningPriceDelay(String " + lSessionName + ", IntHolder lIntHolder) done.::" + result + "::" + lIntHolder.value );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getContinuousQuotePeriodForCredit( String[] args )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ncalling getContinuousQuotePeriodForCredit(String " + lSessionName + ", lIntHolder)::" + lIntHolder.value );
            lInt = sacasAPI.getContinuousQuotePeriodForCredit(lSessionName, lIntHolder);
            System.out.println( "\ngetContinuousQuotePeriodForCredit(String " + lSessionName + ", lIntHolder) done.::" + lInt + "::" + lIntHolder.value );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getMinQuoteCreditDefaultSize( String[] args )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ncalling getMinQuoteCreditDefaultSize(String " + lSessionName + ", lIntHolder)::" + lIntHolder.value );
            lInt = sacasAPI.getMinQuoteCreditDefaultSize(lSessionName, lIntHolder);
            System.out.println( "\ngetMinQuoteCreditDefaultSize(String " + lSessionName + ", lIntHolder) done.::" + lInt + "::" + lIntHolder.value );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getOpeningPeriodTimeRange( String[] args )
    {
        try
        {
            String lSessionName = args[ 1 ];
            lIntHolder.value = 0;
            System.out.println( "\ncalling getOpeningPeriodTimeRange(String " + lSessionName + ", lIntHolder)::" + lIntHolder.value );
            timeRangeStruct = sacasAPI.getOpeningPeriodTimeRange(lSessionName, lIntHolder);
            System.out.println( "\ngetOpeningPeriodTimeRange(String " + lSessionName + ", lIntHolder) done.::" + timeRangeStruct.lowerLimit + "::" + timeRangeStruct.upperLimit );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// TradingSessionService
//
    public static void getAllTemplates( String[] args )
    {
        String[]    largs = new String[ 2 ];
        largs[ 0 ] = "";
        try
        {
            System.out.println( "\ncalling getAllTemplates()" );
            tradingSessionTemplateSequence = sacasAPI.getAllTemplatesV2();
            System.out.println( "\ttrading session template count = " + tradingSessionTemplateSequence.length );

            for( i = 0; i < tradingSessionTemplateSequence.length; i++ )
            {
                largs[ 1 ] = tradingSessionTemplateSequence[ i ].tsElementTemplateStruct.templateName;
                System.out.println( "\ttradingSessionTemplateSequence[" + i + "] = " + largs[ 1 ] );
//                getSessionsForTemplate( largs );
            }

            System.out.println( "\ngetAllTemplates() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getSessionsForTemplate( String[] args )
    {
/*
        try
        {
            System.out.println( "\n\tcalling getSessionsForTemplates(" + args[ 1 ] + ")" );
//KAK            tradingSessionSequence = sacasAPI.getSessionsForTemplate(args[ 1 ]);
            System.out.println( "\t\tsessions for template count = " + tradingSessionSequence.length );
            for( j = 0; j < tradingSessionSequence.length; j++ )
            {
//KAK                System.out.println( "\t\ttradingSessionSequence[" + j + "] = " + tradingSessionSequence[ j ].tradingSessionName );
            }
            System.out.println( "\n\tgetSessionsForTemplates(" + args[ 1 ] + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }

// SessionManagementAdminService
//
    public static void closeSession( String[] args )
    {
        int     lSessionKey = Integer.parseInt( args[ 1 ] );
        try
        {
            System.out.println( "\ncalling closeSession(sessionKey)" );
            sacasAPI.closeSession(lSessionKey);
            System.out.println( "\ncloseSession(sessionKey) done::" + lSessionKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

// SecurityAdminService
//
    public static void getGroups( String[] args )
    {
        try
        {
            String[]    groupsSequence;

            System.out.println( "\ncalling getGroups()" );
            groupsSequence = sacasAPI.getGroups();
            System.out.println( "\tgroups count = " + groupsSequence.length );
            for( i = 0; i < groupsSequence.length; i++ )
            {
                System.out.println( "\tgroupsSequence[" + i + "] = " + groupsSequence[ i ] );
            }
            System.out.println( "\ngetGroups() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getServices( String[] args )
    {
        try
        {
            String[]    servicesSequence;

            System.out.println( "\ncalling getServices()" );
            servicesSequence = sacasAPI.getServices();
            System.out.println( "\tservices count = " + servicesSequence.length );
            for( i = 0; i < servicesSequence.length; i++ )
            {
                System.out.println( "\tservicesSequence[" + i + "] = " + servicesSequence[ i ] );
            }
            System.out.println( "\ngetServices() done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getAccount( String[] args )
    {
        try
        {
            System.out.println( "\ncalling getAccount(" + args[ 1 ] + ")" );
            memberAccountStruct = sacasAPI.getAccount( args[ 1 ] );
            System.out.println( "\t" + "commonName::" + memberAccountStruct.commonName );
            System.out.println( "\t" + "email     ::" + memberAccountStruct.email );
            System.out.println( "\t" + "employeeID::" + memberAccountStruct.employeeID );
            System.out.println( "\t" + "givenName ::" + memberAccountStruct.givenName );
            System.out.println( "\t" + "group     ::" + memberAccountStruct.group );
            System.out.println( "\t" + "status    ::" + memberAccountStruct.status );
            System.out.println( "\t" + "userID    ::" + memberAccountStruct.userID );
            System.out.println( "\ngetAccount(" + args[ 1 ] + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getTradeReportByTradeId(String[] args)
    {
        int     highTradeId = Integer.parseInt( args[ 1 ] );
        int     lowTradeId = Integer.parseInt(args[ 2] );

        try
        {
            System.out.println( "\ncalling getTradeReportByTradeId(lTradeId)" );
            lTradeReport = sacasAPI.getTradeReportByTradeId(new CboeIdStruct(highTradeId, lowTradeId), true);
            ReflectiveStructBuilder.printStruct(lTradeReport, "TradeReport");
            System.out.println( "getTradeReportByTradeId(lTradeId) done::" + lowTradeId );
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void acceptTradeBust(String[] args)
    {
        if (lTradeReport == null)
        {
            System.out.println("Unable to acceptTradeBust, please call getTradeReportByTradeId(int tradeId) first.");
        }
        else    // build BustTradeStruct based on the atomicTrade in the trade report
        {
            BustTradeStruct[] tradeBusted = new BustTradeStruct[lTradeReport.parties.length];
            for (int i = 0; i < lTradeReport.parties.length; i++)
            {
                tradeBusted[i] = new BustTradeStruct();
                tradeBusted[i].atomicTradeId = lTradeReport.parties[i].atomicTradeId;
                //tradeBusted[i].bustedQuantity = lTradeReport.parties[i].quantity;
                tradeBusted[i].bustedQuantity = 1;
                //tradeBusted[i].buyerReinstateRequested = lTradeReport.parties[i].reinstatableForBuyer;
                tradeBusted[i].buyerReinstateRequested = true;
//                tradeBusted[i].buyerReinstateRequested = false;
                tradeBusted[i].sellerReinstateRequested = false;
                //tradeBusted[i].sellerReinstateRequested = lTradeReport.parties[i].reinstatableForSeller;
            }

            ReflectiveStructBuilder.printStruct(tradeBusted, "tradeBusted");

            try
            {
                System.out.println( "\ncalling acceptTradeBust" );
                sacasAPI.acceptTradeBust(sessionName, lTradeReport.productKey, lTradeReport.tradeId, tradeBusted, "Testing");
                System.out.println( "acceptTradeBust done::" + lTradeReport.tradeId );
            }
            catch(Throwable e)
            {
                e.printStackTrace();
            }
        }
    }
// TextMessagingService
//
    public static void sendMessageForUser( String[] args )
    {
        try
        {
            String                  to          = args[ 1 ];
            String                  subject     = args[ 2 ];
            String                  text        = args[ 3 ];
            MessageResultStruct     messageResults;

            DestinationStruct[] receipients     = new DestinationStruct[ 1 ];
            receipients[ 0 ]                    = new DestinationStruct( to, TextMessageTypes.USER );
            MessageStruct messageStruct         = new MessageStruct( UnitTestHelper.createCurrentDateTimeStruct(), 0, 0, userId, subject, false, text );
            MessageTransportStruct  message     = new MessageTransportStruct( TextMessageStates.UNDELIVERED, DEFAULT_TTL, messageStruct );

            System.out.println( "\ncalling sendMessageForUser(" + to + "," + subject + "," + text + ")" );
            messageResults = sacasAPI.sendMessage( receipients, message );
            for( i = 0; i < messageResults.status.length; i++ )
            {
                System.out.println( "    Send error :: \n"
                                    + "        To       :" + messageResults.status[ 0 ].originalReceipient + "\n"
                                    + "        Resolved :" + messageResults.status[ 0 ].resolvedReceipient + "\n"
                                    + "        Error    :" + messageResults.status[ 0 ].error + "\n"
                                    );
            }
            System.out.println( "\nsendMessageForUser(" + to + "," + subject + "," + text + ") done.::" + messageResults.messageKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    public static void sendMessageForProductClass( String[] args )
    {
        try
        {
            String                  to          = args[ 1 ];
            String                  subject     = args[ 2 ];
            String                  text        = args[ 3 ];
            MessageResultStruct     messageResults;

            DestinationStruct[] receipients     = new DestinationStruct[ 1 ];
            receipients[ 0 ]                    = new DestinationStruct( to, TextMessageTypes.PRODUCT );
            MessageStruct messageStruct         = new MessageStruct( UnitTestHelper.createCurrentDateTimeStruct(), 0, 0, userId, subject, false, text );
            MessageTransportStruct  message     = new MessageTransportStruct( TextMessageStates.UNDELIVERED, DEFAULT_TTL, messageStruct );

            System.out.println( "\ncalling sendMessageForProductClass(" + to + "," + subject + "," + text + ")" );
            messageResults = sacasAPI.sendMessage( receipients, message );
            for( i = 0; i < messageResults.status.length; i++ )
            {
                System.out.println( "    Send error :: \n"
                                    + "        To       :" + messageResults.status[ 0 ].originalReceipient + "\n"
                                    + "        Resolved :" + messageResults.status[ 0 ].resolvedReceipient + "\n"
                                    + "        Error    :" + messageResults.status[ 0 ].error + "\n"
                                    );
            }
            System.out.println( "\nsendMessageForProductClass(" + to + "," + subject + "," + text + ") done.::" + messageResults.messageKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    public static void sendMessageToHelpDesk( String[] args )
    {
        try
        {
            String                  subject     = args[ 1 ];
            String                  text        = args[ 2 ];
            MessageResultStruct     messageResults;

            DestinationStruct[] receipients     = new DestinationStruct[ 1 ];
            receipients[ 0 ] = new DestinationStruct( "" + Role.HELP_DESK.getRoleChar(), 
                                                      TextMessageTypes.ROLE );
            MessageStruct messageStruct         = new MessageStruct( UnitTestHelper.createCurrentDateTimeStruct(), 0, 0, userId, subject, false, text );
            MessageTransportStruct  message     = new MessageTransportStruct( TextMessageStates.UNDELIVERED, DEFAULT_TTL, messageStruct );

            System.out.println( "\ncalling sendMessageToHelpDesk(" + subject + "," + text + ")" );
            messageResults = sacasAPI.sendMessage( receipients, message );
            for( i = 0; i < messageResults.status.length; i++ )
            {
                System.out.println( "    Send error :: \n"
                                    + "        To       :" + messageResults.status[ 0 ].originalReceipient + "\n"
                                    + "        Resolved :" + messageResults.status[ 0 ].resolvedReceipient + "\n"
                                    + "        Error    :" + messageResults.status[ 0 ].error + "\n"
                                    );
            }
            System.out.println( "\nsendMessageToHelpDesk(" + subject + "," + text + ") done.::" + messageResults.messageKey );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    public static void publishMessagesForUser( String[] args )
    {
        try
        {
            String              user            = args[ 1 ];

            System.out.println( "\ncalling publishMessagesForUser(" + user + ")" );
            sacasAPI.publishMessagesForUser(user);
            System.out.println( "\npublishMessagesForUser(" + user + ") done.::" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void createTemplate( String[] args )
    {
        try
        {
            String  templateName        = args[ 1 ];
            String  templateText        = args[ 2 ];

            System.out.println( "\ncalling createTemplate(" + templateName +","+ templateText + ")" );
            sacasAPI.createTemplate(templateName, templateText);
            System.out.println( "\ncreateTemplate(" + templateName +","+ templateText + ") done" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void updateTemplate( String[] args )
    {
        try
        {
            String  templateName        = args[ 1 ];
            String  templateText        = args[ 2 ];

            System.out.println( "\ncalling updateTemplate(" + templateName +","+ templateText + ")" );
            sacasAPI.updateTemplate(templateName, templateText);
            System.out.println( "\nupdateTemplate(" + templateName +","+ templateText + ") done" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void deleteTemplate( String[] args )
    {
        try
        {
            String  templateName        = args[ 1 ];

            System.out.println( "\ncalling deleteTemplate(" + templateName + ")" );
            sacasAPI.deleteTemplate(templateName);
            System.out.println( "\ndeleteTemplate(" + templateName + ") done" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getAvailableTemplates( String[] args )
    {
        String[]        templateNames;

        try
        {
            System.out.println( "\ncalling getAvailableTemplates()" );
            templateNames = sacasAPI.getAvailableTemplates();
            System.out.println( "\ttemplate count = " + templateNames.length );
            for( i = 0; i < templateNames.length; i++ )
            {
                System.out.println( "\ttemplateNames[" + i + "] = " + templateNames[ i ] );
            }
            System.out.println( "\ngetAvailableTemplates() done" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void getTemplateText( String[] args )
    {
        String      templateText;

        try
        {
            String  templateName        = args[ 1 ];

            System.out.println( "\ncalling getTemplateText(" + templateName + ")" );
            templateText = sacasAPI.getTemplateText(templateName);
            System.out.println( "\ngetTemplateText(" + templateName + ") done::\n" + templateText + "\n::\n" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


// OrderBookService
//
    public static void getBook( String[] args )
    {
        Tradable[]        tradables   = null;
        Price[]           prices      = new Price[ 1 ];

        try
        {
            lInt        = Integer.parseInt( args[ 1 ] );
            lDouble     = Double.parseDouble( args[ 2 ] );
            prices[ 0 ] = PriceFactory.create( lDouble );

            System.out.println( "\ncalling getBook(" + sessionName + "," + lInt + "," + lDouble + ")" );
//KAK            tradables = sacasAPI.getBook(lInt, prices);
            SessionProduct product = sacasAPI.getProductByKeyForSession(sessionName, lInt);
            tradables = sacasAPI.getBookDetails(product, prices);
            System.out.println( "\ttradables count = " + tradables.length );
            for( i = 0; i < tradables.length; i++ )
            {
                System.out.println( "\ttradables[" + i + "] = "
                                    + tradables[ i ].getUserId()
                                    + "::" + tradables[ i ].getPrice().getWhole() + "." + tradables[ i ].getPrice().getFraction()
                                    + "::" + tradables[ i ].getTradableType()
                                    + "[ highId = " + tradables[ i ].getId().getHighId()
                                    + ": lowId = " + tradables[ i ].getId().getLowId() + " ]"
                                    );
            }
            System.out.println( "\ngetBook(" + sessionName + "," + lInt + "," + lDouble + ") done::\n" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


/*
        quote::InternalQuoteStruct getQuoteForProduct(in cmiProduct::ProductKey productKey, in cmiUtil::CboeIdStruct cboeId)
*/

// MarketMakerQuoteService
//
    public static void getQuoteForProduct( String[] args )
    {
        InternalQuoteStruct         quoteStruct = null;

        try
        {
            lInt        = Integer.parseInt( args[ 1 ] );
            int highId  = Integer.parseInt( args[ 2 ] );
            int lowId   = Integer.parseInt( args[ 3 ] );
            CboeIdStruct cboeId = new CboeIdStruct( highId, lowId );

            System.out.println( "\ncalling getQuoteForProduct(" + sessionName + "," + lInt + "," + highId + "," + lowId + ")" );
//KAK            quoteStruct = sacasAPI.getQuoteForProduct( lInt, cboeId );
            quoteStruct = sacasAPI.getQuoteForProduct( sessionName, lInt, cboeId );
            System.out.println( "\tquoteStruct :: Bid :"
                                + quoteStruct.quoteStruct.bidQuantity + " @ " + quoteStruct.quoteStruct.bidPrice.whole + "." + quoteStruct.quoteStruct.bidPrice.fraction
                                + " Ask :"
                                + quoteStruct.quoteStruct.askQuantity + " @ " + quoteStruct.quoteStruct.askPrice.whole + "." + quoteStruct.quoteStruct.askPrice.fraction
                                );
            System.out.println( "\ncalling getQuoteForProduct(" + sessionName + "," + lInt + "," + highId + "," + lowId + ") done::\n" );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }



    //UserTradingParameterService

    public static void setQRMProfile( String[] args )
    {
        QuoteRiskManagementProfileStruct qrmProfile = new QuoteRiskManagementProfileStruct( Integer.parseInt(args[2]),
                                                                                            Integer.parseInt(args[3]),
                                                                                            Integer.parseInt(args[4]),
                                                                                            (new Boolean(args[5])).booleanValue());

        try
        {
            sacasAPI.setQuoteRiskProfile(args[1], qrmProfile);
        } catch(org.omg.CORBA.UserException e)
        {
            e.printStackTrace();
        }

    }

    public static void getQRMProfile( String[] args )
    {
        try
        {
            QuoteRiskManagementProfileStruct qrmProfile = sacasAPI.getQuoteRiskManagementProfileByClass(args[1], Integer.parseInt(args[2]));
            ReflectiveStructBuilder.printStruct(qrmProfile, GET_QRM_PROFILE);
        } catch (org.omg.CORBA.UserException e)
        {
             e.printStackTrace();
        }
    }

     public static void changeQRMGlobalStatus( String[] args )
     {
        try
        {
            sacasAPI.setQuoteRiskManagementEnabledStatus(args[1], (new Boolean(args[2])).booleanValue());
            System.out.println(" QRM Global Status Changed to " + (new Boolean(sacasAPI.getQuoteRiskManagementEnabledStatus(args[1]))).toString());
        } catch (org.omg.CORBA.UserException e)
        {
             e.printStackTrace();
        }

     }
//
// Quote
//
    public static void subscribeRFQ( String args[] )
    {
/*
        lInt        = Integer.parseInt(args[ 1 ]);

        System.out.println( "\ncalling subscribeRFQ(" + lInt + ", rfqListener)" );
        try
        {
//KAK            sacasAPI.subscribeRFQ( lInt, rfqListener );
            System.out.println( "\ncalling subscribeRFQ(" + lInt + ", rfqListener) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }

    public static void unsubscribeRFQ( String args[] )
    {
/*
        lInt        = Integer.parseInt(args[ 1 ]);

        System.out.println( "\ncalling unsubscribeRFQ(" + lInt + ", rfqListener)" );
        try
        {
//KAK            sacasAPI.unsubscribeRFQ( lInt, rfqListener );
            System.out.println( "\ncalling unsubscribeRFQ(" + lInt + ", rfqListener) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }


//
// Market Maker API
//
    public static void getUserMarketDataByProduct( String args[] )
    {
/*
        lInt        = Integer.parseInt(args[ 1 ]);

        System.out.println( "\ncalling getUserMarketDataByProduct(" + lInt + ", productListener)" );
        try
        {
//KAK            UserMarketDataStruct mmdata = sacasAPI.getUserMarketDataByProduct( lInt, productsListener );
//KAK            ReflectiveStructTester.printStruct(mmdata, mmdata.getClass().toString() );
            System.out.println( "\ncalling getUserMarketDataByProduct(" + lInt + ", productsListener) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }*/
    }


     public static void getAllQuoteRiskProfiles( String[] args )
     {
        try
        {
            UserQuoteRiskManagementProfileStruct allQRMProfiles = sacasAPI.getAllQuoteRiskProfiles(args[1]);
            ReflectiveStructBuilder.printStruct(allQRMProfiles, GET_ALL_QRM_PROFILES);
        } catch (org.omg.CORBA.UserException e)
        {
             e.printStackTrace();
        }
     }

//
// Order Handling Service API
//
    public static void getOrdersForProduct( String args[] )
    {
        String                  lUserId                 = args[ 1 ];
        lInt                                            = Integer.parseInt(args[ 2 ]);
        OrderDetailStruct[]     orderDetailStructSeq    = null;
        UserDefinitionStruct    userDef                 = getDefaultUserDefinitionStruct( lUserId );

        System.out.println( "\ncalling getOrdersForProduct(" + lUserId + "," + lInt + ")" );
        try
        {
//            orderDetailStructSeq = sacasAPI.getOrdersForProduct(userDef, lInt);
            System.out.println( "\torder detail count = " + orderDetailStructSeq.length );
            for( i = 0; i < orderDetailStructSeq.length; i++ )
            {
                System.out.println( "\torderDetailStructSeq[" + i + "] = " + orderDetailStructSeq[ i ].orderStruct.productKey );
            }
            System.out.println( "\ncalling getOrdersForProduct(" + lUserId + "," + lInt + ") done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


    private static UserDefinitionStruct getDefaultUserDefinitionStruct( String userId )
    {
        UserDefinitionStruct            userDefStruct = null;

        userDefStruct = new UserDefinitionStruct(
                                                0
                                                ,0
                                                ,0
                                                ,(short)0
                                                ,'R'
                                                ,userId
                                                ,userId
                                                ,new ExchangeAcronymStruct("exchange",userId)
                                                ,new ProfileStruct[ 0 ]
                                                ,new ProfileStruct( 0, "account", "subaccount", new ExchangeFirmStruct("exchange", "executingGiveupFirm"))
                                                ,new AccountDefinitionStruct[ 0 ]
                                                ,new int[ 0 ]
                                                ,true
                                                ,new com.cboe.idl.cmiUser.ExchangeFirmStruct[ 0 ]
                                                ,UnitTestHelper.createCurrentDateTimeStruct()
                                                ,UnitTestHelper.createCurrentDateTimeStruct()
                                                ,1
                                                ,new DpmStruct[0]
                                                );

        return userDefStruct;
    }


//
// TradingSessionMaintenanceEvent Service API
//
    public static void subscribeTradingSessionEventState( String args[] )
    {

        System.out.println( "\ncalling subscribeTradingSessionEventState( eventStateListener )" );
        try
        {
            sacasAPI.subscribeTradingSessionEventState( eventStateListener );
            System.out.println( "\ncalling subscribeTradingSessionEventState( eventStateListener ) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void unsubscribeTradingSessionEventState( String args[] )
    {

        System.out.println( "\ncalling unsubscribeTradingSessionEventState( eventStateListener )" );
        try
        {
            sacasAPI.unsubscribeTradingSessionEventState( eventStateListener );
            System.out.println( "\ncalling unsubscribeTradingSessionEventState( eventStateListener ) done." );
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }


//
//
//

    public static void TestCase()
    {
        String[]    args;

        args = new String[ 0 ];
        getAllPendingAdjustments( args );
        getCurrentTradingSessions( args );

        args = new String[ 3 ];
        args[ 0 ] = "";
        args[ 1 ] = "Preference Tag";
        args[ 2 ] = "Preference Item";
        setSystemPreferences( args );

        args = new String[ 0 ];
        getSystemPreferences( args );
        getAllUsers( args );
        getGroupTypes( args );

        args = new String[ 2 ];
        args[ 0 ] = "";
        args[ 1 ] = Integer.toString( ProductStates.CLOSED );
        setAllProductStates( args );
        args[ 1 ] = Integer.toString( ProductStates.OPEN );
        setAllProductStates( args );

        args = new String[ 0 ];
        priceAdjustmentUpdateComplete( args );
        getOpeningPriceDelay(args );
        getContinuousQuotePeriodForCredit(args );
        getMinQuoteCreditDefaultSize(args );
        getOpeningPeriodTimeRange(args );

        args = new String[ 2 ];
        args[ 0 ] = "";
        args[ 1 ] = "true";
        getProductTypes( args );

        args = new String[ 0 ];
        getAllTemplates( args );

        args = new String[ 0 ];
        getGroups( args );
        getServices( args );

   }

}
