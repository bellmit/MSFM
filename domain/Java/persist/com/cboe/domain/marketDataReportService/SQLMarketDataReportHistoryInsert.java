package com.cboe.domain.marketDataReportService;

import com.objectwave.persist.SQLInsert;
import com.objectwave.persist.QueryException;
import com.objectwave.persist.SQLAssembler;
import com.cboe.domain.util.PriceSqlType;

/**
 * @author Cognizant Technology Solutions.
 *
 */
public class SQLMarketDataReportHistoryInsert extends SQLInsert
{
    /**
     * Number Of Column in MarketDataHistory table of reports
     */
    public static final int NUM_COLUMNS = 10;

    /**
     * String to hold insert statement including column names and table names 
     */
    public static String _columnString = null;

    /**
     * String to hold prepared statement created.
     */
    public static String _prepString = null;

    /**
     * String to hold table name
     */
    private static String table = MarketDataHistoryForReportsImpl.TABLE_NAME;

    /**
     * Reference to the history entry object for whichSQL query is to be created.
     */
    private MarketDataHistoryForReportsImpl obj = null;

    //Static block
    static
    {
        getStringTillColumn();

        getPString();
    }

    /**
     * Constructor
     * @param historyEntry
     */
    public SQLMarketDataReportHistoryInsert(MarketDataHistoryForReportsImpl historyEntry)
    {
        obj = historyEntry;
    }

    /**
     * Creates insert string including column names and table names 
     * @return String holding insert query till column names
     */ 
    public static String getStringTillColumn()
    {
        if (_columnString == null)
        {
            StringBuffer buf = new StringBuffer(" INSERT INTO ");

            buf.append(table + " ");
            buf.append("(session_name,  ");
            buf.append("prod_key,  ");
            buf.append("entry_type,  ");
            buf.append("entry_time,  ");
            buf.append("last_sale_price,  ");
            buf.append("last_sale_vol,  ");
            buf.append("product_state,  ");
            buf.append("ticker_prefix,  ");
            buf.append("dayOfWeek,  ");
            buf.append("databaseIdentifier) ");
            _columnString = buf.toString();
            
        }
        return _columnString;
    }

    /**
     * Gets prepared statements for insert query
     * @return Prepared statement created for an history entry.
     */
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

    /**
     * Gets prepared statement for insert on MarketDataHistort for reports
     * @return prepared statement query
     */
    public String getPreparedString()
    {
        return getPString();
    }

    /**
     * Gets SQL statement
     */
    public StringBuffer getSqlStatement()
    {
        StringBuffer sb = new StringBuffer(getStringTillColumn());
        sb.append(" VALUES (");
        sb.append(obj.getSessionName() + ", "); 
        sb.append(obj.getProductKey() + ", ");
        sb.append(obj.getEntryType() + ", ");
        sb.append(obj.getEntryTime() + ", ");
        sb.append(((PriceSqlType) obj.getLastSalePrice()) + ", ");
        sb.append(obj.getLastSaleVolume() + ", ");
        sb.append(obj.getProductState() + ", ");
        sb.append(obj.getTickerPrefix() + ", ");
        sb.append(obj.getDayOfWeek() + ", ");
        sb.append(obj.getDatabaseIdentifier() + ")");
        return sb;
    }

    /**
     * Bind values to be inserted into the SQL statement 
     */
    public void bindValues(final java.sql.PreparedStatement stmt, final Class aClass, final boolean verbose)
            throws java.sql.SQLException, QueryException
    {
        try
        {
            stmt.setString(1, obj.getSessionName());
            stmt.setLong(2, obj.getProductKey());
            stmt.setShort(3, obj.getEntryType());
            stmt.setLong(4, obj.getEntryTime());
            stmt.setString(5, obj.getLastSalePrice() != null ? ((PriceSqlType) obj.getLastSalePrice()).toDatabaseString() : null);
            stmt.setInt(6, obj.getLastSaleVolume());
            stmt.setShort(7, obj.getProductState());
            stmt.setString(8, obj.getTickerPrefix());
            stmt.setByte(9, obj.getDayOfWeek());
            getPrepStatementUtil().bindAsStringValue(stmt, 10, obj.getDatabaseIdentifier(), false);
        }
        catch (Exception ex)
        {
            throw new QueryException(ex.toString(), ex);
        }
    }

    public void copyValuesFrom(SQLAssembler assembler)
    {
        obj = ((SQLMarketDataReportHistoryInsert) assembler).getMyObj();
    }

    public MarketDataHistoryForReportsImpl getMyObj()
    {
        return obj;
    }

    public boolean equivalent(SQLAssembler assembler)
    {
        if (assembler instanceof SQLMarketDataReportHistoryInsert)
            return true;
        return false;
    }
}
