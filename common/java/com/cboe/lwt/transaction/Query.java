/*
 * Created on Apr 12, 2004
 *
 */
package com.cboe.lwt.transaction;

/**
 * @author dotyl
 *
 */
public interface Query
{
    void prepare();
    void addBatch();
    void execute();
}
