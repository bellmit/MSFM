rem NOTE:  Field length validation is being done in the OrderHandlingService.
rem        If field lengths are changed, the validations should also be changed.
rem NOTE:  We are creating bc_Status table here because this table is currently
rem        very tightly coupled with order table because of the 'deleteStrategiesWithNoOrder' script
rem        See the deleteStrategiesWithNoOrders script for further details on how this bc_status table
rem        is used (also the values in this table are changed by perftruncate script only).

whenever sqlerror continue

drop table bc_status;

create table bc_status
(
       bcOrderTableStatus varchar2(50),
       lmod_date          date not null
);

insert into bc_status values ('ProductionData',sysdate);

commit;

rem NOTE: Dropping order leg detail and order history here because
rem       of foreign key constraints on those tables pointing to
rem       SBTOrder

drop table SBTOrderLegDetail;
drop table SBTOrderHistory;
drop table SBTAgentAssignedOrder;


drop table SBTOrder;

rem whenever sqlerror exit failure

create table SBTOrder
(
	databaseIdentifier number(20) not null,
	executingOrGiveUpFirm varchar2(5),
	executingOrGiveUpFirmExch varchar2(5),
	branch varchar2(5),
	branchSequenceNumber number(10),
	correspondentFirm varchar2(5),
	orderDate varchar2(8),
	originatorExchAcr varchar2(5),
	originator varchar2(15),
	originalQuantity number(10),
	productKey number(20),
	classKey number(20),
	productType number(3),
	side char(1),
	price varchar2(12),
	timeInForce char(1),
	expireTime number(20),
	contingencyType number(3),
	contingencyPrice varchar2(12),
	contingencyVolume number(10),	
	cmta varchar2(5),
	cmtaExchAcr varchar2(5),
	account varchar2(16),
	subAccount varchar2(10),
	positionEffect char(1),	
	crossingIndicator char(1),
	orderOriginType char(1),
	coverage char(1),
	optionalData varchar2(148),
	userId varchar2(15),
	userKey number(20),
	receivedTime number(20),	
	bookedTime number(20),	
    bookedStatus number(3),
	state number(3),
	tradedQuantity number(10),
	bustedQuantity number(10),
	cancelledQuantity number(10),
	cancelRequestedQuantity number(10),	
	addedQuantity number(10),
	bookedQuantity number(10),		
	orsId varchar2(6),
	source char(1),
	initialTradePrice varchar2(20),
	crossedOrderKey number(20),
	crossedExecutingOrGiveUpFirm varchar2(5),
	crossedBranch varchar2(5),
	crossedBranchSequenceNumber number(10),
	crossedCorrespondentFirm varchar2(5),
	crossedOrderDate varchar2(8),
	userAssignedId varchar2(256),
	transactionSequenceNumber number(20),
	totalPrice number(16,4),
    activeSession varchar2(30),
    sessionNames varchar2(256),
	totalSessionTradedQuantity number(10),
	totalSessionCancelledQuantity number(10),
	totalSessionPrice number(16,4),
	extensions varchar2(128),
	nbboProtectionType number(3),
	hadNBBOProtection char(1),
	nbboProtectionOverriden char(1),
	effectivePrice varchar2(12),
    handlingInstruction varchar2(250),
    shipQuantity number(10),
    location varchar2(64),
    locationtype number(3),
	internalTransSeq number(20),
	manualSequenceNumber number(12),
	firstTimeOnPar char(1),
	previousLocation varchar2(64),
    previousLocationtype number(3), 
    totalPendingCancelQuantity number(10),
    cancelledOrderId number(20),
	replacedOrderId number(20),
	partitionFlag number(1),
	totalRejectOrTimeoutQuantity number(10),
	autobookIndicator	char(1),
	stopOrderTriggered char(1),
	orderEverNBBORejected char(1),
        parPendingCancelQuantity number(10),
	CREAT_REC_TIME timestamp default systimestamp,   /* new field, used by CDB application.*/
        purgetime timestamp NULL,               /* new field, used by cleanupServer script.*/
        outboundVendor varchar2(10)    /* linkage router vendor for multiple routers */
)
TABLESPACE SBTB_LG_DATA01
LOGGING
NOCACHE
NOPARALLEL
PARTITION BY LIST(partitionFlag)
  (
    PARTITION order_orders VALUES('1'),
    PARTITION order_quotes VALUES('0')
  )
;
/* tablespace sbtb_me_data03 */

CREATE UNIQUE INDEX SBTORDERUK ON SBTORDER
(PARTITIONFLAG, BRANCH, BRANCHSEQUENCENUMBER, ORDERDATE, EXECUTINGORGIVEUPFIRM, CORRESPONDENTFIRM)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I1 ON SBTORDER (CLASSKEY, STATE, ORDERORIGINTYPE, CONTINGENCYTYPE, ORSID)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I2 ON SBTORDER (PRODUCTKEY)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I3 ON SBTORDER (USERID)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I4 ON SBTORDER (CREAT_REC_TIME)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I5 ON SBTORDER (DATABASEIDENTIFIER)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
CREATE INDEX SBTORDER_I6 ON SBTORDER (PRODUCTKEY, USERID)
LOGGING
LOCAL
TABLESPACE SBTB_LG_INDX01
NOPARALLEL
/
ALTER TABLE SBTORDER ADD
  CONSTRAINT SBTORDERUK UNIQUE (PARTITIONFLAG, BRANCH, BRANCHSEQUENCENUMBER, ORDERDATE, EXECUTINGORGIVEUPFIRM, CORRESPONDENTFIRM)
    USING INDEX
/

analyze index SBTORDERUK delete statistics; 
analyze index SBTORDER_I1 delete statistics; 
analyze index SBTORDER_I2 delete statistics; 
analyze index SBTORDER_I3 delete statistics; 
analyze index SBTORDER_I4 delete statistics; 
analyze index SBTORDER_I5 delete statistics; 
analyze index SBTORDER_I6 delete statistics; 

