package com.cboe.domain.util;

public class RateMonitorKeyContainer extends Object {

    private String userId;
    private String session;
    private String exchange;
    private String acronym;
    private short type;
    private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public RateMonitorKeyContainer(String userId, String exchange, String acronym, String session, short type)
    {
    	this.userId = userId;
        this.session = session;
        this.exchange = exchange;
        this.acronym = acronym;
        this.type = type;

        StringBuilder sb = new StringBuilder(32);
        hashCode = (sb.append(exchange).append(acronym).append(userId).toString()).hashCode() + type;
    }

    public short getType()
    {
        return type;
    }

    public String getSession()
    {
        return session;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getExchange()
    {
        return exchange;
    }

    public String getAcronym()
    {
        return acronym;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof RateMonitorKeyContainer))
        {
            RateMonitorKeyContainer rateMonitorKey = (RateMonitorKeyContainer)obj;
            return this.hashCode == rateMonitorKey.hashCode
                && this.type == rateMonitorKey.type
                && userId.equals(rateMonitorKey.userId)
                && session.equals(rateMonitorKey.session)
                && exchange.equals(rateMonitorKey.exchange)
                && acronym.equals(rateMonitorKey.acronym);
        }
        return false;
    }

    public String toString()
    {
        StringBuilder name = new StringBuilder(100);
        name.append("RateMonitorKeyContainer::")
            .append("userId:")
            .append(getUserId())
            .append(" session:")
            .append(getSession())
            .append(" exchange:")
            .append(getExchange())
            .append(" acronym:")
            .append(getAcronym())
            .append(" type:")
            .append(getType());
        return name.toString();
    }
}
