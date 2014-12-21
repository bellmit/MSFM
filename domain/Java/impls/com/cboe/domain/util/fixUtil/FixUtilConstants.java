package com.cboe.domain.util.fixUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Sundares
 * Date: Jul 28, 2004
 * Time: 9:48:05 AM
 * To change this template use Options | File Templates.
 */
public class FixUtilConstants {
    /**
     * Constants Specifically for comunication with SBT
     */
    public final static class SbtValues {
        public final static class DataKeys {
            public final static int DEFAULT_KEY=1;
        }
        public final static class EngineMode {
            public final static String ENGINE_MODE="engineMode";
            public final static String FUTURES="FUTURE";
            public final static String HYBRID="HYBRID";
            public final static String TT="TT";
            public final static String STOCK = "STOCK";
        }

        // PAR (Stock) Exchange Strings
        public final static class ParExchange {
            public final static String CBOE = "CBO";
            public final static String CINCINNATI = "CSE";
            public final static String AMERICAN = "ASE";
            public final static String BOSTON = "BOX";
            public final static String CHICAGO = "CHX";
            public final static String NASDAQ = "ACT";
            public final static String NYSE = "NYS";
            public final static String PACIFIC = "PCX";
            public final static String PHILIDELPHIA = "PHX";
        }
        public final static class SecurityExch {
            public final static String C1 = "C1";
            public final static String C2 = "C2";
            public final static String CBOE = "CBOE";
            public final static String CBOE2 = "CBOE2";
        }
    }




    /**
     * FIX DataTypes and constant values
     */
    public final static class DataTypes {
      public final static class Boolean {
        public final static String TRUE = "Y";
        public final static String FALSE = "N";
      }
    }

    /**
     * Account (1) Values
     */
    public final static class Account {
       public final static String TAGNAME="Account";
       public final static int TAGNUMBER=1;
    }

    /**
     * AvgPx (6) Values
     */
    public final static class AvgPx {
       public final static String TAGNAME="AvgPx";
       public final static int TAGNUMBER=6;
    }

    /**
     * BeginString (8) Values
     */
    public final static class BeginString {
       public final static String TAGNAME="BeginString";
       public final static int TAGNUMBER=8;
    }

    /**
     * BodyLength (9) Values
     */
    public final static class BodyLength {
       public final static String TAGNAME="BodyLength";
       public final static int TAGNUMBER=9;
    }

    /**
     * CheckSum (10) Values
     */
    public final static class CheckSum {
       public final static String TAGNAME="CheckSum";
       public final static int TAGNUMBER=10;
    }

    /**
     * ClOrdID (11) Values
     */
    public final static class ClOrdID {
       public final static String TAGNAME="ClOrdID";
       public final static int TAGNUMBER=11;
       public final static String UNKNOWN_ID = "NONE";
    }

    /**
     * Commission (12)
     */
    public final static class Commission
    {
        public final static String TAGNAME="Commission";
        public final static int TAGNUMBER=12;
    }

    /**
     * CommType (13)
     */
    public final static class CommType
    {
        public final static String TAGNAME="CommType";
        public final static int TAGNUMBER=13;
    }

    /**
     * CumQty (14) Values
     */
    public final static class CumQty {
       public final static String TAGNAME="CumQty";
       public final static int TAGNUMBER=14;
    }

    /**
     * Currency (15)
     */
    public final static class Currency
    {
        public final static String TAGNAME="Currency";
        public final static int TAGNUMBER=15;
    }

    /**
     * ExecID (17)
     */
    public final static class ExecID
    {
        public final static String TAGNAME="ExecID";
        public final static int TAGNUMBER=17;
    }

   /**
    * ExecInst(18) values
    */
   public final static class ExecInst {
       public final static String TAGNAME="ExecInst";
       public final static int TAGNUMBER=18;

       public final static String NOT_HELD = "1";
       public final static String WORK = "2";
       public final static String GO_ALONG = "3";
       public final static String OVER_THE_DAY = "4";
       public final static String HELD = "5";
       public final static String PARTICIPATE_DONT_INITIATE = "6";
       public final static String STRICT_SCALE = "7";
       public final static String TRY_TO_SCALE = "8";
       public final static String STAY_ON_BIDSIDE = "9";
       public final static String STAY_ON_OFFERSIDE = "0";
       public final static String NO_CROSS = "A";
       public final static String OK_TO_CROSS = "B";
       public final static String CALL_FIRST = "C";
       public final static String PERCENT_OF_VOLUME = "D";
       public final static String DNI = "E";
       public final static String DNR = "F";
       public final static String AON = "G";
       public final static String INSTITUTIONS_ONLY = "I";
       public final static String LAST_PEG = "L";
       public final static String MID_PRICE_PEG = "M";
       public final static String NON_NEGOTIABLE = "N";
       public final static String OPENING_PEG = "O";
       public final static String MARKET_PEG = "P";
       public final static String PRIMARY_PEG = "R";
       public final static String SUSPEND = "S";
       public final static String CUSTOMER_DISPLAY_INSTRUCTION = "U";
       public final static String NETTING = "V";
       public final static String INTERMARKET_SWEEP = "f";
       public final static String TIED_CROSS = "g";
       public final static String AUTOLINK_CROSS = "h";
       public final static String AUTOLINK_CROSS_MATCH = "i";
       public final static String CROSS_WITHIN = "j";
       public final static String TIED_CROSS_WITHIN = "k";
       public final static String BID_PEG_CROSS = "l"; // lower case "el"
       public final static String OFFER_PEG_CROSS = "m";
       public final static String DO_NOT_ROUTE = "n";
       public final static String CASH_CROSS = "o"; // lower case "Oh"
       public final static String NEXT_DAY_CROSS = "p";
       public final static String TWO_DAY_CROSS = "q";
       public final static String WASH_TRADE_PREVENTION = "w";// Wash Trade Prevention
   }

    /**
     * ExecRefID (19)
     */
    public final static class ExecRefID
    {
        public final static String TAGNAME="ExecRefID";
        public final static int TAGNUMBER=19;
    }

    /**
     * Possible values for ExecTransType(20)
     */
    public final static class ExecTransType {
       public final static String TAGNAME="ExecTransType";
       public final static int TAGNUMBER=20;

       public final static String NEW = "0";
       public final static String CANCEL = "1";
       public final static String CORRECT = "2";
       public final static String STATUS = "3";
    }

    /**
     * HandlInst (21)
     */
    public final static class HandlInst
    {
        public final static String TAGNAME="HandlInst";
        public final static int TAGNUMBER=21;
        public final static String AUTO_NO_BROKER = "1";
        public final static String AUTO_BROKER_ALLOWED = "2";
        public final static String MANUAL = "3";
    }

    /**
     * Possible values for IDSource(22)
     * Only a subset of the possible values have been enumerated.
     */
    public final static class IDSource {
       public final static String TAGNAME="IDSource";
       public final static int TAGNUMBER=22;

       public final static String CUSIP = "1";
       public final static String SEDOL = "2";
       public final static String QUIK = "3";
       public final static String ISIN = "4";
       public final static String RIC = "5";
       public final static String ISO_CURRENCY_CODE = "6";
       public final static String ISO_COUNTRY_CODE = "7";
       public final static String EXCHANGE = "8";
       public final static String CTA = "9";
    }

    /**
     * IOIid (23)
     */
    public final static class IOIid
    {
        public final static String TAGNAME="IOIid";
        public final static int TAGNUMBER=23;
    }

    /**
     * Possible values for LastCapacity(29)
     */
    public final static class LastCapacity {
       public final static String TAGNAME="LastCapacity";
       public final static int TAGNUMBER=29;
    }

    /**
     * Possible values for LastMkt(30)
     */
    public final static class LastMkt {
       public final static String TAGNAME="LastMkt";
       public final static int TAGNUMBER=30;
    }

    /**
     * Possible values for LastPx(31)
     */
    public final static class LastPx {
       public final static String TAGNAME="LastPxType";
       public final static int TAGNUMBER=31;
    }

    /**
     * Possible values for LastShares(32)
     */
    public final static class LastShares {
       public final static String TAGNAME="LastShares";
       public final static int TAGNUMBER=32;
    }

    /**
     * Possible values for MsgSeqNum(34)
     */
    public final static class MsgSeqNum {
       public final static String TAGNAME="MsgSeqNum";
       public final static int TAGNUMBER=34;
    }

    /**
     * Possible values for MsgType(35)
     */
    public final static class MsgType {
       public final static String TAGNAME="MsgType";
       public final static int TAGNUMBER=35;
       public final static String LOGOUT = "5";
       public final static String EXECUTION_REPORT = "8";
       public final static String ORDER_CANCEL_REJECT = "9";
       public final static String LOGON = "A";
       public final static String NEW_ORDER_SINGLE = "D";
       public final static String ORDER_CANCEL_REQUEST = "F";
       public final static String ORDER_CANCEL_REPLACE_REQUEST = "G";
       public final static String DONT_KNOW_TRADE = "Q";
       public final static String TRADING_SESSION_STATUS_REQUEST = "g";
       public final static String TRADING_SESSION_STATUS = "h";
       public final static String BUSINESS_MESSAGE_REJECT = "j";
       
       
    }
    
    /**
     * Possible Quote Message Type Values for tag 35
     * @author Arun Ramachandran
     */
    public final static class QuoteMsgTypes{
    	//QuoteMessage Types
    	public final static String MASS_QUOTE = "i";
        public final static String SINGLE_QUOTE = "S";
        public final static String QUOTE_CANCEL = "Z";
        public final static String QUOTE_STATUS_REQUEST = "a";
        public final static String QUOTE_REQUEST = "R"; 
        private static String quoteMsgTypes[] = {MASS_QUOTE,SINGLE_QUOTE,QUOTE_CANCEL,QUOTE_STATUS_REQUEST, QUOTE_REQUEST};
        
        public final static boolean contains(String msgType){
        	for(int i =0;i<quoteMsgTypes.length;i++){
        		return msgType.equals(quoteMsgTypes[i]);
        	}
        	return false;
        }
        
        public final static int getIndex(String msgType){
        	for(int i =0;i<quoteMsgTypes.length;i++){
        		if(msgType.equals(quoteMsgTypes[i])){
        			return i;
        		};
        	}
        	return -1;
        }   
    }

    /**
     * OrderID(37)
     */
    public final static class OrderID{
       public final static String TAGNAME="OrderID";
       public final static int TAGNUMBER=37;
       public final static String UNKNOWN_ID = "NONE";
    }

    /**
     * Possible values for OrderQty(38)
     */
    public final static class OrderQty {
       public final static String TAGNAME="OrderQty";
       public final static int TAGNUMBER=38;
    }

    /**
     * Possible values for OrdStatus(39)
     */
    public final static class OrdStatus {
       public final static String TAGNAME="OrdStatus";
       public final static int TAGNUMBER=39;

       public final static String NEW = "0";
       public final static String PARTIAL_FILL = "1";
       public final static String FILL = "2";
       public final static String DONE_FOR_DAY = "3";
       public final static String CANCELED = "4";
       public final static String REPLACE = "5";
       public final static String PENDING_CANCEL = "6";
       public final static String STOPPED = "7";
       public final static String REJECTED = "8";
       public final static String SUSPENDED = "9";
       public final static String PENDING_NEW = "A";
       public final static String CALCULATED = "B";
       public final static String EXPIRED = "C";
       public final static String RESTATED = "D";
       public final static String PENDING_REPLACE = "E";
    }

    /**
     * OrdType(40) values
     */
    public final static class OrdType {
       public final static String TAGNAME="OrdType";
       public final static int TAGNUMBER=40;
       public final static String MARKET = "1";
       public final static String LIMIT =  "2";
       public final static String STOP  =  "3";
       public final static String STOP_LIMIT = "4";
       public final static String MARKET_ON_CLOSE = "5";
       public final static String WITH_OR_WITHOUT = "6";
       public final static String LIMIT_OR_BETTER = "7";
       public final static String LIMIT_WITH_OR_WITHOUT = "8";
       public final static String ON_BASIS = "9";
       public final static String ON_CLOSE = "A";
       public final static String LIMIT_ON_CLOSE = "B";
       public final static String FOREX_MARKET = "C";
       public final static String PREVIOUSLY_QUOTED = "D";
       public final static String PREVIOUSLY_INDICATED = "E";
       public final static String FOREX_LIMIT = "F";
       public final static String FOREX_SWAP = "G";
       public final static String FOREX_PREVIOUSLY_QUOTED = "H";
       public final static String MARKET_IF_TOUCHED = "J"; // Not approved yet
       public final static String PEGGED = "P";
    }

    /**
     * OrigClOrdID (41) Values
     */
    public final static class OrigClOrdID {
       public final static String TAGNAME="OrigClOrdID";
       public final static int TAGNUMBER=41;
       public final static String UNKNOWN_ID = "NONE";
    }

    /**
     * Possible values for PossDupFlag(43)
     */
    public final static class PossDupFlag {
       public final static String TAGNAME="PossDupFlag";
       public final static int TAGNUMBER=43;
    }

    /**
     * Possible values for Price(44)
     */
    public final static class Price {
       public final static String TAGNAME="Price";
       public final static int TAGNUMBER=44;
    }

    /**
     * Possible values for RefSeqNum(45)
     */
    public final static class RefSeqNum {
       public final static String TAGNAME="RefSeqNum";
       public final static int TAGNUMBER=45;
    }

    /**
     * Possible values for OrderCapacity(47) a.k.a Rule80A
     */
    public final static class OrderCapacity {
       public final static String TAGNAME="OrderCapacity";
       public final static int TAGNUMBER=47;

       public static final String CUSTOMER = "C";
       public final static String FIRM = "F";
       public final static String BROKER_DEALER = "B"; // CBOE ONLY
       public final static String CUSTOMER_BROKER_DEALER = "X"; //CBOE ONLY
       public final static String MARKET_MAKER = "M"; //CBOE ONLY

       public final static String NON_MEMBER_MARKETMAKER = "N"; //CBOE ONLY - CURRENTLY NOT SUPPORTED
       public final static String STOCKSPECIALIST_IN_UNDERLYING = "Y"; //CBOE ONLY - CURRENTLY NOT SUPPORTED

       // CME ONLY
       public final static String MEMBER_CUSTOMER_SEGREGATED_ACCOUNT = "V";
       public final static String MEMBER_HOUSE_ACCOUNT = "E";
       public final static String MEMBER_SIPC_PROTECTED_ACCOUNT = "Q";
       public final static String PROXY_CUSTOMER_SEGREGATED_ACCOUNT = "G";
       public final static String PROXY_HOUSE_ACCOUNT = "H";
       public final static String PROXY_SIPC_PROTECTED_ACCOUNT = "R";
       public final static String NONMEMBER_HOUSE_ACCOUNT = "O";
       public final static String NONMEMBER_SIPC_PROTECTED_ACCOUNT = "T";
    }

    /**
     * Possible values for Rule80A(47)
     */
    public final static class Rule80A {
       public final static String TAGNAME="Rule80A";
       public final static int TAGNUMBER=47;
    }

    /**
     * Possible values for SecurityID(48)
     */
    public final static class SecurityID {
       public final static String TAGNAME="SecurityID";
       public final static int TAGNUMBER=48;
    }

    /**
     * Possible values for SenderCompID(49)
     */
    public final static class SenderCompID {
       public final static String TAGNAME="SenderCompID";
       public final static int TAGNUMBER=49;
    }

    /**
     * SenderSubID(50)
     */
    public final static class SenderSubID {
      public final static String TAGNAME = "SenderSubID";
      public final static int TAGNUMBER = 50;
      public final static String STAGNUMBER = "50";
    }

    /**
     * Possible values for SendingTime(52)
     */
    public final static class SendingTime {
       public final static String TAGNAME="SendingTime";
       public final static int TAGNUMBER = 52;
    }

    /**
     * Possible values for Side(54)
     * Only a subset of the possible values have been enumerated.
     */
    public final static class Side{
        public final static String TAGNAME="Side";
        public final static int TAGNUMBER=54;

        public final static String BUY = "1";
        public final static String SELL = "2";
        public final static String SELL_SHORT = "5";
        public final static String SELL_SHORT_EXEMPT = "6";
        public final static String CROSS = "8";
        public final static String AS_DEFINED = "B";
        public final static String OPPOSITE = "C" ;
    }

    /**
     * Possible values for Symbol(55)
     */
    public final static class Symbol {
       public final static String TAGNAME="Symbol";
       public final static int TAGNUMBER=55;
       public final static String NA = "NA";       //CBOE Only
    }

    /**
     * Possible values for TargetCompID(56)
     */
    public final static class TargetCompID {
       public final static String TAGNAME="TargetCompID";
       public final static int TAGNUMBER=56;
    }

    /**
     * TargetSubID(57) values
     */
    public final static class TargetSubID {
      public final static String TAGNAME = "TargetSubID";
      public final static int TAGNUMBER = 57;
      public final static String PRODUCTION = "PROD"; //CBOE Only
      public final static String SIMULATOR = "SIM";   //CBOE Only
      public final static String TEST = "TEST";       //CBOE Only
    }

    /**
     * Possible values for Text(58)
     * Only a subset of the possible values have been enumerated.
     */
    public final static class Text{
       public final static String TAGNAME="Text";
       public final static int TAGNUMBER=58;
    }

    /**
     * TimeInForce(59) values
     */
    public final static class TimeInForce {
       public final static String TAGNAME="TimeInForce";
       public final static int TAGNUMBER=59;

       public final static String DAY = "0";
       public final static String GTC = "1";
       public final static String OPG = "2";
       public final static String IOC = "3";
       public final static String FOK = "4";
       public final static String GTX = "5";
       public final static String GTD = "6";
    }

    /**
     * TransactTime (60)
     */
    public final static class TransactTime
    {
        public final static String TAGNAME="TransactTime";
        public final static int TAGNUMBER=60;
    }

    /**
     * SettlmntTyp (63)
     */
    public final static class SettlmntTyp
    {
        public final static String TAGNAME="SettlmntTyp";
        public final static int TAGNUMBER=63;
    }

    /**
     * FutSettDate (64)
     */
    public final static class FutSettDate
    {
        public final static String TAGNAME="FutSettDate";
        public final static int TAGNUMBER=64;
    }

    /**
     * SymbolSfx (65)
     */
    public final static class SymbolSfx
    {
        public final static String TAGNAME="SymbolSfx";
        public final static int TAGNUMBER=65;
    }

    /**
     * ListID (66)
     */
    public final static class ListID
    {
        public final static String TAGNAME="ListID";
        public final static int TAGNUMBER=66;
    }

    /**
     * TradeDate (75)
     */
    public final static class TradeDate
    {
        public final static String TAGNAME="TradeDate";
        public final static int TAGNUMBER=75;
    }

    /**
     * ExecBroker (76)
     */
    public final static class ExecBroker
    {
        public final static String TAGNAME="ExecBroker";
        public final static int TAGNUMBER=76;
    }

   /**
    * OpenClose(77)
    */
   public final static class OpenClose {
       public final static String TAGNAME="OpenClose";
       public final static int TAGNUMBER=77;

       public final static String OPEN = "O";
       public final static String CLOSE = "C";
       public final static String NONE = "";
   }

    /**
     * NoAllocs (78)
     */
    public final static class NoAllocs
    {
        public final static String TAGNAME="NoAllocs";
        public final static int TAGNUMBER=78;
    }

    /**
     * AllocAccount (79)
     */
    public final static class AllocAccount
    {
        public final static String TAGNAME="AllocAccount";
        public final static int TAGNUMBER=79;
    }

    /**
     * AllocShares (80)
     */
    public final static class AllocShares
    {
        public final static String TAGNAME="AllocShares";
        public final static int TAGNUMBER=80;
    }

    /**
     * ProcessCode (81)
     */
    public final static class ProcessCode
    {
        public final static String TAGNAME="ProcessCode";
        public final static int TAGNUMBER=81;
    }

    /**
     * CxlQty (84) Values
     */
    public final static class CxlQty {
       public final static String TAGNAME="CxlQty";
       public final static int TAGNUMBER=84;
    }

    /**
     * Signature (89) Values
     */
    public final static class Signature {
       public final static String TAGNAME="Signature";
       public final static int TAGNUMBER=89;
    }

    /**
     * Possible values for SecureDataLen(90)
     */
    public final static class SecureDataLen {
       public final static String TAGNAME="SecureDataLen";
       public final static int TAGNUMBER=90;
    }

    /**
     * Possible values for SecureData(91)
     */
    public final static class SecureData {
       public final static String TAGNAME="SecureData";
       public final static int TAGNUMBER=91;
    }

    /**
     * Signature (93) Values
     */
    public final static class SignatureLength {
       public final static String TAGNAME="SignatureLength";
       public final static int TAGNUMBER=93;
    }

    /**
     * Email (??)
     * Should be EmailType (94)
     */
    public final static class Email
    {
        public final static String NEW = "0";
        public final static String REPLY = "1";
        public final static String ADMIN_REPLY = "2";
        public static boolean isValidType(String type)
        {
        if(type != null)
        {
            return (type.equals(NEW) || type.equals(REPLY) || type.equals(ADMIN_REPLY));
        }
        return false;
        }
    }

    /**
     * RawDataLength (95) Values
     */
    public final static class RawDataLength {
       public final static String TAGNAME="RawDataLength";
       public final static int TAGNUMBER=95;
    }

    /**
     * RawData (96) Values
     */
    public final static class RawData {
       public final static String TAGNAME="RawData";
       public final static int TAGNUMBER=96;
    }

   /**
    * PossResend(97)
    */
   public final static class PossResend {
       public final static String TAGNAME="PossResend";
       public final static int TAGNUMBER=97;

       public final static String POSSIBLE_RESEND = DataTypes.Boolean.TRUE;
       public final static String ORIGINAL_TRANSMISSION = DataTypes.Boolean.FALSE;
   }

    /**
     * EncryptMethod (98)
     */
    public final static class EncryptMethod
    {
        public final static String TAGNAME="EncryptMethod";
        public final static int TAGNUMBER=98;
        public final static String NONE = "0";
        public final static String PKCS = "1";
        public final static String DES_ECB = "2";
        public final static String PKCS_DES = "3";
        public final static String PGP_DES = "4";
        public final static String PGP_DES_MD5 = "5";
        public final static String PEM_DES_MD5 = "6";
    }

    /**
     * Possible values for StopPx(99)
     */
    public final static class StopPx {
       public final static String TAGNAME="StopPx";
       public final static int TAGNUMBER=99;
    }

    /**
     * ExDestination (100)
     */
    public final static class ExDestination
    {
        public final static String TAGNAME="ExDestination";
        public final static int TAGNUMBER=100;
        public final static String ASE = "ASE";
        public final static String ISX = "ISX";
        public final static String PHO = "PHO";
        public final static String PSE = "PSE";
        public final static String BOX = "BOX";
        public final static String NASDAQ = "NAS";
        public final static String BATS = "54";
        
    }

    /**
     * Possible values for CxlRejReason(102)
     */
    public final static class CxlRejReason {
       public final static String TAGNAME="CxlRejReason";
       public final static int TAGNUMBER=102;

       public final static int TOO_LATE_TO_CANCEL = 0;
       public final static int UNKNOWN_ORDER = 1;
       public final static int BROKER_OPTION = 2;
       public final static int ALREADY_PENDING_CANCEL_OR_CANCEL_REPLACE = 3;
       public final static int INCREASING_QUANTITY_NOT_SUPPORTED = 4;
       public final static int CANCEL_NOT_ACCEPTED_CURRENTLY = 5;
    }

    /**
     * OrdRejReason (103)
     */
    public final static class OrdRejReason
    {
        public final static String TAGNAME="OrdRejReason";
        public final static int TAGNUMBER=103;
        public final static int BROKER_OR_EXCHANGE_OPTION = 0;
        public final static int UNKNOWN_SYMBOL = 1;
        public final static int EXCHANGE_OR_TRADING_SESSION_CLOSED = 2;
        public final static int ORDER_EXCEEDS_LIMIT = 3;
        public final static int TOO_LATE_TO_ENTER = 4;
        public final static int UNKNOWN_ORDER = 5;
        public final static int DUPLICATE_ORDER = 6;
        public final static int DUPLICATE_VERBAL_ORDER = 7;
        public final static int STALE_ORDER = 8;
        public final static int UNSUPPORTED_ORDER_CHARACTERISTIC = 9;
        public final static int OTHER = 99;
    }

    /**
     * Issuer (106)
     */
    public final static class Issuer
    {
        public final static String TAGNAME="Issuer";
        public final static int TAGNUMBER=106;
    }

     /**
     * SecurityDesc (107)
     */
    public final static class SecurityDesc
    {
        public final static String TAGNAME="SecurityDesc";
        public final static int TAGNUMBER=107;
        public final static String UNKNOWN = "1";
        public final static String STRADDLE = "2";
        public final static String PSEUDO_STRADDLE = "3";
        public final static String VERTICAL = "4";
        public final static String RATIO = "5";
        public final static String TIME = "6";
        public final static String DIAGONAL = "7";
        public final static String COMBO = "8";
        public final static String BUY_WRITE = "9";
        /* and so it is possible to know these by names
         * as well as by numbers
         */
        public final static String UNKNOWN_1 = "UNKNOWN";
        public final static String STRADDLE_1 = "STRADDLE";
        public final static String PSEUDO_STRADDLE_1 = "PSEUDO_STRADDLE";
        public final static String PSEUDO_STRADDLE_2 = "PSEUDO-STRADDLE";
        public final static String PSEUDO_STRADDLE_3 = "PSEUDOSTRADDLE";
        public final static String VERTICAL_1 = "VERTICAL";
        public final static String RATIO_1 = "RATIO";
        public final static String TIME_1 = "TIME";
        public final static String DIAGONAL_1 = "DIAGONAL";
        public final static String COMBO_1 = "COMBO";
        public final static String BUY_WRITE_1 = "BUY_WRITE";
        public final static String BUY_WRITE_2 = "BUY-WRITE";
        public final static String BUY_WRITE_3 = "BUYWRITE";
    }

    /**
     * HeartBtInt (108)
     */
    public final static class HeartBtInt
    {
        public final static String TAGNAME="HeartBtInt";
        public final static int TAGNUMBER=108;
    }

    /**
     * ClientID (109)
     */
    public final static class ClientID
    {
        public final static String TAGNAME="ClientID";
        public final static int TAGNUMBER=109;
    }

    /**
     * MinQty (110)
     */
    public final static class MinQty
    {
        public final static String TAGNAME="MinQty";
        public final static int TAGNUMBER=110;
    }

    /**
     * MaxFloor (111)
     */
    public final static class MaxFloor
    {
        public final static String TAGNAME="MaxFloor";
        public final static int TAGNUMBER=111;
    }

    /**
     * ReportToExch (113)
     */
    public final static class ReportToExch
    {
        public final static String TAGNAME="ReportToExch";
        public final static int TAGNUMBER=113;
    }

    /**
     * LocateReqd (114)
     */
    public final static class LocateReqd
    {
        public final static String TAGNAME="LocateReqd";
        public final static int TAGNUMBER=114;
    }

    /**
     * OnBehalfOfCompID(115)
     */
    public final static class OnBehalfOfCompID {
      public final static String TAGNAME = "OnBehalfOfCompID";
      public final static int TAGNUMBER = 115;
      public final static String STAGNUMBER = "115";
    }

    /**
     * OnBehalfOfSubID(116)
     */
    public final static class OnBehalfOfSubID {
      public final static String TAGNAME = "OnBehalfOfSubID";
      public final static int TAGNUMBER = 116;
      public final static String STAGNUMBER = "116";
    }

    /**
     * Possible values for QuoteID[117]
     */
    public final static class QuoteID {
       public final static String TAGNAME="QuoteID";
       public final static int TAGNUMBER=117;
    }

    /**
     * Possible values for SettlCurrAmt[119]
     */
    public final static class SettlCurrAmt {
       public final static String TAGNAME="SettlCurrAmt";
       public final static int TAGNUMBER=119;
    }

    /**
     * Possible values for SettlCurrency(120)
     */
    public final static class SettlCurrency {
       public final static String TAGNAME="SettlCurrency";
       public final static int TAGNUMBER=120;
    }

    /**
     * Possible values for ForexReq(121)
     */
    public final static class ForexReq {
       public final static String TAGNAME="ForexReq";
       public final static int TAGNUMBER=121;
    }

    /**
     * Possible values for OrigSendingTime(122)
     */
    public final static class OrigSendingTime {
       public final static String TAGNAME="OrigSendingTime";
       public final static int TAGNUMBER=122;
    }

   /**
    * ExpireTime (126)
    */
   public final static class ExpireTime {
     public final static String TAGNAME="ExpireTime";
     public final static int TAGNUMBER=126;
   }

    /**
     * DKReason (127)
     */
    public static class DKReason {
      public final static String TAGNAME = "DKReason";
      public final static int TAGNUMBER = 127;

      public static final char UNKNOWN_SYMBOL = 'A';
      public static final char WRONG_SIDE = 'B';
      public static final char QUANTITY_EXCEEDS_ORDER = 'C';
      public static final char NO_MATCHING_ORDER = 'D';
      public static final char PRICE_EXCEEDS_LIMIT = 'E';
      public static final char STALE_EXECUTION = 'F';
      public static final char OTHER = 'Z';
    }

    /**
     * DeliverToCompID (128)
     */
    public final static class DeliverToCompID {
      public final static String TAGNAME = "DeliverToCompID";
      public final static int TAGNUMBER = 128;
    }

    /**
     * DeliverToSubID (129)
     */
    public final static class DeliverToSubID {
      public final static String TAGNAME = "DeliverToSubID";
      public final static int TAGNUMBER = 129;
    }

    /**
     * PrevClosePx (140)
     */
    public final static class PrevClosePx
    {
        public final static String TAGNAME="PrevClosePx";
        public final static int TAGNUMBER=140;
    }

    /**
     * ResetSeqNumFlag (141)
     */
    public final static class ResetSeqNumFlag
    {
        public final static String TAGNAME="ResetSeqNumFlag";
        public final static int TAGNUMBER=141;
    }

    /**
     * SenderLocationID (142)
     */
    public final static class SenderLocationID {
     public final static String TAGNAME = "SenderLocationID";
     public final static int TAGNUMBER = 142;
     public final static String STAGNUMBER = "142";
    }

    /**
     * TargetLocationID (143)
     */
    public final static class TargetLocationID {
     public final static String TAGNAME = "TargetLocationID";
     public final static int TAGNUMBER = 143;
    }

    /**
     * OnBehalfOfLocationID(144)
     */
    public final static class OnBehalfOfLocationID {
      public final static String TAGNAME = "OnBehalfOfLocationID";
      public final static int TAGNUMBER = 144;
      public final static String STAGNUMBER = "144";
    }

    /**
     * DeliverToLocationID(145)
     */
    public final static class DeliverToLocationID {
      public final static String TAGNAME = "DeliverToLocationID";
      public final static int TAGNUMBER = 145;
    }

    /**
     * Possible values for ExecType(150)
     */
    public final static class ExecType {
       public final static String TAGNAME="ExecType";
       public final static int TAGNUMBER=150;

       public final static String NEW = "0";
       public final static String PARTIAL_FILL = "1";
       public final static String FILL = "2";
       public final static String DONE_FOR_DAY = "3";
       public final static String CANCELED = "4";
       public final static String REPLACE = "5";
       public final static String PENDING_CANCEL = "6";
       public final static String STOPPED = "7";
       public final static String REJECTED = "8";
       public final static String SUSPENDED = "9";
       public final static String PENDING_NEW = "A";
       public final static String CALCULATED = "B";
       public final static String EXPIRED = "C";
       public final static String RESTATED = "D";
       public final static String PENDING_REPLACE = "E";
    }

    /**
     * LeavesQty (151) Values
     */
    public final static class LeavesQty {
       public final static String TAGNAME="LeavesQty";
       public final static int TAGNUMBER=151;
    }

    /**
     * CashOrderQty (152)
     */
    public final static class CashOrderQty
    {
        public final static String TAGNAME="CashOrderQty";
        public final static int TAGNUMBER=152;
    }

    /**
     * SettlCurrFxRate (155)
     */
    public final static class SettlCurrFxRate
    {
        public final static String TAGNAME="SettlCurrFxRate";
        public final static int TAGNUMBER=155;
    }

    /**
     * SettlCurrFxRateCalc (156)
     */
    public final static class SettlCurrFxRateCalc
    {
        public final static String TAGNAME="SettlCurrFxRateCalc";
        public final static int TAGNUMBER=156;
    }

    /**
     * Possible values for SecurityType(167)
     * Only a subset of the possible values have been enumerated.
     */
    public final static class SecurityType {
       public final static String TAGNAME="SecurityType";
       public final static int TAGNUMBER=167;

       public final static String OPTION = "OPT";
       public final static String FUTURE = "FUT";
       public final static String COMMON_STOCK = "CS";
       public final static String FOREX_CONTRACT = "FOR";
       public final static String PREFERRED_STOCK = "PS";
       public final static String US_TREASURY_BILL = "USTB";
       public final static String WARRANT = "WAR";

       public final static String INDEX = "INDX";  //CBOE DEFINED ONLY
       public final static String MULTI_LEG = "MLEG"; //CBOE DEFINED ONLY

       public final static String COMMODITY = "CMDTY"; //CBOE DEFINED ONLY
       public final static String DEBT = "DEBT"; //CBOE DEFINED ONLY
       public final static String LINKED_NOTE = "LNKNT"; //CBOE DEFINED ONLY
       public final static String UNIT_INVESTMENT_TRUST = "UIT"; //CBOE DEFINED ONLY
       public final static String VOLATILITY_INDEX = "VIX"; //CBOE DEFINED ONLY
    }

    /**
     * EffectiveTime (168)
     */
    public final static class EffectiveTime
    {
        public final static String TAGNAME="EffectiveTime";
        public final static int TAGNUMBER=168;
    }

    /**
     * Possible values for OrderQty2(192)
     */
    public final static class OrderQty2 {
       public final static String TAGNAME="OrderQty2";
       public final static int TAGNUMBER=192;
    }

    /**
     * Possible values for FutSettDate2(193)
     */
    public final static class FutSettDate2 {
       public final static String TAGNAME="FutSettDate2";
       public final static int TAGNUMBER=193;
    }

    /**
     * Possible values for LastSpotRate(194)
     */
    public final static class LastSpotRate {
       public final static String TAGNAME="LastSpotRate";
       public final static int TAGNUMBER=194;
    }

    /**
     * Possible values for LastForwardPoints(195)
     */
    public final static class LastForwardPoints {
       public final static String TAGNAME="LastForwardPoints";
       public final static int TAGNUMBER=195;
    }

    /**
     * SecondaryOrderID(198)
     */
    public final static class SecondaryOrderID {
       public final static String TAGNAME="SecondaryOrderID";
       public final static int TAGNUMBER=198;
    }

   /**
    * MaturityMonthYear (200) values
    */
   public final static class MaturityMonthYear {
      public final static String TAGNAME="MaturityMonthYear";
      public final static int TAGNUMBER=200;
   }

   /**
    * PutOrCall (201) values
    */
   public final static class PutOrCall {
      public final static String TAGNAME="PutOrCall";
      public final static int TAGNUMBER=201;
      public final static int PUT=0;
      public final static int CALL=1;
   }

   /**
    * StrikePrice (202) values
    */
   public final static class StrikePrice {
      public final static String TAGNAME="StrikePrice";
      public final static int TAGNUMBER=202;
   }

   /**
    * CoveredOrUncovered (203) values
    */
   public final static class CoveredOrUncovered {
      public final static String TAGNAME="CoveredOrUncovered";
      public final static int TAGNUMBER=203;

      public final static int COVERED = 0;
      public final static int UNCOVERED = 1;
   }

   /**
    * CustomerOrFirm (204) values
    */
   public final static class CustomerOrFirm {
      public final static String TAGNAME="CustomerOrFirm";
      public final static int TAGNUMBER=204;
      public final static String STAGNUMBER = "204";

      public final static int CUSTOMER = 0;
      public final static int FIRM = 1;
      public final static int BROKER_DEALER = 2; // CBOE ONLY
      public final static int CUSTOMER_BROKER_DEALER = 3; //CBOE ONLY
      public final static int MARKET_MAKER = 4; //CBOE ONLY

      public final static int NON_MEMBER_MARKETMAKER = 5; //CBOE ONLY - CURRENTLY NOT SUPPORTED
      public final static int STOCKSPECIALIST_IN_UNDERLYING = 6; //CBOE ONLY - CURRENTLY NOT SUPPORTED
   }

   /**
    * MaturityDay (205) values
    */
   public final static class MaturityDay {
      public final static String TAGNAME="MaturityDay";
      public final static int TAGNUMBER=205;
   }

    /**
     * OptAttribute (206)
     */
    public final static class OptAttribute
    {
        public final static String TAGNAME="OptAttribute";
        public final static int TAGNUMBER=206;
    }

    /**
     * Possible values for SecurityExechange(207)
     * Only a subset of the possible values have been enumerated.
     * Based upon the Reuters Exchange Codes
     */
    public final static class SecurityExchange {
        public final static String TAGNAME="SecurityExchange";
        public final static int TAGNUMBER=207;

        public final static String CBOE = "W";         // PAR_CODE:    CBO
        public final static String CBOE2 = "C2OX";    // PAR_CODE:

        public final static String CINCINNATI = "C";   // PAR_CODE:    CSE
        public final static String AMERICAN = "A";     // PAR_CODE:    ASE
        public final static String BOSTON = "B";       // PAR_CODE:    BOX
        public final static String CHICAGO = "MW";     // PAR_CODE:    CHX
        public final static String NASDAQ = "O";       // PAR_CODE:    ACT
        public final static String NYSE = "N";         // PAR_CODE:    NYS
        public final static String PACIFIC = "P";      // PAR_CODE:    PCX
        public final static String PHILIDELPHIA = "PH";// PAR_CODE:    PHX
        // Added for W_STOCK, Stock_MD - may not all pass Appia Validation
        public final static String CBOT = "!";                   // from REDI
        public final static String CME  = "V";                   // from REDI
        public final static String ISE  = "5";                   // from REDI
        public final static String LIFFE  = "2";                 // ?
        public final static String NYME = "12";                  //
        public final static String ONE  = "+";                   // from REDI
        public final static String BOX  = "b";                   // from REDI
        public final static String NASDAQAUTOQUOTE = "S";        // from REDI

    }

    /**
     * Possible values for MaxShow(210)
     */
    public final static class MaxShow {
       public final static String TAGNAME="MaxShow";
       public final static int TAGNUMBER=210;
    }

    /**
     * Possible values for PegDifference(211)
     */
    public final static class PegDifference {
       public final static String TAGNAME="PegDifference";
       public final static int TAGNUMBER=211;
    }

    /**
     * Possible values for XmlDataLen(212)
     */
    public final static class XmlDataLen {
       public final static String TAGNAME="XmlDataLen";
       public final static int TAGNUMBER=212;
    }

    /**
     * Possible values for XmlData(213)
     */
    public final static class XmlData {
       public final static String TAGNAME="XmlData";
       public final static int TAGNUMBER=213;
    }

    /**
     * CouponRate (223)
     */
    public final static class CouponRate
    {
        public final static String TAGNAME="CouponRate";
        public final static int TAGNUMBER=223;
    }

    /**
     * ContractMultiplier (231)
     */
    public final static class ContractMultiplier
    {
        public final static String TAGNAME="ContractMultiplier";
        public final static int TAGNUMBER=231;
    }

   /**
    * MDReqID (262) Constants
    */
   public final static class MDReqID
   {
      public final static String TAGNAME="MDReqID";
      public final static int TAGNUMBER=262;
   }

   /**
    * SubscriptionRequestType (263) Constants
    */
   public final static class SubscriptionRequestType
   {
      public final static String TAGNAME="SubscriptionRequestType";
      public final static int TAGNUMBER=263;

      public final static String SNAPSHOT = "0";
      public final static String SNAPSHOT_UPDATES = "1";
      public final static String DISABLE = "2";
   }



  /**
   * MarketDepth (264) Constants
   */
   public final static class MarketDepth
   {
      public final static String TAGNAME="MarketDepth";
      public final static int TAGNUMBER=264;

      public final static int FULL_BOOK = 0;
      public final static int TOP_OF_BOOK = 1;

   }

  /**
   * MDUpdate (265) Constants
   */
   public final static class MDUpdateType
   {
      public final static String TAGNAME="MDUpdateType";
      public final static int TAGNUMBER=265;

      public final static int FULL_REFRESH = 0;
      public final static int INCREMENTAL_REFRESH = 1;

   }



  /**
   *  MDEntryType (269) Constants
   */
   public final static class MDEntryType
   {

      public final static String TAGNAME="MDEntryType";
      public final static int TAGNUMBER=269;

      public final static String BID = "0";
      public final static String OFFER = "1";
      public final static String TRADE = "2";
      public final static String INDEX_VALUE = "3";
      public final static String OPENING_PRICE = "4";
      public final static String CLOSING_PRICE = "5";
      public final static String SETTLEMENT_PRICE = "6";
      public final static String TRADING_SESSION_HIGH_PRICE = "7";
      public final static String TRADING_SESSION_LOW_PRICE = "8";
      public final static String TRADING_SESSION_VWAP_PRICE = "9";
   }

  /**
   *  MDEntryPX (270) Constants
   */
   public final static class MDEntryPX
   {
      public final static String TAGNAME="MDEntryPX";
      public final static int TAGNUMBER=270;
   }

  /**
   *  MDEntrySize (271) Constants
   */
   public final static class MDEntrySize
   {
      public final static String TAGNAME="MDEntrySize";
      public final static int TAGNUMBER=271;
   }

  /**
   *  MDEntryTime (273) Constants
   */
   public final static class MDEntryTime
   {
      public final static String TAGNAME="MDEntryTime";
      public final static int TAGNUMBER=273;
   }

   /**
    * TickDirection (274)
    */
   public final static class TickDirection
   {
      public final static String TAGNAME="TickDirection";
      public final static int TAGNUMBER=274;

      public final static String PLUS_TICK = "0";
      public final static String ZERO_PLUS_TICK = "1";
      public final static String MINUS_TICK = "2";
      public final static String ZERO_MINUS_TICK = "3";
   }

    /**
     * MDMkt (275)
     */
    public final static class MDMkt
    {
        public final static String TAGNAME = "MDMkt";
        public final static int TAGNUMBER = 275;
        public final static String AMEX = "A";
        public final static String CBOE = "W";
        public final static String CBOE2 = "C2OX";

        public final static String ISC = "Y";
        public final static String NASDAQ = "O";
        public final static String NYSE = "N";
        public final static String PACIFIC = "P";
        public final static String PHIL = "PH";
        public final static String PHIL_OPTIONS = "X";
    }

   /**
    * QuoteCondition (276)
    */
   public final static class QuoteCondition
   {
      public final static String TAGNAME="QuoteCondition";
      public final static int TAGNUMBER=276;

      public final static String OPEN = "A";
      public final static String CLOSED = "B";
      public final static String EXCHANGE_BEST = "C";
      public final static String CONSOLIDATED_BEST = "D";
      public final static String LOCKED = "E";
      public final static String CROSSED = "F";
      public final static String DEPTH = "G";
      public final static String FAST_TRADING = "H";
      public final static String NON_FIRM = "I";
   }

  /**
   * TradeCondition(277)
   */
   public final static class TradeCondition
   {
       public final static String TAGNAME="TradeCondition";
       public final static int TAGNUMBER=277;
       public final static String CASH_MARKET = "A";
       public final static String AVG_PRICE_TRADE = "B";
       public final static String CASH_TRADE = "C";
       public final static String NEXT_DAY_MARKET = "D";
       public final static String OPEN_REOPEN_TRADE_DETAIL = "E";
       public final static String INTRADAY_TRADE_DETAIL = "F";
       public final static String RULE_127_TRADE = "G";
       public final static String RULE_155_TRADE = "H";
       public final static String SOLD_LAST = "I";
       public final static String NEXT_DAY_TRADE = "J";
       public final static String OPENED = "K";
       public final static String SELLER = "L";
       public final static String SOLD = "M";
       public final static String STOPPED_STOCK = "N";
       public final static String IMBALANCE_MORE_BUYERS = "P";
       public final static String IMBALANCE_MORE_SELLERS = "Q";
       public final static String OPENING_PRICE = "R";
       public final static String NO_OPENING_TRADE = "S";
       public final static String MULTIPLE_OPENING_PRICES = "T";
       public final static String NEED_QUOTE_TO_OPEN = "U";
       public final static String PRICE_NOT_IN_QUOTE_RANGE = "V";
       public final static String NEED_DPM_QUOTE_TO_OPEN = "W";
       public final static String PRICE_NOT_IN_BOTR_RANGE = "X";
   }

    /**
     * MDReqRejReason (281)
     */
    public final static class MDReqRejReason
    {
        public final static String TAGNAME="MDReqRejReason";
        public final static int TAGNUMBER=281;
        public final static String UNKNOWN_SYMBOL = "0";
        public final static String DUPLICATE_REQ_ID = "1";
        public final static String INSUFFICIENT_BANDWIDTH = "2";
        public final static String INSUFFICIENT_PERMISSIONS = "3";
        public final static String UNSUPPORTED_REQ_TYPE = "4";
        public final static String UNSUPPORTED_MARKET_DEPTH = "5";
        public final static String UNSUPPORTED_UDPATE_TYPE = "6";
        public final static String UNSUPPORTED_AGGREGATE_BOOK = "7";
        public final static String UNSUPPORTED_ENTRY_TYPE = "8";
        public final static String USE_SPECIFIED_ENGINE = "D";
              public final static String ALREADY_SUBSCRIBED = "E";
              public final static String SUBSCRIPTION_REPLACED = "F";
            public final static String FORCED_UNSUBSCRIBE = "G";


    }

   /**
    *  OpenCloseSettleFlag(286)
    */
   public final static class OpenCloseSettleFlag
   {
      public final static String TAGNAME="OpenCloseSettleFlag";
      public final static int TAGNUMBER=286;

      public final static int DAILY = 0;
      public final static int SESSION = 1;
      public final static int SETTLEMENT = 2;
      public final static int EXPECTED_OPEN = 3; //Non-std extension - pending approval
   }

   /* QuoteAckStatus (297) values
   */
    public final static class QuoteAckStatus {
      public final static String TAGNAME="QuoteAckStatus";
      public final static int TAGNUMBER=297;
      public final static int ACCEPTED = 0;
      public final static int CANCELED_FOR_SYMBOL = 1;
      public final static int CANCELED_FOR_SECURITY_TYPE = 2;
      public final static int CANCELED_FOR_UNDERLYING = 3;
      public final static int CANCELED_ALL = 4;
      public final static int REJECTED = 5;
      public final static int QRM_ONLY = 6;
    }

   /* QuoteCancelType (298) values
   */
    public final static class QuoteCancelType {
      public final static String TAGNAME="QuoteCancelType";
      public final static int TAGNUMBER=298;
      public final static int CANCEL_ALL_QUOTES = 4; //actual values to be changed
      public final static int CANCEL_FOR_SYMBOL = 1;
      public final static int CANCEL_FOR_SECURITY_TYPE = 2;
      public final static int CANCEL_FOR_UNDERLYING = 3;
    }

   /**
    *  QuoteRejectReason (300) values
    */
    public final static class QuoteRejectReason {
        public final static String TAGNAME = "QuoteRejectReason";
        public final static int TAGNUMBER = 300;
        public final static int UNKNOWN_SYMBOL = 1;
        public final static int EXCHANGE_OR_SECURITY_CLOSED = 2;
        public final static int ORDER_EXCEEDS_LIMIT = 3;
        public final static int TOO_LATE_TO_ENTER = 4;
        public final static int UNKNOWN_ORDER = 5;
        public final static int DUPLICATE_ORDER = 6;
        public final static int INVALID_BID_ASK_SPREAD = 7;
        public final static int INVALID_PRICE = 8;
        public final static int NOT_AUTHORIZED_TO_QUOTE_SECURITY = 9;          
        public final static int INSUFFICIENT_QUANTITY = 11;
        public final static int INCOMPLETE_QUOTE = 12;
        public final static int TWO_SIDED_QUOTE_REQUIRED = 13;
        public final static int INVALID_SIDE = 14;
        public final static int INVALID_TRADING_SESSION = 15;
        public final static int EXCHANGE_OR_BROKER_OPTION = 16;
        public final static int QUOTE_CANCEL_IN_PROGRESS = 88;
        public final static int EXCEEDS_CONCURRENT_QUOTE_LIMIT = 89;
        public final static int INVALID_SESSION_ID = 90;
        public final static int QUOTE_RATE_EXCEEDED= 91;
        public final static int SEQUENCE_LIMIT_EXCEEDED= 92;
        public final static int INVALID_QUOTE_UPDATE_CONTROL_ID= 93;
        public final static int QUOTE_TRIGGER= 94;
        public final static int QUOTE_UPDATE_CONTROL = 95;
        public final static int SERVER_NOT_AVAILABLE = 96;
        public final static int QUOTE_BEING_PROCESSED = 97;
        public final static int CALL_LIMIT_EXCEEDED = 98;
        public final static int UNSPECIFIED_REASON = 99;
   }

    /* QuoteResponseLevel (301) values
    */
    public final static class QuoteResponseLevel {
      public final static String TAGNAME = "QuoteResponseLevel";
      public final static int TAGNUMBER=301;
      public final static int ACK_NONE = 0;
      public final static int ACK_ERROR = 1;
      public final static int ACK_EACH = 2;
    }

    /**
     *  UnderlyingSecurityID Symbol (309)
     */
    public final static class UnderlyingSecurityID {
      public final static String TAGNAME="UnderlyingSecurityID";
      public final static int TAGNUMBER=309;

    }

     /**
     *  UnderlyingSecurityType Symbol (310)
     */
    public final static class UnderlyingSecurityType {
      public final static String TAGNAME="UnderlyingSecurityType";
      public final static int TAGNUMBER=310;

    }
    /**
     *  Underlying Symbol (311)
     */
    public final static class UnderlyingSymbol {
      public final static String TAGNAME="UnderlyingSymbol";
      public final static int TAGNUMBER=311;

    }
      /**
     *  UnderlyingMaturityMonthYear Symbol (313)
     */
    public final static class UnderlyingMaturityMonthYear {
      public final static String TAGNAME="UnderlyingMaturityMonthYear";
      public final static int TAGNUMBER=313;

    }

       /**
     *  UnderlyingPutOrCall Symbol (315)
     */
    public final static class UnderlyingPutOrCall{
      public final static String TAGNAME="UnderlyingPutOrCall";
      public final static int TAGNUMBER=315;

    }

     /**
     *  UnderlyingStrikePrice Symbol (316)
     */
    public final static class UnderlyingStrikePrice{
      public final static String TAGNAME="UnderlyingStrikePrice";
      public final static int TAGNUMBER=316;

    }
     /**
     *  SecurityReqID (320)
     */
    public final static class SecurityReqID{
      public final static String TAGNAME="SecurityReqID";
      public final static int TAGNUMBER=320;

    }

  /**
   *  SecurityRequestType (321) values
   */
  public final static class SecurityRequestType
  {
      public final static String TAGNAME="SecurityRequestType";
      public final static int TAGNUMBER=321;
      public final static int IDENTITY_WITH_NAME = 0;
      public final static int IDENTITY = 1;
      public final static int LIST_TYPES = 2;
      public final static int LIST_SECURITIES = 3;
  }

   /**
     *  SecurityResponseID (322)
     */
    public final static class SecurityResponseID{
      public final static String TAGNAME="SecurityResponseID";
      public final static int TAGNUMBER=322;

    }

  /**
   *  SecurityResponseType (323) values
   */
  public final static class SecurityResponseType
  {
      public final static String TAGNAME="SecurityResponseType";
      public final static int TAGNUMBER=323;

      public final static int ACCEPT = 1;
      public final static int ACCEPT_WITH_REVISIONS = 2;
      public final static int LIST_TYPES = 3;
      public final static int LIST_SECURITIES = 4;
      public final static int REJECT = 5;
      public final static int NO_MATCH = 6;
      public final static int PROD_STATUS_SNAPSHOT = 7;
      public final static int PROD_STATUS_UPDATE = 8;
  }

   /**
    *  UnsolicitedIndicator(325)
    */
   public final static class UnsolicitedIndicator
   {
      public final static String TAGNAME="UnsolicitedIndicator";
      public final static int TAGNUMBER=325;
      public final static String UNSOLICITED = FixUtilConstants.DataTypes.Boolean.TRUE;
      public final static String SOLICITED = FixUtilConstants.DataTypes.Boolean.FALSE;
   }

    /**
     *  SecurityTradingStatus(326) values
     */
     public final static class SecurityTradingStatus {

      public final static String TAGNAME="SecurityTradingStatus";
      public final static int TAGNUMBER=326;

      public final static int  OPENING_DELAY = 1;
      public final static int  TRADING_HALT = 2;
      public final static int  RESUME = 3;
      public final static int  NO_OPEN_NO_RESUME = 4;
      public final static int  PRICE_INDICATION = 5;
      public final static int  TRADING_RANGE_INDICATION = 6;
      public final static int  MARKET_IMBALANCE_BUY = 7;
      public final static int  MARKET_IMBALANCE_SELL = 8;
      public final static int  MARKET_ON_CLOSE_IMBALANCE_BUY = 9;
      public final static int  MARKET_ON_CLOSE_IMBALANCE_SELL = 10;
      // Value 11 is not used
      public final static int  NO_MARKET_IMBALANCE = 12;
      public final static int  NO_MARKET_ON_CLOSE_IMBALANCE = 13;
      public final static int  ITS_PRE_OPENING = 14;
      public final static int  NEW_PRICE_INDICATION = 15;
      public final static int  TRADE_DISSEMINATION_TIME = 16;
      public final static int  READY_TO_TRADE = 17; // Start of session
      public final static int  NOT_AVAILABLE_FOR_TRADING = 18; // End of session
      public final static int  NOT_TRADED_ON_THIS_MARKET = 19;
      public final static int  UNKNOWN_OR_INVALID = 20;

      public final static int  PRE_OPEN = 21; //CBOE Only
      public final static int  OPENING_ROTATION = 22; // CBOE Only
      public final static int  FAST_MARKET = 23; // CBOE Only

      public final static int  ON_HOLD = 24; // CBOE Only
      public final static int  OFF_HOLD = 25; // CBOE Only
      public final static int  TEMPORARILY_NOT_AVAILABLE_FOR_TRADING = 26; // CBOE Only "Suspended"
      
      /* CBOE only added as part of Trading Class Status Indicators */
      public static final int CLOSED_OUTAGE = 30;
      public static final int OPEN_AFTER_OUTAGE = 31;
      //end change....
     }

    /**
     * TradSesReqID (335)
     */
    public final static class TradSesReqID
    {
        public final static String TAGNAME = "TradSesReqID";
        public final static int TAGNUMBER = 335;
    }

    /**
     * TradingSessionID (336)
     */
    public static class TradingSessionID
    {
        public final static String TAGNAME = "TradingSessionID";
        public final static int TAGNUMBER = 336;
        /**
         * These session names are only to provide ease of coding.
         * They may not be exhaustive
         */
        public final static String W_MAIN       = "W_MAIN";
        public final static String ONE_MAIN     = "ONE_MAIN";
        public final static String CFE_MAIN     = "CFE_MAIN";
        public final static String W_STOCK      = "W_STOCK";
        public final static String STOCK_MD     = "Stock_MD";
        public final static String UNDERLYING   = "Underlying";
    }

    /**
     * Possible values for ContraTrader(337)
     */
    public final static class ContraTrader {
       public final static String TAGNAME="ContraTrader";
       public final static int TAGNUMBER=337;
    }

    /**
     * Possible values for TradSesMethod(338)
     */
    public final static class TradSesMethod {
       public final static String TAGNAME="TradSesMethod";
       public final static int TAGNUMBER=338;
    }

    /**
     * Possible values for TradSesMode(339)
     */
    public final static class TradSesMode {
       public final static String TAGNAME="TradSesMode";
       public final static int TAGNUMBER=339;
    }

    /**
     * TradSesStatus (340)
     */
    public final static class TradSesStatus
    {
        public final static String TAGNAME="TradSesStatus";
        public final static int TAGNUMBER=340;
        public final static int UNKNOWN_OR_INVALID = 0;
        public final static int HALTED = 1;
        public final static int OPEN = 2;
        public final static int CLOSED = 3;
        public final static int PREOPEN = 4;
        public final static int PRECLOSE = 5;
    }

    /**
     * Possible values for TradSesStartTime(341)
     */
    public final static class TradSesStartTime {
       public final static String TAGNAME="TradSesStartTime";
       public final static int TAGNUMBER=341;
    }

    /**
     * Possible values for TradSesOpenTime(342)
     */
    public final static class TradSesOpenTime {
       public final static String TAGNAME="TradSesOpenTime";
       public final static int TAGNUMBER=342;
    }

    /**
     * Possible values for TradSesPreCloseTime(343)
     */
    public final static class TradSesPreCloseTime {
       public final static String TAGNAME="TradSesPreCloseTime";
       public final static int TAGNUMBER=343;
    }

    /**
     * Possible values for TradSesCloseTime(344)
     */
    public final static class TradSesCloseTime {
       public final static String TAGNAME="TradSesCloseTime";
       public final static int TAGNUMBER=344;
    }

    /**
     * Possible values for TradSesEndTime(345)
     */
    public final static class TradSesEndTime {
       public final static String TAGNAME="TradSesEndTime";
       public final static int TAGNUMBER=345;
    }

    /**
     * Possible values for MessageEncoding(347)
     */
    public final static class MessageEncoding {
       public final static String TAGNAME="MessageEncoding";
       public final static int TAGNUMBER=347;
    }

    /**
     * EncodedIssuerLen (348)
     */
    public final static class EncodedIssuerLen
    {
        public final static String TAGNAME="EncodedIssuerLen";
        public final static int TAGNUMBER=348;
    }

    /**
     * EncodedIssuer (349)
     */
    public final static class EncodedIssuer
    {
        public final static String TAGNAME="EncodedIssuer";
        public final static int TAGNUMBER=349;
    }

    /**
     * EncodedSecurityDescLen (350)
     */
    public final static class EncodedSecurityDescLen
    {
        public final static String TAGNAME="EncodedSecurityDescLen";
        public final static int TAGNUMBER=350;
    }

    /**
     * EncodedSecurityDesc (351)
     */
    public final static class EncodedSecurityDesc
    {
        public final static String TAGNAME="EncodedSecurityDesc";
        public final static int TAGNUMBER=351;
    }

    /**
     * Possible values for EncodedTextLen(354)
     */
    public final static class EncodedTextLen {
       public final static String TAGNAME="EncodedTextLen";
       public final static int TAGNUMBER=354;
    }

    /**
     * Possible values for EncodedText(355)
     */
    public final static class EncodedText {
       public final static String TAGNAME="EncodedText";
       public final static int TAGNUMBER=355;
    }

    /**
     * QuoteEntryRejectReason (368)
     */
    public final static class QuoteEntryRejectReason
    {
        public final static String TAGNAME = "QuoteEntryRejectReason";
        public final static int TAGNUMBER = 368;
        public final static int UNKNOWN_SYMBOL = 1;
        public final static int EXCHANGE_OR_SECURITY_CLOSED = 2;
        public final static int ORDER_EXCEEDS_LIMIT = 3;
        public final static int TOO_LATE_TO_ENTER = 4;
        public final static int UNKNOWN_ORDER = 5;
        public final static int DUPLICATE_ORDER = 6;
        public final static int INVALID_BID_ASK_SPREAD = 7;
        public final static int INVALID_PRICE = 8;
        public final static int NOT_AUTHORIZED_TO_QUOTE_SECURITY = 9;
        public final static int INSUFFICIENT_QUANTITY = 11;
        public final static int INCOMPLETE_QUOTE = 12;
        public final static int TWO_SIDED_QUOTE_REQUIRED = 13;
        public final static int INVALID_SIDE = 14;
        public final static int INVALID_TRADING_SESSION = 15;
        public final static int EXCHANGE_OR_BROKER_OPTION = 16;
        public final static int UNSPECIFIED_REASON = 99;
    }

    /**
     * Possible values for LastMsgSeqNumProcessed(369)
     */
    public final static class LastMsgSeqNumProcessed {
       public final static String TAGNAME="LastMsgSeqNumProcessed";
       public final static int TAGNUMBER=369;
    }

    /**
     * Possible values for OnBehalfOfSendingTime(370)
     */
    public final static class OnBehalfOfSendingTime {
       public final static String TAGNAME="OnBehalfOfSendingTime";
       public final static int TAGNUMBER=370;
    }

    /**
     * Possible values for RefMsgType(372)
     */
    public final static class RefMsgType {
       public final static String TAGNAME="RefMsgType";
       public final static int TAGNUMBER=372;
    }

    /**
     * Possible values for ContraBroker(375)
     */
    public final static class ContraBroker {
       public final static String TAGNAME="ContraBroker";
       public final static int TAGNUMBER=375;
    }

    /**
     * ComplianceID (376)
     */
    public final static class ComplianceID
    {
        public final static String TAGNAME="ComplianceID";
        public final static int TAGNUMBER=376;
    }

    /**
     * SolicitedFlag (377)
     */
    public final static class SolicitedFlag
    {
        public final static String TAGNAME="SolicitedFlag";
        public final static int TAGNUMBER=377;
    }

    /**
     * ExecRestatementReason (378)
     */
    public final static class ExecRestatementReason
    {
        public final static String TAGNAME="ExecRestatementReason";
        public final static int TAGNUMBER=378;
            public final static int BROKER_OPTION = 4 ;
    }

    /**
     * BusinessRejectRefID (379)
     */
    public final static class BusinessRejectRefID
    {
        public final static String TAGNAME="BusinessRejectRefID";
        public final static int TAGNUMBER=379;
    }

   /**
    * BusinessRejectReason (380) values
    */
    public final static class BusinessRejectReason {
       public final static String TAGNAME="BusinessRejectReason";
       public final static int TAGNUMBER=380;

       public final static int OTHER = 0;
       public final static int UNKNOWN_ID = 1;
       public final static int UNKNOWN_SECURITY = 2;
       public final static int UNSUPPORTED_MESSAGE_TYPE = 3;
       public final static int APPLICATION_NOT_AVAILABLE = 4;
       public final static int CONDITIONALLY_REQUIRED_FIELD_MISSING = 5;
    }

    /**
     * GrossTradeAmt(381)
     */
    public final static class GrossTradeAmt {
       public final static String TAGNAME="GrossTradeAmt";
       public final static int TAGNUMBER=381;
    }

    /**
     * NoContraBrokers(382)
     */
    public final static class NoContraBrokers {
       public final static String TAGNAME="NoContraBrokers";
       public final static int TAGNUMBER=382;
    }

    /**
     * MaxMessageSize(383)
     */
    public final static class MaxMessageSize {
       public final static String TAGNAME="MaxMessageSize";
       public final static int TAGNUMBER=383;
    }

    /**
     * NoMsgTypes(384)
     */
    public final static class NoMsgTypes {
       public final static String TAGNAME="NoMsgTypes";
       public final static int TAGNUMBER=384;
    }

    /**
     * MsgDirection(385)
     */
    public final static class MsgDirection {
       public final static String TAGNAME="MsgDirection";
       public final static int TAGNUMBER=385;
    }

    /**
     * NoTradingSessions (386)
     */
    public final static class NoTradingSessions
    {
        public final static String TAGNAME="NoTradingSessions";
        public final static int TAGNUMBER=386;
    }

    /**
     * TotalVolumeTraded (387)
     */
    public final static class TotalVolumeTraded
    {
        public final static String TAGNAME="TotalVolumeTraded";
        public final static int TAGNUMBER=387;
    }

   /**
    * DiscretionInst(388) values
    */
   public final static class DiscretionInst {
       public final static String TAGNAME="DiscretionInst";
       public final static int TAGNUMBER=388;
       public final static String DISPLAY_PRICE = "0";
       public final static String MARKET_PRICE = "1";
       public final static String PRIMARY_PRICE = "2";
       public final static String LOCAL_PRIMARY_PRICE = "3";
       public final static String MIDPOINT_PRICE = "4";
       public final static String LAST_TRADE_PRICE = "5";
   }

    /**
     * DiscretionOffset (389)
     */
    public final static class DiscretionOffset
    {
        public final static String TAGNAME="DiscretionOffset";
        public final static int TAGNUMBER=389;
    }

    /**
     * DayOrderQty (424)
     */
    public final static class DayOrderQty
    {
        public final static String TAGNAME="DayOrderQty";
        public final static int TAGNUMBER=424;
    }

    /**
     * DayCumQty (425)
     */
    public final static class DayCumQty
    {
        public final static String TAGNAME="DayCumQty";
        public final static int TAGNUMBER=425;
    }

    /**
     * DayAvgPx (426)
     */
    public final static class DayAvgPx
    {
        public final static String TAGNAME="DayAvgPx";
        public final static int TAGNUMBER=426;
    }

    /**
     * GTBookingInst (427)
     */
    public final static class GTBookingInst
    {
        public final static String TAGNAME="GTBookingInst";
        public final static int TAGNUMBER=427;
    }

    /**
     * ExpireDate (432)
     */
    public final static class ExpireDate
    {
        public final static String TAGNAME="ExpireDate";
        public final static int TAGNUMBER=432;
    }

    /**
     *  CxlRejResponseTo(434) values
     */
     public final static class CxlRejResponseTo
     {

      public final static String TAGNAME="CxlRejResponseTo";
      public final static int TAGNUMBER=434;
      public final static String ORDER_CANCEL_REQUEST="1";
      public final static String ORDER_CANCEL_REPLACE_REQUEST="2";
     }

    /**
     * Possible values for ContraTradeQty(437)
     */
    public final static class ContraTradeQty {
       public final static String TAGNAME="ContraTradeQty";
       public final static int TAGNUMBER=437;
    }

    /**
     * Possible values for ContraTradeTime(438)
     */
    public final static class ContraTradeTime {
       public final static String TAGNAME="ContraTradeTime";
       public final static int TAGNUMBER=438;
    }

    /**
     * Possible values for ClearingFirm(439)
     */
    public final static class ClearingFirm {
       public final static String TAGNAME="ClearingFirm";
       public final static int TAGNUMBER=439;
    }

    /**
     * Possible values for ClearingAccount(440)
     */
    public final static class ClearingAccount {
       public final static String TAGNAME="ClearingAccount";
       public final static int TAGNUMBER=440;
    }

    /**
     *  MultiLegReportingType(442) values
     */
     public final static class MultiLegReportingType
     {

      public final static String TAGNAME="MultiLegReportingType";
      public final static int TAGNUMBER=442;
      public final static String SINGLE_SECURITY="1";
      public final static String INDIVIDUAL_LEG_OF_MULTILEG="2";
      public final static String MULTILEG_SECURITY="3";
     }


     /**
      * User Defined Tag for MDScope
      *
      * To be added to FIX 4.3
      */
      public final static class MDScope
      {
      public final static String TAGNAME="MDScope";
      public final static int TAGNUMBER=Integer.parseInt(FixUtilUserDefinedTagConstants.MD_SCOPE);

      public final static String LOCAL = "1";
      public final static String NATIONAL = "2";
      public final static String GLOBAL = "3";

      }

    /**
     * User Defined Tag for SecondaryClOrdID
     */
    public final static class SecondaryClOrdID {
      public final static String TAGNAME = "SecondaryClOrdID";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.SECONDARY_CLORDID);
      public final static String STAGNUMBER = FixUtilUserDefinedTagConstants.SECONDARY_CLORDID;
    }

    /**
     *
     */
    public final static class LastBustShares {
      public final static String TAGNAME = "LastBustShares";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.LAST_BUST_SHARES);
    }

     /**
     * User defined tag NBBO Price Protection Scope 9369
     */
    public final static class PriceProtectionScope {
      public final static String TAGNAME = "PriceProtectionScope";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.PRICE_PROTECTION_SCOPE);
      public final static String NONE = "0";
      public final static String LOCAL = "1";
      public final static String NATIONAL = "2";

    }

     /**
     * User defined tag Multi Leg Position Effects 9370
     */
    public final static class MultiLegPositionEffects {
      public final static String TAGNAME = "MultiLegPositionEffects";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.MULTI_LEG_POSITION_EFFECTS);
    }

     /**
     * User defined tag Multi Leg Position Effects 9371
     */
    public final static class MultiLegCoveredOrUncovered {
      public final static String TAGNAME = "MultiLegCoveredOrUncovered";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.MULTI_LEG_COVERED_OR_UNCOVERED);
    }

     /**
     * User defined tag Multi Leg Position Effects 9372
     */
    public final static class MultiLegStockClearingFirm {
      public final static String TAGNAME = "MultiLegStockClearingFirm";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.MULTI_LEG_STOCK_CLEARING_FIRM);
    }

   /**
    * User defined tag Multi Leg Price Per Leg 9379
    */
   public final static class MultiLegPricePerLeg {
     public final static String TAGNAME = "MultiLegPricePerLeg";
     public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.MULTI_LEG_PRICE_PER_LEG);
   }


      /**
      * User defined tag Application Queue Depth 6699
      */
     public final static class ApplicationQueueDepth {
       public final static String TAGNAME = "ApplicationQueueDepth";
       public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.APPLICATION_QUEUE_DEPTH);
       public final static int QUEUE_THRESHOLD = 0;
     }

    /** User Defined Field SubscriptionRequestType 9463.
     * This UDF unfortunately has the exact same name as standard tag 263. For
     * clarity in our code, we prepend "CBOE" to the class name to distinguish
     * the CBOE UDF from the standard tag.
     * Values should be compatible with standard tag 263. We create private
     * values starting at 500, far beyond the likely range of values for
     * standard tag 263.
     */
    public final static class CBOESubscriptionRequestType
    {
       public final static String TAGNAME="CBOESubscriptionRequestType";
       public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.SUBSCRIPTION_REQUEST_TYPE);

       // CBOE extensions in CBOE-defined User Defined Field 9463
       public final static String AUCTION_SUBSCRIBE = "500";
       public final static String AUCTION_UNSUBSCRIBE = "501";
    }

     /**
     * User defined tag Order Originator Type 9465
     */
    public final static class OrderOriginator {
      public final static String TAGNAME = "OrderOriginator";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.ORDER_ORIGINATOR);
    }

     /**
     * User defined tag Cancel ID 9468
     */
    public final static class UserAssignedCancelID {
      public final static String TAGNAME = "UserAssignedCancelID";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.USERASSIGNED_CANCELID);
    }

     /**
     * User defined tag Extended Price Type 9469
     */
    public final static class ExtendedPriceType {
      public final static String TAGNAME = "ExtendedPriceType";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.EXTENDED_PRICE_TYPE);
      public final static String CABINET = "4";
    }
  /**
     * User defined tag Quote Status Request Type 5349
     */
    public final static class QuoteStatusRequestType {
      public final static String TAGNAME = "QuoteStatusRequestType";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.QUOTE_STATUS_REQUEST_TYPE);
      public final static String TradeNotification = "0";
    }

    /** User defined tag PIP Management Type 9743 */
    public final static class PIPManagementType
    {
        public final static String TAGNAME = "PIPManagementType";
        public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.PIP_MANAGEMENT_TYPE);
        public final static String AUCTION_SOLICITATION = "1";
    }

   /**
         * User defined tag UDF Indicator
   */
        public final static class UDFSupportIndicator  {
          public final static String TAGNAME = "UDFSupportIndicator";
          public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.UDF_SUPPORT_INDICATOR);
          public final static String NOT_SUPPORTED = "0";
          public final static String SUPPORTS_UDF_ONLY_IN_MESSAGES = "1";
          public final static String SUPPORTS_UDF_IN_REPEATING_GROUP_IN_ALL_MESSAGES = "2";
          public final static String SUPPORTS_UDF_IN_REPEATING_GROUP_IN_MASS_QUOTE = "3";
          public final static String SUPPORTS_UDF_IN_REPEATING_GROUP_IN_MARKET_DATA = "4";
        }

    /**
          * User defined tag ClordId Format
    */
         public final static class ClOrdIdFormat{
           public final static String TAGNAME = "ClOrdIdFormat";
           public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.CLORDID_FORMAT);
           public final static String SUPPORTS_LESS_THAN_3_CHAR_BRANCH_ID = "1";
         }

    public final static class ParentClOrdId{
      public final static String TAGNAME = "ParentClOrdId";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.PARENT_CLORDID);
      public final static String STAGNUMBER = FixUtilUserDefinedTagConstants.PARENT_CLORDID;
    }

        public final static class STOCK_FIRM_NAME  {
          public final static String TAGNAME = "STOCK_FIRM_NAME";
          public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.STOCK_FIRM_NAME);
        }

    public final static class STOCK_FIRM_NAME_KEY  {
      public final static String TAGNAME = "STOCK_FIRM_NAME_KEY";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.STOCK_FIRM_NAME_KEY);
    }

    /**
     * Market Alert Types
     */
    public final static class MarketAlertTypes {
        public final static String P_ORDER_PARTIAL_TRADED = "P_ORDER_PARTIAL_TRADED";
    }

    public final static class ENHANCED_CXL_RE_IND {
        public static final String TAGNAME = "ENHANCED_CXL_RE_IND";
        public static final int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.ENHANCED_CXL_RE_IND);
        public final static String NOT_SUPPORTED = "0";
        public final static String SUPPORTED = "1";
    }
    
    
    /**
     * User defined tag TRADING_GROUP_STATUS_SUBSCRIPTION_REQUEST_TYPE 9263
     */
    public final static class TradingGroupStatusSubscriptionRequestType{
      public final static String TAGNAME = "TradingGroupStatusSubscriptionRequestType";
      public final static int TAGNUMBER = Integer.parseInt(FixUtilUserDefinedTagConstants.TRADING_GROUP_STATUS_SUBSCRIPTION_REQUEST_TYPE);
      public final static String SUBSCRIBE_GROUP = "0";
      public final static String UNSUBSCRIBE_GROUP = "1";
      public final static String SUBSCRIBE_CLASS = "2";
      public final static String UNSUBSCRIBE_CLASS = "3";
    }
    
    public final static class CLIENT_TYPES
    {
        public static String CLIENT_TYPE="CLIENT_TYPE";
        public static String CAS="cas";
        public static String FIXCAS="fixcas";
        public static String CFIX="cfix";
        public static String MDCAS="mdcas";
        public static String MDX="mdx";
        public static String SACAS="sacas";
    }

}
