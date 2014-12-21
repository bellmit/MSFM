package com.cboe.interfaces.events;

import com.cboe.util.ChannelKey;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import org.omg.CORBA.SystemException;

public interface CalendarUpdateConsumerHome
{
	public final static String HOME_NAME = "CalendarUpdateConsumerHome";

    public CalendarUpdateConsumer find();

    public CalendarUpdateConsumer create();

    public void addConsumer(CalendarUpdateConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeConsumer(CalendarUpdateConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void removeConsumer(CalendarUpdateConsumer consumer)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}

