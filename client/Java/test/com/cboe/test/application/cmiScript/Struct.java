package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.AlertHdrStruct;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketBestStruct;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiIntermarketMessages.FillRejectStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderCancelRequestStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderDetailStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderBookDetailPriceStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderReminderStruct;
import com.cboe.idl.cmiIntermarketMessages.PreOpeningIndicationPriceStruct;
import com.cboe.idl.cmiIntermarketMessages.PreOpeningResponsePriceStruct;
import com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;
import com.cboe.idl.cmiMarketData.BookDepthUpdatePriceStruct;
import com.cboe.idl.cmiMarketData.BookDepthUpdateStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.LastSaleStructV4;
import com.cboe.idl.cmiMarketData.MarketDataDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailEntryStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryDetailStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryEntryStruct;
import com.cboe.idl.cmiMarketData.MarketDataHistoryStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStruct;
import com.cboe.idl.cmiMarketData.MarketVolumeStructV4;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.NBBOStructV4;
import com.cboe.idl.cmiMarketData.OrderBookPriceStruct;
import com.cboe.idl.cmiMarketData.OrderBookPriceStructV2;
import com.cboe.idl.cmiMarketData.OrderBookPriceViewStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.RecapStructV4;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiMarketData.TickerStructV4;
import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.idl.cmiOrder.AuctionSubscriptionResultStruct;
import com.cboe.idl.cmiOrder.BustReinstateReportStruct;
import com.cboe.idl.cmiOrder.BustReportStruct;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.ContraPartyStruct;
import com.cboe.idl.cmiOrder.CrossOrderStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStructV2;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderEntryStruct;
import com.cboe.idl.cmiOrder.LightOrderResultStruct;
import com.cboe.idl.cmiOrder.OrderBustReinstateReportStruct;
import com.cboe.idl.cmiOrder.OrderBustReportStruct;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderEntryStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderResultStruct;
import com.cboe.idl.cmiOrder.OrderResultStructV2;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiOrder.PendingOrderStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV3;
import com.cboe.idl.cmiQuote.LockNotificationStruct;
import com.cboe.idl.cmiQuote.QuoteBustReportStruct;
import com.cboe.idl.cmiQuote.QuoteCancelReportStruct;
import com.cboe.idl.cmiQuote.QuoteDeleteReportStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStructV3;
import com.cboe.idl.cmiQuote.QuoteEntryStructV4;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.RFQEntryStruct;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiQuote.UserQuoteRiskManagementProfileStruct;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStateStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiTrade.ExternalAtomicTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalAtomicTradeResultStruct;
import com.cboe.idl.cmiTrade.ExternalBustTradeStruct;
import com.cboe.idl.cmiTrade.ExternalTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalTradeReportStruct;
import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiTraderActivity.ActivityFieldStruct;
import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;
import com.cboe.idl.cmiTraderActivity.ActivityRecordStruct;
import com.cboe.idl.cmiUser.AccountStruct;
import com.cboe.idl.cmiUser.DpmStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUser.PreferenceStruct;
import com.cboe.idl.cmiUser.ProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.KeyDescriptionStruct;
import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.cmiUtil.OperationResultStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.TimeStruct;

public class Struct
{
    private static final String EMPTY_String = "";
    private static final int INDEX_FIRST_KEY = 4; // Start of key value pairs for makeStruct

    private EngineAccess engineAccess;

    // PriceStruct.fraction has range 0 ... 999,999,999. The highest digit
    // is in position 100,000,000.
    private static final int FRACTION_TOP_DIGIT = 100000000;

    public Struct(EngineAccess ea)
    {
        engineAccess = ea;
    }

    /** Create an object according to command-line specification
     * @param command "struct" structType varName = key value [key value]...
     * @return Object as specified, or null if invalid name
     */
    public Object makeStruct(String command[])
    {
        if (command.length < 4)
        {
            Log.message("Ignoring empty " + command[0] + " command");
            return null;
        }
        String t = command[1];
        if (t.equalsIgnoreCase("UserLogonStruct"))
        {
            return makeUserLogonStruct(command);
        }
        if (t.equalsIgnoreCase("AdminStruct"))
        {
            return makeAdminStruct(command);
        }
        if (t.equalsIgnoreCase("AuctionTypeSequence"))
        {
            return makeAuctionTypeSequence(command);
        }
        if (t.equalsIgnoreCase("CancelRequestStruct"))
        {
            return makeCancelRequestStruct(command);
        }
        if (t.equalsIgnoreCase("ClassKeySequence"))
        {
            return makeClassKeySequence(command);
        }
        if (t.equalsIgnoreCase("DateTimeStruct"))
        {
            // Input format is one value, not lots of fields. 
            return makeDateTimeStruct(command[INDEX_FIRST_KEY]);
        }
        if (t.equalsIgnoreCase("ExchangeAcronymStruct"))
        {
            return makeExchangeAcronymStruct(command);
        }
        if (t.equalsIgnoreCase("ExchangeFirmStruct"))
        {
            return makeExchangeFirmStruct(command);
        }
        if (t.equalsIgnoreCase("ExchangeSequence"))
        {
            return makeExchangeSequence(command);
        }
        if (t.equalsIgnoreCase("ExternalAtomicTradeEntryStruct"))
        {
            return makeExternalAtomicTradeEntryStruct(command);
        }
        if (t.equalsIgnoreCase("ExternalAtomicTradeEntryStructSequence"))
        {
            return makeExternalAtomicTradeEntryStructSequence(command);
        }
        if (t.equalsIgnoreCase("ExternalBustTradeStruct"))
        {
            return makeExternalBustTradeStruct(command);
        }
        if (t.equalsIgnoreCase("ExternalBustTradeStructSequence"))
        {
            return makeExternalBustTradeStructSequence(command);
        }
        if (t.equalsIgnoreCase("ExternalTradeEntryStruct"))
        {
            return makeExternalTradeEntryStruct(command);
        }
        if (t.equalsIgnoreCase("FloorTradeEntryStruct"))
        {
            return makeFloorTradeEntryStruct(command);
        }
        if (t.equalsIgnoreCase("LegOrderEntryStruct"))
        {
            return makeLegOrderEntryStruct(command);
        }
        if (t.equalsIgnoreCase("LegOrderEntryStructSequence"))
        {
            return makeLegOrderEntryStructSequence(command);
        }
        if (t.equalsIgnoreCase("LightOrderEntryStruct"))
        {
            return makeLightOrderEntryStruct(command);
        }
        if (t.equalsIgnoreCase("MessageStruct"))
        {
            return makeMessageStruct(command);
        }
        if (t.equalsIgnoreCase("OrderContingencyStruct"))
        {
            return makeOrderContingencyStruct(command);
        }
        if (t.equalsIgnoreCase("OrderEntryStruct"))
        {
            return makeOrderEntryStruct(command);
        }
        if (t.equalsIgnoreCase("OrderIdStruct"))
        {
            return makeOrderIdStruct(command);
        }
        if (t.equalsIgnoreCase("OriginTypeSequence"))
        {
            return makeOriginTypeSequence(command);
        }
        if (t.equalsIgnoreCase("PreferenceStruct"))
        {
            return makePreferenceStruct(command);
        }
        if (t.equalsIgnoreCase("PreferenceStructSequence"))
        {
            return makePreferenceStructSequence(command);
        }
        if (t.equalsIgnoreCase("PreOpeningIndicationPriceStruct"))
        {
            return makePreOpeningIndicationPriceStruct(command);
        }
        if (t.equalsIgnoreCase("PreOpeningResponsePriceStruct"))
        {
            return makePreOpeningResponsePriceStruct(command);
        }
        if (t.equalsIgnoreCase("PreOpeningResponsePriceStructSequence"))
        {
            return makePreOpeningResponsePriceStructSequence(command);
        }
        if (t.equalsIgnoreCase("ProductGroupSequence"))
        {
            return makeProductGroupSequence(command);
        }
        if (t.equalsIgnoreCase("ProductNameStruct"))
        {
            return makeProductNameStruct(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStruct"))
        {
            return makeQuoteEntryStruct(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStructV3"))
        {
            return makeQuoteEntryStructV3(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStructV4"))
        {
            return makeQuoteEntryStructV4(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStructSequence"))
        {
            return makeQuoteEntryStructSequence(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStructV3Sequence"))
        {
            return makeQuoteEntryStructV3Sequence(command);
        }
        if (t.equalsIgnoreCase("QuoteEntryStructV4Sequence"))
        {
            return makeQuoteEntryStructV4Sequence(command);
        }
        if (t.equalsIgnoreCase("QuoteRiskManagementProfileStruct"))
        {
            return makeQuoteRiskManagementProfileStruct(command);
        }
        if (t.equalsIgnoreCase("RFQEntryStruct"))
        {
            return makeRFQEntryStruct(command);
        }
        if (t.equalsIgnoreCase("StrategyLegStruct"))
        {
            return makeStrategyLegStruct(command);
        }
        if (t.equalsIgnoreCase("StrategyRequestStruct"))
        {
            return makeStrategyRequestStruct(command);
        }
        if (t.equalsIgnoreCase("TradingSessionNameSequence"))
        {
            return makeTradingSessionNameSequence(command);
        }
        // ==== todo: more structure types

        // Unrecognized structure name
        Log.message("Unknown type of struct:" + t);
        return null;
    }

    // Copied from UserAccess class because methods here are static and method
    // in UserAccess is object-based.
    // todo ==== see about removing duplication
    private static String[] getParameters(String parmName[], String command[], int startIndex)
    {
        String value[] = new String[parmName.length];
        int commandIndex = startIndex;

        while (commandIndex < command.length)
        {
            if (commandIndex == command.length-1)
            {
                Log.message("No value supplied for parameter:" + command[commandIndex]);
                return null;
            }
            boolean found = false;
            for (int nameIndex = 0; nameIndex < parmName.length && !found; ++ nameIndex)
            {
                if (command[commandIndex].equalsIgnoreCase(parmName[nameIndex]))
                {
                    value[nameIndex] = command[commandIndex+1];
                    found = true;
                }
            }
            if (found)
            {
                // advance over name and value, ready for next pair
                commandIndex += 2;
            }
            else
            {
                Log.message("Unknown parameter:" + command[commandIndex]);
                return null;
            }
        }
        return value;
    }

    private static char[] makeListOfChars(String input[])
    {
        int count = input.length - INDEX_FIRST_KEY;
        char result[] = new char[count];
        int fromInput = INDEX_FIRST_KEY;
        int toOutput = 0;
        while (fromInput < input.length)
        {
            if (input[fromInput] == null
            ||  input[fromInput].equals(EMPTY_String))
            {
                Log.message("Empty value in list");
                return null;
            }
            result[toOutput++] = input[fromInput++].charAt(0);
        }
        return result;
    }

    private static short[] makeListOfShorts(String input[])
    {
        int count = input.length - INDEX_FIRST_KEY;
        short result[] = new short[count];
        int fromInput = INDEX_FIRST_KEY;
        int toOutput = 0;
        while (fromInput < input.length)
        {
            if (input[fromInput] == null
            ||  input[fromInput].equals(EMPTY_String))
            {
                Log.message("Empty value in list");
                return null;
            }
            result[toOutput++] = Short.parseShort(input[fromInput++]);
        }
        return result;
    }

    private static int[] makeListOfInts(String input[])
    {
        int count = input.length - INDEX_FIRST_KEY;
        int result[] = new int[count];
        int fromInput = INDEX_FIRST_KEY;
        int toOutput = 0;
        while (fromInput < input.length)
        {
            if (input[fromInput] == null
            ||  input[fromInput].equals(EMPTY_String))
            {
                Log.message("Empty value in list");
                return null;
            }
            result[toOutput++] = Integer.parseInt(input[fromInput++]);
        }
        return result;
    }

    private static String[] makeListOfStrings(String input[])
    {
        int count = input.length - INDEX_FIRST_KEY;
        String result[] = new String[count];
        int fromInput = INDEX_FIRST_KEY;
        int toOutput = 0;
        while (fromInput < input.length)
        {
            result[toOutput++] = input[fromInput++];
        }
        return result;
    }

// Make StringBuilder out of sequences of IDL-primitive types

    public static StringBuilder toString(byte bseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(bseq.length).append("{");
        for (byte b : bseq)
        {
            sb.append(' ').append(b);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(short sseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(sseq.length).append("{");
        for (short s : sseq)
        {
            sb.append(' ').append(s);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(int iseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(iseq.length).append("{");
        for (int i : iseq)
        {
            sb.append(' ').append(i);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(long lseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(lseq.length).append("{");
        for (long l : lseq)
        {
            sb.append(' ').append(l);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(String strseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(strseq.length).append("{");
        int index = 0;
        for (String s : strseq)
        {
            sb.append("\n[").append(index).append("] ").append(s);
            ++index;
        }
        sb.append(" }");
        return sb;
    }

// cmiAdmin

    public static StringBuilder toString(HeartBeatStruct hb)
    {
        StringBuilder sb = toString(hb.currentDate);
        sb.append('_').append(toString(hb.currentTime))
          .append(" request:").append(hb.requestID)
          .append(" pulseInterval:").append(hb.pulseInterval);
        return sb;
    }

    public static StringBuilder toString(MessageStruct m)
    {
        StringBuilder result = toString(m.timeStamp);
        result.append(" messageKey:").append(m.messageKey)
           .append(" originalMessageKey:").append(m.originalMessageKey)
           .append(" sender:").append(m.sender)
           .append(" subject:").append(m.subject)
           .append(" replyRequested:").append(m.replyRequested)
           .append(" messageText:").append(m.messageText);
        return result;
    }

    private MessageStruct makeMessageStruct(String command[])
    {
        String names[] = { "messageKey", "originalMessageKey", "sender",
                "subject", "replyRequested", "messageText" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        MessageStruct m = new MessageStruct();
        if (values[0] == null)
        {
            Log.message("Missing messageKey");
            return null;
        }
        m.messageKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing originalMessageKey");
            return null;
        }
        m.originalMessageKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing sender");
            return null;
        }
        m.sender = values[2];

        if (values[3] == null)
        {
            Log.message("Missing subject");
            return null;
        }
        m.subject = values[3];

        if (values[4] == null)
        {
            Log.message("Missing replyRequested");
            return null;
        }
        m.replyRequested = CommandLine.booleanValue(values[4]);

        if (values[5] == null)
        {
            Log.message("Missing messageText");
            return null;
        }
        m.messageText = values[4];

        return m;
    }

    public static StringBuilder toString(MessageStruct mseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(mseq.length).append("{");
        int index = 0;
        for (MessageStruct m : mseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(m));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiIntermarketMessages

    public static StringBuilder toString(AdminStruct a)
    {
        StringBuilder result = new StringBuilder();
        result.append("messageStruct:{").append(toString(a.messageStruct))
                .append('}')
              .append(" userAssignedId:").append(a.userAssignedId)
              .append(" productKey:").append(a.productKey)
              .append(" sourceExchange:").append(a.sourceExchange)
              .append(" destinationExchange:").append(a.destinationExchange);
        return result;
    }

    private AdminStruct makeAdminStruct(String command[])
    {
        String names[] = { "messageStruct", "userAssignedId", "productKey",
                "sourceExchange", "destinationExchange" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        AdminStruct a = new AdminStruct();
        if (values[0] == null)
        {
            Log.message("Missing messageStruct");
            return null;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof MessageStruct))
        {
            Log.message("Not a MessageStruct:" + objName);
            return null;
        }
        a.messageStruct = (MessageStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing userAssignedId");
            return null;
        }
        a.userAssignedId = values[1];

        if (values[2] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        a.productKey = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing sourceExchange");
            return null;
        }
        a.sourceExchange = values[3];

        if (values[4] == null)
        {
            Log.message("Missing destinationExchange");
            return null;
        }
        a.destinationExchange = values[4];

        return a;
    }

    public static StringBuilder toString(AlertHdrStruct ah)
    {
        StringBuilder result = new StringBuilder();
        result.append("alertId:").append(toString(ah.alertId))
              .append(" alertCreationTime:")
                .append(toString(ah.alertCreationTime))
              .append(" alertType:").append(ah.alertType)
              .append(" sessionName:").append(ah.sessionName)
              .append(" hdrExtensions:").append(ah.hdrExtensions);
        return result;
    }

    public static StringBuilder toString(AdminStruct aseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(aseq.length).append("{");
        int index = 0;
        for (AdminStruct a : aseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(a));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(BookDepthDetailedStruct bdd)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(bdd.productKeys))
                .append('}')
              .append(" sessionName:").append(bdd.sessionName)
              .append(" buyOrdersAtDifferentPrice:")
                .append(toString(bdd.buyOrdersAtDifferentPrice))
              .append(" sellOrdersAtDifferentPrice:")
                .append(toString(bdd.sellOrdersAtDifferentPrice))
              .append(" transactionSequenceNumber:")
                .append(bdd.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(CurrentIntermarketBestStruct cib)
    {
        StringBuilder result = new StringBuilder();
        result.append("exchange:").append(cib.exchange)
              .append(" marketCondition:").append(cib.marketCondition)
              .append(" bidPrice:").append(toString(cib.bidPrice))
              .append(" bidVolume:").append(cib.bidVolume)
              .append(" askPrice:").append(toString(cib.askPrice))
              .append(" askVolume:").append(cib.askVolume)
              .append(" sentTime:").append(toString(cib.sentTime));
        return result;
    }

    public static StringBuilder toString(CurrentIntermarketBestStruct cibseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(cibseq.length).append("{");
        int index = 0;
        for (CurrentIntermarketBestStruct cib : cibseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cib));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CurrentIntermarketStruct ci)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(ci.productKeys))
                .append('}')
              .append(" otherMarketsBest:")
                .append(toString(ci.otherMarketsBest));
        return result;
    }

    public static StringBuilder toString(CurrentIntermarketStruct ciseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(ciseq.length).append("{");
        int index = 0;
        for (CurrentIntermarketStruct ci : ciseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ci));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ExchangeMarketStruct em)
    {
        StringBuilder result = new StringBuilder();
        result.append("marketInfoType:").append(em.marketInfoType)
              .append(" bestBidPrice:").append(toString(em.bestBidPrice))
              .append(" bidExchangeVolumes:")
                .append(toString(em.bidExchangeVolumes))
              .append(" bestAskPrice:").append(toString(em.bestAskPrice))
              .append(" askExchangeVolumes:")
                .append(toString(em.askExchangeVolumes));
        return result;
    }

    public static StringBuilder toString(ExchangeMarketStruct emseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(emseq.length).append("{");
        int index = 0;
        for (ExchangeMarketStruct em : emseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(em));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(FillRejectStruct fr)
    {
        StringBuilder result = new StringBuilder();
        result.append("tradeId:").append(toString(fr.tradeId))
              .append(" order:{").append(toString(fr.order)).append('}')
              .append(" transactionSequenceNumber:")
                .append(fr.transactionSequenceNumber)
              .append(" rejectReason:").append(fr.rejectReason)
              .append(" extensions:").append(fr.extensions)
              .append(" text:").append(fr.text);
        return result;
    }

    public static StringBuilder toString(FillRejectStruct frseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(frseq.length).append("{");
        int index = 0;
        for (FillRejectStruct fr : frseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(fr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(HeldOrderCancelReportStruct hocr)
    {
        StringBuilder result = new StringBuilder();
        result.append("heldOrderDetail:{")
                .append(toString(hocr.heldOrderDetail)).append('}')
              .append(" cancelReqId:").append(toString(hocr.cancelReqId))
              .append(" cancelReport:{").append(toString(hocr.cancelReport))
                .append('}');
        return result;
    }

    public static StringBuilder toString(HeldOrderCancelRequestStruct hocr)
    {
        StringBuilder result = new StringBuilder();
        result.append("cancelReqId:").append(toString(hocr.cancelReqId))
              .append(" cancelRequest:{").append(toString(hocr.cancelRequest))
                .append('}');
        return result;
    }

    public static StringBuilder toString(HeldOrderDetailStruct hod)
    {
        StringBuilder result = new StringBuilder();
        result.append("productInformation:{")
                .append(toString(hod.productInformation)).append('}')
              .append(" statusChange:").append(hod.statusChange)
              .append(" heldOrder:{").append(toString(hod.heldOrder))
                .append('}');
        return result;
    }

    public static StringBuilder toString(HeldOrderDetailStruct hodseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(hodseq.length).append("{");
        int index = 0;
        for (HeldOrderDetailStruct hod : hodseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(hod));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(HeldOrderFilledReportStruct hofr)
    {
        StringBuilder result = new StringBuilder();
        result.append("heldOrderDetail:{")
                .append(toString(hofr.heldOrderDetail)).append('}')
              .append(" filledReport:").append(toString(hofr.filledReport));
        return result;
    }

    public static StringBuilder toString(HeldOrderStruct ho)
    {
        StringBuilder result = new StringBuilder();
        result.append("order:{").append(toString(ho.order)).append('}')
              .append(" currentMarketBest:")
                .append(toString(ho.currentMarketBest));
        return result;
    }

    public static StringBuilder toString(OrderBookStruct ob)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderId:{").append(toString(ob.orderId)).append('}')
              .append(" originalQuantity:").append(ob.originalQuantity)
              .append(" remainingQuantity:").append(ob.remainingQuantity)
              .append(" classKey:").append(ob.classKey)
              .append(" productKey:").append(ob.productKey)
              .append(" productType:").append(ob.productType)
              .append(" side:").append(ob.side)
              .append(" price:").append(toString(ob.price))
              .append(" timeInForce:").append(ob.timeInForce)
              .append(" receivedTime:").append(toString(ob.receivedTime))
              .append(" contingency:{").append(toString(ob.contingency))
                .append('}')
              .append(" orderOriginType:").append(ob.orderOriginType)
              .append(" state:").append(ob.state)
              .append(" orderNBBOProtectionType:")
                .append(ob.orderNBBOProtectionType)
              .append(" optionalData:").append(ob.optionalData)
              .append(" tradableType:").append(ob.tradableType);
        return result;
    }

    public static StringBuilder toString(OrderBookStruct obseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(obseq.length).append("{");
        int index = 0;
        for (OrderBookStruct ob : obseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ob));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderBookDetailPriceStruct obdp)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderInfo:").append(toString(obdp.orderInfo))
              .append(" price:").append(toString(obdp.price));
        return result;
    }

    public static StringBuilder toString(OrderBookDetailPriceStruct obdpseq[])
    {
        StringBuilder result = new StringBuilder(100);
        result.append(obdpseq.length).append("{");
        int index = 0;
        for (OrderBookDetailPriceStruct obdp : obdpseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(obdp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderFillRejectStruct ofr)
    {
        StringBuilder result = new StringBuilder();
        result.append("rejectedFillOrder:{")
                .append(toString(ofr.rejectedFillOrder)).append('}')
              .append(" fillRejectReports:")
                .append(toString(ofr.fillRejectReports));
        return result;
    }

    public static StringBuilder toString(OrderReminderStruct or)
    {
        StringBuilder result = new StringBuilder();
        result.append("reminderId:{").append(toString(or.reminderId))
                .append('}')
              .append(" reminderReason:").append(or.reminderReason)
              .append(" timeSent:").append(toString(or.timeSent));
        return result;
    }

    private static PreOpeningIndicationPriceStruct
            makePreOpeningIndicationPriceStruct(String command[])
    {
        String names[] = { "preOpenType", "preOpenOriginType",
                "lowOpeningPrice", "highOpeningPrice", "side",
                "principalQuantity" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        PreOpeningIndicationPriceStruct poip =
                new PreOpeningIndicationPriceStruct();

        if (values[0] == null)
        {
            Log.message("Missing preOepnType");
            return null;
        }
        poip.preOpenType = Short.parseShort(values[0]);

        if (values[1] == null || values[1].equals(EMPTY_String))
        {
            Log.message("Missing preOpenOriginType");
            return null;
        }
        poip.preOpenOriginType = values[1].charAt(0);

        if (values[2] == null)
        {
            Log.message("Missing lowOpeningPrice");
            return null;
        }
        poip.lowOpeningPrice = makePriceStruct(values[2]);
        if (poip.lowOpeningPrice == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[3] == null)
        {
            Log.message("Missing highOpeningPrice");
            return null;
        }
        poip.highOpeningPrice = makePriceStruct(values[3]);
        if (poip.highOpeningPrice == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[4] == null || values[4].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        poip.side = values[4].charAt(0);

        if (values[5] == null)
        {
            Log.message("Missing principalQuantity");
            return null;
        }
        poip.principalQuantity = Integer.parseInt(values[5]);

        return poip;
    }

    private static PreOpeningResponsePriceStruct
            makePreOpeningResponsePriceStruct(String command[])
    {
        String names[] = { "orderState", "side", "principalQuantity",
                "agencyQuantity", "responsePrice" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        PreOpeningResponsePriceStruct porp =
                new PreOpeningResponsePriceStruct();

        if (values[0] == null)
        {
            Log.message("Missing orderState");
            return null;
        }
        porp.orderState = Short.parseShort(values[0]);

        if (values[1] == null || values[1].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        porp.side = values[1].charAt(0);

        if (values[2] == null)
        {
            Log.message("Missing principalQuantity");
            return null;
        }
        porp.principalQuantity = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing agencyQuantity");
            return null;
        }
        porp.agencyQuantity = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing responsePrice");
            return null;
        }
        porp.responsePrice = makePriceStruct(values[4]);
        if (porp.responsePrice == null)
        {
            return null; // error already reported, leave now.
        }

        return porp;
    }

    private PreOpeningResponsePriceStruct[]
            makePreOpeningResponsePriceStructSequence(String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        PreOpeningResponsePriceStruct pseq[] = new PreOpeningResponsePriceStruct[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof PreOpeningResponsePriceStruct))
            {
                Log.message("Not a PreOpeningResponsePriceStruct:" + objName);
                return null;
            }
            pseq[seqIndex++] = (PreOpeningResponsePriceStruct) o;
        }
        return pseq;
    }

    public static StringBuilder toString(SatisfactionAlertStruct sa)
    {
        StringBuilder result = new StringBuilder();
        result.append("alertHdr:{").append(toString(sa.alertHdr)).append('}')
              .append(" tradedThroughquantity:")
                .append(sa.tradedThroughquantity)
              .append(" tradedThroughPrice:")
                .append(toString(sa.tradedThroughPrice))
              .append(" side:").append(sa.side)
              .append(" lastSale:{").append(toString(sa.lastSale)).append('}')
              .append(" tradedThroughOrders:")
                .append(toString(sa.tradedThroughOrders))
              .append(" extensions:").append(sa.extensions);
        return result;
    }

// cmiMarketData

    public static StringBuilder toString(BookDepthStruct bd)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(bd.productKeys))
                .append('}')
              .append(" sessionName:").append(bd.sessionName)
              .append(" buySideSequence:").append(toString(bd.buySideSequence))
              .append(" sellSideSequence:")
                .append(toString(bd.sellSideSequence))
              .append(" allPricesIncluded:").append(bd.allPricesIncluded)
              .append(" transactionSequenceNumber:")
                .append(bd.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(BookDepthStruct bdseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(bdseq.length).append("{");
        int index = 0;
        for (BookDepthStruct bd : bdseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(bd));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(BookDepthStructV2 bd)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(bd.productKeys))
                .append('}')
              .append(" sessionName:").append(bd.sessionName)
              .append(" buySideSequence:").append(toString(bd.buySideSequence))
              .append(" sellSideSequence:")
                .append(toString(bd.sellSideSequence))
              .append(" allPricesIncluded:").append(bd.allPricesIncluded)
              .append(" transactionSequenceNumber:")
                .append(bd.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(BookDepthUpdatePriceStruct bdup)
    {
        StringBuilder result = new StringBuilder();
        result.append("updateType:").append(bdup.updateType)
              .append(" price:").append(toString(bdup.price))
              .append(" totalVolume:").append(bdup.totalVolume)
              .append(" contingencyVolume:").append(bdup.contingencyVolume);
        return result;
    }

    public static StringBuilder toString(BookDepthUpdatePriceStruct bdupseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(bdupseq.length).append("{");
        int index = 0;
        for (BookDepthUpdatePriceStruct bdup : bdupseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(bdup));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(BookDepthUpdateStruct bdu)
    {
        StringBuilder result = new StringBuilder();
        result.append("sequenceNumber:").append(bdu.sequenceNumber)
              .append(" productKeys:{").append(toString(bdu.productKeys))
                .append('}')
              .append(" sessionName:").append(bdu.sessionName)
              .append(" buySideChanges:").append(toString(bdu.buySideChanges))
              .append(" sellSideChanges:").append(toString(bdu.sellSideChanges));
        return result;
    }

    public static StringBuilder toString(BookDepthUpdateStruct bduseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(bduseq.length).append("{");
        int index = 0;
        for (BookDepthUpdateStruct bdu : bduseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(bdu));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CurrentMarketStruct cm)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(cm.productKeys))
                .append('}')
              .append(" sessionName:").append(cm.sessionName)
              .append(" exchange:").append(cm.exchange)
              .append(" bidPrice:").append(toString(cm.bidPrice))
              .append(" bidSizeSequence:").append(toString(cm.bidSizeSequence))
              .append(" bidIsMarketBest:").append(cm.bidIsMarketBest)
              .append(" askPrice:").append(toString(cm.askPrice))
              .append(" askSizeSequence:").append(toString(cm.askSizeSequence))
              .append(" asksMarketBest:").append(cm.askIsMarketBest)
              .append(" sentTime:").append(toString(cm.sentTime))
              .append(" legalMarket:").append(cm.legalMarket);
        return result;
    }

    public static StringBuilder toString(CurrentMarketStruct cmseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cmseq.length).append("{");
        int index = 0;
        for (CurrentMarketStruct cm : cmseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cm));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CurrentMarketStructV4 cm)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(cm.classKey)
              .append(" productKey:").append(cm.productKey)
              .append(" productType:").append(cm.productType)
              .append(" exchange:").append(cm.exchange)
              .append(" sentTime:").append(cm.sentTime)
              .append(" currentMarketType:").append(cm.currentMarketType)
              .append(" bidPrice:").append(cm.bidPrice)
              .append(" bidTickDirection:").append(cm.bidTickDirection)
              .append(" bidSizeSequence:").append(toString(cm.bidSizeSequence))
              .append(" askPrice:").append(cm.askPrice)
              .append(" askSizeSequence:").append(toString(cm.askSizeSequence))
              .append(" marketIndicator:").append(cm.marketIndicator)
              .append(" productState:").append(cm.productState)
              .append(" priceScale:").append(cm.priceScale);
        return result;
    }

    public static StringBuilder toString(CurrentMarketStructV4 cmseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cmseq.length).append("{");
        int index = 0;
        for (CurrentMarketStructV4 cm : cmseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cm));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ExchangeIndicatorStruct ei)
    {
        StringBuilder result = new StringBuilder();
        result.append("exchange:").append(ei.exchange)
              .append(" marketCondition:").append(ei.marketCondition);
        return result;
    }

    public static StringBuilder toString(ExchangeIndicatorStruct eiseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(eiseq.length).append("{");
        int index = 0;
        for (ExchangeIndicatorStruct ei : eiseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ei));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ExchangeVolumeStruct ev)
    {
        StringBuilder result = new StringBuilder();
        result.append("exchange:").append(ev.exchange)
              .append(" volume:").append(ev.volume);
        return result;
    }

    public static StringBuilder toString(ExchangeVolumeStruct evseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(evseq.length).append("{");
        int index = 0;
        for (ExchangeVolumeStruct ev : evseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ev));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ExpectedOpeningPriceStruct eop)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(eop.productKeys))
                .append('}')
              .append(" sessionName:").append(eop.sessionName)
              .append(" eopType:").append(eop.eopType)
              .append(" expectedOpeningPrice:")
                .append(toString(eop.expectedOpeningPrice))
              .append(" imbalanceQuantity:").append(eop.imbalanceQuantity)
              .append(" legalMarket:").append(eop.legalMarket);
        return result;
    }

    public static StringBuilder toString(ExpectedOpeningPriceStruct eopseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(eopseq.length).append("{");
        int index = 0;
        for (ExpectedOpeningPriceStruct eop : eopseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(eop));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(LastSaleStructV4 lss)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(lss.classKey)
              .append(" productKey:").append(lss.productKey)
              .append(" productType:").append(lss.productType)
              .append(" exchange:").append(lss.exchange)
              .append(" sentTime:").append(lss.sentTime)
              .append(" priceScale:").append(lss.priceScale)
              .append(" lastSaleTime:").append(lss.lastSaleTime)
              .append(" lastSalePrice:").append(lss.lastSalePrice)
              .append(" lastSaleVolume:").append(lss.lastSaleVolume)
              .append(" totalVolume:").append(lss.totalVolume)
              .append(" tickDirection:").append(lss.tickDirection)
              .append(" netPriceChange:").append(lss.netPriceChange);
        return result;
    }

    public static StringBuilder toString(LastSaleStructV4 lssseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(lssseq.length).append("{");
        int index = 0;
        for (LastSaleStructV4 lss : lssseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(lss));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(MarketDataDetailStruct mdd)
    {
        StringBuilder result = new StringBuilder();
        result.append("overrideIndicator:").append(mdd.overrideIndicator)
              .append(" nbboAskPrice:").append(toString(mdd.nbboAskPrice))
              .append(" nbboAskExchanges:")
                .append(toString(mdd.nbboAskExchanges))
              .append(" nbboBidPrice:").append(toString(mdd.nbboBidPrice))
              .append(" nbboBidExchanges:")
                .append(toString(mdd.nbboBidExchanges))
              .append(" tradeThroughIndicator:")
                .append(mdd.tradeThroughIndicator)
              .append(" exchangeIndicators:")
                .append(toString(mdd.exchangeIndicators))
              .append(" bestPublishedBidPrice:")
                .append(toString(mdd.bestPublishedBidPrice))
              .append(" bestPublishedBidVolume:")
                .append(mdd.bestPublishedBidVolume)
              .append(" bestPublishedAskPrice:")
                .append(toString(mdd.bestPublishedAskPrice))
              .append(" bestPublishedAskVolume:")
                .append(mdd.bestPublishedAskVolume)
              .append(" brokers:").append(toString(mdd.brokers))
              .append(" contras:").append(toString(mdd.contras))
              .append(" extensions:").append(toString(mdd.extensions));
        return result;
    }

    public static StringBuilder toString(
            MarketDataHistoryDetailEntryStruct mdhde)
    {
        StringBuilder result = new StringBuilder();
        result.append("historyEntry:{").append(toString(mdhde.historyEntry))
                .append('}')
              .append(" detailData:{").append(toString(mdhde.detailData))
                .append('}');
        return result;
    }

    public static StringBuilder toString(
            MarketDataHistoryDetailEntryStruct mdhdeseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(mdhdeseq.length).append("{");
        int index = 0;
        for (MarketDataHistoryDetailEntryStruct mdhde : mdhdeseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(mdhde));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(MarketDataHistoryDetailStruct mdhd)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(mdhd.productKeys))
                .append('}')
              .append(" sessionName:").append(mdhd.sessionName)
              .append(" startTime:").append(toString(mdhd.startTime))
              .append(" endTime:").append(toString(mdhd.endTime))
              .append(" isOutOfSequence:").append(mdhd.isOutOfSequence)
              .append(" entries:").append(toString(mdhd.entries));
        return result;
    }

    public static StringBuilder toString(MarketDataHistoryEntryStruct mdhe)
    {
        StringBuilder result = new StringBuilder();
        result.append("entryType:").append(mdhe.entryType)
              .append(" source:").append(mdhe.source)
              .append(" reportTime:").append(toString(mdhe.reportTime))
              .append(" price:").append(toString(mdhe.price))
              .append(" quantity:").append(mdhe.quantity)
              .append(" sellerAcronym:").append(mdhe.sellerAcronym)
              .append(" buyerAcronym:").append(mdhe.buyerAcronym)
              .append(" bidSize:").append(mdhe.bidSize)
              .append(" bidPrice:").append(toString(mdhe.bidPrice))
              .append(" askSize:").append(mdhe.askSize)
              .append(" askPrice:").append(toString(mdhe.askPrice))
              .append(" underlyingLastSalePrice:")
                .append(toString(mdhe.underlyingLastSalePrice))
              .append(" eopType:").append(mdhe.eopType)
              .append(" marketCondition:").append(mdhe.marketCondition)
              .append(" optionalData:").append(mdhe.optionalData)
              .append(" exceptionCode:").append(mdhe.exceptionCode)
              .append(" physLocation:").append(mdhe.physLocation)
              .append(" prefix:").append(mdhe.prefix);
        return result;
    }

    public static StringBuilder toString(MarketDataHistoryEntryStruct mdheseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(mdheseq.length).append("{");
        int index = 0;
        for (MarketDataHistoryEntryStruct mdhe : mdheseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(mdhe));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(MarketDataHistoryStruct mdh)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(mdh.productKeys))
                .append('}')
              .append(" sessionName:").append(mdh.sessionName)
              .append(" startTime:").append(toString(mdh.startTime))
              .append(" endTime:").append(toString(mdh.endTime))
              .append(" entries:").append(toString(mdh.entries));
        return result;
    }

    public static StringBuilder toString(MarketVolumeStruct mv)
    {
        StringBuilder result = new StringBuilder();
        result.append("volumeType:").append(mv.volumeType)
              .append(" quantity:").append(mv.quantity)
              .append(" multipleParties:").append(mv.multipleParties);
        return result;
    }

    public static StringBuilder toString(MarketVolumeStruct mvseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(mvseq.length).append("{");
        int index = 0;
        for (MarketVolumeStruct mv : mvseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(mv));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(MarketVolumeStructV4 mv)
    {
        StringBuilder result = new StringBuilder();
        result.append("volumeType:").append(mv.volumeType)
              .append(" quantity:").append(mv.quantity)
              .append(" multipleParties:").append(mv.multipleParties);
        return result;
    }

    public static StringBuilder toString(MarketVolumeStructV4 mvseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(mvseq.length).append("{");
        int index = 0;
        for (MarketVolumeStructV4 mv : mvseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(mv));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(NBBOStruct nbbo)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(nbbo.productKeys))
                .append('}')
              .append(" sessionName:").append(nbbo.sessionName)
              .append(" bidPrice:").append(toString(nbbo.bidPrice))
              .append(" bidExchangeVolume:").append(toString(nbbo.bidExchangeVolume))
              .append(" askPrice:").append(toString(nbbo.askPrice))
              .append(" askExchangeVolume:").append(toString(nbbo.askExchangeVolume))
              .append(" sendTime:").append(toString(nbbo.sentTime));
        return result;
    }

    public static StringBuilder toString(NBBOStruct nbboseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(nbboseq.length).append("{");
        int index = 0;
        for (NBBOStruct nbbo : nbboseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(nbbo));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(NBBOStructV4 nbbo)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(nbbo.classKey)
              .append(" productKey:").append(nbbo.productKey)
              .append(" productType:").append(nbbo.productType)
              .append(" sentTime:").append(nbbo.sentTime)
              .append(" bidPrice:").append(nbbo.bidPrice)
              .append(" bidExchangeVolume:")
                .append(toString(nbbo.bidExchangeVolume))
              .append(" askPrice:").append(nbbo.askPrice)
              .append(" askExchangeVolume:")
                .append(toString(nbbo.askExchangeVolume))
              .append(" priceScale:").append(nbbo.priceScale);
        return result;
    }

    public static StringBuilder toString(NBBOStructV4 nbboseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(nbboseq.length).append("{");
        int index = 0;
        for (NBBOStructV4 nbbo : nbboseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(nbbo));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderBookPriceStruct obp)
    {
        StringBuilder result = new StringBuilder();
        result.append("price:").append(toString(obp.price))
              .append(" totalVolume:").append(obp.totalVolume)
              .append(" contingencyVolume:").append(obp.contingencyVolume);
        return result;
    }

    public static StringBuilder toString(OrderBookPriceStruct obpseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(obpseq.length).append("{");
        int index = 0;
        for (OrderBookPriceStruct obp : obpseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(obp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderBookPriceStructV2 obp)
    {
        StringBuilder result = new StringBuilder();
        result.append("price:").append(toString(obp.price))
              .append(" views:").append(toString(obp.views));
        return result;
    }

    public static StringBuilder toString(OrderBookPriceStructV2 obpseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(obpseq.length).append("{");
        int index = 0;
        for (OrderBookPriceStructV2 obp : obpseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(obp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderBookPriceViewStruct obpv)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderBookPriceViewType:")
                .append(obpv.orderBookPriceViewType)
              .append(" viewSequence:").append(toString(obpv.viewSequence));
        return result;
    }

    public static StringBuilder toString(OrderBookPriceViewStruct obpvseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(obpvseq.length).append("{");
        int index = 0;
        for (OrderBookPriceViewStruct obpv : obpvseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(obpv));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(RecapStruct r)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(r.productKeys))
                .append('}')
              .append(" sessionName:").append(r.sessionName)
              .append(" productInformation:{")
                .append(toString(r.productInformation)).append('}')
              .append(" lastSalePrice:").append(toString(r.lastSalePrice))
              .append(" tradeTime:").append(toString(r.tradeTime))
              .append(" lastSaleVolume:").append(r.lastSaleVolume)
              .append(" totalVolume:").append(r.totalVolume)
              .append(" tickDirection:").append(r.tickDirection)
              .append(" netChangeDirection:").append(r.netChangeDirection)
              .append(" bidDirection:").append(r.bidDirection)
              .append(" netChange:").append(toString(r.netChange))
              .append(" bidPrice:").append(toString(r.bidPrice))
              .append(" bidSize:").append(r.bidSize)
              .append(" bidTime:").append(toString(r.bidTime))
              .append(" askPrice:").append(toString(r.askPrice))
              .append(" askSize:").append(r.askSize)
              .append(" askTime:").append(toString(r.askTime))
              .append(" recapPrefix:").append(r.recapPrefix)
              .append(" tick:").append(toString(r.tick))
              .append(" lowPrice:").append(toString(r.lowPrice))
              .append(" highPrice:").append(toString(r.highPrice))
              .append(" openPrice:").append(toString(r.openPrice))
              .append(" closePrice:").append(toString(r.closePrice))
              .append(" openInterest:").append(r.openInterest)
              .append(" previousClosePrice:").append(toString(r.previousClosePrice))
              .append(" isOTC:").append(r.isOTC);
        return result;
    }

    public static StringBuilder toString(RecapStruct rseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(rseq.length).append("{");
        int index = 0;
        for (RecapStruct r : rseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(r));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(RecapStructV4 r)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(r.classKey)
              .append(" productKey:").append(r.productKey)
              .append(" productType:").append(r.productType)
              .append(" exchange:").append(r.exchange)
              .append(" sentTime:").append(r.sentTime)
              .append(" priceScale:").append(r.priceScale)
              .append(" lowPrice:").append(r.lowPrice)
              .append(" highPrice:").append(r.highPrice)
              .append(" openPrice:").append(r.openPrice)
              .append(" previousClosePrice:").append(r.previousClosePrice)
              .append(" statusCodes:").append(r.statusCodes);
        return result;
    }

    public static StringBuilder toString(RecapStructV4 rseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(rseq.length).append("{");
        int index = 0;
        for (RecapStructV4 r : rseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(r));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(TickerStruct t)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(t.productKeys))
                .append('}')
              .append(" sessionName:").append(t.sessionName)
              .append(" exchangeSymbol:").append(t.exchangeSymbol)
              .append(" salePrefix:").append(t.salePrefix)
              .append(" lastSalePrice:").append(toString(t.lastSalePrice))
              .append(" lastSaleVolume:").append(t.lastSaleVolume)
              .append(" salePostfix:").append(t.salePostfix);
        return result;
    }

    public static StringBuilder toString(TickerStruct tseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(tseq.length).append("{");
        int index = 0;
        for (TickerStruct t : tseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(t));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(TickerStructV4 t)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(t.classKey)
              .append(" productKey:").append(t.productKey)
              .append(" productType:").append(t.productType)
              .append(" exchange:").append(t.exchange)
              .append(" sentTime:").append(t.sentTime)
              .append(" priceScale:").append(t.priceScale)
              .append(" tradeTime:").append(t.tradeTime)
              .append(" tradePrice:").append(t.tradePrice)
              .append(" tradeVolume:").append(t.tradeVolume)
              .append(" salePrefix:").append(t.salePrefix)
              .append(" salePostfix:").append(t.salePostfix);
        return result;
    }

    public static StringBuilder toString(TickerStructV4 tseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(tseq.length).append("{");
        int index = 0;
        for (TickerStructV4 t : tseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(t));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiOrder

    public static StringBuilder toString(AuctionStruct a)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(a.sessionName)
              .append(" classKey:").append(a.classKey)
              .append(" productType:").append(a.productType)
              .append(" productKey:").append(a.productKey)
              .append(" auctionId:{").append(toString(a.auctionId)).append('}')
              .append(" auctionType:").append(a.auctionType)
              .append(" auctionState:").append(a.auctionState)
              .append(" side:").append(a.side)
              .append(" auctionQuantity:").append(a.auctionQuantity)
              .append(" startingPrice:").append(toString(a.startingPrice))
              .append(" auctionedOrderContingencyType:")
                .append(a.auctionedOrderContingencyType)
              .append(" entryTime:").append(toString(a.entryTime))
              .append(" extensions:").append(a.extensions);
        return result;
    }

    public static StringBuilder toString(AuctionSubscriptionResultStruct asr)
    {
        StringBuilder result = new StringBuilder();
        result.append("auctionType:").append(asr.auctionType)
              .append(" subscriptionResult:{")
                .append(toString(asr.subscriptionResult)).append('}');
        return result;
    }

    public static StringBuilder toString(
            AuctionSubscriptionResultStruct asrseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(asrseq.length).append("{");
        int index = 0;
        for (AuctionSubscriptionResultStruct asr : asrseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(asr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static short[] makeAuctionTypeSequence(String command[])
    {
        return makeListOfShorts(command);
    }

    public static StringBuilder toString(BustReportStruct br)
    {
        StringBuilder result = new StringBuilder();
        result.append("tradeId:").append(toString(br.tradeId))
              .append(" bustReportType:").append(br.bustReportType)
              .append(" sessionName:").append(br.sessionName)
              .append(" executingOrGiveUpFirm:{")
                .append(toString(br.executingOrGiveUpFirm)).append('}')
              .append(" userId:").append(br.userId)
              .append(" userAcronym:{").append(toString(br.userAcronym))
                .append('}')
              .append(" bustedQuantity:").append(br.bustedQuantity)
              .append(" price:").append(toString(br.price))
              .append(" productKey:").append(br.productKey)
              .append(" side:").append(br.side)
              .append(" timeSent:").append(br.timeSent)
              .append(" reinstateRequestedQuantity:")
                .append(br.reinstateRequestedQuantity)
              .append(" transactionSeuqenceNumber:")
                .append(br.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(BustReinstateReportStruct brr)
    {
        StringBuilder result = new StringBuilder();
        result.append("tradeId:").append(toString(brr.tradeId))
              .append(" bustedQuantity:").append(brr.bustedQuantity)
              .append(" reinstatedQuantity:").append(brr.reinstatedQuantity)
              .append(" totalRemainingQuantity:")
                .append(brr.totalRemainingQuantity)
              .append(" price:").append(toString(brr.price))
              .append(" productKey:").append(brr.productKey)
              .append(" sessionName:").append(brr.sessionName)
              .append(" side:").append(brr.side)
              .append(" timeSent:").append(toString(brr.timeSent))
              .append(" transactionSequenceNumber:")
                .append(brr.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(BustReportStruct brseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(brseq.length).append("{");
        int index = 0;
        for (BustReportStruct br : brseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(br));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CancelReportStruct cr)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderId:{").append(toString(cr.orderId)).append('}')
              .append(" cancelReportType:").append(cr.cancelReportType)
              .append(" cancelReason:").append(cr.cancelReason)
              .append(" productKey:").append(cr.productKey)
              .append(" sessionName:").append(cr.sessionName)
              .append(" cancelledQuantity:").append(cr.cancelledQuantity)
              .append(" tlcQuantity:").append(cr.tlcQuantity)
              .append(" mismatchedQuantity:").append(cr.mismatchedQuantity)
              .append(" timeSent:").append(toString(cr.timeSent))
              .append(" orsId:").append(cr.orsId)
              .append(" totalCancelledQuantity:")
                .append(cr.totalCancelledQuantity)
              .append(" transactionSequenceNumber:")
                .append(cr.transactionSequenceNumber)
              .append(" userAssignedCancelId:").append(cr.userAssignedCancelId);
        return result;
    }

    public static StringBuilder toString(CancelReportStruct crseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(crseq.length).append("{");
        int index = 0;
        for (CancelReportStruct cr : crseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(cr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CancelRequestStruct cr)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderId:{").append(toString(cr.orderId)).append('}')
              .append(" sessionName:").append(cr.sessionName)
              .append(" userAssignedCancelId:").append(cr.userAssignedCancelId)
              .append(" cancelType:").append(cr.cancelType)
              .append(" quantity:").append(cr.quantity);
        return result;
    }

    private CancelRequestStruct makeCancelRequestStruct(String command[])
    {
        String names[] = { "orderId", "sessionName", "userAssignedCancelId",
                "cancelType", "quantity"};
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        CancelRequestStruct cr = new CancelRequestStruct();
        if (values[0] == null)
        {
            Log.message("Missing orderId");
            return null;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof OrderIdStruct))
        {
            Log.message("Not an OrderIdStruct:" + objName);
            return null;
        }
        cr.orderId = (OrderIdStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        cr.sessionName = values[1];

        if (values[2] == null)
        {
            Log.message("Missing userAssignedCancelId");
            return null;
        }
        cr.userAssignedCancelId = values[2];

        if (values[3] == null)
        {
            Log.message("Missing cancelType");
            return null;
        }
        cr.cancelType = Short.parseShort(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing quantity");
            return null;
        }
        cr.quantity = Integer.parseInt(values[4]);

        return cr;
    }

    public static StringBuilder toString(ContraPartyStruct cp)
    {
        StringBuilder result = new StringBuilder();
        result.append("user:{").append(toString(cp.user)).append('}')
              .append(" firm:{").append(toString(cp.firm)).append('}')
              .append(" quantity:").append(cp.quantity);
        return result;
    }

    public static StringBuilder toString(ContraPartyStruct cpseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cpseq.length).append("{");
        int index = 0;
        for (ContraPartyStruct cp : cpseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(cp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(CrossOrderStruct co)
    {
        StringBuilder result = new StringBuilder();
        result.append("bjySideOrder:{").append(toString(co.buySideOrder))
                .append('}')
              .append(" sellSideOrder:{").append(toString(co.sellSideOrder))
                .append('}');
        return result;
    }

    public static StringBuilder toString(FilledReportStruct fr)
    {
        StringBuilder result = new StringBuilder();
        result.append("tradeId:").append(toString(fr.tradeId))
              .append(" fillReportType:").append(fr.fillReportType)
              .append(" executingOrGiveUpFirm:{")
                .append(toString(fr.executingOrGiveUpFirm)).append('}')
              .append(" userId:").append(fr.userId)
              .append(" userAcronym:{").append(toString(fr.userAcronym))
                .append('}')
              .append(" productKey:").append(fr.productKey)
              .append(" sessionName:").append(fr.sessionName)
              .append(" tradedQuantity:").append(fr.tradedQuantity)
              .append(" leavesQuantity:").append(fr.leavesQuantity)
              .append(" price:").append(toString(fr.price))
              .append(" side:").append(fr.side)
              .append(" orsId:").append(fr.orsId)
              .append(" executingBroker:").append(fr.executingBroker)
              .append(" cmta:{").append(toString(fr.cmta)).append('}')
              .append(" account:").append(fr.account)
              .append(" subaccount:").append(fr.subaccount)
              .append(" originator:{").append(toString(fr.originator))
                .append('}')
              .append(" optionalData:").append(fr.optionalData)
              .append(" userAssignedId:").append(fr.userAssignedId)
              .append(" extensions:").append(fr.extensions)
              .append(" contraParties:").append(toString(fr.contraParties))
              .append(" timeSent:").append(toString(fr.timeSent))
              .append(" positionEffect:").append(fr.positionEffect)
              .append(" transactionSequenceNumber:")
                .append(fr.transactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(FilledReportStruct frseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(frseq.length).append("{");
        int index = 0;
        for (FilledReportStruct fr : frseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(fr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(InternalizationOrderResultStruct ior)
    {
        StringBuilder result = new StringBuilder();
        result.append("primaryOrderResult:{")
                .append(toString(ior.primaryOrderResult)).append('}')
              .append(" matchOrderResult:{")
                .append(toString(ior.matchOrderResult)).append('}');
        return result;
    }

    public static StringBuilder toString(InternalizationOrderResultStructV2 ior)
    {
        StringBuilder result = new StringBuilder();
        result.append("primaryOrderResult:{")
                .append(toString(ior.primaryOrderResult)).append('}')
              .append(" matchOrderResult:{")
                .append(toString(ior.matchOrderResult)).append('}');
        return result;
    }

    public static StringBuilder toString(LegOrderDetailStruct lod)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKey").append(lod.productKey)
              .append(" mustUsePrice:").append(toString(lod.mustUsePrice))
              .append(" clearingFirm:{").append(toString(lod.clearingFirm))
                .append('}')
              .append(" coverage:").append(lod.coverage)
              .append(" positionEffect:").append(lod.positionEffect)
              .append(" side:").append(lod.side)
              .append(" originalQuantity:").append(lod.originalQuantity)
              .append(" tradedQuantity:").append(lod.tradedQuantity)
              .append(" cancelledQuantity:").append(lod.cancelledQuantity)
              .append(" leavesQuantity:").append(lod.leavesQuantity);
        return result;
    }

    public static StringBuilder toString(LegOrderDetailStruct lodseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(lodseq.length).append("{");
        int index = 0;
        for (LegOrderDetailStruct lod : lodseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(lod));
            ++index;
        }
        result.append(" }");
        return result;
    }

    private LegOrderEntryStruct makeLegOrderEntryStruct(String command[])
    {
        String names[] = { "productKey", "mustUsePrice", "clearingFirm",
                "coverage", "positionEffect" };        
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        LegOrderEntryStruct leo = new LegOrderEntryStruct();
        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        leo.productKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing mustUsePrice");
            return null;
        }
        leo.mustUsePrice = makePriceStruct(values[1]);
        if (leo.mustUsePrice == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[2] == null)
        {
            Log.message("Missing clearingFirm");
            return null;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        leo.clearingFirm = (ExchangeFirmStruct) o;

        if (values[3] == null || values[3].equals(EMPTY_String))
        {
            Log.message("Missing coverage");
            return null;
        }
        leo.coverage = values[3].charAt(0);

        if (values[4] == null || values[4].equals(EMPTY_String))
        {
            Log.message("Missing positionEffect");
            return null;
        }
        leo.positionEffect = values[4].charAt(0);

        return leo;
    }

    private LegOrderEntryStruct[] makeLegOrderEntryStructSequence(
            String command[])
    {
        // No keys on this line, only a list of object names
        int nItems = command.length - INDEX_FIRST_KEY;
        LegOrderEntryStruct leoseq[] = new LegOrderEntryStruct[nItems];

        int legnum = 0;
        for (int cindex = INDEX_FIRST_KEY; cindex < command.length; ++cindex)
        {
            String objName = command[cindex];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof LegOrderEntryStruct))
            {
                Log.message("Not a LegOrderEntryStruct:" + objName);
                return null;
            }
            leoseq[legnum] = (LegOrderEntryStruct) o;
            ++legnum;
        }

        return leoseq;
    }

    private LightOrderEntryStruct makeLightOrderEntryStruct(String command[])
    {
        String names[] = { "branch", "branchSequenceNumber", "originalQuantity",
                "Price", "productKey", "side", "positionEffect", "coverage",
                "isNBBOProtected", "isIOC", "orderOriginType", "cmtaExchange",
                "cmtaFirmNumber", "pdpm", "userAssignedId", "activeSession" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        LightOrderEntryStruct loe = new LightOrderEntryStruct();
        if (values[0] == null)
        {
            Log.message("Missing branch");
            return null;
        }
        loe.branch = values[0];

        if (values[1] == null)
        {
            Log.message("Missing branchSequenceNumber");
            return null;
        }
        loe.branchSequenceNumber = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing originalQuantity");
            return null;
        }
        loe.originalQuantity = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing Price");
            return null;
        }
        loe.Price = Double.parseDouble(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        loe.productKey = Integer.parseInt(values[4]);

        if (values[5] == null || values[5].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        loe.side = values[5].charAt(0);

        if (values[6] == null || values[6].equals(EMPTY_String))
        {
            Log.message("Missing positionEffect");
            return null;
        }
        loe.positionEffect = values[6].charAt(0);

        if (values[7] == null || values[7].equals(EMPTY_String))
        {
            Log.message("Missing coverage");
            return null;
        }
        loe.coverage = values[7].charAt(0);

        if (values[8] == null)
        {
            Log.message("Missing isNBBOProtected");
            return null;
        }
        loe.isNBBOProtected = CommandLine.booleanValue(values[8]);

        if (values[9] == null)
        {
            Log.message("Missing isIOC");
            return null;
        }
        loe.isIOC = CommandLine.booleanValue(values[9]);

        if (values[10] == null || values[10].equals(EMPTY_String))
        {
            Log.message("Missing orderOriginType");
            return null;
        }
        loe.orderOriginType = values[10].charAt(0);

        if (values[11] == null)
        {
            Log.message("Missing exchange");
            return null;
        }
        loe.cmtaExchange = values[11];

        if (values[12] == null)
        {
            Log.message("Missing firmNumber");
            return null;
        }
        loe.cmtaFirmNumber = values[12];

        if (values[13] == null)
        {
            Log.message("Missing pdpm");
            return null;
        }
        loe.pdpm = values[13];

        if (values[14] == null)
        {
            Log.message("Missing userAssignedId");
            return null;
        }
        loe.userAssignedId = values[14];

        if (values[15] == null)
        {
            Log.message("Missing activeSession");
            return null;
        }
        loe.activeSession = values[15];

        return loe;
    }

    public static StringBuilder toString(LightOrderResultStruct lor)
    {
        StringBuilder result = new StringBuilder();
        result.append("branch:").append(lor.branch)
              .append(" branchSequenceNumber:").append(lor.branchSequenceNumber)
              .append(" orderHighId:").append(lor.orderHighId)
              .append(" orderLowId:").append(lor.orderLowId)
              .append(" side:").append(lor.side)
              .append(" leavesQuantity:").append(lor.leavesQuantity)
              .append(" tradedQuantity:").append(lor.tradedQuantity)
              .append(" cancelledQuantity:").append(lor.cancelledQuantity)
              .append(" reason:").append(lor.reason)
              .append(" time:").append(toString(lor.time));
        return result;
    }

    public static StringBuilder toString(LightOrderResultStruct lorseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(lorseq.length).append("{");
        int index = 0;
        for (LightOrderResultStruct lor : lorseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(lor));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderBustReinstateReportStruct obrr)
    {
        StringBuilder result = new StringBuilder();
        result.append("reinstatedOrder:{")
                .append(toString(obrr.reinstatedOrder)).append('}')
              .append(" bustReinstatedReport:{")
                .append(toString(obrr.bustReinstatedReport)).append('}');
        return result;
    }

    public static StringBuilder toString(OrderBustReportStruct obr)
    {
        StringBuilder result = new StringBuilder();
        result.append("bustedOrder:{").append(toString(obr.bustedOrder))
                .append('}')
              .append(" bustedReport:").append(toString(obr.bustedReport));
        return result;
    }

    public static StringBuilder toString(OrderCancelReportStruct ocr)
    {
        StringBuilder result = new StringBuilder();
        result.append("cancelledOrder:{").append(toString(ocr.cancelledOrder))
                .append('}')
              .append(" cancelReport:").append(toString(ocr.cancelReport));
        return result;
    }

    public static StringBuilder toString(OrderContingencyStruct oc)
    {
        StringBuilder result = new StringBuilder();
        result.append("type:").append(oc.type)
              .append(" price:").append(toString(oc.price))
              .append(" volume:").append(oc.volume);
        return result;
    }

    private OrderContingencyStruct makeOrderContingencyStruct(String command[])
    {
        String names[] = { "type", "price", "volume" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        OrderContingencyStruct oc = new OrderContingencyStruct();
        if (values[0] == null)
        {
            Log.message("Missing type");
            return null;
        }
        oc.type = Short.parseShort(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing price");
            return null;
        }
        oc.price = makePriceStruct(values[1]);
        if (oc.price == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[2] == null)
        {
            Log.message("Missing volume");
            return null;
        }
        oc.volume = Integer.parseInt(values[2]);

        return oc;
    }

    private OrderEntryStruct makeOrderEntryStruct(String command[])
    {
        String names[] = { "executingOrGiveUpFirm", "branch",
                "branchSequenceNumber", "correspondentFirm", "orderDate",
                "originator", "originalQuantity", "productKey", "side",
                "price", "timeInForce", "expireTime", "contingency", "cmta",
                "extensions", "account", "subaccount", "positionEffect",
                "cross", "orderOriginType", "coverage",
                "orderNBBOProtectionType", "optionalData", "userAssignedId",
                "sessionNames" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        OrderEntryStruct oe = new OrderEntryStruct();
        String objName = values[0];
        if (objName == null)
        {
            oe.executingOrGiveUpFirm = new ExchangeFirmStruct();
            oe.executingOrGiveUpFirm.exchange = EMPTY_String;
            oe.executingOrGiveUpFirm.firmNumber = EMPTY_String;
        }
        else
        {
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof ExchangeFirmStruct))
            {
                Log.message("Not an ExchangeFirmStruct:" + objName);
                return null;
            }
            oe.executingOrGiveUpFirm = (ExchangeFirmStruct) o;
        }

        if (values[1] == null)
        {
            Log.message("Missing branch");
            return null;
        }
        oe.branch = values[1];

        if (values[2] == null)
        {
            Log.message("Missing branchSequenceNumber");
            return null;
        }
        oe.branchSequenceNumber = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing correspondentFirm");
            return null;
        }
        oe.correspondentFirm = values[3];

        if (values[4] == null)
        {
            Log.message("Missing orderDate");
            return null;
        }
        oe.orderDate = values[4];

        if (values[5] == null)
        {
            Log.message("Missing originator");
            return null;
        }
        objName = values[5]; 
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        oe.originator = (ExchangeAcronymStruct) o;

        if (values[6] == null)
        {
            Log.message("Missing originalQuantity");
            return null;
        }
        oe.originalQuantity = Integer.parseInt(values[6]);

        if (values[7] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        oe.productKey = Integer.parseInt(values[7]);

        if (values[8] == null || values[8].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        oe.side = values[8].charAt(0);

        if (values[9] == null)
        {
            Log.message("Missing price");
            return null;
        }
        oe.price = makePriceStruct(values[9]);
        if (oe.price == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[10] == null || values[10].equals(EMPTY_String))
        {
            Log.message("Missing timeInForce");
            return null;
        }
        oe.timeInForce = values[10].charAt(0);

        if (values[11] == null)
        {
            Log.message("Missing expireTime");
            return null;
        }
        oe.expireTime = makeDateTimeStruct(values[11]);
        if (oe.expireTime == null)
        {
            return null; // error already reported, leave now.
        }

        if (values[12] == null)
        {
            Log.message("Missing contingency");
            return null;
        }
        objName = values[12];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof OrderContingencyStruct))
        {
            Log.message("Not an OrdeContingencyStruct:" + objName);
            return null;
        }
        oe.contingency = (OrderContingencyStruct) o;

        if (values[13] == null)
        {
            Log.message("Missing cmta");
            return null;
        }
        objName = values[13];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        oe.cmta = (ExchangeFirmStruct) o;

        oe.extensions = (values[14] == null) ?
                oe.extensions = EMPTY_String : values[14];

        if (values[15] == null)
        {
            Log.message("Missing account");
            return null;
        }
        oe.account = values[15];

        if (values[16] == null)
        {
            Log.message("Missing subaccount");
            return null;
        }
        oe.subaccount = values[16];

        if (values[17] == null || values[17].equals(EMPTY_String))
        {
            Log.message("Missing positionEffect");
            return null;
        }
        oe.positionEffect = values[17].charAt(0);

        oe.cross = values[18] != null && CommandLine.booleanValue(values[18]);

        if (values[19] == null || values[19].equals(EMPTY_String))
        {
            Log.message("Missing orderOriginType");
            return null;
        }
        oe.orderOriginType = values[19].charAt(0);

        if (values[20] == null || values[20].equals(EMPTY_String))
        {
            Log.message("Missing coverage");
            return null;
        }
        oe.coverage = values[20].charAt(0);

        if (values[21] == null)
        {
            Log.message("Missing orderNBBOProtectionType");
            return null;
        }
        oe.orderNBBOProtectionType = Short.parseShort(values[21]);

        oe.optionalData = (values[22] == null) ? EMPTY_String : values[22];

        if (values[23] == null)
        {
            Log.message("Missing userAssignedId");
            return null;
        }
        oe.userAssignedId = values[23];

        if (values[24] == null)
        {
            Log.message("Missing sessionNames");
            return null;
        }
        objName = values[24];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof String[]))
        {
            Log.message("Not a TradingSessionNameSequence:" + objName);
            return null;
        }
        oe.sessionNames = (String[]) o;
        return oe;
    }

    public static StringBuilder toString(OrderFilledReportStruct ofr)
    {
        StringBuilder result = new StringBuilder();
        result.append("filledOrder:{").append(toString(ofr.filledOrder))
                .append('}')
              .append(" filledReport:").append(toString(ofr.filledReport));
        return result;
    }

    public static StringBuilder toString(OrderIdStruct oi)
    {
        StringBuilder result = new StringBuilder();
        result.append("executingOrGiveUpFirm:{")
                .append(toString(oi.executingOrGiveUpFirm)).append('}')
              .append(" branch:").append(oi.branch)
              .append(" branchSequenceNumber:").append(oi.branchSequenceNumber)
              .append(" correspondentFirm:").append(oi.correspondentFirm)
              .append(" orderDate:").append(oi.orderDate)
              .append(" highCboeId:").append(oi.highCboeId)
              .append(" lowCboeId:").append(oi.lowCboeId);
        return result;
    }

    public static StringBuilder toString(OrderIdStruct oiseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(oiseq.length).append("{");
        int index = 0;
        for (OrderIdStruct oi : oiseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(oi));
            ++index;
        }
        result.append(" }");
        return result;
    }

    private OrderIdStruct makeOrderIdStruct(String command[])
    {
        String names[] = { "executingOrGiveUpFirm", "branch",
                "branchSequenceNumber", "correspondentFirm", "orderDate",
                "highCboeId", "lowCboeId" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        OrderIdStruct oi = new OrderIdStruct();
        String objName = values[0];
        if (objName == null)
        {
            oi.executingOrGiveUpFirm = new ExchangeFirmStruct();
            oi.executingOrGiveUpFirm.exchange = EMPTY_String;
            oi.executingOrGiveUpFirm.firmNumber = EMPTY_String;
        }
        else
        {
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof ExchangeFirmStruct))
            {
                Log.message("Not an ExchangeFirmStruct:" + objName);
                return null;
            }
            oi.executingOrGiveUpFirm = (ExchangeFirmStruct) o;
        }

        oi.branch = (values[1] == null) ? EMPTY_String : values[1];
        oi.branchSequenceNumber = (values[2] == null) ?
                0 : Integer.parseInt(values[2]);

        oi.correspondentFirm = (values[3] == null ? EMPTY_String : values[3]);
        oi.orderDate = (values[4] == null) ? EMPTY_String : values[4];

        oi.highCboeId = (values[5] == null) ? 0 : Integer.parseInt(values[5]);
        oi.lowCboeId  = (values[6] == null) ? 0 : Integer.parseInt(values[6]);

        return oi;
    }

    public static StringBuilder toString(OrderResultStruct or)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderId:{").append(toString(or.orderId)).append('}')
              .append(" result:{").append(toString(or.result)).append('}');
        return result;
    }

    public static StringBuilder toString(OrderResultStructV2 or)
    {
        StringBuilder result = new StringBuilder();
        result.append("order:{").append(toString(or.order)).append('}')
              .append(" result:{").append(toString(or.result)).append('}');
        return result;
    }

    public static StringBuilder toString(OrderStruct o)
    {
        StringBuilder result = new StringBuilder();
        result.append("orderId:{").append(toString(o.orderId)).append('}')
              .append(" originator:{").append(toString(o.originator))
                .append('}')
              .append(" originalQuantity:").append(o.originalQuantity)
              .append(" productKey:").append(o.productKey)
              .append(" side:").append(o.side)
              .append(" price:").append(toString(o.price))
              .append(" timeInForce:").append(o.timeInForce)
              .append(" expireTime:").append(toString(o.expireTime))
              .append(" contingency:{").append(toString(o.contingency))
                .append('}')
              .append(" cmta:{").append(toString(o.cmta)).append('}')
              .append(" extensions:").append(o.extensions)
              .append(" account:").append(o.account)
              .append(" subaccount:").append(o.subaccount)
              .append(" positionEffect:").append(o.positionEffect)
              .append(" cross:").append(o.cross)
              .append(" orderOriginType:").append(o.orderOriginType)
              .append(" coverage:").append(o.coverage)
              .append(" orderNBBOProtectionType:")
                .append(o.orderNBBOProtectionType)
              .append(" optionalData:").append(o.optionalData)
              .append(" userId:").append(o.userId)
              .append(" userAcronym:{").append(toString(o.userAcronym))
                .append('}')
              .append(" productType:").append(o.productType)
              .append(" classKey:").append(o.classKey)
              .append(" receivedTime:").append(toString(o.receivedTime))
              .append(" state:").append(o.state)
              .append(" tradedQuantity:").append(o.tradedQuantity)
              .append(" cancelledQuantity:").append(o.cancelledQuantity)
              .append(" leavesQuantity:").append(o.leavesQuantity)
              .append(" averagePrice:").append(toString(o.averagePrice))
              .append(" sessionTradedQuantity:").append(o.sessionTradedQuantity)
              .append(" sessionCancelledQuantity:")
                .append(o.sessionCancelledQuantity)
              .append(" sessionAveragePrice:")
                .append(toString(o.sessionAveragePrice))
              .append(" orsId:").append(o.orsId)
              .append(" source:").append(o.source)
              .append(" crossedOrder:{").append(toString(o.crossedOrder))
                .append('}')
              .append(" transactionSequenceNumber:")
                .append(o.transactionSequenceNumber)
              .append(" userAssignedId:").append(o.userAssignedId)
              .append(" sessionNames:").append(toString(o.sessionNames))
              .append(" activeSession:").append(o.activeSession)
              .append(" legOrderDetails:").append(toString(o.legOrderDetails));
        return result;
    }

    public static StringBuilder toString(OrderStruct oseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(oseq.length).append("{");
        int index = 0;
        for (OrderStruct o : oseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(o));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(OrderDetailStruct od)
    {
        StringBuilder result = new StringBuilder();
        result.append("productInformation:{")
                .append(toString(od.productInformation)).append('}')
              .append(" statusChange:").append(od.statusChange)
              .append(" orderStruct:{").append(toString(od.orderStruct))
                .append('}');
        return result;
    }

    public static StringBuilder toString(OrderDetailStruct odseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(odseq.length).append("{");
        int index = 0;
        for (OrderDetailStruct od : odseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(od));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static char[] makeOriginTypeSequence(String command[])
    {
        return makeListOfChars(command);
    }

    public static StringBuilder toString(PendingOrderStruct po)
    {
        StringBuilder result = new StringBuilder();
        result.append("pendingProductName:{")
                .append(toString(po.pendingProductName)).append('}')
              .append(" pendingOrder:{").append(toString(po.pendingOrder))
                .append('}')
              .append(" currentOrder:{").append(toString(po.currentOrder))
                .append('}');
        return result;
    }

    public static StringBuilder toString(PendingOrderStruct poseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(poseq.length).append("{");
        int index = 0;
        for (PendingOrderStruct po : poseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(po));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiProduct

    private static int[] makeClassKeySequence(String command[])
    {
        return makeListOfInts(command);
    }

    public static StringBuilder toString(ClassStruct c)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(c.classKey)
              .append(" productType:").append(c.productType)
              .append(" listingState:").append(c.listingState)
              .append(" classSymbol:").append(c.classSymbol)
              .append(" underlyingProduct:{")
                .append(toString(c.underlyingProduct)).append('}')
              .append(" primaryExchange:").append(c.primaryExchange)
              .append(" activationDate:").append(toString(c.activationDate))
              .append(" inactivationDate:").append(toString(c.inactivationDate))
              .append(" createdTime:").append(toString(c.createdTime))
              .append(" lastModifiedTime:").append(toString(c.lastModifiedTime))
              .append(" epwValues:").append(toString(c.epwValues))
              .append(" epwFastMarketMultiplier:")
                .append(c.epwFastMarketMultiplier)
              .append(" productDescription:")
                .append(toString(c.productDescription)).append('}')
              .append(" testClass:").append(c.testClass)
              .append(" reportingClasses:")
                .append(toString(c.reportingClasses));
        return result;
    }

    public static StringBuilder toString(ClassStruct cseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cseq.length).append("{");
        int index = 0;
        for (ClassStruct c : cseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(c));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(EPWStruct epw)
    {
        StringBuilder result = new StringBuilder();
        result.append("minimumBidRange:").append(epw.minimumBidRange)
              .append(" maximumBidRange:").append(epw.maximumBidRange)
              .append(" maximumAllowableSpread:")
                .append(epw.maximumAllowableSpread);
        return result;
    }

    public static StringBuilder toString(EPWStruct epwseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(epwseq.length).append("{");
        int index = 0;
        for (EPWStruct epw : epwseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(epw));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(PendingAdjustmentStruct pa)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(pa.classKey)
              .append(" effectiveDate:").append(toString(pa.effectiveDate))
              .append(" submittedDate:").append(toString(pa.submittedDate))
              .append(" type:").append(pa.type)
              .append(" active:").append(pa.active)
              .append(" productsPending:").append(toString(pa.productsPending));
        return result;
    }

    public static StringBuilder toString(PendingAdjustmentStruct paseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(paseq.length).append("{");
        int index = 0;
        for (PendingAdjustmentStruct pa : paseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(pa));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(PendingNameStruct pn)
    {
        StringBuilder result = new StringBuilder();
        result.append("action:").append(pn.action)
              .append(" productStruct:{").append(toString(pn.productStruct))
                .append('}')
              .append(" pendingProductName:{")
                .append(toString(pn.pendingProductName)).append('}');
        return result;
    }

    public static StringBuilder toString(PendingNameStruct pnseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(pnseq.length).append("{");
        int index = 0;
        for (PendingNameStruct pn : pnseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(pn));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ProductDescriptionStruct pd)
    {
        StringBuilder result = new StringBuilder();
        result.append("name:").append(pd.name)
              .append(" baseDescriptionName:").append(pd.baseDescriptionName)
              .append(" minimumStrikePriceFraction:")
                .append(toString(pd.minimumStrikePriceFraction))
              .append(" maxStrikePrice:").append(toString(pd.maxStrikePrice))
              .append(" premiumBreakPoint:")
                .append(toString(pd.premiumBreakPoint))
              .append(" minimumAbovePremiumFraction:")
                .append(toString(pd.minimumAbovePremiumFraction))
              .append(" minimumBelowPremiumFraction:")
                .append(toString(pd.minimumBelowPremiumFraction))
              .append(" priceDisplayType:").append(pd.priceDisplayType)
              .append(" premiumPriceFormat:").append(pd.premiumPriceFormat)
              .append(" strikePriceFormat:").append(pd.strikePriceFormat)
              .append(" underlyingPriceFormat:")
                .append(pd.underlyingPriceFormat);
        return result;
    }

    private static String[] makeProductGroupSequence(String command[])
    {
        return makeListOfStrings(command);
    }

    public static StringBuilder toString(ProductKeysStruct pk)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKey:").append(pk.productKey)
              .append(" classKey:").append(pk.classKey)
              .append(" productType:").append(pk.productType)
              .append(" reportingClass:").append(pk.reportingClass);
        return result;
    }

    public static StringBuilder toString(ProductNameStruct pn)
    {
        StringBuilder result = new StringBuilder();
        result.append("reportingClass:").append(pn.reportingClass)
              .append(" exercisePrice:").append(toString(pn.exercisePrice))
              .append(" expirationDate:").append(toString(pn.expirationDate))
              .append(" optionType:").append(pn.optionType)
              .append(" productSymbol:").append(pn.productSymbol);
        return result;
    }

    private ProductNameStruct makeProductNameStruct(String command[])
    {
        String names[] = { "reportingClass", "exercisePrice", "expirationDate",
                "optionType", "productSymbol"};
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ProductNameStruct pn = new ProductNameStruct();

        if (values[0] == null)
        {
            Log.message("Missing reportingClass");
            return null;
        }
        pn.reportingClass = values[0];

        if (values[1] == null)
        {
            Log.message("Missing exercisePrice");
            return null;
        }
        pn.exercisePrice = makePriceStruct(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing expirationDate");
            return null;
        }
        pn.expirationDate = makeDateStruct(values[2]);
        if (pn.expirationDate == null)
        {
            return null;
        }

        if (values[3] == null || values[3].equals(EMPTY_String))
        {
            Log.message("Missing optionType");
            return null;
        }
        pn.optionType = values[3].charAt(0);

        if (values[4] == null)
        {
            Log.message("Missing productSymbol");
            return null;
        }
        pn.productSymbol = values[4];

        return pn;
    }

    public static StringBuilder toString(ProductStruct p)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(p.productKeys))
                .append('}')
              .append(" productName:{").append(toString(p.productName))
                .append('}')
              .append(" listingState:").append(p.listingState)
              .append(" description:").append(p.description)
              .append(" companyName:").append(p.companyName)
              .append(" unitMeasure:").append(p.unitMeasure)
              .append(" standardQuantity:").append(p.standardQuantity)
              .append(" maturityDate:").append(toString(p.maturityDate))
              .append(" activationDate:").append(toString(p.activationDate))
              .append(" inactivationDate:").append(toString(p.inactivationDate))
              .append(" createdTime:").append(toString(p.createdTime))
              .append(" lastModifiedTime:").append(toString(p.lastModifiedTime))
              .append(" opraMonthCode:").append(p.opraMonthCode)
              .append(" opraPriceCode:").append(p.opraPriceCode);
        return result;
    }

    public static StringBuilder toString(ProductStruct pseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(pseq.length).append("{");
        int index = 0;
        for (ProductStruct p : pseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(p));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ProductTypeStruct pt)
    {
        StringBuilder result = new StringBuilder();
        result.append("type:").append(pt.type)
              .append(" name:").append(pt.name)
              .append(" description:").append(pt.description)
              .append(" createdTime:").append(toString(pt.createdTime))
              .append(" lastModifiedTime:")
                .append(toString(pt.lastModifiedTime));
        return result;
    }

    public static StringBuilder toString(ProductTypeStruct ptseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(ptseq.length).append("{");
        int index = 0;
        for (ProductTypeStruct pt : ptseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(pt));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ReportingClassStruct rc)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(rc.classKey)
              .append(" productType:").append(rc.productType)
              .append(" reportingClassSymbol:").append(rc.reportingClassSymbol)
              .append(" productClassSymbol:").append(rc.productClassSymbol)
              .append(" productClassKey:").append(rc.productClassKey)
              .append(" listingState:").append(rc.listingState)
              .append(" contractSize:").append(rc.contractSize)
              .append(" transactionFeeCode:").append(rc.transactionFeeCode)
              .append(" activationDate:").append(toString(rc.activationDate))
              .append(" inactivationDate:")
                .append(toString(rc.inactivationDate))
              .append(" createdTime:").append(toString(rc.createdTime))
              .append(" lastModifiedTime:")
                .append(toString(rc.lastModifiedTime));
        return result;
    }

    public static StringBuilder toString(ReportingClassStruct rcseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(rcseq.length).append("{");
        int index = 0;
        for (ReportingClassStruct rc : rcseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(rc));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiQuote

    public static StringBuilder toString(ClassQuoteResultStructV2 cqr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteKey:").append(cqr.quoteKey)
              .append(" productKey:").append(cqr.productKey)
              .append(" errorCode:").append(cqr.errorCode);
        return result;
    }

    public static StringBuilder toString(ClassQuoteResultStructV2 cqrseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cqrseq.length).append("{");
        int index = 0;
        for (ClassQuoteResultStructV2 cqr : cqrseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cqr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ClassQuoteResultStructV3 cqr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteResult:{").append(toString(cqr.quoteResult))
                .append('}')
              .append(" quoteUpdateControlId:")
                .append(cqr.quoteUpdateControlId);
        return result;
    }

    public static StringBuilder toString(ClassQuoteResultStructV3 cqrseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(cqrseq.length).append("{");
        int index = 0;
        for (ClassQuoteResultStructV3 cqr : cqrseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cqr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(LockNotificationStruct ln)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(ln.sessionName)
              .append(" productType:").append(ln.productType)
              .append(" classKey:").append(ln.classKey)
              .append(" productKey:").append(ln.productKey)
              .append(" side:").append(ln.side)
              .append(" price:").append(toString(ln.price))
              .append(" quantity:").append(ln.quantity)
              .append(" extension:").append(ln.extensions)
              .append(" buySideUserAcronyms:{")
                .append(toString(ln.buySideUserAcronyms)).append('}')
              .append(" sellSideUserAcronyms:{")
                .append(toString(ln.sellSideUserAcronyms)).append('}');
        return result;
    }

    public static StringBuilder toString(LockNotificationStruct lnseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(lnseq.length).append("{");
        int index = 0;
        for (LockNotificationStruct ln : lnseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ln));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(QuoteBustReportStruct qbr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteKey:").append(qbr.quoteKey)
              .append(" productKeys:{").append(toString(qbr.productKeys))
                .append('}')
              .append(" productName:{").append(toString(qbr.productName))
                .append('}')
              .append(" bustedReport:").append(toString(qbr.bustedReport))
              .append(" statusChange:").append(qbr.statusChange);
        return result;
    }

    public static StringBuilder toString(QuoteCancelReportStruct qcr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteKey:").append(qcr.quoteKey)
              .append(" productKeys:{").append(toString(qcr.productKeys))
                .append('}')
              .append(" productName:{").append(toString(qcr.productName))
                .append('}')
              .append(" cancelReason:").append(qcr.cancelReason)
              .append(" statusChange:").append(qcr.statusChange);
        return result;
    }

    public static StringBuilder toString(QuoteDeleteReportStruct qdr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quote:{").append(toString(qdr.quote)).append('}')
              .append(" deleteReason:").append(qdr.deleteReason);
        return result;
    }

    public static StringBuilder toString(QuoteDeleteReportStruct qdrseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(qdrseq.length).append("{");
        int index = 0;
        for (QuoteDeleteReportStruct qdr : qdrseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(qdr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(QuoteDetailStruct qd)
    {
        StringBuilder result = new StringBuilder();
        result.append(" productKeys:{").append(toString(qd.productKeys))
                .append('}')
              .append(" productName:{").append(toString(qd.productName))
                .append('}')
              .append(" statusChange:").append(qd.statusChange)
              .append(" quote:{").append(toString(qd.quote)).append('}');
        return result;
    }

    public static StringBuilder toString(QuoteDetailStruct qdseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(qdseq.length).append("{");
        int index = 0;
        for (QuoteDetailStruct qd : qdseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(qd));
            ++index;
        }
        result.append(" }");
        return result;
    }

    private QuoteEntryStruct makeQuoteEntryStruct(String command[])
    {
        String names[] = { "productKey", "sessionName", "bidPrice",
                "bidQuantity", "askPrice", "askQuantity", "userAssignedId"};
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        QuoteEntryStruct qe = new QuoteEntryStruct();

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        qe.productKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        qe.sessionName = values[1];

        if (values[2] == null)
        {
            Log.message("Missing bidPrice");
            return null;
        }
        qe.bidPrice = makePriceStruct(values[2]);
        if (qe.bidPrice == null)
        {
            return null;
        }

        if (values[3] == null)
        {
            Log.message("Missing bidQuantity");
            return null;
        }
        qe.bidQuantity = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing askPrice");
            return null;
        }
        qe.askPrice = makePriceStruct(values[4]);
        if (qe.askPrice == null)
        {
            return null;
        }

        if (values[5] == null)
        {
            Log.message("Missing askQuantity");
            return null;
        }
        qe.askQuantity = Integer.parseInt(values[5]);

        qe.userAssignedId = (values[6] == null) ? EMPTY_String : values[6];

        return qe;
    }

    private QuoteEntryStruct[] makeQuoteEntryStructSequence(String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        QuoteEntryStruct qesseq[] =
                new QuoteEntryStruct[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof QuoteEntryStruct))
            {
                Log.message("Not a QuoteEntryStruct:" + objName);
                return null;
            }
            qesseq[seqIndex++] = (QuoteEntryStruct) o;
        }
        return qesseq;
    }

    private QuoteEntryStructV3 makeQuoteEntryStructV3(String command[])
    {
        String names[] = { "quoteEntry", "quoteUpdateControlId" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        QuoteEntryStructV3 qe = new QuoteEntryStructV3();

        if (values[0] == null)
        {
            Log.message("Missing quoteEntry");
            return null;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof QuoteEntryStruct))
        {
            Log.message("Not a QuoteEntryStruct:" + objName);
            return null;
        }
        qe.quoteEntry = (QuoteEntryStruct) o;

        if (values[1] == null)
        {
            Log.message("Missing quoteUpdateControlId");
            return null;
        }
        qe.quoteUpdateControlId = Short.parseShort(values[1]);

        return qe;
    }

    private QuoteEntryStructV3[] makeQuoteEntryStructV3Sequence(String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        QuoteEntryStructV3 qesseq[] = new QuoteEntryStructV3[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof QuoteEntryStructV3))
            {
                Log.message("Not a QuoteEntryStructV3:" + objName);
                return null;
            }
            qesseq[seqIndex++] = (QuoteEntryStructV3) o;
        }
        return qesseq;
    }

    private QuoteEntryStructV4 makeQuoteEntryStructV4(String command[])
    {
        String names[] = { "quoteEntryV3", "sellShortIndicator", "extensions" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        QuoteEntryStructV4 qe = new QuoteEntryStructV4();

        if (values[0] == null)
        {
            Log.message("Missing quoteEntry");
            return null;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof QuoteEntryStructV3))
        {
            Log.message("Not a QuoteEntryStructV3:" + objName);
            return null;
        }
        qe.quoteEntryV3 = (QuoteEntryStructV3) o;

        if (values[1] == null || values[1].equals(EMPTY_String))
        {
            Log.message("Missing sellShortIndicator");
            return null;
        }
        qe.sellShortIndicator = values[1].charAt(0);

        if (values[2] == null)
        {
            Log.message("Missing extensions");
            return null;
        }
        qe.extensions = values[2];

        return qe;
    }

    private QuoteEntryStructV4[] makeQuoteEntryStructV4Sequence(
            String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        QuoteEntryStructV4 qesseq[] =
                new QuoteEntryStructV4[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof QuoteEntryStructV4))
            {
                Log.message("Not a QuoteEntryStructV4:" + objName);
                return null;
            }
            qesseq[seqIndex++] = (QuoteEntryStructV4) o;
        }
        return qesseq;
    }

    public static StringBuilder toString(QuoteFilledReportStruct qfr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteKey:").append(qfr.quoteKey)
              .append(" productKeys:{").append(toString(qfr.productKeys))
                .append('}')
              .append(" productName:{").append(toString(qfr.productName))
                .append('}')
              .append(" filledReport:").append(toString(qfr.filledReport))
              .append(" statusChange:").append(qfr.statusChange);
        return result;
    }

    public static StringBuilder toString(QuoteRiskManagementProfileStruct qrmp)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(qrmp.classKey)
              .append(" volumeThreshold:").append(qrmp.volumeThreshold)
              .append(" timeWindow:").append(qrmp.timeWindow)
              .append(" quoteRiskManagementEnabled:")
                .append(qrmp.quoteRiskManagementEnabled);
        return result;
    }

    public static StringBuilder toString(
            QuoteRiskManagementProfileStruct qrmpseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(qrmpseq.length).append("{");
        int index = 0;
        for (QuoteRiskManagementProfileStruct qrmp : qrmpseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(qrmp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    private QuoteRiskManagementProfileStruct
        makeQuoteRiskManagementProfileStruct(String command[])
    {
        String names[] = { "classKey", "volumeThreshold", "timeWindow",
                "quoteRiskManagementEnabled" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        QuoteRiskManagementProfileStruct qrmp =
                new QuoteRiskManagementProfileStruct();

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return null;
        }
        qrmp.classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing volumeThreshold");
            return null;
        }
        qrmp.volumeThreshold = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing timeWindow");
            return null;
        }
        qrmp.timeWindow = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing quoteRiskManagementEnabled");
            return null;
        }
        qrmp.quoteRiskManagementEnabled = CommandLine.booleanValue(values[3]);

        return qrmp;
    }

    public static StringBuilder toString(QuoteStruct q)
    {
        StringBuilder result = new StringBuilder();
        result.append("quoteKey:").append(q.quoteKey)
              .append(" productKey:").append(q.productKey)
              .append(" sessionName:").append(q.sessionName)
              .append(" userId:").append(q.userId)
              .append(" bidPrice:").append(toString(q.bidPrice))
              .append(" bidQuantity:").append(q.bidQuantity)
              .append(" askPrice:").append(toString(q.askPrice))
              .append(" askQuantity:").append(q.askQuantity)
              .append(" transactionSequenceNumber:")
                .append(q.transactionSequenceNumber)
              .append(" userAssignedId:").append(q.userAssignedId);
        return result;
    }

    private RFQEntryStruct makeRFQEntryStruct(String command[])
    {
        String names[] = { "productKey", "sessionName", "quantity" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        RFQEntryStruct re = new RFQEntryStruct();
        if (values[0] == null)
        {
            Log.message("Missing productKye");
            return null;
        }
        re.productKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        re.sessionName = values[1];

        if (values[2] == null)
        {
            Log.message("Missing quantity");
            return null;
        }
        re.quantity = Integer.parseInt(values[2]);
        return re;
    }

    public static StringBuilder toString(RFQStruct rfq)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(rfq.productKeys))
                .append('}')
              .append(" sessionName:").append(rfq.sessionName)
              .append(" quantity:").append(rfq.quantity)
              .append(" timeToLive:").append(rfq.timeToLive)
              .append(" rfqType:").append(rfq.rfqType)
              .append(" entryTime:").append(toString(rfq.entryTime));
        return result;
    }

    public static StringBuilder toString(RFQStruct rfqseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(rfqseq.length).append("{");
        int index = 0;
        for (RFQStruct rfqStruct : rfqseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(rfqStruct));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(
            UserQuoteRiskManagementProfileStruct uqrmp)
    {
        StringBuilder result = new StringBuilder();
        result.append("globalQuoteRiskManagementEnabled:")
                .append(uqrmp.globalQuoteRiskManagementEnabled)
              .append(" defaultQuoteRiskProfile:{")
                .append(toString(uqrmp.defaultQuoteRiskProfile)).append('}')
              .append(" quoteRiskProfiles:")
                .append(toString(uqrmp.quoteRiskProfiles));
        return result;
    }

// cmiSession

    public static StringBuilder toString(ClassStateStruct cs)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(cs.classKey)
              .append(" sessionName:").append(cs.sessionName)
              .append(" classState:").append(cs.classState)
              .append(" classStateTransactionSequenceNumber:")
                .append(cs.classStateTransactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(ClassStateStruct csseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(csseq.length).append("{");
        int index = 0;
        for (ClassStateStruct cs : csseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(cs));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ProductStateStruct ps)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKeys:{").append(toString(ps.productKeys))
                .append('}')
              .append(" sessionName:").append(ps.sessionName)
              .append(" productState:").append(ps.productState)
              .append(" productStateTransactionSequenceNumber:")
                .append(ps.productStateTransactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(ProductStateStruct psseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(psseq.length).append("{");
        int index = 0;
        for (ProductStateStruct ps : psseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ps));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(SessionClassStruct sc)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(sc.sessionName)
              .append(" underlyingSessionName:")
                .append(sc.underlyingSessionName)
              .append(" eligibleSessions:")
                .append(toString(sc.eligibleSessions))
              .append(" classState:").append(sc.classState)
              .append(" classStruct:{").append(toString(sc.classStruct))
                .append('}')
              .append(" classStateTransactionSequenceNumber:")
                .append(sc.classStateTransactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(SessionClassStruct scseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(scseq.length).append("{");
        int index = 0;
        for (SessionClassStruct sc : scseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(sc));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(SessionProductStruct sp)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(sp.sessionName)
              .append(" productState:").append(sp.productState)
              .append(" productStruct:{").append(toString(sp.productStruct))
                .append('}')
              .append(" productStateTransactionSequenceNumber:")
                .append(sp.productStateTransactionSequenceNumber);
        return result;
    }

    public static StringBuilder toString(SessionProductStruct spseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(spseq.length).append("{");
        int index = 0;
        for (SessionProductStruct sp : spseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(sp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(SessionStrategyLegStruct ssl)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(ssl.sessionName)
              .append(" product:").append(ssl.product)
              .append(" ratioQuantity:").append(ssl.ratioQuantity)
              .append(" side:").append(ssl.side);
        return result;
    }

    public static StringBuilder toString(SessionStrategyLegStruct sslseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(sslseq.length).append("{");
        int index = 0;
        for (SessionStrategyLegStruct ssl : sslseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(ssl));
            ++index;
        }
        result.append(" }");
        return result;
    }    

    public static StringBuilder toString(SessionStrategyStruct ss)
    {
        StringBuilder result = new StringBuilder();
        result.append("strategyType:").append(ss.strategyType)
              .append(" sessionProductStruct:{")
                .append(toString(ss.sessionProductStruct)).append('}')
              .append(" sessionStrategyLegs:")
                .append(toString(ss.sessionStrategyLegs));
        return result;
    }

    public static StringBuilder toString(SessionStrategyStruct ssseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(ssseq.length).append("{");
        int index = 0;
        for (SessionStrategyStruct ss : ssseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ss));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(TradingSessionStateStruct tss)
    {
        StringBuilder result = new StringBuilder();
        result.append("sessionName:").append(tss.sessionName)
              .append(" sessionState:").append(tss.sessionState);
        return result;
    }

// cmiStrategy

    public static StringBuilder toString(StrategyLegStruct sl)
    {
        StringBuilder result = new StringBuilder();
        result.append("product:").append(sl.product)
              .append(" ratioQuantity:").append(sl.ratioQuantity)
              .append(" side:").append(sl.side);
        return result;
    }

    public static StringBuilder toString(StrategyLegStruct slseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(slseq.length).append("{");
        int index = 0;
        for (StrategyLegStruct sl : slseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(sl));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StrategyLegStruct makeStrategyLegStruct(String command[])
    {
        String names[] = { "product", "ratioQuantity", "side" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        StrategyLegStruct sl = new StrategyLegStruct();
        if (values[0] == null)
        {
            Log.message("Missing product");
            return null;
        }
        sl.product = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing ratioQuantity");
            return null;
        }
        sl.ratioQuantity = Integer.parseInt(values[1]);

        if (values[2] == null || values[2].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        sl.side = values[2].charAt(0);

        return sl;
    }

    public static StringBuilder toString(StrategyRequestStruct sr)
    {
        StringBuilder result = new StringBuilder();
        result.append(sr.strategyLegs.length).append("{");
        int index = 0;
        for (StrategyLegStruct s : sr.strategyLegs)
        {
            result.append("\n[").append(index).append("] ").append(toString(s));
            ++index;
        }
        result.append(" }");
        return result;
    }

    // No field names, just names of StrategyLegStruct objects
    private StrategyRequestStruct makeStrategyRequestStruct(String command[])
    {
        int nLegs = command.length - INDEX_FIRST_KEY;
        if (nLegs < 2)
        {
            Log.message("StrategyRequestStruct needs at least 2 StrategyLegStructs");
            return null;
        }

        StrategyRequestStruct sr = new StrategyRequestStruct();
        sr.strategyLegs = new StrategyLegStruct[nLegs];

        for (int legIndex = 0; legIndex < nLegs; ++legIndex)
        {
            int cmdIndex = INDEX_FIRST_KEY + legIndex;
            String objName = command[cmdIndex];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof StrategyLegStruct))
            {
                Log.message("Not a StrategyLegStruct:" + objName);
                return null;
            }
            StrategyLegStruct sl = (StrategyLegStruct) o;
            sr.strategyLegs[legIndex] = sl;
        }

        return sr;
    }

    public static StringBuilder toString(StrategyStruct s)
    {
        StringBuilder result = new StringBuilder();
        result.append("product:{").append(toString(s.product))
                .append('}')
              .append(" strategyType:").append(s.strategyType)
              .append(" strategyLegs:").append(toString(s.strategyLegs));
        return result;
    }

    public static StringBuilder toString(StrategyStruct sseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(sseq.length).append("{");
        int index = 0;
        for (StrategyStruct s : sseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(s));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiTrade

    private ExternalAtomicTradeEntryStruct makeExternalAtomicTradeEntryStruct(
            String command[])
    {
        String names[] = { "entryTime", "entryType", "quantity", "sessionName",
                "buyerOriginType", "buyerCmta", "buyerPositionEffect",
                "buyerAccount", "buyerSubaccount", "buyerBroker",
                "buyerOriginator", "buyerFirm", "buyerOptionalData",
                "sellerOriginType", "sellerCmta", "sellerPositionEffect",
                "sellerAccount", "sellerSubaccount", "sellerBroker",
                "sellerOriginator", "sellerFirm", "sellerOptionalData" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ExternalAtomicTradeEntryStruct eate =
                new ExternalAtomicTradeEntryStruct();
        if (values[0] == null)
        {
            Log.message("Missing entryTime");
            return null;
        }
        eate.entryTime = makeDateTimeStruct(values[0]);

        if (values[1] == null || values[1].equals(EMPTY_String))
        {
            Log.message("Missing entryType");
            return null;
        }
        eate.entryType = values[1].charAt(0);

        if (values[2] == null)
        {
            Log.message("Missing quantity");
            return null;
        }
        eate.quantity = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        eate.sessionName = values[3];

        if (values[4] == null || values[4].equals(EMPTY_String))
        {
            Log.message("Missing buyerOriginType");
            return null;
        }
        eate.buyerOriginType = values[4].charAt(0);

        if (values[5] == null)
        {
            Log.message("Missing buyerCmta");
            return null;
        }
        String objName = values[5];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        eate.buyerCmta = (ExchangeFirmStruct) o;

        if (values[6] == null || values[6].equals(EMPTY_String))
        {
            Log.message("Missing buyerPositionEffect");
            return null;
        }
        eate.buyerPositionEffect = values[6].charAt(0);

        if (values[7] == null)
        {
            Log.message("Missing buyerAccount");
            return null;
        }
        eate.buyerAccount = values[7];

        if (values[8] == null)
        {
            Log.message("Missing buyerSubaccount");
            return null;
        }
        eate.buyerSubaccount = values[8];

        if (values[9] == null)
        {
            Log.message("Missing buyerBroker");
            return null;
        }
        objName = values[9];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        eate.buyerBroker = (ExchangeAcronymStruct) o;

        if (values[10] == null)
        {
            Log.message("Missing buyerOriginator");
            return null;
        }
        objName = values[10];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        eate.buyerOriginator = (ExchangeAcronymStruct) o;

        if (values[11] == null)
        {
            Log.message("Missing buyerFirm");
            return null;
        }
        objName = values[11];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        eate.buyerFirm = (ExchangeFirmStruct) o;

        if (values[12] == null)
        {
            Log.message("Missing buyerOptionalData");
            return null;
        }
        eate.buyerOptionalData = values[12];

        if (values[13] == null || values[13].equals(EMPTY_String))
        {
            Log.message("Missing sellerOriginType");
            return null;
        }
        eate.sellerOriginType = values[13].charAt(0);

        if (values[14] == null)
        {
            Log.message("Missing sellerCmta");
            return null;
        }
        objName = values[14];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        eate.sellerCmta = (ExchangeFirmStruct) o;

        if (values[15] == null || values[15].equals(EMPTY_String))
        {
            Log.message("Missing sellerPositionEffect");
            return null;
        }
        eate.sellerPositionEffect = values[15].charAt(0);

        if (values[16] == null)
        {
            Log.message("Missing sellerAccount");
            return null;
        }
        eate.sellerAccount = values[16];

        if (values[17] == null)
        {
            Log.message("Missing sellerSubaccount");
            return null;
        }
        eate.sellerSubaccount = values[17];

        if (values[18] == null)
        {
            Log.message("Missing sellerBroker");
            return null;
        }
        objName = values[18];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        eate.sellerBroker = (ExchangeAcronymStruct) o;

        if (values[19] == null)
        {
            Log.message("Missing sellerOriginator");
            return null;
        }
        objName = values[19];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        eate.sellerOriginator = (ExchangeAcronymStruct) o;

        if (values[20] == null)
        {
            Log.message("Missing sellerFirm");
            return null;
        }
        objName = values[20];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        eate.sellerFirm = (ExchangeFirmStruct) o;

        if (values[21] == null)
        {
            Log.message("Missing sellerOptionalData");
            return null;
        }
        eate.sellerOptionalData = values[21];

        return eate;
    }

    private ExternalAtomicTradeEntryStruct[]
        makeExternalAtomicTradeEntryStructSequence(String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        ExternalAtomicTradeEntryStruct eateseq[] =
                new ExternalAtomicTradeEntryStruct[
                        command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof ExternalAtomicTradeEntryStruct))
            {
                Log.message("Not an ExternalAtomicTradeEntryStruct:" + objName);
                return null;
            }
            eateseq[seqIndex++] = (ExternalAtomicTradeEntryStruct) o;
        }
        return eateseq;
    }

    public static StringBuilder toString(ExternalAtomicTradeResultStruct eatr)
    {
        StringBuilder result = new StringBuilder();
        result.append("atomicTradeId:").append(toString(eatr.atomicTradeId))
              .append(" matchedSequenceNumber:")
                .append(eatr.matchedSequenceNumber)
              .append(" active:").append(eatr.active)
              .append(" entryTime:").append(toString(eatr.entryTime))
              .append(" entryType:").append(eatr.entryType)
              .append(" lastUpdateTime:").append(toString(eatr.lastUpdateTime))
              .append(" lastEntryType:").append(eatr.lastEntryType)
              .append(" quantity:").append(eatr.quantity)
              .append(" sessionName:").append(eatr.sessionName)
              .append(" buyerOriginType:").append(eatr.buyerOriginType)
              .append(" buyerFirmBranch:").append(eatr.buyerFirmBranch)
              .append(" buyerFirmBranchSequenceNumber:")
                .append(eatr.buyerFirmBranchSequenceNumber)
              .append(" buyerCmta:{").append(toString(eatr.buyerCmta))
                .append('}')
              .append(" buyerCorrespondentId:")
                .append(eatr.buyerCorrespondentId)
              .append(" buyerPositionEffect:").append(eatr.buyerPositionEffect)
              .append(" buyerAccount:").append(eatr.buyerAccount)
              .append(" buyerSubaccount:").append(eatr.buyerSubaccount)
              .append(" buyerBroker:{").append(toString(eatr.buyerBroker))
                .append('}')
              .append(" buyerOriginator:{")
                .append(toString(eatr.buyerOriginator)).append('}')
              .append(" buyerFirm:{").append(toString(eatr.buyerFirm))
                .append('}')
              .append(" buyerOptionalData:").append(eatr.buyerOptionalData)
              .append(" buyerOrderOrQuoteKey:{")
                .append(eatr.buyerOrderOrQuoteKey).append('}')
              .append(" buyerOrderOrQuote:").append(eatr.buyerOrderOrQuote)
              .append(" reinstatableForBuyer:")
                .append(eatr.reinstatableForBuyer)
              .append(" sellerOriginType:").append(eatr.sellerOriginType)
              .append(" sellerFirmBranch:").append(eatr.sellerFirmBranch)
              .append(" sellerFirmBranchSequenceNumber:")
                .append(eatr.sellerFirmBranchSequenceNumber)
              .append(" sellerCmta:{").append(toString(eatr.sellerCmta))
                .append('}')
              .append(" sellerCorrespondentId:")
                .append(eatr.sellerCorrespondentId)
              .append(" sellerPositionEffect:")
                .append(eatr.sellerPositionEffect)
              .append(" sellerAccount:").append(eatr.sellerAccount)
              .append(" sellerSubaccount:").append(eatr.sellerSubaccount)
              .append(" sellerBroker:{").append(toString(eatr.sellerBroker))
                .append('}')
              .append(" sellerOriginator:{")
                .append(toString(eatr.sellerOriginator)).append('}')
              .append(" sellerFirm:{").append(toString(eatr.sellerFirm))
                .append('}')
              .append(" sellerOptionalData:").append(eatr.sellerOptionalData)
              .append(" sellerOrderOrQuoteKey:{")
                .append(eatr.sellerOrderOrQuoteKey).append('}')
              .append(" sellerOrderOrQuote:").append(eatr.sellerOrderOrQuote)
              .append(" reinstatableForSeller:")
                .append(eatr.reinstatableForSeller);
        return result;
    }

    public static StringBuilder toString(
            ExternalAtomicTradeResultStruct eatrseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(eatrseq.length).append("{");
        int index = 0;
        for (ExternalAtomicTradeResultStruct eatr : eatrseq)
        {
            result.append("\n[").append(index).append("] ")
                    .append(toString(eatr));
            ++index;
        }
        result.append(" }");
        return result;
    }

    private ExternalBustTradeStruct makeExternalBustTradeStruct(
            String command[])
    {
        String names[] = { "atomicTtradeId", "bustedQuantity",
                "buyerReinstateRequested", "sellerReinstateRequested",
                "atomicTradeId" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ExternalBustTradeStruct ebt = new ExternalBustTradeStruct();
        if (values[0] == null && values[4] == null)
        {
            Log.message("Missing atomicTtradeId");
            return null;
        }
        String ati = values[0] != null ? values[0] : values[4];
        ebt.atomicTtradeId = makeCboeIdStruct(ati);

        if (values[1] == null)
        {
            Log.message("Missing bustedQuantity");
            return null;
        }
        ebt.bustedQuantity = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing buyerReinstateRequested");
            return null;
        }
        ebt.buyerReinstateRequested = CommandLine.booleanValue(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing sellerReinstateRequested");
            return null;
        }
        ebt.sellerReinstateRequested = CommandLine.booleanValue(values[3]);

        return ebt;
    }

    private ExternalBustTradeStruct[] makeExternalBustTradeStructSequence(
            String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        ExternalBustTradeStruct ebtseq[] =
                new ExternalBustTradeStruct[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof ExternalBustTradeStruct))
            {
                Log.message("Not an ExternalBustTradeStruct:" + objName);
                return null;
            }
            ebtseq[seqIndex++] = (ExternalBustTradeStruct) o;
        }
        return ebtseq;
    }

    private ExternalTradeEntryStruct makeExternalTradeEntryStruct(
            String command[])
    {
        String names[] = { "quantity", "price", "sessionName", "productKey",
                "theTradeSource", "handlingInstruction", "externalTradeType",
                "bustable", "businessDate", "timeTraded", "parties",
                "settlementDate", "transactionTime", "asOfFlag" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ExternalTradeEntryStruct ete = new ExternalTradeEntryStruct();
        if (values[0] == null)
        {
            Log.message("Missing quantity");
            return null;
        }
        ete.quantity = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing price");
            return null;
        }
        ete.price = makePriceStruct(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        ete.sessionName = values[2];

        if (values[3] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        ete.productKey = Integer.parseInt(values[3]);

        if (values[4] == null)
        {
            Log.message("Missing theTradeSource");
            return null;
        }
        ete.theTradeSource = values[4];

        if (values[5] == null)
        {
            Log.message("Missing handlingInstruction");
            return null;
        }
        ete.handlingInstruction = Short.parseShort(values[5]);

        if (values[6] == null || values[6].equals(EMPTY_String))
        {
            Log.message("Missing externalTradeType");
            return null;
        }
        ete.externalTradeType = values[6].charAt(0);

        if (values[7] == null)
        {
            Log.message("Missing bustable");
            return null;
        }
        ete.bustable = CommandLine.booleanValue(values[7]);

        if (values[8] == null)
        {
            Log.message("Missing businessDate");
            return null;
        }
        ete.businessDate = makeDateStruct(values[8]);

        if (values[9] == null)
        {
            Log.message("Missing timeTraded");
            return null;
        }
        ete.timeTraded = makeDateTimeStruct(values[9]);

        if (values[10] == null)
        {
            Log.message("Missing parties");
            return null;
        }
        String objName = values[10];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExternalAtomicTradeEntryStruct[]))
        {
            Log.message("Not an ExternalAtomicTradeEntryStructSequence:" + objName);
            return null;
        }
        ete.parties = (ExternalAtomicTradeEntryStruct[]) o;

        if (values[11] == null)
        {
            Log.message("Missing settlementDate");
            return null;
        }
        ete.settlementDate = makeDateStruct(values[11]);

        if (values[12] == null)
        {
            Log.message("Missing transactionTime");
            return null;
        }
        ete.transactionTime = makeDateTimeStruct(values[12]);

        if (values[13] == null)
        {
            Log.message("Missing asOfFlag");
            return null;
        }
        ete.asOfFlag = CommandLine.booleanValue(values[13]);

        return ete;
    }

    public static StringBuilder toString(ExternalTradeReportStruct etr)
    {
        StringBuilder result = new StringBuilder();
        result.append("quantity:").append(etr.quantity)
              .append(" price:").append(toString(etr.price))
              .append(" sessionName:").append(etr.sessionName)
              .append(" productKey:").append(etr.productKey)
              .append(" theTradeSource:").append(etr.theTradeSource)
              .append(" tradeId:").append(toString(etr.tradeId))
              .append(" externalTradeType:").append(etr.externalTradeType)
              .append(" bustable:").append(etr.bustable)
              .append(" businessDate:").append(toString(etr.businessDate))
              .append(" timeTraded:").append(toString(etr.timeTraded))
              .append(" parties:").append(toString(etr.parties))
              .append(" settlementDate:").append(toString(etr.settlementDate))
              .append(" transactionTime:").append(toString(etr.transactionTime))
              .append(" asOfFlag:").append(etr.asOfFlag);
        return result;
    }

    private FloorTradeEntryStruct makeFloorTradeEntryStruct(String command[])
    {
        String names[] = { "sessionName", "productKey","quantity", "price",
                "side", "account", "subaccount", "cmta", "executingMarketMaker",
                "firm", "positionEffect", "contraBroker", "contraFirm",
                "timeTraded", "optionalData" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        FloorTradeEntryStruct fte = new FloorTradeEntryStruct();

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return null;
        }
        fte.sessionName = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return null;
        }
        fte.productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing quantity");
            return null;
        }
        fte.quantity = Integer.parseInt(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing price");
            return null;
        }
        fte.price = makePriceStruct(values[3]);

        if (values[4] == null || values[4].equals(EMPTY_String))
        {
            Log.message("Missing side");
            return null;
        }
        fte.side = values[4].charAt(0);

        if (values[5] == null)
        {
            Log.message("Missing account");
            return null;
        }
        fte.account = values[5];

        if (values[6] == null)
        {
            Log.message("Missing subaccount");
            return null;
        }
        fte.subaccount = values[6];

        if (values[7] == null)
        {
            Log.message("Missing cmta");
            return null;
        }
        String objName = values[7];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        fte.cmta = (ExchangeFirmStruct) o;

        if (values[8] == null)
        {
            Log.message("Missing executingMarketMaker");
            return null;
        }
        objName = values[8];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        fte.executingMarketMaker = (ExchangeAcronymStruct) o;

        if (values[9] == null)
        {
            Log.message("Missing firm");
            return null;
        }
        objName = values[9];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        fte.firm = (ExchangeFirmStruct) o;

        if (values[10] == null || values[10].equals(EMPTY_String))
        {
            Log.message("Missing positionEffect");
            return null;
        }
        fte.positionEffect = values[10].charAt(0);

        if (values[11] == null)
        {
            Log.message("Missing contraBroker");
            return null;
        }
        objName = values[11];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return null;
        }
        fte.contraBroker = (ExchangeAcronymStruct) o;

        if (values[12] == null)
        {
            Log.message("Missing contraFirm");
            return null;
        }
        objName = values[12];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return null;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return null;
        }
        fte.contraFirm = (ExchangeFirmStruct) o;

        if (values[13] == null)
        {
            Log.message("Missing timeTraded");
            return null;
        }
        fte.timeTraded = makeDateTimeStruct(values[13]);

        if (values[14] == null)
        {
            Log.message("Missing optionalData");
            return null;
        }
        fte.optionalData = values[14];

        return fte;
    }

// cmiTraderActivity

    public static StringBuilder toString(ActivityFieldStruct af)
    {
        StringBuilder result = new StringBuilder();
        result.append("fieldType:").append(af.fieldType)
              .append(" fieldName:").append(af.fieldName)
              .append(" fieldValue:").append(af.fieldValue);
        return result;
    }

    public static StringBuilder toString(ActivityFieldStruct afseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(afseq.length).append("{");
        int index = 0;
        for (ActivityFieldStruct af : afseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(af));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ActivityHistoryStruct ah)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(ah.classKey)
              .append(" startTime:").append(toString(ah.startTime))
              .append(" endTime:").append(toString(ah.endTime))
              .append(" activityRecords:").append(toString(ah.activityRecords));
        return result;
    }

    public static StringBuilder toString(ActivityRecordStruct ar)
    {
        StringBuilder result = new StringBuilder();
        result.append("productKey:").append(ar.productKey)
              .append(" eventTime:").append(toString(ar.eventTime))
              .append(" entryType:").append(ar.entryType)
              .append(" activityFields:").append(toString(ar.activityFields));
        return result;
    }

    public static StringBuilder toString(ActivityRecordStruct arseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(arseq.length).append("{");
        int index = 0;
        for (ActivityRecordStruct ar : arseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(ar));
            ++index;
        }
        result.append(" }");
        return result;
    }

// cmiUser

    private static String[] makeExchangeSequence(String[] input)
    {
        return makeListOfStrings(input);
    }

    private static String[] makeTradingSessionNameSequence(String[] input)
    {
        return makeListOfStrings(input);
    }

    /** Create a UserLogonStruct from an input command.
     * @param input 2 words to ignore, then pairs of field name, value
     * @return Object based on input command.
     */
    private static UserLogonStruct makeUserLogonStruct(String[] input)
    {
        UserLogonStruct result = new UserLogonStruct();
        result.userId = EMPTY_String;
        result.password = EMPTY_String;
        result.version = EMPTY_String;
        result.loginMode = ' ';

        int i = INDEX_FIRST_KEY;
        while (i < input.length)
        {
            String key = input[i++];
            if (i >= input.length)
            {
                // We ran out of input, ignore this last keyword
        /*while*/ break;
            }
            String value = input[i++];
            if (key.equalsIgnoreCase("userId"))
            {
                result.userId = value;
            }
            else if (key.equalsIgnoreCase("password"))
            {
                result.password = value;
            }
            else if (key.equalsIgnoreCase("version"))
            {
                result.version = value;
            }
            else if (key.equalsIgnoreCase("loginMode"))
            {
                result.loginMode = value.charAt(0);
            }
            else
            {
                Log.message("Ignoring unknown keyword:" + key);
            }
        }
        return result;
    }

    public static StringBuilder toString(AccountStruct a)
    {
        StringBuilder result = new StringBuilder();
        result.append("account:").append(a.account)
              .append(" executingGiveupFirm:{")
              .append(toString(a.executingGiveupFirm)).append('}');
        return result;
    }

    public static StringBuilder toString(AccountStruct aseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(aseq.length).append("{");
        int index = 0;
        for (AccountStruct a : aseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(a));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(DpmStruct d)
    {
        StringBuilder result = new StringBuilder();
        result.append("dpmUserId:").append(d.dpmUserId)
              .append(" dpmAssignedClasses:")
                .append(toString(d.dpmAssignedClasses));
        return result;
    }

    public static StringBuilder toString(DpmStruct dseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(dseq.length).append("{");
        int index = 0;
        for (DpmStruct d : dseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(d));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ExchangeAcronymStruct ea)
    {
        StringBuilder result = new StringBuilder();
        result.append("exchange:").append(ea.exchange)
              .append(" acronym:").append(ea.acronym);
        return result;
    }

    public static StringBuilder toString(ExchangeAcronymStruct easeq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(easeq.length).append("{");
        int index = 0;
        for (ExchangeAcronymStruct ea : easeq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ea));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static ExchangeAcronymStruct makeExchangeAcronymStruct(String command[])
    {
        String names[] = { "exchange" , "acronym" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ExchangeAcronymStruct ea = new ExchangeAcronymStruct();
        if (values[0] == null)
        {
            Log.message("Missing exchange");
            return null;
        }
        ea.exchange = values[0];

        if (values[1] == null)
        {
            Log.message("Missing acronym");
            return null;
        }
        ea.acronym = values[1];

        return ea;
    }

    public static StringBuilder toString(ExchangeFirmStruct ef)
    {
        StringBuilder result = new StringBuilder();
        result.append("exchange:").append(ef.exchange)
              .append(" firmNumber:").append(ef.firmNumber);
        return result;
    }

    private ExchangeFirmStruct makeExchangeFirmStruct(String command[])
    {
        String names[] = { "exchange", "firmNumber" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        ExchangeFirmStruct ef = new ExchangeFirmStruct();
        if (values[0] == null)
        {
            Log.message("Missing exchange");
            return null;
        }
        ef.exchange = values[0];

        if (values[1] == null)
        {
            Log.message("Missing firmNumber");
            return null;
        }
        ef.firmNumber = values[1];

        return ef;
    }

    public static StringBuilder toString(ExchangeFirmStruct efseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(efseq.length).append("{");
        int index = 0;
        for (ExchangeFirmStruct ef : efseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(ef));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(PreferenceStruct p)
    {
        StringBuilder result = new StringBuilder();
        result.append("name:").append(p.name)
              .append(" value:").append(p.value);
        return result;
    }

    private PreferenceStruct makePreferenceStruct(String command[])
    {
        String names[] = { "name", "value" };
        String values[] = getParameters(names, command, INDEX_FIRST_KEY);
        if (values == null)
        {
            return null; // error already reported, leave now.
        }

        PreferenceStruct p = new PreferenceStruct();
        if (values[0] == null)
        {
            Log.message("Missing name");
            return null;
        }
        p.name = values[0];

        if (values[1] == null)
        {
            Log.message("Missing value");
            return null;
        }
        p.value = values[1];

        return p;        
    }

    private PreferenceStruct[] makePreferenceStructSequence(String command[])
    {
        // No keys on this line, only a list of object names
        int index = INDEX_FIRST_KEY;
        PreferenceStruct pseq[] = new PreferenceStruct[command.length-INDEX_FIRST_KEY];
        int seqIndex = 0;

        while (index < command.length)
        {
            String objName = command[index];
            Object o = engineAccess.getObjectFromStore(objName);
            if (o == null)
            {
                Log.message("Cannot find object:" + objName);
                return null;
            }
            if (! (o instanceof PreferenceStruct))
            {
                Log.message("Not a PreferenceStruct:" + objName);
                return null;
            }
            pseq[seqIndex++] = (PreferenceStruct) o;
        }
        return pseq;
    }

    public static StringBuilder toString(PreferenceStruct pseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(pseq.length).append("{");
        int index = 0;
        for (PreferenceStruct p : pseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(p));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(ProfileStruct ps)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(ps.classKey)
              .append(" account:").append(ps.account)
              .append(" subAccount:").append(ps.subAccount)
              .append(" executingGiveUpFirm:{")
                .append(toString(ps.executingGiveupFirm)).append('}');
        return result;
    }

    public static StringBuilder toString(ProfileStruct pseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(pseq.length).append("{");
        int index = 0;
        for (ProfileStruct p : pseq)
        {
            result.append("\n[").append(index).append("] ").append(toString(p));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(SessionProfileStruct sp)
    {
        StringBuilder result = new StringBuilder();
        result.append("classKey:").append(sp.classKey)
              .append(" account:").append(sp.account)
              .append(" subAccount:").append(sp.subAccount)
              .append(" executingGiveupFirm:{")
                .append(toString(sp.executingGiveupFirm)).append('}')
              .append(" sessionName:").append(sp.sessionName)
              .append(" isAccountBlanked:").append(sp.isAccountBlanked)
              .append(" originCode:").append(sp.originCode);
        return result;
    }

    public static StringBuilder toString(SessionProfileStruct spseq[])
    {
        StringBuilder result = new StringBuilder();
        result.append(spseq.length).append("{");
        int index = 0;
        for (SessionProfileStruct sp : spseq)
        {
            result.append("\n[").append(index).append("] ")
                  .append(toString(sp));
            ++index;
        }
        result.append(" }");
        return result;
    }

    public static StringBuilder toString(SessionProfileUserStruct spu)
    {
        StringBuilder result = new StringBuilder();
        result.append("userAcronym:{").append(toString(spu.userAcronym))
              .append(" userId:").append(spu.userId)
              .append(" firm:{").append(toString(spu.firm)).append('}')
              .append(" fullName:").append(spu.fullName)
              .append(" role:").append(spu.role)
              .append(" executingGiveupFirms:")
                .append(toString(spu.executingGiveupFirms))
              .append(" accounts:").append(toString(spu.accounts))
              .append(" assignedClasses:").append(toString(spu.assignedClasses))
              .append(" dpms:").append(toString(spu.dpms))
              .append(" sessionProfilesByClass:")
                .append(toString(spu.sessionProfilesByClass))
              .append(" defaultSessionProfiles:")
                .append(toString(spu.defaultSessionProfiles))
              .append(" defaultProfile:{").append(toString(spu.defaultProfile))
              .append('}');
        return result;                
    }

    public static StringBuilder toString(UserStruct u)
    {
        StringBuilder result = new StringBuilder();
        result.append("userAcronym:{").append(toString(u.userAcronym))
                .append('}')
              .append(" userId:").append(u.userId)
              .append(" firm:{").append(toString(u.firm)).append('}')
              .append(" fullName:").append(u.fullName)
              .append(" role:").append(u.role)
              .append(" executingGiveUpFirms:")
                .append(toString(u.executingGiveupFirms))
              .append(" profilesByClass:").append(toString(u.profilesByClass))
              .append(" defaultProfile:{").append(toString(u.defaultProfile))
                .append('}')
              .append(" accounts:").append(toString(u.accounts))
              .append(" assignedClasses:").append(toString(u.assignedClasses))
              .append(" dpms:").append(toString(u.dpms));
        return result;
    }

// cmiUtil

    public static StringBuilder toString(DateStruct d)
    {
        StringBuilder date = new StringBuilder(10);
        date.append(d.year).append('-');
        if (d.month < 10)
        {
            date.append('0');
        }
        date.append(d.month).append('-');
        if (d.day < 10)
        {
            date.append('0');
        }
        date.append(d.day);
        return date;
    }

    public static StringBuilder toString(DateStruct dseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(dseq.length).append("{");
        for (DateStruct d : dseq)
        {
            sb.append(' ').append(d);
        }
        sb.append(" }");
        return sb;
    }

    public static DateStruct makeDateStruct(String s)
    {
        String parts[] = s.split("[-/]");
        if (parts.length != 3)
        {
            Log.message("Wrong number of values for date:" + s);
            return null;
        }
        DateStruct d = new DateStruct();
        d.year = Short.parseShort(parts[0]);
        d.month = Byte.parseByte(parts[1]);
        d.day = Byte.parseByte(parts[2]);
        return d;
    }

    public static StringBuilder toString(PriceStruct p)
    {
        StringBuilder result = new StringBuilder();
        if (p.type == PriceTypes.NO_PRICE)
        {
            result.append("$--");
        }
        else if (p.type == PriceTypes.MARKET)
        {
            result.append("$MKT");
        }
        else
        {
            if (p.type == PriceTypes.LIMIT)
            {
                result.append("L$");
            }
            else if (p.type == PriceTypes.VALUED)
            {
                result.append("V$");
            }
            else // CABINET
            {
                result.append("C$");
            }
            result.append(p.whole).append('.');
            int frac = p.fraction;
            int fdigits = 0;
            while (frac != 0)
            {
                int digit = frac / FRACTION_TOP_DIGIT;
                result.append(digit);
                frac = (frac % FRACTION_TOP_DIGIT) * 10;
                ++ fdigits;
            }
            while (fdigits < 2)
            {
                result.append("0");
                ++ fdigits;
            }
        }
        return result;
    }

    /** Turn a string into a PriceStruct.
     * Input may be $MKT or MKT for market price.
     * Input may be $-- or -- for no price.
     * Other prices have have a type indicator and/or a negative indicator,
     *   and must have a numerical part.
     * "L$" indicates a limit price, "C$" indicates a cabinet price;
     * "V$" or "$" or no such prefix indicates a valued price.
     * A minus sign "-" may appear before or after the above prefixes.
     * Numerical portion is whole and/or fraction portion, with a dot "."
     * after the last whole digit/before the first fractional digit. 
     * @param s String representing a price.
     * @return Converted value, or null on error.
     */
    public static PriceStruct makePriceStruct(String s)
    {
        PriceStruct ps = new PriceStruct();
        ps.whole = 0;
        ps.fraction  = 0;
        if (s.equals("$--") || s.equals("--"))
        {
            ps.type = PriceTypes.NO_PRICE;
        }
        else if (s.equalsIgnoreCase("$MKT") | s.equalsIgnoreCase("MKT"))
        {
            ps.type = PriceTypes.MARKET;
        }
        else
        {
            ps.type = PriceTypes.VALUED; // default

            int index = 0;
            int sign = 1;
            if (s.length() == 1)
            {
                // If only 1 character, it must be a digit
                char c = s.charAt(0);
                if (c < '0' || c > '9')
                {
                    Log.message("Invalid price:" + s);
                    return null;
                }
                ps.whole = c - '0';
                return ps;
            }
            if (s.startsWith("-"))
            {
                // Leading minus sign
                sign = -1;
                ++ index;
            }
            String head = s.substring(index, 2);
            if (head.equals("L$"))
            {
                ps.type = PriceTypes.LIMIT;
                index += 2;
            }
            else if (head.equals("C$"))
            {
                ps.type = PriceTypes.CABINET;
                index += 2;
            }
            else
            {
                ps.type = PriceTypes.VALUED;
                if (head.equals("V$"))
                {
                    index += 2;
                }
                else if (head.startsWith("$"))
                {
                    ++index;
                }
                else
                {
                    // No prefix, we must have numbers now
                    char c = s.charAt(index);
                    if (c < '0' || c > '9')
                    {
                        Log.message("Invalid price:" + s);
                        return null;
                    }
                }
            }
            if (index < s.length() && s.charAt(index) == '-' && sign == 1)
            {
                // Minus sign after the prefix
                sign = -1;
                ++index;
            }
            if (index >= s.length())
            {
                // All prefix and no numbers, no good
                Log.message("Invalid price:" + s);
                return null;
            }
            while (index < s.length() && s.charAt(index) >= '0' && s.charAt(index) <= '9')
            {
                ps.whole = ps.whole * 10 + s.charAt(index) - '0';
                ++index;
            }
            ps.whole *= sign;
            if (index >= s.length())
            {
                // Whole number only, we're done
                return ps;
            }
            if (s.charAt(index) != '.')
            {
                Log.message("Invalid price:" + s);
                return null;
            }
            ++index;
            int fracDigits = 0;
            while (index < s.length() && s.charAt(index) >= '0' && s.charAt(index) <= '9')
            {
                ps.fraction = ps.fraction * 10 + s.charAt(index) - '0';
                ++index;
                ++fracDigits;
            }
            if (index < s.length())
            {
                Log.message("Invalid price:" + s);
                return null;
            }
            while (fracDigits < 9)
            {
                ps.fraction *= 10;
                ++fracDigits;
            }
        }
        return ps;
    }

    public static StringBuilder toString(TimeStruct t)
    {
        StringBuilder time = new StringBuilder(11);
        time.append(t.hour).append(':');
        if (t.minute < 10)
        {
            time.append('0');
        }
        time.append(t.minute).append(':');
        if (t.second < 10)
        {
            time.append('0');
        }
        time.append(t.second).append('.');
        if (t.fraction < 10)
        {
            time.append('0');
        }
        time.append(t.fraction);
        return time;
    }

    public static StringBuilder toString(TimeStruct tseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(tseq.length).append("{");
        for (TimeStruct t : tseq)
        {
            sb.append(' ').append(t);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(DateTimeStruct dt)
    {
        return toString(dt.date).append('_').append(toString(dt.time));
    }

    public static StringBuilder toString(DateTimeStruct dtseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(dtseq.length).append("{");
        for (DateTimeStruct dt : dtseq)
        {
            sb.append(' ').append(dt);
        }
        sb.append(" }");
        return sb;
    }

    /** Create a DateTimeStruct from String input.
     * Print a message if input is in error.
     * @param s year-month-day-hour-minute-second-hundredths.
     *     Allowable delimiters are - . / : _ in any order or combination.
     *     second and hundredths (of a second) are optional.
     * @return Structure as interpreted, or null on error.
     */
    public static DateTimeStruct makeDateTimeStruct(String s)
    {
        String parts[] = s.split("[-./:_]");
        if (parts.length < 5 || parts.length > 7)
        {
            Log.message("Wrong number of values for dateTime:" + s);
            return null;
        }
        DateTimeStruct dt = new DateTimeStruct();
        dt.date = new DateStruct();
        dt.time = new TimeStruct();
        dt.date.year = Short.parseShort(parts[0]);
        dt.date.month = Byte.parseByte(parts[1]);
        dt.date.day = Byte.parseByte(parts[2]);
        dt.time.hour = Byte.parseByte(parts[3]);
        dt.time.minute = Byte.parseByte(parts[4]);
        dt.time.second = (parts.length > 5) ? Byte.parseByte(parts[5]) : 0;
        dt.time.fraction = (parts.length > 6) ? Byte.parseByte(parts[6]) : 0;
        return dt;
    }

    public static StringBuilder toString(CallbackInformationStruct cb)
    {
        StringBuilder sb = new StringBuilder(80);
        sb.append("subscriptionInterface:").append(cb.subscriptionInterface)
          .append(" subscriptionOperation:").append(cb.subscriptionOperation)
          .append(" subscriptionValue:").append(cb.subscriptionValue)
          .append(" ior:").append(cb.ior);
        return sb;
    }

    public static StringBuilder toString(CboeIdStruct id)
    {
        StringBuilder sb = new StringBuilder(45);
        sb.append(id.highCboeId).append(':').append(id.lowCboeId);
        return sb;
    }

    /** Create a CboeIdStruct from String input.
     * Print a message if input is in error.
     * @param s highCboeId:lowCboeId. Two decimal integers separated by
     *     a : character.
     * @return Structure as interpreted, or null on error.
     */
    public static CboeIdStruct makeCboeIdStruct(String s)
    {
        String parts[] = s.split(":");
        if (parts.length != 2)
        {
            Log.message("Wrong number of values for CboeIdStruct:" + s);
            return null;
        }

        CboeIdStruct ci = new CboeIdStruct();
        ci.highCboeId = Integer.parseInt(parts[0]);
        ci.lowCboeId = Integer.parseInt(parts[1]);
        return ci;
    }

    public static StringBuilder toString(KeyValueStruct kv)
    {
        StringBuilder sb = new StringBuilder(
            kv.key.length() + kv.value.length() + 2);
        sb.append(kv.key).append("->").append(kv.value);
        return sb;
    }

    public static StringBuilder toString(KeyValueStruct kvseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(kvseq.length).append("{");
        for (KeyValueStruct kv : kvseq)
        {
            sb.append(' ').append(kv);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(KeyDescriptionStruct kd)
    {
        StringBuilder sb = new StringBuilder(kd.description.length() + 14);
        sb.append(kd.key).append("=\"").append(kd.description).append('"');
        return sb;
    }

    public static StringBuilder toString(KeyDescriptionStruct kdseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(kdseq.length).append("{");
        for (KeyDescriptionStruct kd : kdseq)
        {
            sb.append(' ').append(kd);
        }
        sb.append(" }");
        return sb;
    }

    public static StringBuilder toString(OperationResultStruct or)
    {
        StringBuilder sb = new StringBuilder(or.errorMessage.length()+12);
        sb.append(or.errorCode).append(':').append(or.errorMessage);
        return sb;
    }

    public static StringBuilder toString(OperationResultStruct orseq[])
    {
        StringBuilder sb = new StringBuilder();
        sb.append(orseq.length).append("{");
        int index = 0;
        for (OperationResultStruct or : orseq)
        {
            sb.append("\n[").append(index).append("] ").append(toString(or));
            ++index;
        }
        sb.append(" }");
        return sb;
    }
}
