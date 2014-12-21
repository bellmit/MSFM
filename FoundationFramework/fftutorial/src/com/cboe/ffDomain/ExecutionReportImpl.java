package com.cboe.ffDomain;

import com.cboe.ffInterfaces.ExecutionReport;
import com.cboe.ffUtil.TimeHelper;
import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import java.lang.reflect.Field;
import java.util.Vector;

public class ExecutionReportImpl
    extends PersistentBObject
    implements ExecutionReport
{
    protected static final String TABLE_NAME="execution_rpt";

    private String acronym;
    private String productSymbol;
    private float price;
    private int volume;
    private char side;
    private int sentTime;

    public String getAcronym()
    {
        return (String)editor.get(_acronym, acronym);
    }
    public String getProductSymbol()
    {
        return (String)editor.get(_productSymbol, productSymbol);
    }
    public float getPrice()
    {
        return editor.get(_price, price);
    }
    public int getVolume()
    {
        return editor.get(_volume, volume);
    }
    public char getSide()
    {
        return editor.get(_side, side);
    }
    public int getSentTimeSeconds()
    {
        return editor.get(_sentTime, sentTime);
    }
    public TimeStruct getSentTime()
    {
        return TimeHelper.toStruct(getSentTimeSeconds());
    }

    public void setAcronym(String aValue)
    {
        editor.set(_acronym, aValue, acronym);
    }
    public void setProductSymbol(String aValue)
    {
        editor.set(_productSymbol, aValue, productSymbol);
    }
    public void setPrice(float aValue)
    {
        editor.set(_price, aValue, price);
    }
    public void setVolume(int aValue)
    {
        editor.set(_volume, aValue, volume);
    }
    public void setSide(char aValue)
    {
        editor.set(_side, aValue, side);
    }
    public void setSentTimeSeconds(int aValue)
    {
        editor.set(_sentTime, aValue, sentTime);
    }
    public void setSentTime(TimeStruct struct)
    {
        setSentTimeSeconds(TimeHelper.toSeconds(struct));
    }

    private static Field _acronym;
    private static Field _productSymbol;
    private static Field _price;
    private static Field _volume;
    private static Field _side;
    private static Field _sentTime;
    private static Vector classDescriptor;

    static
    {
        try
        {
            _acronym = ExecutionReportImpl.class.getDeclaredField("acronym");
            _productSymbol = ExecutionReportImpl.class.getDeclaredField("productSymbol");
            _price = ExecutionReportImpl.class.getDeclaredField("price");
            _volume = ExecutionReportImpl.class.getDeclaredField("volume");
            _side = ExecutionReportImpl.class.getDeclaredField("side");
            _sentTime = ExecutionReportImpl.class.getDeclaredField("sentTime");
            _acronym.setAccessible(true);
            _productSymbol.setAccessible(true);
            _price.setAccessible(true);
            _volume.setAccessible(true);
            _side.setAccessible(true);
            _sentTime.setAccessible(true);
        }
        catch (Exception ex)
        {
            System.err.println("Error creating Field objects");
            ex.printStackTrace(System.err);
        }
    }

    protected void initDescriptor() 
    {
        synchronized(ExecutionReportImpl.class)
        {
            if (classDescriptor != null)
            {
                return;
            }
            Vector tempDescriptor = getSuperDescriptor();
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("acr", _acronym));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_sym", _productSymbol));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("price", _price));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("vol", _volume));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("side", _side));
            tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("sent_time", _sentTime));
            classDescriptor = tempDescriptor;
        }
    }

    public ObjectChangesIF initializeObjectEditor()
    {
        final DBAdapter result = (DBAdapter)super.initializeObjectEditor();
        if (classDescriptor == null)
        {
            initDescriptor();
        }
        result.setTableName(TABLE_NAME);
        result.setClassDescription(classDescriptor);
        return result;
    }

    public ExecutionReportStruct toStruct()
    {
        ExecutionReportStruct struct = new ExecutionReportStruct();
        struct.symbol = getProductSymbol();
        struct.user = getAcronym();
        struct.side = getSide();
        struct.price = getPrice();
        struct.quantity = getVolume();
        struct.sentTime = getSentTime();
        return struct;
    }
}
