package com.cboe.internalPresentation.trade;

import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.trade.TradeReportStructV3;
import com.cboe.idl.constants.TradeTypes;
import com.cboe.idl.constants.TradeSources;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.interfaces.internalPresentation.trade.*;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.domain.util.TradeReportStructBuilder;

public class TradeReportFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private TradeReportFactory()
    {}

    /**
     * Creates an instance of a TradeReportImpl from a passed in TradeReportStruct.
     * @param tradeReportStruct to wrap in instance of TradeReport
     * @return TradeReport to represent the TradeReportStruct
     */
    public static TradeReport createTradeReport(TradeReportStructV2 tradeReportStruct)
    {
        return createTradeReportModel(tradeReportStruct);
    }

    public static TradeReportV3 createTradeReport(TradeReportStructV3 tradeReportStruct)
    {
        if (tradeReportStruct == null)
        {
            throw new IllegalArgumentException("TradeReportStructV3 can not be null.");
        }

        TradeReportV3 tradeReport = new TradeReportV3Impl(tradeReportStruct);

        return tradeReport;
    }


    /**
     * Creates an instance of a TradeReportImpl from a passed in tradeReportStruct.
     * @param tradeReportStruct to wrap in instance of TradeReportModel
     * @return TradeReportModel to represent the TradeReportStruct
     */
    public static TradeReportModel createTradeReportModel(TradeReportStructV2 tradeReportStruct)
    {
        TradeReportModel tradeReport = null;

        if ( tradeReportStruct == null )
        {
            throw new IllegalArgumentException("TradeReportStruct can not be null.");
        }

        tradeReport = new TradeReportImpl(tradeReportStruct);

        return tradeReport;
    }

    /**
     * Creates a default instance of a TradeReportImpl.
     * @return TradeReportModel
     */
    public static TradeReportModel createDefaultTradeReportModel()
    {
        TradeReportModel tradeReport = null;

        TradeReportStructV2 tradeReportStruct = TradeReportStructBuilder.buildTradeReportStructV2();

        tradeReport = new TradeReportImpl(tradeReportStruct);
//        tradeReport.setDefaults();

        return tradeReport;
    }

    /**
     * Creates a default instance of a Block TradeReportImpl.
     * @return TradeReportModel
     */
    public static TradeReportModel createDefaultBlockTradeReportModel()
    {
        TradeReportModel tradeReport = null;

        TradeReportStructV2 tradeReportStruct = TradeReportStructBuilder.buildTradeReportStructV2();

        tradeReportStruct.tradeReport.price.type = PriceTypes.VALUED;
        tradeReportStruct.tradeReport.tradeSource = TradeSources.MANUAL;
        tradeReportStruct.tradeReport.tradeType = TradeTypes.BLOCK_TRADE;

        tradeReport = new TradeReportImpl(tradeReportStruct);

        return tradeReport;
    }

    /**
     * Creates a default instance of a TradeReportImpl.
     * @return TradeReport
     */
    public static TradeReport createDefaultTradeReport()
    {
        return createDefaultTradeReportModel();
    }

    /**
     * Creates a default instance of a Block TradeReportImpl.
     * @return TradeReport
     */
    public static TradeReport createDefaultBlockTradeReport()
    {
        return createDefaultBlockTradeReportModel();
    }

    /**
     * Creates a default instance of a AtomicTradeModel
     * @return AtomicTradeModel
     */
    public static AtomicTradeModel createDefaultAtomicTradeModel()
    {
        AtomicTradeModel atomicTrade = null;

        AtomicTradeStruct atomicTradeStruct = TradeReportStructBuilder.buildAtomicTradeStruct();

        atomicTrade = new AtomicTradeImpl(atomicTradeStruct);

        return atomicTrade;
    }

    /**
     * Creates a default instance of a AtomicTrade.
     * @return AtomicTrade
     */
    public static AtomicTrade createDefaultAtomicTrade()
    {
        return createDefaultAtomicTradeModel();
    }

    /**
     * Creates an instance of a AtomicTradeImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeModel
     * @return AtomicTradeModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeModel createAtomicTradeModel(AtomicTradeStruct atomicTradeStruct)
    {
        AtomicTradeModel atomicTrade = null;

        if (atomicTradeStruct == null)
        {
            throw new IllegalArgumentException("AtomicTradeStruct can not be null.");
        }

        atomicTrade = new AtomicTradeImpl(atomicTradeStruct);

        return atomicTrade;
    }

    /**
     * Creates an instance of a AtomicTradeImpl from a passed in atomicTradeStruct, tradeId, and productKey.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeModel
     * @param tradeId to wrap in instance of AtomicTradeModel
     * @param productKey to wrap in instance of AtomicTradeModel
     * @return AtomicTradeModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeModel createAtomicTradeModel(AtomicTradeStruct atomicTradeStruct, CBOEId tradeId, int productKey)
    {
        AtomicTradeModel atomicTrade = null;

        if (atomicTradeStruct == null)
        {
            throw new IllegalArgumentException("AtomicTradeStruct can not be null.");
        }

        atomicTrade = new AtomicTradeImpl(atomicTradeStruct, tradeId, productKey);

        return atomicTrade;
    }

    /**
     * Creates an instance of a AtomicTradeImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeModel
     * @param tradeReport represented by this atomicTradeStruct
     * @return AtomicTradeModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeModel createAtomicTradeModel(AtomicTradeStruct atomicTradeStruct, TradeReport tradeReport)
    {
        AtomicTradeModel atomicTrade = null;

        if ( atomicTradeStruct == null )
        {
            throw new IllegalArgumentException("AtomicTradeStruct can not be null.");
        }

        if (tradeReport == null)
        {
            throw new IllegalArgumentException("TradeReport can not be null.");
        }

        atomicTrade = new AtomicTradeImpl(atomicTradeStruct, tradeReport);

        return atomicTrade;
    }

    /**
     * Creates an instance of a AtomicTradeImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTrade
     * @param tradeReport represented by this atomicTradeStruct
     * @return AtomicTrade to represent the AtomicTradeStruct
     */
    public static AtomicTrade createAtomicTrade(AtomicTradeStruct atomicTradeStruct, TradeReport tradeReport)
    {
        return createAtomicTradeModel(atomicTradeStruct, tradeReport);
    }

    /**
     * Creates an instance of a AtomicTradeImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTrade
     * @return AtomicTrade to represent the AtomicTradeStruct
     */
    public static AtomicTrade createAtomicTrade(AtomicTradeStruct atomicTradeStruct)
    {
        return createAtomicTradeModel(atomicTradeStruct);
    }

    /**
     * Creates an instance of a AtomicTradeBuySideImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeSideModel
     * @return AtomicTradeBuySideModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeSideModel createAtomicTradeBuySideModel(AtomicTradeStruct atomicTradeStruct)
    {
        AtomicTradeSideModel atomicTradeSide = null;

        if ( atomicTradeStruct == null )
        {
            throw new IllegalArgumentException("AtomicTradeStruct can not be null.");
        }

        atomicTradeSide = new AtomicTradeBuySideImpl(atomicTradeStruct);

        return atomicTradeSide;
    }
    /**
     * Creates an instance of a AtomicTradeBuySideImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeSideModel
     * @return AtomicTradeBuySideModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeSide createAtomicTradeBuySide(AtomicTradeStruct atomicTradeStruct)
    {
        return createAtomicTradeBuySideModel(atomicTradeStruct);
    }

    /**
     * Creates an instance of a AtomicTradeSellSideImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeSideModel
     * @return AtomicTradeSellSideModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeSideModel createAtomicTradeSellSideModel(AtomicTradeStruct atomicTradeStruct)
    {
        AtomicTradeSideModel atomicTradeSide = null;

        if ( atomicTradeStruct == null )
        {
            throw new IllegalArgumentException("AtomicTradeStruct can not be null.");
        }

        atomicTradeSide = new AtomicTradeSellSideImpl(atomicTradeStruct);

        return atomicTradeSide;
    }
    /**
     * Creates an instance of a AtomicTradeSellSideImpl from a passed in atomicTradeStruct.
     * @param atomicTradeStruct to wrap in instance of AtomicTradeSideModel
     * @return AtomicTradeSellSideModel to represent the AtomicTradeStruct
     */
    public static AtomicTradeSide createAtomicTradeSellSide(AtomicTradeStruct atomicTradeStruct)
    {
        return createAtomicTradeSellSideModel(atomicTradeStruct);
    }

}
