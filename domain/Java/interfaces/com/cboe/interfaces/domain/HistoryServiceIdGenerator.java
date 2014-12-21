package com.cboe.interfaces.domain;

/*
 * Interface for generating Id's. This interface created specifically
 * for QT4 rollout (May 2006). The current impl of Id server has sync issues
 * which decreases quote thruput. Infra is coming up with a new version of
 * Id service which will be used sometime in future. Once the new Id service
 * is in place then this interface along with the classes implementing this 
 * need to be deleted; MDH and QH need to be modified to use the new Id service. 
 * - Dalji 3/22/06
 */
public interface HistoryServiceIdGenerator
{
	Object getId();
}
