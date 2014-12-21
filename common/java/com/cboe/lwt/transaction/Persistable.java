/*
 * Created on Apr 12, 2004
 *
 */
package com.cboe.lwt.transaction;

import java.sql.Connection;

/**
 * @author dotyl
 *
 */
public interface Persistable extends Identifiable
{
    void prepare( Connection p_con ) throws PersistException;
    void insert( Connection p_con )  throws PersistException;
    void update( Connection p_con )  throws PersistException;
    void delete( Connection p_con )  throws PersistException;
}
