package com.cboe.ffDomain;

import com.cboe.ffidl.ffExceptions.NotFoundException;
import com.cboe.ffInterfaces.Product;
import com.cboe.ffInterfaces.ProductHome;
import com.cboe.ffUtil.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.persistenceService.NoSuchObjectException;
import java.util.Collection;

public class ProductHomeImpl
    extends BOHome
    implements ProductHome
{
    protected ProductImpl createImpl()
    {
        ProductImpl product = new ProductImpl();
        addToContainer(product);
        return product;
    }

    public void goSlave()
    {
        // Pre-populate the transient store with some products.
        //
        Transaction.startTransaction();
        boolean committed = false;
        try
        {
            String[] symbols = { "IBM", "AOL", "DJX", "ORCL", "YHOO", "GM" };
            for (int i=0; i < symbols.length; i++)
            {
                Product prod = create(symbols[i], symbols[i] + " full name");
            }
            committed = Transaction.commit();
        }
        finally
        {
            if (!committed)
            {
                Transaction.rollback();
            }
        }
    }

    public Product create()
    {
        return createImpl();
    }

    public Product create(String symbol, String fullName)
    {
        Product product = createImpl();
        product.setSymbol(symbol);
        product.setFullName(fullName);
        return product;
    }

    public Product find(String symbol) throws PersistenceException, NotFoundException
    {
        ProductImpl queryExample = createImpl();
        ObjectQuery query = new ObjectQuery(queryExample);
        queryExample.setSymbol(symbol);
        try
        {
            return (Product)query.findUnique();
        }
        catch (NoSuchObjectException ex)
        {
            throw ExceptionBuilder.notFoundException("No product found for symbol '" + symbol + "'", 0);
        }
    }

    public Collection findAll() throws PersistenceException
    {
        ProductImpl queryExample = createImpl();
        ObjectQuery query = new ObjectQuery(queryExample);
        return query.find();
    }
}
