package com.cboe.interfaces.application.inprocess;

import com.cboe.domain.util.CmiOrderIdStructContainer;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotAcceptedException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.idl.cmiOrder.InternalizationOrderResultStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiQuote.RFQStruct;

/**
 * @author Jing Chen
 */
public interface OrderEntry {
	public void setFixCasSession(CasSession fixCasSession);

	public void setOrderStatusConsumer(OrderStatusConsumer orderStatusConsumer);

	public void acceptStrategyOrder(OrderStruct orderStruct,
			LegOrderEntryStruct[] legOrderEntryStructs,
			ProductStruct productStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException;

	public void acceptStrategyOrderV7(OrderStruct orderStruct,
			LegOrderEntryStructV2[] legOrderEntryStructsV2,
			ProductStruct productStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException;

	public OrderIdStruct acceptOrder(OrderStruct orderStruct,
			ProductStruct productStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException;

	public InternalizationOrderResultStruct acceptInternalizationOrder(
			OrderStruct primaryOrder, ProductStruct primaryProductStruct,
			OrderStruct matchOrder, ProductStruct matchProductStruct,
			short matchType) throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException;

	public InternalizationOrderResultStruct acceptInternalizationStrategyOrder(
			OrderStruct primaryOrder,
			LegOrderEntryStruct[] primaryLegOrderEntryStruct,
			ProductStruct primaryProductStruct, OrderStruct matchOrder,
			LegOrderEntryStruct[] matchLegOrderEntryStruct,
			ProductStruct matchProductStruct, short matchType)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException;

	public InternalizationOrderResultStruct acceptInternalizationStrategyOrderV7(
			OrderStruct primaryOrder,
			LegOrderEntryStructV2[] primaryLegOrderEntryStructV2,
			ProductStruct primaryProductStruct, OrderStruct matchOrder,
			LegOrderEntryStructV2[] matchLegOrderEntryStructV2,
			ProductStruct matchProductStruct, short matchType)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException,
			AlreadyExistsException;

	public void acceptOrderCancelRequest(
			CancelRequestStruct cancelRequestStruct,
			ProductKeysStruct productKeysStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException;

	public OrderIdStruct acceptOrderCancelReplaceRequest(
			CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct,
			ProductStruct productStruct) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException;

	public void acceptStrategyOrderCancelReplaceRequest(
			CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct,
			ProductStruct productStruct,
			LegOrderEntryStruct[] legOrderEntryStructs) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException;

	public void acceptStrategyOrderCancelReplaceRequestV7(
			CancelRequestStruct cancelRequestStruct, OrderStruct orderStruct,
			ProductStruct productStruct,
			LegOrderEntryStructV2[] legOrderEntryStructsV2)
			throws SystemException, CommunicationException,
			AuthorizationException, DataValidationException,
			NotAcceptedException, TransactionFailedException;

	public void acceptRequestForQuote(RFQStruct aRFQ) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException;

	public void acceptCrossingOrder(OrderStruct orderStruct,
			OrderStruct orderStruct1, ProductStruct productStruct,
			ProductStruct productStruct1) throws SystemException,
			CommunicationException, AuthorizationException,
			DataValidationException, NotAcceptedException,
			TransactionFailedException, AlreadyExistsException;

	public void addToOrderEntryCache(CmiOrderIdStructContainer orderId,
			int productKey);

	public void removeFromOrderEntryCache(CmiOrderIdStructContainer orderId);

	public boolean checkOrderEntryCache(OrderIdStruct orderId);

	public void addToPendingCancelCache(
			CmiOrderIdStructContainer orderIdStruct,
			PendingCancelCacheElement cancelRequest);

	public String queryOrderCache(String branchSeqNum);

}
