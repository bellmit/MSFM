package com.cboe.domain.supplier.proxy;

/**
 * This is container class for our data struct.
 * @author Connie Feng
 */
public class ConsumerHashKey {
    private String ior;
    private String consumerClassType;
    /**
      * Sets the internal fields to the passed values
      */
    public ConsumerHashKey(String ior, Object callback) {
		this.ior = ior;
		this.consumerClassType = callback.getClass().toString();
    }

    public String getIOR()
    {
        return ior;
    }

    public String getConsumerClassType()
    {
        return consumerClassType;
    }

    public boolean equals(Object obj)
    {
        // check the equivalence of the IOR strings.
        if (obj instanceof ConsumerHashKey)
        {
            ConsumerHashKey key = (ConsumerHashKey)obj;

            return (key.getConsumerClassType().equals(consumerClassType) && key.getIOR().equals(ior));
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (ior.hashCode() + consumerClassType.hashCode())/2;
    }

}
