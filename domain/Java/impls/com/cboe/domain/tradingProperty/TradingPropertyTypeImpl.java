//
// -----------------------------------------------------------------------------------
// Source file: TradingPropertyTypeImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import com.cboe.idl.tradingProperty.GlobalTradingPropertyTypes;
import com.cboe.idl.constants.PropertyCategoryTypes;

import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Provides the references and conversion between a Trading Property integer constant and the name.
 */
public class TradingPropertyTypeImpl implements TradingPropertyType
{
    //***** START OF EDITABLE SECTION *****************
    // This section is designed to provide definition and mapping between the trading property type and name.
    // It is based in part on an ENUM type approach implementable in java.
    // Each and every trading property MUST have its own, unique type-to-name definition within all defined trading
    // properties.
    // All trading properties, whether "old" IDL based or new, should be defined here in the following way:
    //
    // 1. Each trading property is given a private, static, numeric constant
    // 2. Then, a static TradingPropertyType() is created for that constant with the other attributes defined
    // 3. Finally, the new TradingPropertyType element is added to the seq. of all properties.
    //
    // i.e.
    //      private static final int my_new_trading_property = <next_number>;
    //      public static final TradingPropertyType MY_NEW_TRADING_PROPERTY =
    //          new TradingPropertyType( "mynewtradingproperty", "My New Trading Property", my_new_trading_property,
    //                                   true );
    //      public static final TradingPropertyType[] globalTradingPropertyTypes =
    //          { ....., MY_NEW_TRADING_PROPERTY, ... };
    //
    private static final int epw_struct = GlobalTradingPropertyTypes.EPW_STRUCT;
    private static final int min_quote_credit_default_size =
            GlobalTradingPropertyTypes.MIN_QUOTE_CREDIT_DEFAULT_SIZE;
    private static final int rfq_response_ratio = GlobalTradingPropertyTypes.RFQ_RESPONSE_RATIO;
    private static final int allocation_strategy = GlobalTradingPropertyTypes.ALLOCATION_STRATEGY;
    private static final int contingency_time_to_live = GlobalTradingPropertyTypes.CONTINGENCY_TIME_TO_LIVE;
    private static final int continuous_quote_period_for_credit =
            GlobalTradingPropertyTypes.CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT;
    private static final int fast_market_spread_multiplier = GlobalTradingPropertyTypes.FAST_MARKET_SPREAD_MULTIPLIER;
    private static final int opening_time_period_range = GlobalTradingPropertyTypes.OPENING_TIME_PERIOD_RANGE;
    private static final int opening_price_delay = GlobalTradingPropertyTypes.OPENING_PRICE_DELAY;
    private static final int opening_price_rate = GlobalTradingPropertyTypes.OPENING_PRICE_RATE;
    private static final int preclosing_time_period = GlobalTradingPropertyTypes.PRECLOSING_TIME_PERIOD;
    private static final int prescribed_width_ratio = GlobalTradingPropertyTypes.PRESCRIBED_WIDTH_RATIO;
    private static final int rfq_timeout = GlobalTradingPropertyTypes.RFQ_TIMEOUT;
    private static final int dpm_participation_percentage = GlobalTradingPropertyTypes.DPM_PARTICIPATION_PERCENTAGE;
    private static final int book_depth_size = GlobalTradingPropertyTypes.BOOK_DEPTH_SIZE;
    private static final int min_size_for_block_trade = GlobalTradingPropertyTypes.MIN_SIZE_FOR_BLOCK_TRADE;
    private static final int ipp_min_size = GlobalTradingPropertyTypes.IPP_MIN_SIZE;
    private static final int ipp_trade_through_flag = GlobalTradingPropertyTypes.IPP_TRADE_THROUGH_FLAG;
    private static final int quote_lock_timer = GlobalTradingPropertyTypes.QUOTE_LOCK_TIMER;
    private static final int quote_lock_notification_timer = GlobalTradingPropertyTypes.QUOTE_LOCK_NOTIFICATION_TIMER;
    private static final int quote_trigger_timer = GlobalTradingPropertyTypes.QUOTE_TRIGGER_TIMER;
    private static final int dpm_rights_scales = GlobalTradingPropertyTypes.DPM_RIGHTS_SCALES;
    private static final int dpm_rights_split_rate = GlobalTradingPropertyTypes.DMP_RIGHTS_SPLIT_RATE;
    private static final int uma_split_rate = GlobalTradingPropertyTypes.UMA_SPLIT_RATE;
    private static final int uma_equal_distribution_weight_for_dpm =
            GlobalTradingPropertyTypes.UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM;
    private static final int lot_size = GlobalTradingPropertyTypes.LOT_SIZE;
    private static final int etf_flag = GlobalTradingPropertyTypes.ETF_FLAG;
    private static final int ipp_tolerance_amount = GlobalTradingPropertyTypes.IPP_TOLERANCE_AMOUNT;
    private static final int needs_dpm_quote_to_open = GlobalTradingPropertyTypes.NEEDS_DPM_QUOTE_TO_OPEN;
    private static final int s_order_time_to_live = GlobalTradingPropertyTypes.S_ORDER_TIME_TO_LIVE;
    private static final int s_order_time_to_create = GlobalTradingPropertyTypes.S_ORDER_TIME_TO_CREATE;
    private static final int s_order_time_to_create_before_close =
            GlobalTradingPropertyTypes.S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE;
    private static final int s_order_time_to_reject_fill = GlobalTradingPropertyTypes.S_ORDER_TIME_TO_REJECT_FILL;
    private static final int p_order_time_to_live = GlobalTradingPropertyTypes.P_ORDER_TIME_TO_LIVE;
    private static final int pa_order_time_to_live = GlobalTradingPropertyTypes.PA_ORDER_TIME_TO_LIVE;
    private static final int trade_type = GlobalTradingPropertyTypes.TRADE_TYPE;
    private static final int product_open_procedure_type = GlobalTradingPropertyTypes.PRODUCT_OPEN_PROCEDURE_TYPE;
    private static final int satisfaction_alert_flag = GlobalTradingPropertyTypes.SATISFACTION_ALERT_FLAG;
    private static final int firm_principal_quote_size = GlobalTradingPropertyTypes.FIRM_PRINCIPAL_QUOTE_SIZE;
    private static final int firm_customer_quote_size = GlobalTradingPropertyTypes.FIRM_CUSTOMER_QUOTE_SIZE;
    private static final int linkage_enabled_flag = GlobalTradingPropertyTypes.LINKAGE_ENABLED_FLAG;
    private static final int quote_lock_min_trade_qty = GlobalTradingPropertyTypes.QUOTE_LOCK_MIN_TRADE_QTY;
    private static final int auto_ex_eligible_strategy_types = GlobalTradingPropertyTypes.AUTO_EX_ELIGIBLE_STRATEGY_TYPES;
    private static final int auction_enabled = GlobalTradingPropertyTypes.AUCTION_ENABLED;
    private static final int auction_min_order_size_for_ticks_above_nbbo =
            GlobalTradingPropertyTypes.AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO;
    private static final int auction_min_price_increment = GlobalTradingPropertyTypes.AUCTION_MIN_PRICE_INCREMENT;
    private static final int auction_min_quoters = GlobalTradingPropertyTypes.AUCTION_MIN_QUOTERS;
    private static final int auction_order_ticks_away_from_nbbo =
            GlobalTradingPropertyTypes.AUCTION_ORDER_TICKS_AWAY_FROM_NBBO;
    private static final int auction_receiver_types = GlobalTradingPropertyTypes.AUCTION_RECEIVER_TYPES;
    private static final int auction_time_to_live = GlobalTradingPropertyTypes.AUCTION_TIME_TO_LIVE;
    private static final int internalization_guaranteed_percentage =
            GlobalTradingPropertyTypes.INTERNALIZATION_GUARANTEED_PERCENTAGE;
    
    
    //end of definitions to mimick IDL constants, start of new constants

    private static final int auction_min_max_order_size                  = 1000;
    private static final int opening_price_validation                    = 1001;
    private static final int opening_exchange_prescribed_width           = 1002;   
    private static final int allow_market_order                          = 1003;
    private static final int price_protect_percentage                    = 1004;
    private static final int size_increase_percentage_for_new_rfp        = 1005;
    private static final int pdpm_rights_scales                          = 1006;
    private static final int botr_enabled                                = 1007;
    private static final int eop_start_time                              = 1008;
    private static final int leaps_oepw                                  = 1009;
    private static final int botr_range_scale                            = 1010;
    private static final int eop_interval                                = 1011;
    private static final int market_turner_percentage                    = 1012;
    private static final int allowed_hal_types                           = 1013;
    private static final int hal_trigger_timer                           = 1014;
    private static final int reporting_price_adjustment                  = 1015;
    private static final int its_commitment_auto_cancel_threshold        = 1016;
    private static final int moc_interval_seconds                        = 1017;
    private static final int manualquote_cancel_timer_for_par            = 1018;
    private static final int manualquote_cancel_timer_for_nonpar         = 1019;
    private static final int autoex_size_against_noncustomer             = 1020;
    private static final int extremely_wide_quote_width                  = 1021;
    private static final int index_hybrid_indicator                      = 1022;


    private static final int cross_min_order_size                        = 1023;
    private static final int cross_min_dollar_amount                     = 1024;
    private static final int auto_link_enabled                           = 1027;
    private static final int uim_timeout                                 = 1030; // (jumping to 1030 due to multiple conflicts on other branches)
    private static final int uim_active                                  = 1031;

    private static final int hold_back_timer                             = 1032;
    private static final int nbbo_cross_min_order_size                   = 1033;
    private static final int nbbo_cross_min_dollar_amount                = 1034;
    private static final int bbo_allocation_percentage                   = 1035;
    private static final int allocate_all_to_rights_participant          = 1036;
    private static final int directed_aim_timer                          = 1037;    
    
    private static final int publish_large_trade_ticker_size             = 1044;//  (juming to 1044, incase other requires the numbers inbetween)
    private static final int aon_solicitation_minumum_quantity           = 1045;
    private static final int allowed_halo_types                           = 1054;//  (juming to 1054, incase other requires the numbers inbetween)
    private static final int halo_trigger_timer                           = 1055;
    
    private static final int market_lot_size                             = 1061;
    
    private static final int opening_max_quote_inversion                 = 1070;
    private static final int opening_max_retries_waiting_for_quote       = 1072;
    
    private static final int ticks_away_for_reCOA                        = 1073;
    private static final int reCOA_interval                              = 1074;
    private static final int number_of_attempts_reCOA                    = 1075;
    private static final int sleep_timer_for_reCOA                       = 1076;    
    private static final int strategy_epw                                = 1077;
    
    private static final int allowed_hal_origin_codes                    = 1078;
    
    private static final int auto_link_preferred_tie_exchanges           = 1079;
    private static final int auto_link_origin_codes                      = 1080;
    private static final int auto_link_disqualified_exchanges            = 1081;
    private static final int mkt_order_drill_through_pennies             = 1082;
        

    private static final int ticks_away_for_cps_reCOA                    = 1090;
    private static final int cps_reCOA_interval                          = 1091;
    private static final int number_of_attempts_cps_reCOA                = 1092;
    private static final int sleep_timer_for_cps_reCOA                   = 1093;    
    private static final int enable_cps_booking                          = 1094;
    private static final int qcc_min_value				 = 1095;

    private static final int regular_market_hours		         = 1096;
    private static final int enable_regular_market_time_validation       = 1097;
    private static final int asynchoronous_trading_enabled               = 1098;
    private static final int max_prod_per_bd_class_refresh_cmd           = 1099;
    
    private static final int enable_cps_mkt_splitting                    = 1100;
    private static final int variance_strip_indicator                    = 1101;
    private static final int cps_splitting_minimum_stock_nbbo_quantity   = 1102;
    private static final int cps_splitting_minimum_option_nbbo_bid       = 1103;
    private static final int cps_splitting_max_option_order_size         = 1104;
    private static final int cps_splitting_max_stock_order_size          = 1109;
    
    private static final int rollout_flag_by_bc			 	 = 1105;
        
    private static final int allowed_sal_origin_codes                    = 1106;
    
    private static final int allowed_wtp_origin_codes                    = 1115;
    
    private static final int allow_complex_trades_with_quotes            = 1108;
    private static final int enable_early_close = 1110;
    
    public static final TradingPropertyType EPW_STRUCT =
            new TradingPropertyTypeImpl("epwstruct", "Exchange Prescribed Width", epw_struct, true);
    public static final TradingPropertyType MIN_QUOTE_CREDIT_DEFAULT_SIZE =
            new TradingPropertyTypeImpl("minquotecreditdefaultsize", "Minimum Quote Credit Default Size",
                                        min_quote_credit_default_size, false);
    public static final TradingPropertyType RFQ_RESPONSE_RATIO =
            new TradingPropertyTypeImpl("rfqresponseratio", "RFQ Response Ratio", rfq_response_ratio, false);
    public static final TradingPropertyType ALLOCATION_STRATEGY =
            new TradingPropertyTypeImpl("allocationstrategy", "Allocation Strategy", allocation_strategy, true);
    public static final TradingPropertyType CONTINGENCY_TIME_TO_LIVE =
            new TradingPropertyTypeImpl("contingencytimetolive", "Contingency Time to Live", contingency_time_to_live,
                                        true);
    public static final TradingPropertyType CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT =
            new TradingPropertyTypeImpl("continuousquoteperiodforcredit", "Continuous Quote Period for Credit",
                                        continuous_quote_period_for_credit, false);
    public static final TradingPropertyType FAST_MARKET_SPREAD_MULTIPLIER =
            new TradingPropertyTypeImpl("fastmarketspreadmultiplier", "Fast Market Spread Multiplier",
                                        fast_market_spread_multiplier, true);
    public static final TradingPropertyType OPENING_TIME_PERIOD_RANGE =
            new TradingPropertyTypeImpl("openingtimeperiodrange", "Opening Time Period Range",
                                        opening_time_period_range, false);
    public static final TradingPropertyType OPENING_PRICE_DELAY =
            new TradingPropertyTypeImpl("openingpricedelay", "Opening Price Delay", opening_price_delay, false);
    public static final TradingPropertyType OPENING_PRICE_RATE =
            new TradingPropertyTypeImpl("openingpricerate", "Opening Price Rate", opening_price_rate, false);
    public static final TradingPropertyType PRECLOSING_TIME_PERIOD =
            new TradingPropertyTypeImpl("preclosingtimeperiod", "Pre-Closing Time Period", preclosing_time_period,
                                        false);
    public static final TradingPropertyType PRESCRIBED_WIDTH_RATIO =
            new TradingPropertyTypeImpl("prescribedwidthratio", "Prescribed Width Ratio", prescribed_width_ratio,
                                        false);
    public static final TradingPropertyType RFQ_TIMEOUT =
            new TradingPropertyTypeImpl("rfqtimeout", "RFQ Timeout", rfq_timeout, true);
    public static final TradingPropertyType DPM_PARTICIPATION_PERCENTAGE =
            new TradingPropertyTypeImpl("dpmparticipationpercentage", "DPM Participation Percentage",
                                        dpm_participation_percentage, true);
    public static final TradingPropertyType BOOK_DEPTH_SIZE =
            new TradingPropertyTypeImpl("bookdepthsize", "Book Depth Size", book_depth_size, true);
    public static final TradingPropertyType MIN_SIZE_FOR_BLOCK_TRADE =
            new TradingPropertyTypeImpl("minsizeforblocktrade", "Minimum Size for Block Trade",
                                        min_size_for_block_trade, true);
    public static final TradingPropertyType IPP_MIN_SIZE =
            new TradingPropertyTypeImpl("ippminsize", "IPP Minimum Size", ipp_min_size, true);
    public static final TradingPropertyType IPP_TRADE_THROUGH_FLAG =
            new TradingPropertyTypeImpl("ipptradethroughflag", "IPP Trade Through Flag", ipp_trade_through_flag, true);
    public static final TradingPropertyType QUOTE_LOCK_TIMER =
            new TradingPropertyTypeImpl("quotelocktimer", "Quote Lock Timer", quote_lock_timer, true);
    public static final TradingPropertyType QUOTE_LOCK_NOTIFICATION_TIMER =
            new TradingPropertyTypeImpl("quotelocknotificationtimer", "Quote Lock Notification Timer",
                                        quote_lock_notification_timer, true);
    public static final TradingPropertyType QUOTE_TRIGGER_TIMER =
            new TradingPropertyTypeImpl("quotetriggertimer", "Quote Trigger Timer", quote_trigger_timer, true);
    public static final TradingPropertyType DPM_RIGHTS_SCALES =
            new TradingPropertyTypeImpl("dpmrightsscales", "DPM Rights Scales", dpm_rights_scales, true);
    public static final TradingPropertyType DPM_RIGHTS_SPLIT_RATE =
            new TradingPropertyTypeImpl("dmprightssplitrate", "DPM Rights Split Rate", dpm_rights_split_rate, true);
    public static final TradingPropertyType UMA_SPLIT_RATE =
            new TradingPropertyTypeImpl("umasplitrate", "UMA Split Rate", uma_split_rate, true);
    public static final TradingPropertyType UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM =
            new TradingPropertyTypeImpl("umaequaldistributionweightfordpm", "UMA Equal Distribution Weight for DPM",
                                        uma_equal_distribution_weight_for_dpm, true);
    public static final TradingPropertyType LOT_SIZE = new TradingPropertyTypeImpl("lotsize", "Lot Size", lot_size,
                                                                                   true);
    public static final TradingPropertyType ETF_FLAG = new TradingPropertyTypeImpl("etfflag", "ETF Flag", etf_flag,
                                                                                   true);
    public static final TradingPropertyType IPP_TOLERANCE_AMOUNT =
            new TradingPropertyTypeImpl("ipptoleranceamount", "IPP Tolerance Amount", ipp_tolerance_amount, true);
    public static final TradingPropertyType NEEDS_DPM_QUOTE_TO_OPEN =
            new TradingPropertyTypeImpl("needsdpmquotetoopen", "Needs DPM Quote to Open", needs_dpm_quote_to_open,
                                        true);
    public static final TradingPropertyType S_ORDER_TIME_TO_LIVE =
            new TradingPropertyTypeImpl("sordertimetolive", "Satisfaction Order Time to Live", s_order_time_to_live,
                                        true);
    public static final TradingPropertyType S_ORDER_TIME_TO_CREATE =
            new TradingPropertyTypeImpl("sordertimetocreate", "Satisfaction Order Time to Create",
                                        s_order_time_to_create, true);
    public static final TradingPropertyType S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE =
            new TradingPropertyTypeImpl("sordertimetocreatebeforeclose",
                                        "Satisfaction Order Time to Create Before Close",
                                        s_order_time_to_create_before_close, true);
    public static final TradingPropertyType S_ORDER_TIME_TO_REJECT_FILL =
            new TradingPropertyTypeImpl("sordertimetorejectfill", "Satisfaction Order Time to Reject Fill",
                                        s_order_time_to_reject_fill, true);
    public static final TradingPropertyType P_ORDER_TIME_TO_LIVE =
            new TradingPropertyTypeImpl("pordertimetolive", "P Order Time to Live", p_order_time_to_live, true);
    public static final TradingPropertyType PA_ORDER_TIME_TO_LIVE =
            new TradingPropertyTypeImpl("paordertimetolive", "PA Order Time to Live", pa_order_time_to_live, true);
    public static final TradingPropertyType TRADE_TYPE = new TradingPropertyTypeImpl("tradetype", "Trade Type",
                                                                                     trade_type, true);
    public static final TradingPropertyType PRODUCT_OPEN_PROCEDURE_TYPE =
            new TradingPropertyTypeImpl("productopenproceduretype", "Product Open Procedure Type",
                                        product_open_procedure_type, true);
    public static final TradingPropertyType SATISFACTION_ALERT_FLAG =
            new TradingPropertyTypeImpl("satisfactionalertflag", "Satisfaction Alert Flag", satisfaction_alert_flag,
                                        true);
    public static final TradingPropertyType FIRM_PRINCIPAL_QUOTE_SIZE =
            new TradingPropertyTypeImpl("firmprincipalquotesize", "Firm Principal Quote Size",
                                        firm_principal_quote_size, true);
    public static final TradingPropertyType FIRM_CUSTOMER_QUOTE_SIZE =
            new TradingPropertyTypeImpl("firmcustomerquotesize", "Firm Customer Quote Size", firm_customer_quote_size,
                                        true);
    public static final TradingPropertyType LINKAGE_ENABLED_FLAG =
            new TradingPropertyTypeImpl("linkageenabledflag", "Linkage Enabled Flag", linkage_enabled_flag, true);
    public static final TradingPropertyType QUOTE_LOCK_MIN_TRADE_QTY =
            new TradingPropertyTypeImpl("quotelockmintradeqty", "Quote Lock Minimum Trade Quantity",
                                        quote_lock_min_trade_qty, true);
    public static final TradingPropertyType AUTO_EX_ELIGIBLE_STRATEGY_TYPES =
            new TradingPropertyTypeImpl("autoexeligiblestrategytypes", "Auto Execute Eligible Strategy Types",
                                        auto_ex_eligible_strategy_types, true);
    public static final TradingPropertyType AUCTION_ENABLED =
            new TradingPropertyTypeImpl("auctionenabled", "Auction Enabled", auction_enabled, true);
    public static final TradingPropertyType AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO =
            new TradingPropertyTypeImpl("auctionminordersizeforticksabovenbbo",
                                        "Auction Minimum Order Size For Ticks Above NBBO",
                                        auction_min_order_size_for_ticks_above_nbbo, true);
    public static final TradingPropertyType AUCTION_MIN_PRICE_INCREMENT =
            new TradingPropertyTypeImpl("auctionminpriceincrement", "Auction Minimum Price Increment",
                                        auction_min_price_increment, true);
    public static final TradingPropertyType AUCTION_MIN_QUOTERS =
            new TradingPropertyTypeImpl("auctionminquoters", "Auction Minimum Quoters",
                                        auction_min_quoters, true);
    public static final TradingPropertyType AUCTION_ORDER_TICKS_AWAY_FROM_NBBO =
            new TradingPropertyTypeImpl("auctionorderticksawayfromnbbo", "Auction Order Ticks Away From NBBO",
                                        auction_order_ticks_away_from_nbbo, true);
    public static final TradingPropertyType AUCTION_RECEIVER_TYPES =
            new TradingPropertyTypeImpl("auctionreceivertypes", "Auction Receiver Types",
                                        auction_receiver_types, true);
    public static final TradingPropertyType AUCTION_TIME_TO_LIVE =
            new TradingPropertyTypeImpl("auctiontimetolive", "Auction Time To Live",
                                        auction_time_to_live, true);
    public static final TradingPropertyType INTERNALIZATION_GUARANTEED_PERCENTAGE =
            new TradingPropertyTypeImpl("internalizationguaranteedpercentage", "Internalization Guaranteed Percentage",
                                        internalization_guaranteed_percentage, true);
    public static final TradingPropertyType AUCTION_MIN_MAX_ORDER_SIZE =
            new TradingPropertyTypeImpl("AuctionMinMaxOrderSize", "Auction Minimum and Maximum Order Size",
                                        auction_min_max_order_size, true);
    public static final TradingPropertyType OPENING_PRICE_VALIDATION =
            new TradingPropertyTypeImpl("OpeningPriceValidation", "Opening Price Validation Method",
                                        opening_price_validation,true);
    public static final TradingPropertyType OPENING_EXCHANGE_PRESCRIBED_WIDTH =
        new TradingPropertyTypeImpl("OpeningExchangePrescribedWidth", "Opening EPW",
                                    opening_exchange_prescribed_width,true);
    public static final TradingPropertyType PDPM_RIGHTS_SCALES =
            new TradingPropertyTypeImpl("PDPMRightsScales", "PDPM Rights Scales",
                                        pdpm_rights_scales, true);
    public static final TradingPropertyType ALLOW_MARKET_ORDER =
        new TradingPropertyTypeImpl("AllowMarketOrder", "Allow Market Order",
                                    allow_market_order, true);
    public static final TradingPropertyType PRICE_PROTECT_PERCENTAGE =
        new TradingPropertyTypeImpl("PriceProtectPercentage", "Price Protect Percentage",
                                    price_protect_percentage, true);
    public static final TradingPropertyType SIZE_INCREASE_PERCENTAGE_FOR_NEW_RFP =
        new TradingPropertyTypeImpl("SizeIncreasePercentageForNewRfp", "Size Increase Percentage For New RFP",
                                    size_increase_percentage_for_new_rfp, true);
    public static final TradingPropertyType BOTR_ENABLED =
            new TradingPropertyTypeImpl("BOTREnabled", "Enable BOTR Calculation", botr_enabled, true);
    public static final TradingPropertyType EOP_START_TIME =
            new TradingPropertyTypeImpl("EOPStartTime", "EOP PreOpen Calculation Start Time", eop_start_time, true);
    public static final TradingPropertyType LEAPS_OEPW =
            new TradingPropertyTypeImpl("LeapsOepw", "Opening EPW for LEAPS", leaps_oepw, true);
    public static final TradingPropertyType BOTR_RANGE_SCALE =
            new TradingPropertyTypeImpl("BOTRRangeScales", "Percent Within BOTR-By Range", botr_range_scale, true);
    public static final TradingPropertyType EOP_INTERVAL =
            new TradingPropertyTypeImpl("EOPInterval", "EOP PreOpen Calculation Interval", eop_interval, true);
    public static final TradingPropertyType MARKET_TURNER_PERCENTAGE =
            new TradingPropertyTypeImpl("MarketTurnerPercentage","Market Turner Percentage",market_turner_percentage,true);
    public static final TradingPropertyType ALLOWED_HAL_TYPES =
            new TradingPropertyTypeImpl("AllowedHALTypes", "Allowed HALTypes",
                                        allowed_hal_types, true);
    public static final TradingPropertyType HAL_TRIGGER_TIMER =
        new TradingPropertyTypeImpl("HALTriggerTimer", "HAL Trigger Timer",
                                    hal_trigger_timer, true);
    public static final TradingPropertyType HALO_TRIGGER_TIMER =
        new TradingPropertyTypeImpl("HALOTriggerTimer", "HALO Trigger Timer",
                                    halo_trigger_timer, true);
    public static final TradingPropertyType MAX_PROD_PER_BD_CLASS_REFRESH_CMD =
    	 new TradingPropertyTypeImpl("maxProductsPerBookDepthClassRefreshCommand", "Max Products Per BookDepth Class Refresh Command",
    			 max_prod_per_bd_class_refresh_cmd, true);
    
    public static final TradingPropertyType REPORTING_PRICE_ADJUSTMENT =
        new TradingPropertyTypeImpl("ReportingPriceAdjustment", "Reporting Price Adjustment",
                                    reporting_price_adjustment, true);
    public static final TradingPropertyType ITS_COMMITMENT_AUTO_CANCEL_THRESHOLD =
        new TradingPropertyTypeImpl("ITSCommitmentAutoCancelThreshold", "ITS Commitment Auto Cancel Threshold",
                                    its_commitment_auto_cancel_threshold, true);

    public static final TradingPropertyType MOC_INTERVAL_SECONDS =
        new TradingPropertyTypeImpl("MOCIntervalSeconds", "MOC Order Activation Interval Before Close in seconds",
                                    moc_interval_seconds, true);

    public static final TradingPropertyType MANUALQUOTE_CANCEL_TIMER_FOR_PAR =
        new TradingPropertyTypeImpl("ManualQuoteCancelTimerForPar", "Manual Quote Cancel Timer For PAR",
                                    manualquote_cancel_timer_for_par, true);

    public static final TradingPropertyType MANUALQUOTE_CANCEL_TIMER_FOR_NONPAR =
            new TradingPropertyTypeImpl("ManualQuoteCancelTimerForNonPar", "Manual Quote Cancel Timer For Non PAR",
                                        manualquote_cancel_timer_for_nonpar, true);

    public static final TradingPropertyType AUTOEX_SIZE_AGAINST_NONCUSTOMER =
            new TradingPropertyTypeImpl("AutoExSizeAgainstNonCustomer", "Auto Ex Size Against Non Customer",
                                        autoex_size_against_noncustomer, true);

    public static final TradingPropertyType EXTREMELY_WIDE_QUOTE_WIDTH =
            new TradingPropertyTypeImpl("ExtremelyWideQuoteWidth", "Extremely Wide Quote Width",
                                         extremely_wide_quote_width, true);

    public static final TradingPropertyType INDEX_HYBRID_INDICATOR =
            new TradingPropertyTypeImpl("IndexHybridIndicator", "Index Hybrid Indicator",
                                        index_hybrid_indicator, true);

    public static final TradingPropertyType UIM_ACTIVE =
        new TradingPropertyTypeImpl("UIMActive", "UIM Active", uim_active, false);

    public static final TradingPropertyType UIM_TIMEOUT =
        new TradingPropertyTypeImpl("UIMTimeoutMS", "UIM Timeout", uim_timeout, false);

    public static final TradingPropertyType CROSS_MIN_ORDER_SIZE =
        new TradingPropertyTypeImpl("CrossMinOrderSize",  "Cross Minumum Order Size",
                                    cross_min_order_size, true);
    public static final TradingPropertyType CROSS_MIN_DOLLAR_AMOUNT =
        new TradingPropertyTypeImpl("CrossMinDollarAmount", "Cross Minumum Dollar Amount",
                                    cross_min_dollar_amount, true);
    public static final TradingPropertyType MARKET_LOT_SIZE =
        new TradingPropertyTypeImpl("MarketLotSize",  "Market Lot Size",
                                    market_lot_size, true);
    
    public static final TradingPropertyType AUTO_LINK_ENABLED =
        new TradingPropertyTypeImpl("AutoLinkEnabled", "Enable Auto Link", auto_link_enabled , true);

    public static final TradingPropertyType HOLD_BACK_TIMER =
            new TradingPropertyTypeImpl("HoldBackTimer", "Hold Back Timer",
                                        hold_back_timer, true);


    public static final TradingPropertyType NBBO_CROSS_MIN_ORDER_SIZE =
        new TradingPropertyTypeImpl("NBBOCrossMinOrderSize",  "NBBO Cross Minumum Order Size",
                                    nbbo_cross_min_order_size, true);
    public static final TradingPropertyType NBBO_CROSS_MIN_DOLLAR_AMOUNT =
        new TradingPropertyTypeImpl("NBBOCrossMinDollarAmount", "NBBO Cross Minumum Dollar Amount",
                                    nbbo_cross_min_dollar_amount, true);
    public static final TradingPropertyType BBO_ALLOCATION_PERCENTAGE =
        new TradingPropertyTypeImpl("BBOAllocationPercentage", "BBO Allocation Percentage for SAL", 
                                    bbo_allocation_percentage , true);

    public static final TradingPropertyType ALLOCATE_ALL_TO_RIGHTS_PARTICIPANT =
        new TradingPropertyTypeImpl("AllocateAllToRightsParticipant", "Allocate all to opposite side rights participant",
                                    allocate_all_to_rights_participant, true);
    

    public static final TradingPropertyType PUBLISH_LARGE_TRADE_TICKER_SIZE =
        new TradingPropertyTypeImpl("PublishLargeTradeTickerSize", "Publish Large Trade Ticker Size",
                                    publish_large_trade_ticker_size, true);

    public static final TradingPropertyType AON_SOLICITATION_MIN_QUANTITY =
        new TradingPropertyTypeImpl("AONSolicitationMinimumQuantity", "AON Solicitation Minimum Quantity",
                                    aon_solicitation_minumum_quantity, true);
    
    public static final TradingPropertyType OPENING_MAX_QUOTE_INVERSION =
        new TradingPropertyTypeImpl("OpeningMaxQuoteInversion", "Opening Max Quote Inversion",
                                    opening_max_quote_inversion,true);
    
    public static final TradingPropertyType OPENING_MAX_RETRIES_WAITING_FOR_QUOTE =
        new TradingPropertyTypeImpl("OpeningMaxRetriesWaitingForQuote", "Opening Max Retries Waiting For Quote",
                                    opening_max_retries_waiting_for_quote,true);

    public static final TradingPropertyType ALLOWED_HAL_ORIGIN_CODES =
        new TradingPropertyTypeImpl("AllowedHalOriginCodes", "Allowed HAL Origin Codes",
                                allowed_hal_origin_codes , true);
    
    public static final TradingPropertyType AUTO_LINK_PREFERRED_TIE_EXCHANGES =
        new TradingPropertyTypeImpl("AutoLinkPreferredTieExchanges", "Auto Link Preferred Tie Exchanges",
                auto_link_preferred_tie_exchanges , true);
    
    public static final TradingPropertyType AUTO_LINK_ORIGIN_CODES =
        new TradingPropertyTypeImpl("AutoLinkOriginCodes", "Auto Link Origin Codes",
                auto_link_origin_codes , true);
    
    public static final TradingPropertyType AUTO_LINK_DISQUALIFIED_EXCHANGES =
        new TradingPropertyTypeImpl("AutoLinkDisqualifiedExchanges", "Auto Link Disqualified Exchanges",
                auto_link_disqualified_exchanges , true);
    
    public static final TradingPropertyType TICKS_AWAY_FOR_RECOA =
        new TradingPropertyTypeImpl("TicksAwayForReCOA", "Ticks Away For ReCOA",
                                     ticks_away_for_reCOA, true);
    
    
    public static final TradingPropertyType RECOA_INTERVAL =
        new TradingPropertyTypeImpl("ReCOAInterval", "ReCOA Interval",
                                     reCOA_interval, true);
    
    
    public static final TradingPropertyType NUMBER_OF_ATTEMPTS_FOR_RECOA =
        new TradingPropertyTypeImpl("NumberOfAttemptsForReCOA", "Number Of Attempts For ReCOA",
                                     number_of_attempts_reCOA, true);    
    
    public static final TradingPropertyType SLEEP_TIMER_FOR_RECOA =
        new TradingPropertyTypeImpl("SleepTimerForReCOA", "Sleep Timer For ReCOA",
                                     sleep_timer_for_reCOA, true);    
    
    public static final TradingPropertyType STRATEGY_EPW =
        new TradingPropertyTypeImpl("StrategyEPW", "Strategy EPW",
                strategy_epw, true);
    
    public static final TradingPropertyType DIRECTED_AIM_TIMER =
        new TradingPropertyTypeImpl("DirectedAIMTimer", "Directed AIM Timer",
                                    directed_aim_timer, true);    

    public static final TradingPropertyType MKT_ORDER_DRILL_THROUGH_PENNIES =
        new TradingPropertyTypeImpl("MKTOrderDrillThroughPennies", "MKT Order Drill Through Amount", 
                mkt_order_drill_through_pennies, true);

    public static final TradingPropertyType TICKS_AWAY_FOR_CPS_RECOA =
        new TradingPropertyTypeImpl("TicksAwayForCPSReCOA", "Ticks Away For CPS ReCOA",
                ticks_away_for_cps_reCOA, true);
    
    
    public static final TradingPropertyType CPS_RECOA_INTERVAL =
        new TradingPropertyTypeImpl("CPSReCOAInterval", "CPS ReCOA Interval",
                cps_reCOA_interval, true);
    
    
    public static final TradingPropertyType NUMBER_OF_ATTEMPTS_FOR_CPS_RECOA =
        new TradingPropertyTypeImpl("NumberOfAttemptsForCPSReCOA", "Number Of Attempts For CPS ReCOA",
                number_of_attempts_cps_reCOA, true);    
    
    public static final TradingPropertyType SLEEP_TIMER_FOR_CPS_RECOA =
        new TradingPropertyTypeImpl("SleepTimerForCPSReCOA", "Sleep Timer For CPS ReCOA",
                sleep_timer_for_cps_reCOA, true);
    
    public static final TradingPropertyType ENABLE_CPS_BOOKING =
        new TradingPropertyTypeImpl("EnableCPSBooking", "Enable CPS Booking",
                enable_cps_booking, true);
    
    public static final TradingPropertyType QCC_MIN_VALUE = 
    	new TradingPropertyTypeImpl("QCCMinValue", "QCC Min Value", qcc_min_value,true);
    
    public static final TradingPropertyType ROLLOUT_FLAG_BY_BC = 
    	new TradingPropertyTypeImpl("RolloutFlagByBC", "Rollout Flag By BC", rollout_flag_by_bc,true);
    
    public static final TradingPropertyType REGULAR_MARKET_HOURS =
        new TradingPropertyTypeImpl("RegularMarketHours", "Regular Market Hours",
        		regular_market_hours, true);
    
    public static final TradingPropertyType ENABLE_REGULAR_MARKET_TIME_VALIDATION =
        new TradingPropertyTypeImpl("EnableRegularMarketTimeValidation", "Regular Market Hour Validation Enable",
        		enable_regular_market_time_validation, true);

    public static final TradingPropertyType CPS_SPLITTING_MINIMUM_STOCK_NBBO_QUANTITY =
        new TradingPropertyTypeImpl("CPSSplittingMinimumStockNBBOQuantity", "CPS Splitting Stock NBBO Min Quantity",
                                    cps_splitting_minimum_stock_nbbo_quantity, true);

    public static final TradingPropertyType CPS_SPLITTING_MINIMUM_OPTION_NBBO_BID =
        new TradingPropertyTypeImpl("CPSSplittingMinimumOptionNBBOBid", "CPS Splitting Option NBBO Min Bid",
                                    cps_splitting_minimum_option_nbbo_bid, true);

    public static final TradingPropertyType CPS_SPLITTING_MAX_STOCK_ORDER_SIZE =
        new TradingPropertyTypeImpl("CPSSplittingMaxStockOrderSize", "CPS Splitting Stock Order Max Size",
                                    cps_splitting_max_stock_order_size, true);

    public static final TradingPropertyType CPS_SPLITTING_MAX_OPTION_ORDER_SIZE =
        new TradingPropertyTypeImpl("CPSSplittingMaxOptionOrderSize", "CPS splitting Option Order Max Size",
                                    cps_splitting_max_option_order_size, true);

    public static final TradingPropertyType ENABLE_CPS_MKT_SPLITTING =
        new TradingPropertyTypeImpl("EnableCPSMktSplitting", "Enable CPS order splitting",
                                    enable_cps_mkt_splitting, true);
    
    public static final TradingPropertyType VARIANCE_STRIP_INDICATOR =
        new TradingPropertyTypeImpl("VarianceStripIndicator", "Variance Strip Indicator",
                                     variance_strip_indicator, true);
    
    public static final TradingPropertyType ALLOWED_SAL_ORIGIN_CODES =
        new TradingPropertyTypeImpl("AllowedSALOriginCodes", "Allowed SAL Origin Codes",
                                    allowed_sal_origin_codes, true);
    
    public static final TradingPropertyType ALLOW_COMPLEX_TRADES_WITH_QUOTES =
        new TradingPropertyTypeImpl("AllowComplexTradesWithQuotes", "Allow Complex Trades With Quotes", allow_complex_trades_with_quotes, true);
    public static final TradingPropertyType ASYNCHRONOUS_TRADING_ENABLED =
        new TradingPropertyTypeImpl("AsynchronousTradingEnabled", "Asynchronous Trading Enabled", asynchoronous_trading_enabled,
                                    true);
    
    public static final TradingPropertyType ALLOWED_WTP_ORIGIN_CODES =
        new TradingPropertyTypeImpl("AllowedWtpOriginCodes", "Allowed WTP Origin Codes",
                                    allowed_wtp_origin_codes, true);
    public static final TradingPropertyType ENABLE_EARLY_CLOSE =
        new TradingPropertyTypeImpl("EnableEarlyClose", "Enable early close",
                                    enable_early_close, true);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // When adding a new TradingPropertyType, please add it to newWayTradingPropertyTypes BELOW.
    //        
    // these are the new TradingPropertyTypes using PROPERTY SERVICE.
    public static final TradingPropertyType[] newWayTradingPropertyTypes = {

        AUCTION_MIN_MAX_ORDER_SIZE,
        OPENING_PRICE_VALIDATION,
        OPENING_EXCHANGE_PRESCRIBED_WIDTH,
        PDPM_RIGHTS_SCALES,
        ALLOW_MARKET_ORDER,
        PRICE_PROTECT_PERCENTAGE,
        SIZE_INCREASE_PERCENTAGE_FOR_NEW_RFP,
        BOTR_ENABLED,
        EOP_START_TIME,
        LEAPS_OEPW,
        BOTR_RANGE_SCALE,
        EOP_INTERVAL,
        MARKET_TURNER_PERCENTAGE,
        ALLOWED_HAL_TYPES,
        HAL_TRIGGER_TIMER,
        REPORTING_PRICE_ADJUSTMENT,
        ITS_COMMITMENT_AUTO_CANCEL_THRESHOLD,
        MOC_INTERVAL_SECONDS,
        CROSS_MIN_ORDER_SIZE,
        CROSS_MIN_DOLLAR_AMOUNT,
        MARKET_LOT_SIZE,
        AUTO_LINK_ENABLED,
        MANUALQUOTE_CANCEL_TIMER_FOR_PAR,
        MANUALQUOTE_CANCEL_TIMER_FOR_NONPAR,
        AUTOEX_SIZE_AGAINST_NONCUSTOMER,
        EXTREMELY_WIDE_QUOTE_WIDTH,
        INDEX_HYBRID_INDICATOR,
        UIM_ACTIVE,
        UIM_TIMEOUT,
        HOLD_BACK_TIMER,
        NBBO_CROSS_MIN_ORDER_SIZE,
        NBBO_CROSS_MIN_DOLLAR_AMOUNT,
        BBO_ALLOCATION_PERCENTAGE,
        ALLOCATE_ALL_TO_RIGHTS_PARTICIPANT,
        PUBLISH_LARGE_TRADE_TICKER_SIZE,
        AON_SOLICITATION_MIN_QUANTITY,
        HALO_TRIGGER_TIMER,
        OPENING_MAX_QUOTE_INVERSION,
        OPENING_MAX_RETRIES_WAITING_FOR_QUOTE,
        TICKS_AWAY_FOR_RECOA,
        RECOA_INTERVAL,
        NUMBER_OF_ATTEMPTS_FOR_RECOA,
        SLEEP_TIMER_FOR_RECOA,
        STRATEGY_EPW,
        DIRECTED_AIM_TIMER,
        MAX_PROD_PER_BD_CLASS_REFRESH_CMD,
        ALLOWED_HAL_ORIGIN_CODES,
        AUTO_LINK_PREFERRED_TIE_EXCHANGES,
        AUTO_LINK_ORIGIN_CODES,
        AUTO_LINK_DISQUALIFIED_EXCHANGES,
        MKT_ORDER_DRILL_THROUGH_PENNIES,
        TICKS_AWAY_FOR_CPS_RECOA,
        CPS_RECOA_INTERVAL,
        NUMBER_OF_ATTEMPTS_FOR_CPS_RECOA,
        SLEEP_TIMER_FOR_CPS_RECOA,        
        QCC_MIN_VALUE,
        ENABLE_CPS_BOOKING,
        QCC_MIN_VALUE,
        ROLLOUT_FLAG_BY_BC,
        REGULAR_MARKET_HOURS,
        ENABLE_REGULAR_MARKET_TIME_VALIDATION,
        ASYNCHRONOUS_TRADING_ENABLED,
        CPS_SPLITTING_MINIMUM_STOCK_NBBO_QUANTITY,
        CPS_SPLITTING_MINIMUM_OPTION_NBBO_BID,
        CPS_SPLITTING_MAX_STOCK_ORDER_SIZE,
        CPS_SPLITTING_MAX_OPTION_ORDER_SIZE,
        ENABLE_CPS_MKT_SPLITTING,       
        VARIANCE_STRIP_INDICATOR,
        ALLOWED_SAL_ORIGIN_CODES,
        ALLOWED_WTP_ORIGIN_CODES,
        ALLOW_COMPLEX_TRADES_WITH_QUOTES,
        ENABLE_EARLY_CLOSE
        //***** END OF EDITABLE SECTION *******************
    };

    
    // these are OLD trading properties using TradingProperty Service, DO NOT EDIT
    public static final TradingPropertyType[] oldTradingPropertyTypes = {
        EPW_STRUCT,
        MIN_QUOTE_CREDIT_DEFAULT_SIZE,
        RFQ_RESPONSE_RATIO,
        ALLOCATION_STRATEGY,
        CONTINUOUS_QUOTE_PERIOD_FOR_CREDIT,
        FAST_MARKET_SPREAD_MULTIPLIER,
        OPENING_TIME_PERIOD_RANGE,
        OPENING_PRICE_DELAY,
        OPENING_PRICE_RATE,
        PRECLOSING_TIME_PERIOD,
        PRESCRIBED_WIDTH_RATIO,
        RFQ_TIMEOUT,
        DPM_PARTICIPATION_PERCENTAGE,
        BOOK_DEPTH_SIZE,
        MIN_SIZE_FOR_BLOCK_TRADE,
        IPP_MIN_SIZE,
        IPP_TRADE_THROUGH_FLAG,
        QUOTE_LOCK_TIMER,
        QUOTE_LOCK_NOTIFICATION_TIMER,
        QUOTE_TRIGGER_TIMER,
        DPM_RIGHTS_SCALES,
        DPM_RIGHTS_SPLIT_RATE,
        UMA_SPLIT_RATE,
        UMA_EQUAL_DISTRIBUTION_WEIGHT_FOR_DPM,
        LOT_SIZE,
        ETF_FLAG,
        IPP_TOLERANCE_AMOUNT,
        NEEDS_DPM_QUOTE_TO_OPEN,
        S_ORDER_TIME_TO_LIVE,
        S_ORDER_TIME_TO_CREATE,
        S_ORDER_TIME_TO_CREATE_BEFORE_CLOSE,
        S_ORDER_TIME_TO_REJECT_FILL,
        P_ORDER_TIME_TO_LIVE,
        PA_ORDER_TIME_TO_LIVE,
        TRADE_TYPE,
        PRODUCT_OPEN_PROCEDURE_TYPE,
        SATISFACTION_ALERT_FLAG,
        FIRM_PRINCIPAL_QUOTE_SIZE,
        FIRM_CUSTOMER_QUOTE_SIZE,
        LINKAGE_ENABLED_FLAG,
        QUOTE_LOCK_MIN_TRADE_QTY,
        AUTO_EX_ELIGIBLE_STRATEGY_TYPES,
        AUCTION_ENABLED,
        AUCTION_MIN_ORDER_SIZE_FOR_TICKS_ABOVE_NBBO,
        AUCTION_MIN_PRICE_INCREMENT,
        AUCTION_MIN_QUOTERS,
        AUCTION_ORDER_TICKS_AWAY_FROM_NBBO,
        AUCTION_RECEIVER_TYPES,
        AUCTION_TIME_TO_LIVE,
        INTERNALIZATION_GUARANTEED_PERCENTAGE     
        
     };

    
    public static final TradingPropertyType[] globalTradingPropertyTypes = new TradingPropertyType[oldTradingPropertyTypes.length + newWayTradingPropertyTypes.length];    
    static
    {
        System.arraycopy(oldTradingPropertyTypes, 0, globalTradingPropertyTypes, 0, oldTradingPropertyTypes.length);
        System.arraycopy(newWayTradingPropertyTypes, 0, globalTradingPropertyTypes, oldTradingPropertyTypes.length, newWayTradingPropertyTypes.length);
    }
    
    private static final String ALL_CLASSES_POSTFIX = "AllClasses";

    private int       type;
    private String    name;
    private String    allClassesName;
    private String    propertyCategory;
    private String    fullName;
    private boolean   isProductClassSpecific;

    /**
     * Marked as private to prevent empty instantiation.
     */
    private TradingPropertyTypeImpl()
    {}

    /**
     * Constructor that uses PropertyCategoryTypes.TRADING_PROPERTIES as the set propertyCategory.
     * @param name programmatic name
     * @param fullName full english name for display
     * @param type programmatic numeric identifier
     * @param isProductClassSpecific determines whether this type is product clas specific or only available for a
     * trading session
     */
    protected TradingPropertyTypeImpl(String name, String fullName, int type, boolean isProductClassSpecific)
    {
        this(PropertyCategoryTypes.TRADING_PROPERTIES, name, fullName, type, isProductClassSpecific);
    }

    /**
     * Constructor
     * @param propertyCategory to use
     * @param name programmatic name
     * @param fullName full english name for display
     * @param type programmatic numeric identifier
     * @param isProductClassSpecific determines whether this type is product clas specific or only available for a
     * trading session
     */
    protected TradingPropertyTypeImpl(String propertyCategory, String name, String fullName,
                                      int type, boolean isProductClassSpecific)
    {
        this();
        setName(name);
        setAllClassesName(name);
        setType(type);
        setFullName(fullName);
        setProductClassSpecific(isProductClassSpecific);
        setPropertyCategory(propertyCategory);
    }

    /**
     * Compares equality based on getName().
     */
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof TradingPropertyType))
        {
            return false;
        }

        final TradingPropertyType tradingPropertyType = (TradingPropertyType) o;

        if(!getName().equals(tradingPropertyType.getName()))
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the hashCode of getName();
     * @return
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /**
     * Returns the defined integer constant
     */
    public int getType()
    {
        return type;
    }

    /**
     * Gets the property category that will contain this type.
     */
    public String getPropertyCategory()
    {
        return propertyCategory;
    }

    /**
     * Returns the programmatic name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the English formatted name
     */
    public String getFullName()
    {
        return fullName;
    }

    public String getAllClassesName()
    {
        return allClassesName;
    }

    /**
     * Returns whether the type is product class specific or not.
     * @return True if applicable to specific product classes, false if only applicable to trading sessions.
     */
    public boolean isProductClassSpecific()
    {
        return isProductClassSpecific;
    }

    private void setName( String name )
    {
        if(name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("name may not be null or empty.");
        }
        this.name   = name;
    }

    private void setPropertyCategory(String propertyCategory)
    {
        if(propertyCategory == null || propertyCategory.length() == 0)
        {
            throw new IllegalArgumentException("propertyCategory may not be null or empty.");
        }
        this.propertyCategory = propertyCategory;
    }

    private void setType( int type )
    {
        this.type = type;
    }

    private void setFullName(String fullName)
    {
        if(fullName == null || fullName.length() == 0)
        {
            throw new IllegalArgumentException("fullName may not be null or empty.");
        }
        this.fullName = fullName;
    }

    private void setProductClassSpecific(boolean isProductClassSpecific)
    {
        this.isProductClassSpecific = isProductClassSpecific;
    }

    private void setAllClassesName(String name)
    {
        this.allClassesName = name + ALL_CLASSES_POSTFIX;
    }
}
