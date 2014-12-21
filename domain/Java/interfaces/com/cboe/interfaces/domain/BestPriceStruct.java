package com.cboe.interfaces.domain;

/**
 * Can not set any default price to NO PRICE because
 * 
 *  1. This class has to be in "domain", so domain interfaces can use it
 *  2. The dependency structure makes it not possible for this class to
 *  3. I do not want to change the dependency structure, which needs a whole architecture level review
 *  So, VERY IMPORTANT, check null on any values that may be null
 *  
 *  Also note, due to the way how NBBO is used in derived quote calculation,
 *     for the legMarkets that are returned from DQ,
 *         only when isNBBO=true && MDS.useNBBO=true, it truly means is-NBBO.
 *         if useNBBO=false, isNBBO will always be true.
 *  
 * @author wu
 *
 */
public final class BestPriceStruct
{
    public int productKey = 0;
    public Side side = null;
    public Price bp = null;
    public int bq = 0;
    public Price bvc = null;
    public int bvcqMax = 0;
    public int bvcqMin = 0;
    public boolean bvcOnTop = false;
    public boolean bvcOnlyOnTop = false;
    
    public Price nbbo = null;
    public int nbboVol = 0;
    public boolean isVcNBBO = false;
    public boolean isNBBO = false;
    
    public Price lastSale = null;
       
    public BestPriceStruct(Side side)
    {
        this.side = side;
    }
    
    BestPriceStruct(BestPriceStruct toCopy)
    {
        copy(toCopy);
    }

    public BestPriceStruct copy()
    {
        return new BestPriceStruct(this);
    }
    
    public void copy(BestPriceStruct toCopy)
    {
        this.productKey = toCopy.productKey;
        this.side = toCopy.side;
        this.bp = toCopy.bp;
        this.bq = toCopy.bq;
        this.bvc = toCopy.bvc;
        this.bvcqMax = toCopy.bvcqMax;
        this.bvcqMin = toCopy.bvcqMin;
        this.bvcOnTop = toCopy.bvcOnTop;
        this.bvcOnlyOnTop = toCopy.bvcOnlyOnTop;
        this.lastSale = toCopy.lastSale;
        this.isNBBO = toCopy.isNBBO;
        this.isVcNBBO = toCopy.isVcNBBO;
        this.nbbo = toCopy.nbbo;
        this.nbboVol = toCopy.nbboVol;
    }
}