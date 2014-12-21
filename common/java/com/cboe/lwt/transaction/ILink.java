package com.cboe.lwt.transaction;


/**
 * interface for all Links
 * Known implementations: 
 *      Link  - simple link to an object
 *      TLink - Transactional Link
 *      SLink - Session Link (Transactional by the session's current transaction)
 *      PLink - Persistent Link  (coming soon)
 */
public interface ILink
{
    Object  get();
    void    delete();
    
    boolean isDeleted();
    
    void    release();
}