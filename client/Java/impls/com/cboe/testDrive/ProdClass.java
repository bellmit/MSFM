package com.cboe.testDrive;

class ProdClass
{
    public String itsSessionName;
    public int itsClassKey;
    public int itsProductKey;
    public ProdClass(int theClassKey, int theProductKey, String sessionName)
    {
        itsClassKey = theClassKey;
        itsProductKey = theProductKey;
        itsSessionName = sessionName;
    }
}