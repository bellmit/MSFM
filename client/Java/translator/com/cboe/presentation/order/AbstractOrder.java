//
// -----------------------------------------------------------------------------------
// Source file: AbstractOrder.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import java.text.ParseException;
import java.util.Comparator;

import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.domain.util.OptionalDataHelper;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.idl.cmiConstants.OrderOrigins;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.TimesInForce;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableOrderId;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.interfaces.presentation.validation.ValidationErrorCodes;
import com.cboe.interfaces.presentation.validation.ValidationResult;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.exchange.GUIExchangeHome;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.OrderFormatter;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.user.ExchangeAcronymFactory;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.userSession.UserSessionFactory;
import com.cboe.presentation.validation.ValidationResultImpl;
import com.cboe.presentation.order.riskControlValidation.OrderRiskControlValidationHelper;

public abstract class AbstractOrder extends AbstractMutableBusinessModel implements Order
{
    private boolean createdFromStruct;
    protected boolean newOrder;

    protected OrderId orderId;
    protected ExchangeAcronym originator;
    protected Integer originalQuantity;
    protected Integer productKey;
    protected Character side;
    protected Price price;
    protected Character timeInForce;
    protected DateTime expireTime;
    protected OrderContingency contingency;
    protected ExchangeFirm cmta;
    // Using com.cboe.domain.util.ExtensionsHelper instead of keeping a String extensions.  This will make the GUI's use
    // of the Order's extensions field consistent with the server and CASeasier and less error prone
    protected ExtensionsHelper extensionsHelper;
    protected String account;
    protected String subaccount;
    protected Character positionEffect;
    protected Boolean cross;
    protected Character orderOriginType;
    protected Character coverage;
    protected Short orderNBBOProtectionType;
    protected String optionalData;
    protected String userId;
    protected ExchangeAcronym userAcronym;
    protected Short productType;
    protected Integer classKey;
    protected DateTime receivedTime;
    protected Short state;
    protected Integer tradedQuantity;
    protected Integer cancelledQuantity;
    protected Integer leavesQuantity;
    protected Price averagePrice;
    protected Integer sessionTradedQuantity;
    protected Integer sessionCancelledQuantity;
    protected Price sessionAveragePrice;
    protected String orsId;
    protected Character source;
    protected OrderId crossedOrder;
    protected Integer transactionSequenceNumber;
    protected String userAssignedId;
    protected String[] sessionNames;
    protected String activeSession;
    protected LegOrderDetail[] legOrderDetails;

    protected SessionProduct sessionProduct;
    protected SessionProductClass sessionProductClass;

    private static final Comparator<Order> myStdComparator = new Comparator<Order>()
    {
        public int compare(Order order1, Order order2)
        {
            OrderId order1Id = order1.getOrderId();
            OrderId order2Id = order2.getOrderId();

            return order1Id.compareTo(order2Id);
        }
    };

    public AbstractOrder(OrderStruct orderStruct)
    {
        this();
        checkParam(orderStruct, "OrderStruct");
        createdFromStruct = true;
        setOrderFields(orderStruct);
    }

    public AbstractOrder()
    {
        super();
        initialize();
    }

    public int hashCode()
    {
        return getOrderId().hashCode();
    }

    public boolean equals(Object otherOrder)
    {
        boolean equal = super.equals(otherOrder);

        if(!equal)
        {
            if(otherOrder instanceof Order)
            {
                Order castedObject = (Order)otherOrder;
                equal = getOrderId().equals(castedObject.getOrderId());
            }
        }
        return equal;
    }

    private void initialize()
    {
        setComparator(myStdComparator);

        MutableOrderId orderId = OrderIdFactory.createMutableOrderId(new OrderIdStruct());
        orderId.setOrderDate("");
        orderId.setCorrespondentFirm("");
        _setOrderId(orderId); // necessary for the executing give up firm

        UserStructModel user = UserSessionFactory.findUserSession().getUserModel();

        ExchangeFirm firm = user.getDefaultProfile().getExecutingGiveupFirm();
        if(firm == null)
        {
            orderId.setExecutingOrGiveUpFirm(ExchangeFirmFactory.createExchangeFirm("", ""));
        }
        else
        {
            orderId.setExecutingOrGiveUpFirm(firm);
        }
        _setAccount(user.getDefaultProfile().getAccount());

        DateTime now = new DateTimeImpl();
        _setExpireTime(now);
        _setReceivedTime(now);

        _setSubaccount("");
        _setOptionalData("");
        _setCmta(null);
        _setSide(Sides.BUY);

        Role role = UserSessionFactory.findUserSession().getUserModel().getRole();
        if (role == Role.MARKET_MAKER || role == Role.DPM)
        {
            _setOrderOriginType(OrderOrigins.MARKET_MAKER);
        }
        else
        {
            _setOrderOriginType(OrderOrigins.CUSTOMER);
        }
        _setCoverage(CoverageTypes.UNCOVERED);
        _setPositionEffect(PositionEffects.CLOSED);
        _setPrice(DisplayPriceFactory.create(0.0));
        _setAveragePrice(DisplayPriceFactory.create(0.0));
        _setSessionAveragePrice(DisplayPriceFactory.create(0.0));
        _setSessionTradedQuantity(0);
        _setTimeInForce(TimesInForce.DAY);
        _setOriginalQuantity(0);
        _setLeavesQuantity(0);
        _setCross(Boolean.FALSE);
        _setUserAssignedId("");
        _setExtensions("");

        _setLegOrderDetails(new LegOrderDetail[0]);

        try
        {
            _setProductKey(Integer.valueOf(APIHome.findProductQueryAPI().getDefaultSessionProduct().getProductKey()));
            _setClassKey(Integer.valueOf(APIHome.findProductQueryAPI().getDefaultSessionProductClass().getClassKey()));
            _setSessionProduct(APIHome.findProductQueryAPI().getDefaultSessionProduct());
            _setSessionProductClass(APIHome.findProductQueryAPI().getDefaultSessionProductClass());
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

    }

    void setOrderFields(OrderStruct orderStruct)
    {
        _setOrderId(OrderIdFactory.createMutableOrderId(orderStruct.orderId));
        _setOriginator(ExchangeAcronymFactory.createExchangeAcronym(orderStruct.originator));
        _setOriginalQuantity(orderStruct.originalQuantity);
        _setProductKey(orderStruct.productKey);
        _setSide(orderStruct.side);
        _setPrice(DisplayPriceFactory.create(orderStruct.price));
        _setTimeInForce(orderStruct.timeInForce);
        _setExpireTime(DateTimeFactory.getDateTime(orderStruct.expireTime));
        _setContingency(OrderContingencyFactory.createOrderContingency(orderStruct.contingency));
        _setCmta(ExchangeFirmFactory.createExchangeFirm(orderStruct.cmta));
        _setExtensions(orderStruct.extensions);
        _setAccount(orderStruct.account);
        _setSubaccount(orderStruct.subaccount);
        _setPositionEffect(orderStruct.positionEffect);
        _setCross(orderStruct.cross);
        _setOrderOriginType(orderStruct.orderOriginType);
        _setCoverage(orderStruct.coverage);
        _setOrderNBBOProtectionType(orderStruct.orderNBBOProtectionType);
        _setOptionalData(orderStruct.optionalData);
        _setUserId(orderStruct.userId);
        _setUserAcronym(ExchangeAcronymFactory.createExchangeAcronym(orderStruct.userAcronym));
        _setProductType(orderStruct.productType);
        _setClassKey(orderStruct.classKey);
        _setReceivedTime(DateTimeFactory.getDateTime(orderStruct.receivedTime));
        _setState(orderStruct.state);
        _setTradedQuantity(orderStruct.tradedQuantity);
        _setCancelledQuantity(orderStruct.cancelledQuantity);
        _setLeavesQuantity(orderStruct.leavesQuantity);
        _setAveragePrice(DisplayPriceFactory.create(orderStruct.averagePrice));
        _setSessionTradedQuantity(orderStruct.sessionTradedQuantity);
        _setSessionCancelledQuantity(orderStruct.sessionCancelledQuantity);
        _setSessionAveragePrice(DisplayPriceFactory.create(orderStruct.sessionAveragePrice));
        _setOrsId(orderStruct.orsId);
        _setSource(orderStruct.source);
        _setCrossedOrder(OrderIdFactory.createOrderId(orderStruct.crossedOrder));
        _setTransactionSequenceNumber(orderStruct.transactionSequenceNumber);
        _setUserAssignedId(orderStruct.userAssignedId);
        String[] clonedNames = new String[orderStruct.sessionNames.length];
        for (int i = 0; i < clonedNames.length; i++)
        {
            clonedNames[i] = orderStruct.sessionNames[i];
        }
        _setSessionNames(clonedNames);
        _setActiveSession(orderStruct.activeSession);
        LegOrderDetail[] legOrderDetails = new LegOrderDetail[orderStruct.legOrderDetails.length];

        for (int i = 0; i < orderStruct.legOrderDetails.length; i++)
        {
            legOrderDetails[i] = LegOrderDetailFactory.createLegOrderDetail(orderStruct.legOrderDetails[i]);
        }
        _setLegOrderDetails(legOrderDetails);
        try
        {
            _setSessionProduct(APIHome.findProductQueryAPI().getProductByKeyForSession(getActiveSession(), getProductKey().intValue()));
            _setSessionProductClass(APIHome.findProductQueryAPI().getClassByKeyForSession(getActiveSession(), getClassKey().intValue()));
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    public boolean isNewOrder()
    {
        return newOrder;
    }

    /**
     * Returns the Order's away exchange.  Only P orders (orderOriginType='P') have an away exchange.  The
     * ExchangeFactory will return null if the Exchange isn't in its list of Exchanges.  The Exchange Factory will
     * contain all exchanges in the IDL; if an Order has an away exchange that isn't in the ExchangeFactory, a new
     * Exchange will be created to represent the Order's away exchange.
     *
     * Won't keep a reference to the away exchange, so it will always be parsed from the extensions field (via
     * ExtensionsHelper) to ensure that it's accurate/up-to-date.
     *
     * NOTE: P/A and S orders also include the away exchange extension.
     *
     * @return Exchange
     */
    public Exchange getAwayExchange()
    {
        String exchangeName = getExtensionsHelper().getValue(ExtensionFields.EXCHANGE_DESTINATION);
        Exchange exchange = GUIExchangeHome.find().findExchange(exchangeName);
        // if this order's extensions field had an EXCHANGE_DESTINATION, but it wasn't in the factory, then create an Exchange
        if(exchangeName != null && exchange == null)
        {
            exchange = GUIExchangeHome.find().createExchange(exchangeName, exchangeName);
        }
        return exchange;
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public ExchangeAcronym getOriginator()
    {
        return originator;
    }

    public Integer getOriginalQuantity()
    {
        return originalQuantity;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public Character getSide()
    {
        return side;
    }

    public Price getPrice()
    {
        return price;
    }

    public Character getTimeInForce()
    {
        return timeInForce;
    }

    public DateTime getExpireTime()
    {
        return expireTime;
    }

    public OrderContingency getContingency()
    {
        return contingency;
    }

    public ExchangeFirm getCmta()
    {
        return cmta;
    }

    public String getExtensions()
    {
//        return extensions;
        return getExtensionsHelper().toString();
    }

    public String getAccount()
    {
        return account;
    }

    public String getSubaccount()
    {
        return subaccount;
    }

    public Character getPositionEffect()
    {
        return positionEffect;
    }

    public Character getOrderOriginType()
    {
        return orderOriginType;
    }

    public Character getCoverage()
    {
        return coverage;
    }

    public Short getOrderNBBOProtectionType()
    {
        return orderNBBOProtectionType;
    }

    public String getOptionalData()
    {
        return optionalData;
    }

    public String getUserId()
    {
        return userId;
    }

    public ExchangeAcronym getUserAcronym()
    {
        return userAcronym;
    }

    public Short getProductType()
    {
        return productType;
    }

    public Integer getClassKey()
    {
        return classKey;
    }

    public DateTime getReceivedTime()
    {
        return receivedTime;
    }

    public Short getState()
    {
        return state;
    }

    public Integer getTradedQuantity()
    {
        return tradedQuantity;
    }

    public Integer getCancelledQuantity()
    {
        return cancelledQuantity;
    }

    public Integer getLeavesQuantity()
    {
        return leavesQuantity;
    }

    public Price getAveragePrice()
    {
        return averagePrice;
    }

    public Integer getSessionTradedQuantity()
    {
        return sessionTradedQuantity;
    }

    public Integer getSessionCancelledQuantity()
    {
        return sessionCancelledQuantity;
    }

    public Price getSessionAveragePrice()
    {
        return sessionAveragePrice;
    }

    public String getOrsId()
    {
        return orsId;
    }

    public String getDisplayOrsId()
    {
        return OrderFormatter.formatOrsIdForDisplay(orsId);
    }
    public Character getSource()
    {
        return source;
    }

    public OrderId getCrossedOrder()
    {
        return crossedOrder;
    }

    public Integer getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    public String getUserAssignedId()
    {
        return userAssignedId;
    }

    public String[] getSessionNames()
    {
        return sessionNames;
    }

    public String getActiveSession()
    {
        return activeSession;
    }

    public LegOrderDetail[] getLegOrderDetails()
    {
        return legOrderDetails;
    }

    /**
     * Gets the underlying struct
     * @return OrderStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderStruct getStruct()
    {
        OrderStruct struct = OrderFactory.createDefaultOrderStruct();
        struct.account = getAccount();
        struct.activeSession = getActiveSession();
        struct.averagePrice = getAveragePrice().toStruct();
        struct.cancelledQuantity = getCancelledQuantity().intValue();
        struct.classKey = getClassKey().intValue();
        struct.cmta = getCmta().getExchangeFirmStruct();
        struct.contingency = getContingency().getStruct();
        struct.coverage = getCoverage().charValue();
        struct.cross = isCross().booleanValue();
        struct.crossedOrder = getCrossedOrder().getStruct();
        struct.expireTime = getExpireTime().getDateTimeStruct();
        struct.extensions = getExtensions();
        struct.leavesQuantity = getLeavesQuantity().intValue();
        struct.optionalData = getOptionalData();
        struct.orderId = getOrderId().getStruct();
        struct.orderNBBOProtectionType = getOrderNBBOProtectionType().shortValue();
        struct.orderOriginType = getOrderOriginType().charValue();
        struct.originalQuantity = getOriginalQuantity().intValue();
        struct.originator = getOriginator().getExchangeAcronymStruct();
        struct.orsId = getOrsId();
        struct.positionEffect = getPositionEffect().charValue();
        struct.price = getPrice().toStruct();
        struct.productKey = getProductKey().intValue();
        struct.productType = getProductType().shortValue();
        struct.receivedTime = getReceivedTime().getDateTimeStruct();
        struct.sessionAveragePrice = getSessionAveragePrice().toStruct();
        struct.sessionCancelledQuantity = getSessionCancelledQuantity().intValue();
        struct.sessionNames = getSessionNames();
        struct.sessionTradedQuantity = getSessionTradedQuantity().intValue();
        struct.side = getSide().charValue();
        struct.source = getSource().charValue();
        struct.state = getState().shortValue();
        struct.subaccount = getSubaccount();
        struct.timeInForce = getTimeInForce().charValue();
        struct.tradedQuantity = getTradedQuantity().intValue();
        struct.transactionSequenceNumber = getTransactionSequenceNumber().intValue();
        struct.userAcronym = getUserAcronym().getExchangeAcronymStruct();
        struct.userAssignedId = getUserAssignedId();
        struct.userId = getUserId();

        LegOrderDetail[] details = getLegOrderDetails();
        struct.legOrderDetails = new LegOrderDetailStruct[details.length];
        for (int i = 0; i < details.length; i++)
        {
            struct.legOrderDetails[i] = details[i].getStruct();
        }
        return struct;
    }

    public Boolean isCross()
    {
        return cross;
    }

    public SessionProductClass getSessionProductClass()
    {
        return sessionProductClass;
    }

    public SessionProduct getSessionProduct()
    {
        return sessionProduct;
    }

    public ValidationResult validate()
    {
        ValidationResult result;

        result = validatePriceQuantity();

        if(result.isValid())
        {
            result = validateBranch();
        }

        if(result.isValid())
        {
            result = validateOptData();
        }

        // if the order data is valid, check if the it requires further user-confirmation
        // based on the user's configured pre-trade risk controls
        if (result.isValid())
        {
            result = OrderRiskControlValidationHelper.validateOrder(this);
        }
        return result;
    }

    protected ExtensionsHelper getExtensionsHelper()
    {
        if(extensionsHelper == null)
        {
            extensionsHelper = new ExtensionsHelper();
        }
        return extensionsHelper;
    }

    // could make an implementation of extensions-support generic enough to handle any extension key/value pair, but for now only exposing Away Exchange
    /**
     * Update the Order's extensions field with the new Away Exchange value.  If exchange is null, remove the
     * EXCHANGE_DESTINATION from extensions.
     *
     * @param exchange
     */
    protected void _setAwayExchange(Exchange exchange)
    {
        String exchangeValue = null;
        if( exchange != null )
        {
            exchangeValue = exchange.getExchange();
        }
        setExtensionFieldValue(ExtensionFields.EXCHANGE_DESTINATION, exchangeValue);
    }

    protected void _setOrderId(OrderId orderId)
    {
        this.orderId = orderId;
    }

    protected void _setOriginator(ExchangeAcronym originator)
    {
        this.originator = originator;
    }

    protected void _setOriginalQuantity(Integer originalQuantity)
    {
        this.originalQuantity = originalQuantity;
    }

    protected void _setProductKey(Integer productKey)
    {
        this.productKey = productKey;
    }

    protected void _setSide(Character side)
    {
        this.side = side;
    }

    protected void _setPrice(Price price)
    {
        this.price = price;
    }

    protected void _setTimeInForce(Character timeInForce)
    {
        this.timeInForce = timeInForce;
    }

    protected void _setExpireTime(DateTime expireTime)
    {
        this.expireTime = expireTime;
    }

    protected void _setContingency(OrderContingency contingency)
    {
        this.contingency = contingency;
    }

    protected void _setCmta(ExchangeFirm cmta)
    {
        this.cmta = cmta;
    }

    // this will reset all extensions, e.g., away exchange (key = ExtensionFields.EXCHANGE_DESTINATION)
    protected void _setExtensions(String extensions)
    {
        try
        {
            getExtensionsHelper().setExtensions(extensions);
        }
        catch(ParseException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    protected void _setAccount(String account)
    {
        this.account = account;
    }

    protected void _setSubaccount(String subaccount)
    {
        this.subaccount = subaccount;
    }

    protected void _setPositionEffect(Character positionEffect)
    {
        this.positionEffect = positionEffect;
    }

    protected void _setCross(Boolean cross)
    {
        this.cross = cross;
    }

    protected void _setOrderOriginType(Character orderOriginType)
    {
        this.orderOriginType = orderOriginType;
    }

    protected void _setCoverage(Character coverage)
    {
        this.coverage = coverage;
    }

    protected void _setOrderNBBOProtectionType(Short orderNBBOProtectionType)
    {
        this.orderNBBOProtectionType = orderNBBOProtectionType;
    }

    protected void _setOptionalData(String optionalData)
    {
        this.optionalData = optionalData;
    }

    protected void _setUserId(String userId)
    {
        this.userId = userId;
    }

    protected void _setUserAcronym(ExchangeAcronym userAcronym)
    {
        this.userAcronym = userAcronym;
    }

    protected void _setProductType(Short productType)
    {
        this.productType = productType;
    }

    protected void _setClassKey(Integer classKey)
    {
        this.classKey = classKey;
    }

    protected void _setReceivedTime(DateTime receivedTime)
    {
        this.receivedTime = receivedTime;
    }

    protected void _setState(Short state)
    {
        this.state = state;
    }

    protected void _setTradedQuantity(Integer tradedQuantity)
    {
        this.tradedQuantity = tradedQuantity;
    }

    protected void _setCancelledQuantity(Integer cancelledQuantity)
    {
        this.cancelledQuantity = cancelledQuantity;
    }

    protected void _setLeavesQuantity(Integer leavesQuantity)
    {
        this.leavesQuantity = leavesQuantity;
    }

    protected void _setAveragePrice(Price averagePrice)
    {
        this.averagePrice = averagePrice;
    }

    protected void _setSessionTradedQuantity(Integer sessionTradedQuantity)
    {
        this.sessionTradedQuantity = sessionTradedQuantity;
    }

    protected void _setSessionCancelledQuantity(Integer sessionCancelledQuantity)
    {
        this.sessionCancelledQuantity = sessionCancelledQuantity;
    }

    protected void _setSessionAveragePrice(Price sessionAveragePrice)
    {
        this.sessionAveragePrice = sessionAveragePrice;
    }

    protected void _setOrsId(String orsId)
    {
        this.orsId = orsId;
    }

    protected void _setSource(Character source)
    {
        this.source = source;
    }

    protected void _setCrossedOrder(OrderId crossedOrder)
    {
        this.crossedOrder = crossedOrder;
    }

    protected void _setTransactionSequenceNumber(Integer transactionSequenceNumber)
    {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }

    protected void _setUserAssignedId(String userAssignedId)
    {
        this.userAssignedId = userAssignedId;
    }

    protected void _setSessionNames(String[] sessionNames)
    {
        this.sessionNames = sessionNames;
    }

    protected void _setActiveSession(String activeSession)
    {
        this.activeSession = activeSession;
    }

    protected void _setLegOrderDetails(LegOrderDetail[] legOrderDetails)
    {
        this.legOrderDetails = legOrderDetails;
    }

    protected void _setSessionProductClass(SessionProductClass sessionProductClass)
    {
        this.sessionProductClass = sessionProductClass;
        _setActiveSession(sessionProductClass.getTradingSessionName());
        _setClassKey(sessionProductClass.getClassKey());
    }

    protected void _setSessionProduct(SessionProduct sessionProduct)
    {
        this.sessionProduct = sessionProduct;
        _setActiveSession(sessionProduct.getTradingSessionName());
        _setProductKey(sessionProduct.getProductKey());
    }

    private ValidationResult validatePrice()
    {
        ValidationResult result = new ValidationResultImpl();
        String errorMessage = null;
        int errorCode = ValidationErrorCodes.VALID;
        
        if(getPrice().isNoPrice())
        {
            errorCode = ValidationErrorCodes.ORDER_PRICE_INVALID;
            errorMessage = "Order Price is invalid.";
        }
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    private ValidationResult validateQuantity()
    {
        ValidationResult result = new ValidationResultImpl();
        String errorMessage = null;
        int errorCode = ValidationErrorCodes.VALID;
        
        if(getOriginalQuantity().intValue() <= 0)
        {
            errorCode = ValidationErrorCodes.ORDER_QUANTITY_INVALID;
            errorMessage = "Quantity must be greater than 0";
        }
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
    
    protected ValidationResult validatePriceQuantity()
    {
        
        ValidationResult result = validateQuantity();
        if (result.isValid())
        {
            result = validatePrice();
        }
        return result;
    }

    private ValidationResult validateBranch()
    {
        ValidationResult result = new ValidationResultImpl();
        String errorMessage = null;
        int errorCode = ValidationErrorCodes.VALID;

        // trim the blanks from branch
        String branch = getOrderId().getBranch().trim();

        // if the original Branch was all blanks then trim should result in an empty string

        if(branch.length() == 0)
        {
            // don't allow empty string
            errorCode = ValidationErrorCodes.ORDER_BRANCH_INVALID;
            errorMessage = "Branch can not be empty or all blanks";
        }
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);

        return result;
    }

    private ValidationResult validateOptData()
    {
        ValidationResult result = new ValidationResultImpl();
        String errorMessage = null;
        int errorCode = ValidationErrorCodes.VALID;

        // get optional data
        String optData = this.getOptionalData();

        // if user entered optional data, validate any execution instruction
        if(optData != null && optData.length() != 0)
        {
            // validate
            try
            {
                String opt = OptionalDataHelper.getExecutionInstruction(optData);
            }
            catch (DataValidationException e)
            {
                errorCode = ValidationErrorCodes.ORDER_OPTIONAL_DATA_INVALID;
                errorMessage = e.getMessage();
            }
        }
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);

        return result;
    }

    protected void setExtensionFieldValue(String key, String value)
    {
        if (key != null)
        {
            if (value != null)
            {
                try
                {
                    getExtensionsHelper().setValue(key, value);
                }
                catch (ParseException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
            }
            else
            {
                getExtensionsHelper().removeKey(key);
            }
        }
    }

    public String getExtensionValue(String key)
    {
        return getExtensionsHelper().getValue(key);
    }

    boolean isCreatedFromStruct()
    {
        return createdFromStruct;
    }
}