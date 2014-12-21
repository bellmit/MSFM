package com.cboe.ffDomain;

import com.cboe.ffInterfaces.Product;
import com.cboe.infrastructureServices.foundationFramework.PersistentBObject;
import com.cboe.infrastructureServices.persistenceService.AttributeDefinition;
import com.cboe.infrastructureServices.persistenceService.DBAdapter;
import com.cboe.infrastructureServices.persistenceService.ObjectChangesIF;
import java.lang.reflect.Field;
import java.util.Vector;

public class ProductImpl
    extends PersistentBObject
    implements Product
{
    protected static final String TABLE_NAME="product";

    private String symbol;
    private String fullName;

    public void setSymbol(String value)
    {
        editor.set(_symbol, value, symbol);
    }

    public void setFullName(String value)
    {
        editor.set(_fullName, value, fullName);
    }

    public String getSymbol()
    {
        return (String)editor.get(_symbol, symbol);
    }

    public String getFullName()
    {
        return (String)editor.get(_fullName, fullName);
    }

    private static Field _symbol;
    private static Field _fullName;
    private static Vector classDescriptor;

    static
    {
        try
        {
            _symbol = ProductImpl.class.getDeclaredField("symbol");
            _fullName = ProductImpl.class.getDeclaredField("fullName");
            _symbol.setAccessible(true);
            _fullName.setAccessible(true);
        }
        catch (Exception ex)
        {
            System.err.println("Error creating Field objects");
            ex.printStackTrace(System.err);
        }
    }

    protected void initDescriptor() 
    {
        synchronized(ProductImpl.class)
        {
            if (classDescriptor != null)
            {
                return;
            }
            Vector tempDecriptor = getSuperDescriptor();
            tempDecriptor.addElement(AttributeDefinition.getAttributeRelation("sym", _symbol));
            tempDecriptor.addElement(AttributeDefinition.getAttributeRelation("full_name", _fullName));
            classDescriptor = tempDecriptor;
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
}
