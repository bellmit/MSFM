package com.cboe.interfaces.events;

/** Interface used by server to announce the start of an auction.
 * This interface renames the interface produced by the IDL compiler. The
 * renaming removes a dependency between the specific IDL compiler we use and
 * any classes or interfaces that use the output of the IDL compiler.
 */
public interface AuctionConsumer
        extends com.cboe.idl.consumers.AuctionConsumerOperations
{ }
