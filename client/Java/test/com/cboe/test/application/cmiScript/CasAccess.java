package com.cboe.test.application.cmiScript;

/** Functions to access the CAS.
 * Implemented by UserAccess, but we have circular compilation dependencies if
 * other classes reference UserAccess and UserAccess references those other classes. 
 */
interface CasAccess
{
    void reauthenticate();

    /** Convert a productKey to a classKey, or throw an exception.
     * @param productKey key to convert.
     * @return corresponding classKey.
     * @throws java.lang.Throwable for any CAS error.
     */
    int productKeyToClassKey(int productKey) throws Throwable;
}
