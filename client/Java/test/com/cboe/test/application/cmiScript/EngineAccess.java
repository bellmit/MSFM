package com.cboe.test.application.cmiScript;

interface EngineAccess
{
    /** After creating a CORBA Servant object, associate it with an ORB.
     * @param servant The CORBA Servant object.
     */
    void associateWithOrb(org.omg.PortableServer.Servant servant);

    /** Get the named object from the object store.
     * @param objectName Name of the object to get.
     * @return Object from the store, or null if not found.
     */
    Object getObjectFromStore(String objectName);

    /** Extract values from a list of strings.
     * @param parmName List of names to identify values.
     * @param command List of name value name value ...
     * @param startIndex Index into command of first name in list.
     * @return List of values corresponding to items in parmName[], or null on
     *    error. If a name in parmName[] does not appear in command[], the
     *    corresponding returned value will be null.
     */
    public String[] getParameters(String parmName[], String command[], int startIndex);
}
