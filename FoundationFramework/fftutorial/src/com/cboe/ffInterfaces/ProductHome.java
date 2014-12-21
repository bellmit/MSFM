package com.cboe.ffInterfaces;

import com.cboe.ffidl.ffExceptions.NotFoundException;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import java.util.Collection;

public interface ProductHome
{
    static final String HOME_NAME="ProductHome";

    Product create();
    Product create(String symbol, String fullName);
    Product find(String symbol) throws PersistenceException, NotFoundException;
    Collection findAll() throws PersistenceException;
}
