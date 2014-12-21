package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.application.supplier.proxy.LargeTradeLastSaleConsumerProxy;

public class LargeTradeLastSaleSupplier extends UserSessionMarketDataBaseSupplier {

	public LargeTradeLastSaleSupplier(BaseSessionManager session) {
		super(session);
	}

	@Override
	protected String getListenerClassName() {
		return LargeTradeLastSaleConsumerProxy.class.getName();
	}

}
