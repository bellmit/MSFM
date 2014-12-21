whenever sqlerror exit failure


ALTER TABLE sbtorderhistory ADD 
(
fillRejectReason number(5),
subAccount varchar2(10),
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
    auctionType CHAR(1),
    quantityTradedInAuction NUMBER(10),
    earlyAuctionEndFlag CHAR(1),
    complexOrderType CHAR(1),
    sourceField     VARCHAR2(32),
    routeDescription VARCHAR(512),
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
    rejectIndicator	CHAR(1),
    CONTRABROKER                   VARCHAR2(200 BYTE), 
    volumeMaintenanceQuantity NUMBER(10),
    stopOrderTriggered CHAR(1)
);


ALTER TABLE sbtorderhistory MODIFY
(
	subEventType number(5)	
);


