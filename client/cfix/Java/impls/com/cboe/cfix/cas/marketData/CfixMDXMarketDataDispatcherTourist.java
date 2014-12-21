package com.cboe.cfix.cas.marketData;

/**
 * Tourist Parameters:
 */

import com.cboe.client.util.tourist.AbstractTourist;
import com.cboe.client.util.DateHelper;
import com.cboe.client.util.IntegerHelper;
import com.cboe.client.util.Pair;
import com.cboe.client.util.ThreadHelper;
import com.cboe.client.util.collections.IntObjectArrayHolder;
import com.cboe.client.util.collections.IntIntMultipleValuesMap;
import com.cboe.client.util.collections.IntIntArrayHolderIF;
import com.cboe.client.util.collections.IntIntArrayHolder;
import com.cboe.cfix.interfaces.FixSessionIF;
import com.cboe.cfix.fix.session.FixSessionManagerLocator;
import com.cboe.cfix.util.DebugFlagBuilder;
import com.cboe.cfix.util.debugMarketDataMapper;
import com.cboe.interfaces.cfix.CfixMDXMarketDataDispatcherVisitor;
import com.cboe.interfaces.cfix.CfixMDXMarketDataDispatcherIF;
import com.cboe.interfaces.cfix.CfixMarketDataDispatcherInstrumentation;
import com.cboe.interfaces.cfix.CfixMarketDataConsumerHolder;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

import java.io.Writer;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Vivek Beniwal
 */
public class CfixMDXMarketDataDispatcherTourist extends AbstractTourist
{

    protected String[] mandatoryKeys = new String[]{"what"};

    public String[] getMandatoryKeys()
    {
        return mandatoryKeys;
    }

    public Writer visit(final Writer writer) throws Exception
    {
        String what = getValue("what");
        String dispatcherName = getValue("dispatcherName"); // not always available
        final String finalDispatcherName = dispatcherName;

        if ("viewDispatcherInformation".equals(what))
        {
            String sessionName = getValue("sessionName");
            FixSessionIF tempFixSession = null;
            if (sessionName != null)
            {
                tempFixSession = FixSessionManagerLocator.getFixSessionByName(sessionName);
                if (tempFixSession == null)
                {
                    writer.write("<error type=\"lookup\" text=\"no session named '" + sessionName + "'\"/>");
                    return writer;
                }
            }

            final FixSessionIF fixSession = tempFixSession;

            CfixMDXMarketDataDispatcherVisitor marketDataInstrumentationVisitor = new CfixMDXMarketDataDispatcherVisitor()
            {
                public Exception exception;

                public boolean visit(CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher) throws Exception
                {
                    if (finalDispatcherName != null && !cfixMDXMarketDataDispatcher.getName().equals(finalDispatcherName))
                    {
                        return true;
                    }

                    CfixMarketDataDispatcherInstrumentation cfixMarketDataDispatcherInstrumentation = cfixMDXMarketDataDispatcher.getCfixMarketDataDispatcherInstrumentation();

                    CfixKeyToConsumersMap cfixKeyToConsumersMap;
                    int processed;
                    int consumed;
                    int dispatched;
                    CfixKeyToConsumersMap[] cfixKeyToConsumersMaps = new CfixKeyToConsumersMap[2];

                    cfixMDXMarketDataDispatcher.debugGetSubscriptionMaps(cfixKeyToConsumersMaps);

                    writer.write("<dispatcher name=\"" + cfixMDXMarketDataDispatcher.getName() + "\" debugFlags=\"" + DebugFlagBuilder.stringizeDispatcherDebugFlags(cfixMDXMarketDataDispatcher.getDebugFlags()) + "\">");

                    if (!"instrumentation".equals(CfixMDXMarketDataDispatcherTourist.this.getValue("suppress")))
                    {
                        writer.write("<instrumentation>");

                        consumed   = cfixMarketDataDispatcherInstrumentation.getConsumedMessageCount();
                        processed  = cfixMarketDataDispatcherInstrumentation.getProcessedMessageCount();
                        dispatched = cfixMarketDataDispatcherInstrumentation.getDispatchedMessageCount();

                        if ((consumed | processed | dispatched) != 0)
                        {
                            writer.write("<" + cfixMDXMarketDataDispatcher.getHandledMarketDataTypeName() + ">");

                            writer.write("<consumed count=\"" + consumed   + "\" time=\"" + DateHelper.stringizeDateInLogFormat(cfixMarketDataDispatcherInstrumentation.getConsumedMessageTime()) + "\"/>");
                            writer.write("<processed count=\"" + processed  + "\" time=\"" + DateHelper.stringizeDateInLogFormat(cfixMarketDataDispatcherInstrumentation.getProcessedMessageTime()) + "\"/>");
                            writer.write("<dispatched count=\"" + dispatched + "\" time=\"" + DateHelper.stringizeDateInLogFormat(cfixMarketDataDispatcherInstrumentation.getDispatchedMessageTime()) + "\"/>");

                            writer.write("</" + cfixMDXMarketDataDispatcher.getHandledMarketDataTypeName() + ">");
                        }

                        writer.write("</instrumentation>");
                    }

                    if (!"map".equals(CfixMDXMarketDataDispatcherTourist.this.getValue("suppress")))
                    {
                        CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder;
                        IntObjectArrayHolder intObjectArrayHolder = new IntObjectArrayHolder();
                        int key;

                        writer.write("<maps count=\"2\">");

                        for (int i = 0; i < cfixKeyToConsumersMaps.length; i++)
                        {
                            cfixKeyToConsumersMap = cfixKeyToConsumersMaps[i];

                            writer.write("<map name=\"" + cfixKeyToConsumersMap.getName() + "\" keycount=\"" + cfixKeyToConsumersMap.size() + "\">");

                            intObjectArrayHolder.clear();

                            cfixKeyToConsumersMap.getData(intObjectArrayHolder);

                            if (fixSession != null)
                            {
                                for (int j = 0; j < intObjectArrayHolder.size(); j++)
                                {
                                    key                             = intObjectArrayHolder.getKey(j);
                                    cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) intObjectArrayHolder.getValue(j);
                                    if (fixSession == cfixFixMarketDataConsumerHolder.getCfixMarketDataConsumer())
                                    {
                                        writer.write("<key value=\"" + key + "\" session=\"" + cfixFixMarketDataConsumerHolder.getCfixMarketDataConsumer() + "\" mdReqID=\"" + cfixFixMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID() + "\"");
                                        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
                                        {
                                            writer.write(" symbol=\"" + cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.productSymbol + "\"");
                                            writer.write(" reportingClass=\"" + cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.reportingClass + "\"");
                                        }
                                        else
                                        {
                                            writer.write(" symbol=\"" + cfixFixMarketDataConsumerHolder.getSessionClassStruct().classStruct.classSymbol + "\"");
                                        }
                                        writer.write("/>");
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                String symbol = getValue("symbol");
                                boolean processSymbol = true;
                                boolean xmlKeyOpen = false;
                                int prevKey = 0;

                                for (int j = 0; j < intObjectArrayHolder.size(); j++)
                                {
                                    key                             = intObjectArrayHolder.getKey(j);
                                    cfixFixMarketDataConsumerHolder = (CfixMarketDataConsumerHolder) intObjectArrayHolder.getValue(j);

                                    if (prevKey != 0 && key != prevKey && xmlKeyOpen)
                                    {
                                        xmlKeyOpen = false;
                                        writer.write("</key>");
                                    }

                                    if (key != prevKey)
                                    {
                                        if (cfixFixMarketDataConsumerHolder.containsSessionProductStruct())
                                        {
                                            if (symbol == null ||
                                               (symbol.equals(cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.productSymbol) ||
                                                symbol.equals(cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.reportingClass)))
                                            {
                                                writer.write("<key value=\"" + key + "\" count=\"" + cfixKeyToConsumersMap.countConsumersForKey(key) + "\"");
                                                writer.write(" symbol=\"" + cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.productSymbol + "\"");
                                                writer.write(" reportingClass=\"" + cfixFixMarketDataConsumerHolder.getSessionProductStruct().productStruct.productName.reportingClass + "\">");
                                                processSymbol = true;
                                                xmlKeyOpen = true;
                                            }
                                            else
                                            {
                                                processSymbol = false;
                                            }
                                        }
                                        else
                                        {
                                            if (symbol == null ||
                                                symbol.equals(cfixFixMarketDataConsumerHolder.getSessionClassStruct().classStruct.classSymbol))
                                            {
                                                writer.write("<key value=\"" + key + "\" count=\"" + cfixKeyToConsumersMap.countConsumersForKey(key) + "\"");
                                                writer.write(" symbol=\"" + cfixFixMarketDataConsumerHolder.getSessionClassStruct().classStruct.classSymbol + "\">");
                                                processSymbol = true;
                                                xmlKeyOpen = true;
                                            }
                                            else
                                            {
                                                processSymbol = false;
                                            }
                                        }
                                    }

                                    if (processSymbol)
                                    {
                                        writer.write("<value session=\"" + cfixFixMarketDataConsumerHolder.getCfixMarketDataConsumer() + "\" mdReqID=\"" + cfixFixMarketDataConsumerHolder.getOverlayPolicyMarketDataList().getMdReqID() + "\"/>");
                                    }

                                    prevKey = key;
                                }

                                if (prevKey != 0 && xmlKeyOpen)
                                {
                                    writer.write("</key>");
                                }
                            }

                            writer.write("</map>");
                        }

                        writer.write("</maps>");
                    }

                    writer.write("</dispatcher>");

                    return true;
                }

                public boolean exceptionHappened(Exception exception) throws Exception
                {
                    this.exception = exception;
                    return false;
                }

                public Exception getException() throws Exception
                {
                    return exception;
                }
            };

            writer.write("<dispatchers count=\"" + CfixMarketDataDispatcherHomeImpl.getInstance().size() + "\">");

            CfixMarketDataDispatcherHomeImpl.getInstance().accept(marketDataInstrumentationVisitor);

            writer.write("</dispatchers>");

            if (marketDataInstrumentationVisitor.getException() != null)
            {
                throw marketDataInstrumentationVisitor.getException();
            }

            return writer;
        }

        if ("setDebugFlags".equals(what))
        {
            CfixMDXMarketDataDispatcherVisitor marketDataSetDebugFlagsVisitor;
            String debugFlag            = getValue("debugFlag");

            if (debugFlag != null && debugFlag.length() > 0)
            {
                final int newDebugFlag = DebugFlagBuilder.buildDispatcherDebugFlags(debugFlag);
                if (newDebugFlag == IntegerHelper.INVALID_VALUE)
                {
                    writer.write("<error type=\"data\" text=\"can't parseInt debugFlag value '" + debugFlag + "'\"/>");
                    return writer;
                }

                marketDataSetDebugFlagsVisitor = new CfixMDXMarketDataDispatcherVisitor()
                {
                    public Exception exception;

                    public boolean visit(CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher) throws Exception
                    {
                        if ("*".equals(finalDispatcherName))
                        {
                            int oldDebugFlag = cfixMDXMarketDataDispatcher.setDebugFlags(newDebugFlag);

                            writer.write("<dispatcher name=\"" + cfixMDXMarketDataDispatcher.getName() + "\" newDebugFlag=\"" + DebugFlagBuilder.stringizeDispatcherDebugFlags(newDebugFlag) + "\" oldDebugFlag=\"" + DebugFlagBuilder.stringizeDispatcherDebugFlags(oldDebugFlag) + "\"/>");
                        }
                        else if (cfixMDXMarketDataDispatcher.getName().equals(finalDispatcherName))
                        {
                            int oldDebugFlag = cfixMDXMarketDataDispatcher.setDebugFlags(newDebugFlag);

                            writer.write("<dispatcher name=\"" + cfixMDXMarketDataDispatcher.getName() + "\" newDebugFlag=\"" + DebugFlagBuilder.stringizeDispatcherDebugFlags(newDebugFlag) + "\" oldDebugFlag=\"" + DebugFlagBuilder.stringizeDispatcherDebugFlags(oldDebugFlag) + "\"/>");

                            return false;
                        }

                        return true;
                    }

                    public boolean exceptionHappened(Exception exception) throws Exception
                    {
                        this.exception = exception;
                        return false;
                    }

                    public Exception getException() throws Exception
                    {
                        return exception;
                    }
                };

                CfixMarketDataDispatcherHomeImpl.getInstance().accept(marketDataSetDebugFlagsVisitor);
            }
            else
            {
                marketDataSetDebugFlagsVisitor = new CfixMDXMarketDataDispatcherVisitor()
                {
                    public Exception exception;

                    public boolean visit(CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher) throws Exception
                    {
                        if (finalDispatcherName.equals(cfixMDXMarketDataDispatcher.getName()))
                        {
                            writer.write("<dispatcher name=\"" + cfixMDXMarketDataDispatcher.getName() + "\" debugFlag=\"" + cfixMDXMarketDataDispatcher.getDebugFlags() + "\"/>");
                        }

                        return true;
                    }

                    public boolean exceptionHappened(Exception exception) throws Exception
                    {
                        this.exception = exception;
                        return false;
                    }

                    public Exception getException() throws Exception
                    {
                        return exception;
                    }
                };

                writer.write("<dispatchers count=\"" + CfixMarketDataDispatcherHomeImpl.getInstance().size() + "\">");

                CfixMarketDataDispatcherHomeImpl.getInstance().accept(marketDataSetDebugFlagsVisitor);

                writer.write("</dispatchers>");
            }

            if (marketDataSetDebugFlagsVisitor.getException() != null)
            {
                throw marketDataSetDebugFlagsVisitor.getException();
            }

            return writer;
        }

        if ("submitCurrentMarket".equals(what))
        {
            String filename    = getValue("fileName");
            String sessionName = getValue("sessionName");

            int nRepeat;
            int sleepBetween;

            nRepeat = IntegerHelper.parseInt(getValue("repeat"));
            if (nRepeat == IntegerHelper.INVALID_VALUE)
            {
                nRepeat = 0;
            }

            sleepBetween = IntegerHelper.parseInt(getValue("sleepBetween"));
            if (sleepBetween == IntegerHelper.INVALID_VALUE)
            {
                sleepBetween = 0;
            }

            if (sessionName == null)
            {
                sessionName = "W_MAIN";
            }

            if (filename == null)
            {
                filename = "/tmp/dmitry.currentMarket";
            }

            writer.write("<debug repeat=\"" + nRepeat + "\" sleepBetween=\"" + sleepBetween + "\" fileName=\"" + filename + "\"/>");

            final Pair dispatcherPair = new Pair();
            dispatcherName = sessionName + "{CurrentMarket}";

            CfixMDXMarketDataDispatcherVisitor marketDataInstrumentationVisitor = new CfixMDXMarketDataDispatcherVisitor()
            {
                public Exception exception;

                public boolean visit(CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher) throws Exception
                {
                    if (finalDispatcherName.equals(cfixMDXMarketDataDispatcher.getName()))
                    {
                        dispatcherPair.setFirst(cfixMDXMarketDataDispatcher);
                        return false;
                    }

                    return true;
                }

                public boolean exceptionHappened(Exception exception) throws Exception
                {
                    this.exception = exception;
                    return false;
                }

                public Exception getException() throws Exception
                {
                    return exception;
                }
            };

            CfixMarketDataDispatcherHomeImpl.getInstance().accept(marketDataInstrumentationVisitor);

            if (marketDataInstrumentationVisitor.getException() != null)
            {
                throw marketDataInstrumentationVisitor.getException();
            }

            if (dispatcherPair.getFirst() == null)
            {
                Log.information("DISPATCHER: NO DISPATCHER(" + dispatcherName + ")");
                return writer;
            }

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            int[] ints = new int[3];
            IntIntMultipleValuesMap optionMap = new IntIntMultipleValuesMap();
            IntIntMultipleValuesMap futureMap = new IntIntMultipleValuesMap();
            String line;

            for (; (line = reader.readLine()) != null;)
            {
                if (line.trim().length() == 0 || line.startsWith("#"))
                {
                    continue;
                }

                if (IntegerHelper.parseInts(line, ints) == 3)
                {
                    if (ints[0] == ProductTypes.OPTION)
                    {
                        optionMap.putKeyValue(ints[1], ints[2]);
                    }
                    else if (ints[0] == ProductTypes.FUTURE)
                    {
                        futureMap.putKeyValue(ints[1], ints[2]);
                    }
                }
            }
            reader.close();

            CurrentMarketStruct[] currentMarketStructs;
            CurrentMarketStruct[] partial_currentMarketStructs4 = new CurrentMarketStruct[4];
            CurrentMarketStruct[] partial_currentMarketStructs3 = new CurrentMarketStruct[3];
            CurrentMarketStruct[] partial_currentMarketStructs2 = new CurrentMarketStruct[2];
            CurrentMarketStruct[] partial_currentMarketStructs1 = new CurrentMarketStruct[1];
            CfixMarketDataDispatcherImpl cfixMarketDataDispatcher = (CfixMarketDataDispatcherImpl) dispatcherPair.getFirst();

            ChannelKey channelKey = new ChannelKey(ChannelType.CURRENT_MARKET_BY_CLASS, null);

            debugMarketDataMapper debugMarketDataMapper = new debugMarketDataMapper();

            int numberClassesDispatched = 0;
            int numberProductsDispatched = 0;
            int i;
            int j;
            IntIntArrayHolderIF arrayHolder = new IntIntArrayHolder();
            optionMap.getData(arrayHolder);

            j = 0;
            for (i = 0; i <= nRepeat; i++)
            {
                currentMarketStructs = debugMarketDataMapper.makeCurrentMarketStruct(i, sessionName, ProductTypes.OPTION, arrayHolder.getKey(0), arrayHolder.values(), false);

                j = 0;

                while (currentMarketStructs.length - j >= 4)
                {
                    partial_currentMarketStructs4[0] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[1] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[2] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[3] = currentMarketStructs[j++];
                    cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs4));
                }

                switch (currentMarketStructs.length - j)
                {
                    case 3:
                        partial_currentMarketStructs3[0] = currentMarketStructs[j++];
                        partial_currentMarketStructs3[1] = currentMarketStructs[j++];
                        partial_currentMarketStructs3[2] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs3));
                        break;
                    case 2:
                        partial_currentMarketStructs2[0] = currentMarketStructs[j++];
                        partial_currentMarketStructs2[1] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs2));
                        break;
                    case 1:
                        partial_currentMarketStructs1[0] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs1));
                        break;
                }

                numberClassesDispatched++;
                numberProductsDispatched += arrayHolder.size();

                if (sleepBetween > 0 && nRepeat > 0)
                {
                    ThreadHelper.sleep(sleepBetween);
                }
            }

            if (j > 0)
            {
                writer.write("<debug type=\"OPTION\">ClassesDispatched(" + numberClassesDispatched + ") ProductsDispatched(" + numberProductsDispatched + ")</debug>");
            }

            arrayHolder.clear();

            futureMap.getData(arrayHolder);

            numberClassesDispatched = 0;
            numberProductsDispatched = 0;

            j = 0;
            for (i = 0; i <= nRepeat; i++)
            {
                currentMarketStructs = debugMarketDataMapper.makeCurrentMarketStruct(i, sessionName, ProductTypes.FUTURE, arrayHolder.getKey(0), arrayHolder.values(), false);

                j = 0;

                while (currentMarketStructs.length - j >= 4)
                {
                    partial_currentMarketStructs4[0] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[1] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[2] = currentMarketStructs[j++];
                    partial_currentMarketStructs4[3] = currentMarketStructs[j++];
                    cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs4));
                }

                switch (currentMarketStructs.length - j)
                {
                    case 3:
                        partial_currentMarketStructs3[0] = currentMarketStructs[j++];
                        partial_currentMarketStructs3[1] = currentMarketStructs[j++];
                        partial_currentMarketStructs3[2] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs3));
                        break;
                    case 2:
                        partial_currentMarketStructs2[0] = currentMarketStructs[j++];
                        partial_currentMarketStructs2[1] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs2));
                        break;
                    case 1:
                        partial_currentMarketStructs1[0] = currentMarketStructs[j++];
                        cfixMarketDataDispatcher.channelUpdate(new ChannelEvent(null, channelKey, partial_currentMarketStructs1));
                        break;
                }

                numberClassesDispatched++;
                numberProductsDispatched += arrayHolder.size();

                if (sleepBetween > 0 && nRepeat > 0)
                {
                    ThreadHelper.sleep(sleepBetween);
                }
            }

            if (j > 0)
            {
                writer.write("<debug type=\"FUTURE\">ClassesDispatched(" + numberClassesDispatched + ") ProductsDispatched(" + numberProductsDispatched + ")</debug>");
            }

            return writer;
        }

        return writer;
    }
}
