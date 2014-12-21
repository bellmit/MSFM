package com.cboe.interfaces.events;
import com.cboe.util.*;
import com.cboe.exceptions.*;

public interface ExpectedOpeningPriceConsumerHome
{
      public final static String HOME_NAME = "ExpectedOpeningPriceConsumerHome";

      public ExpectedOpeningPriceConsumer find();

      public ExpectedOpeningPriceConsumer create();

      public void addConsumer(ExpectedOpeningPriceConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

      public void removeConsumer(ExpectedOpeningPriceConsumer consumer, ChannelKey key)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

      public void removeConsumer(ExpectedOpeningPriceConsumer consumer)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}

