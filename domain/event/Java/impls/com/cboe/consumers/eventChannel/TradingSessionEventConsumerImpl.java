package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.idl.cmiSession.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;
import com.cboe.idl.session.TradingSessionEventHistoryStruct;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class TradingSessionEventConsumerImpl extends com.cboe.idl.events.POA_TradingSessionEventConsumer implements TradingSessionConsumer {
    private TradingSessionConsumer delegate;
    /**
     * constructor comment.
     */
    public TradingSessionEventConsumerImpl(TradingSessionConsumer TradingSessionConsumer) {
        super();
        delegate = TradingSessionConsumer;
    }

    public void acceptBusinessDayEvent(BusinessDayStruct currentDay) {
        delegate.acceptBusinessDayEvent(currentDay);
    }

    public void acceptTradingSessionState(TradingSessionStateStruct sessionState) {
        delegate.acceptTradingSessionState(sessionState);
    }

    public void setProductStates(int classKey, String sessionName, ProductStateStruct[] productStates) {
        delegate.setProductStates(classKey, sessionName, productStates);
    }

    public void setClassState(ClassStateStruct newState) {
        delegate.setClassState(newState);
    }

    public void updateProduct(SessionProductStruct product) {
        delegate.updateProduct(product);
    }

    public void updateProductClass(SessionClassStruct productClass) {
        delegate.updateProductClass(productClass);
    }

    public void updateProductStrategy(SessionStrategyStruct updatedStrategy) {
        delegate.updateProductStrategy(updatedStrategy);
    }

    /**
     * @author Jeff Illian
     */
    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }

	@Override
	public void acceptTradingSessionElementUpdate(
			TradingSessionElementStruct sessionElement) {
		delegate.acceptTradingSessionElementUpdate(sessionElement);
		
	}
    
	@Override
	public void acceptTradingSessionElementUpdateV2(TradingSessionElementStructV2 sessionElement)
	{
		delegate.acceptTradingSessionElementUpdateV2(sessionElement);
	}
}
