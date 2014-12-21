whenever sqlerror continue

drop table SBTOrderHistory;

whenever sqlerror exit failure

create table SBTOrderHistory
(
    databaseIdentifier number(20) not null,
    /* prefix used by every record */
    userId varchar2(15),
    userKey number(20),
    productKey number(20),
    classKey number(20),
    eventType number(3),
    eventTime number(24),
    /* link to order table (all records) */
    orderDBId number(20),
    /* fields common to most event types */
	userAssignedId varchar2(256),
	transactionSequenceNumber number(20),
    /* field(s) commented by event */
   	orderPrice varchar2(12),            /* NEW_ORDER */
	side char(1),                       /* NEW_ORDER */
	originalQuantity number(10),        /* NEW_ORDER */
	contingencyType number(3),          /* NEW_ORDER */
    timeInForce char(1),                /* NEW_ORDER */
    tradePrice varchar2(12),            /* FILL_ORDER */
	tradedQuantity number(10),          /* FILL_ORDER */
    leavesQuantity number(10),          /* FILL_ORDER CANCEL_ORDER */
    fillRejectReason number(5),			/* Fill Reject Reason - for Linkage or Manual Fill*/
    /* link to trade table */
    tradeDBId number(20),               /* FILL_ORDER BUST_ORDER_FILL BUST_REINSTATE_ORDER */
    cancelReason number(3),             /* CANCEL_ORDER CANCEL_REPLACE_ORDER CANCEL_ALL_ORDERS*/
	cancelledQuantity number(10),       /* CANCEL_ORDER CANCEL_REPLACE_ORDER */
    tlcQuantity number(10),             /* CANCEL_ORDER CANCEL_REPLACE_ORDER */
	bustedQuantity number(10),          /* BUST_ORDER_FILL BUST_REINSTATE_ORDER */
	reinstatedQuantity number(10),      /* BUST_REINSTATE_ORDER  */
    eventStatus char(1),                /* CANCEL_REPLACE_ORDER */
    mismatchedQuantity number(10),      /* CANCEL_REPLACE_ORDER */
    /* link to order table */
    replaceOrderDBId number(20),        /* CANCEL_REPLACE_ORDER */
	optionalData varchar2(148),         /* UPDATE_ORDER */
   	cmta varchar2(5),                   /* UPDATE_ORDER */
	cmtaExchAcr varchar2(5),
	account varchar2(16),               /* UPDATE_ORDER */
    subAccount varchar2(10),            /* UPDATE_ORDER */
    bookedQuantity number(10),          /* BOOK_ORDER */
    orderState number(2),               /* STATE_CHANGE_ORDER */
    newProductKey number(20),           /* PRICE_ADJUST_ORDER */
    newOrderPrice varchar2(12),         /* PRICE_ADJUST_ORDER */
    newQuantity number(10),              /* PRICE_ADJUST_ORDER */
    session_name varchar2(30),
    exec_broker varchar2(8),
    handlingInstruction varchar2(250),
    returnCode number(5),
    subEventType number(5),
    bulkorderreqid number(20),
    routeReason NUMBER(3),
    location VARCHAR2(64),
    locationType NUMBER(3),
    bboBidPrice VARCHAR2(12),	
    bboBidSize NUMBER(10),	
    bboAskPrice VARCHAR2(12),	
    bboAskSize NUMBER(10), 	
    botrAskPrice VARCHAR2(12),	
    botrBidPrice VARCHAR2(12),	
    botrAskExchanges VARCHAR2(200),	
    botrBidExchanges VARCHAR2(200),
    nbboAskPrice VARCHAR2(12),	
    nbboBidPrice VARCHAR2(12),	
    nbboAskExchanges VARCHAR2(200),	
    nbboBidExchanges VARCHAR2(200),
    dsmBidPrice VARCHAR2(12),	
    dsmBidSize NUMBER(10),	
    dsmAskPrice VARCHAR2(12),	
    dsmAskSize NUMBER(10),
    bookBidPrice VARCHAR2(12),	
    bookBidSize NUMBER(10),	
    bookAskPrice VARCHAR2(12),	
    bookAskSize NUMBER(10),    
    auctionType VARCHAR2(3),
    quantityTradedInAuction NUMBER(10),
    earlyAuctionEndFlag CHAR(1),
    complexOrderType CHAR(1),
    sourceField     VARCHAR2(32),
    routeDescription VARCHAR(512),
    partitionFlag number(1),
    deltaNeutralIndicator CHAR(1),
    sourceFieldType NUMBER(3),
    btmIndicator CHAR(1),
    ttIndicator CHAR(1),
    exchangesIndicators VARCHAR2(200),
    marketibilityIndicator CHAR(1),
    isAutoLinked CHAR(1),
    relatedOrderDBId  NUMBER(20),
	relatedOrderFirmNumber          VARCHAR2(5),
	relatedOrderFirmExchange      	VARCHAR2(5),
	relatedOrderbranch              VARCHAR2(5),
	relatedOrderBranchSeqNumber     NUMBER(10),
	relatedOrderCorrespondentFirm   VARCHAR2(5),
	relatedOrderOrderDate           VARCHAR2(8),
	relatedOrderOrsId               VARCHAR2(6),
    routedQuantity  NUMBER(10),
    activityTime number(24),
    coaEligibilityIndicator CHAR(1),
    CONTRABROKER                   VARCHAR2(200 BYTE),  /* Used for Buy Writes */
    rejectIndicator	CHAR(1),
    volumeMaintenanceQuantity NUMBER(10),
    stopOrderTriggered CHAR(1),
    EXCHANGENAME   VARCHAR2(6),
    CREAT_REC_TIME timestamp default systimestamp,  /* new field, used by CDB application.*/    
    outboundVendor varchar2(10),     /* linkage router vendor for multiple routers */
    workstation   VARCHAR2(4)
)
TABLESPACE SBTB_LG_DATA01
LOGGING
NOCACHE
NOPARALLEL
partition by list(partitionFlag)
   (
     partition order_hist_orders values('1'),
     partition order_hist_quotes values('0')
   )
;
/* tablespace sbtb_me_data03 */

CREATE INDEX SBTORDERHISTORY_I1 ON SBTORDERHISTORY
(ORDERDBID)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDERHISTORY_I2 ON SBTORDERHISTORY
(CREAT_REC_TIME)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDERHISTORY_I3 ON SBTORDERHISTORY
(DATABASEIDENTIFIER)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/

analyze index SBTORDERHISTORY_I1 delete statistics; 
analyze index SBTORDERHISTORY_I2 delete statistics; 
analyze index SBTORDERHISTORY_I3 delete statistics;

