/*
 * Created on Apr 8, 2005
 * Used for creating market data history entries.
 *
 */
package com.cboe.domain.marketData;

import com.objectwave.persist.SQLInsert;
import com.objectwave.persist.QueryException;
import com.objectwave.persist.SQLAssembler;
import com.cboe.domain.util.PriceSqlType;

/**
 * @author Singh
 */
public class SQLMarketDataHistoryInsert extends SQLInsert
{
    public static final int NUM_COLUMNS = 43;

    public static String _columnString = null;

    public static String _prepString = null;

    private static String table = MarketDataHistoryEntryImpl.TABLE_NAME;

    private MarketDataHistoryEntryImpl obj = null;

    private static StringBuffer insertStatementBuffer;

    static
    {
        getStringTillColumn();

        getPString();
    }

    public SQLMarketDataHistoryInsert(MarketDataHistoryEntryImpl pojo)
    {
        obj = pojo;
    }

    public static String getStringTillColumn()
    {
        if (_columnString == null)
        {
            StringBuffer buf = new StringBuffer(" INSERT INTO ");

            buf.append(table);
            buf.append(" ( ask_price,  ");
            buf.append("ask_size,  ");
            buf.append("best_pub_ask_cust_size,  ");
            buf.append("best_pub_ask_price,  ");
            buf.append("best_pub_ask_size,  ");
            buf.append("best_pub_bid_cust_size,  ");
            buf.append("best_pub_bid_price,  ");
            buf.append("best_pub_bid_size,  ");
            buf.append("bid_price,  ");
            buf.append("bid_size,  ");
            buf.append("botr_ask_exchanges,  ");
            buf.append("botr_ask_price,  ");
            buf.append("botr_bid_exchanges,  ");
            buf.append("botr_bid_price,  ");
            buf.append("broker,  ");
            buf.append("contra,  ");
            buf.append("dayOfWeek,  ");
            buf.append("entry_time,  ");
            buf.append("entry_type,  ");
            buf.append("eop_type,  ");
            buf.append("exchanges_indicators,  ");
            buf.append("imbalance_qty,  ");
            buf.append("last_sale_price,  ");
            buf.append("last_sale_vol,  ");
            buf.append("nbbo_ask_exchanges,  ");
            buf.append("nbbo_ask_price,  ");
            buf.append("nbbo_bid_exchanges,  ");
            buf.append("nbbo_bid_price,  ");
            buf.append("non_cont_ask_price,  ");
            buf.append("non_cont_ask_size,  ");
            buf.append("non_cont_bid_price,  ");
            buf.append("non_cont_bid_size,  ");
            buf.append("override_indicator,  ");
            buf.append("physical_location,  ");
            buf.append("prod_key,  ");
            buf.append("product_state,  ");
            buf.append("session_name,  ");
            buf.append("ticker_prefix,  ");
            buf.append("trade_id,  ");
            buf.append("trade_through_indicator,  ");
            buf.append("undly_last_sale_price, ");
            buf.append("databaseIdentifier, ");
            buf.append("trade_server_id) ");
            _columnString = buf.toString();
        }
        return _columnString;
    }

    public static String getPString()
    {
        if (_prepString == null)
        {
            StringBuffer sb = new StringBuffer(getStringTillColumn());
            sb.append(" VALUES (");
            for (int i = 0; i < (NUM_COLUMNS - 1); i++)
            {
                sb.append(" ? ,");
            }
            sb.append(" ? )");
            _prepString = sb.toString();
        }
        return _prepString;
    }

    public String getPreparedString()
    {
        return getPString();
    }

    public StringBuffer getSqlStatement()
    {
        StringBuffer sb = new StringBuffer(getStringTillColumn());
        sb.append(" VALUES (");

        sb.append(obj.getAskPrice() + ", ");
        sb.append(obj.getAskSize() + ", ");
        sb.append(obj.getBestPublicCustomerAskSize() + ", ");
        sb.append(obj.getBestPublicAskPrice() + ", ");
        sb.append(obj.getBestPublicAskSize() + ", ");
        sb.append(obj.getBestPublicCustomerBidSize() + ", ");
        sb.append(obj.getBestPublicBidPrice() + ", ");
        sb.append(obj.getBestPublicBidSize() + ", ");
        sb.append(obj.getBidPrice() + ", ");
        sb.append(obj.getBidSize() + ", ");
        sb.append(obj.getBotrAskExchange() + ", ");
        sb.append(obj.getBotrAskPrice() + ", ");
        sb.append(obj.getBotrBidExchange() + ", ");
        sb.append(obj.getBotrBidPrice() + ", ");
        sb.append(obj.getBrokerHolder() + ", ");
        sb.append(obj.getContraHolder() + ", ");
        sb.append(obj.getDayOfWeek() + ", ");
        sb.append(obj.getEntryTime() + ", ");
        sb.append(obj.getEntryType() + ", ");
        sb.append(obj.getEopType() + ", ");
        sb.append(obj.getExchangeIndicators() + ", ");
        sb.append(obj.getImbalanceQuantity() + ", ");
        sb.append(((PriceSqlType) obj.getLastSalePrice()) + ", ");
        sb.append(obj.getLastSaleVolume() + ", ");
        sb.append(obj.getNbboAskExchange() + ", ");
        sb.append(obj.getNbboAskPrice() + ", ");
        sb.append(obj.getNbboBidExchange() + ", ");
        sb.append(obj.getNbboBidPrice() + ", ");
        sb.append(obj.getBestLimitAskPrice() + ", ");
        sb.append(obj.getBestLimitAskSize() + ", ");
        sb.append(obj.getBestLimitBidPrice() + ", ");
        sb.append(obj.getBestLimitBidSize() + ", ");
        sb.append(obj.getOverrideIndicator() + ", ");
        sb.append(obj.getPhysicalLocation() + ", ");
        sb.append(obj.getProductKey() + ", ");
        sb.append(obj.getProductState() + ", ");
        sb.append(obj.getSessionName() + ", ");
        sb.append(obj.getTickerPrefix() + ", ");
        sb.append(obj.getTradeID() + ", ");
        sb.append(obj.getTradeThroughIndicatorAsChar() + ", ");
        sb.append(obj.getUnderlyingLastSalePrice() + ", ");
        sb.append(obj.getDatabaseIdentifier() + ", ");
        sb.append(obj.getTradeServerId() + ")");
        return sb;
    }

    public void bindValues(final java.sql.PreparedStatement stmt, final Class aClass, final boolean verbose)
            throws java.sql.SQLException, QueryException
    {
        try
        {
            stmt.setString(1, obj.getAskPrice() != null ? obj.getAskPrice().toDatabaseString() : null);
            stmt.setInt(2, obj.getAskSize());
            stmt.setInt(3, obj.getBestPublicCustomerAskSize());
            stmt.setString(4, obj.getBestPublicAskPrice() != null ? obj.getBestPublicAskPrice().toDatabaseString() : null);
            stmt.setInt(5, obj.getBestPublicAskSize());
            stmt.setInt(6, obj.getBestPublicCustomerBidSize());
            stmt.setString(7, obj.getBestPublicBidPrice() != null ? obj.getBestPublicBidPrice().toDatabaseString() : null);
            stmt.setInt(8, obj.getBestPublicBidSize());
            stmt.setString(9, obj.getBidPrice() != null ? obj.getBidPrice().toDatabaseString() : null);
            stmt.setInt(10, obj.getBidSize());
            stmt.setString(11, obj.getBotrAskExchange() != null ? obj.getBotrAskExchange().toDatabaseString() : null);
            stmt.setString(12, obj.getBotrAskPrice() != null ? obj.getBotrAskPrice().toDatabaseString() : null);
            stmt.setString(13, obj.getBotrBidExchange() != null ? obj.getBotrBidExchange().toDatabaseString() : null);
            stmt.setString(14, obj.getBotrBidPrice() != null ? obj.getBotrBidPrice().toDatabaseString() : null);
            stmt.setString(15, obj.getBrokerHolder() != null ? obj.getBrokerHolder().toDatabaseString() : null);
            stmt.setString(16, obj.getContraHolder() != null ? obj.getContraHolder().toDatabaseString() : null);
            stmt.setByte(17, obj.getDayOfWeek());
            stmt.setLong(18, obj.getEntryTime());
            stmt.setShort(19, obj.getEntryType());
            stmt.setShort(20, obj.getEopType());
            stmt.setString(21, obj.getExchangeIndicators() != null ? obj.getExchangeIndicators().toDatabaseString() : null);
            stmt.setInt(22, obj.getImbalanceQuantity());
            stmt.setString(23, obj.getLastSalePrice() != null ? ((PriceSqlType) obj.getLastSalePrice()).toDatabaseString() : null);
            stmt.setInt(24, obj.getLastSaleVolume());
            stmt.setString(25, obj.getNbboAskExchange() != null ? obj.getNbboAskExchange().toDatabaseString() : null);
            stmt.setString(26, obj.getNbboAskPrice() != null ? obj.getNbboAskPrice().toDatabaseString() : null);
            stmt.setString(27, obj.getNbboBidExchange() != null ? obj.getNbboBidExchange().toDatabaseString() : null);
            stmt.setString(28, obj.getNbboBidPrice() != null ? obj.getNbboBidPrice().toDatabaseString() : null);
            stmt.setString(29, obj.getBestLimitAskPrice() != null ? obj.getBestLimitAskPrice().toDatabaseString() : null);
            stmt.setInt(30, obj.getBestLimitAskSize());
            stmt.setString(31, obj.getBestLimitBidPrice() != null ? obj.getBestLimitBidPrice().toDatabaseString() : null);
            stmt.setInt(32, obj.getBestLimitBidSize());
            getPrepStatementUtil().bindAsStringValue(stmt, 33, new Character(obj.getOverrideIndicator()), false);
            stmt.setString(34, obj.getPhysicalLocation());
            stmt.setLong(35, obj.getProductKey());
            stmt.setShort(36, obj.getProductState());
            stmt.setString(37, obj.getSessionName());
            stmt.setString(38, obj.getTickerPrefix());
            stmt.setLong(39, obj.getTradeID());
            getPrepStatementUtil().bindAsStringValue(stmt, 40, new Character(obj.getTradeThroughIndicatorAsChar()), false);
            stmt.setString(41, obj.getUnderlyingLastSalePrice() != null ? obj.getUnderlyingLastSalePrice().toDatabaseString()
                    : null);
            getPrepStatementUtil().bindAsStringValue(stmt, 42, obj.getDatabaseIdentifier(), false);
            stmt.setByte(43, obj.getTradeServerId());
        }
        catch (Exception ex)
        {
            throw new QueryException(ex.toString(), ex);
        }
    }

    public void setAvailableForPool(boolean value)
    {
        // do nothing
    }

    public void copyValuesFrom(SQLAssembler assembler)
    {
        obj = ((SQLMarketDataHistoryInsert) assembler).getMyObj();
    }

    public MarketDataHistoryEntryImpl getMyObj()
    {
        return obj;
    }

    public boolean equivalent(SQLAssembler assembler)
    {
        if (assembler instanceof SQLMarketDataHistoryInsert)
            return true;
        return false;
    }
}
