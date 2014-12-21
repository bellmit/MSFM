

whenever sqlerror exit failure

ALTER TABLE sbtorder
 ADD (
	manualSequenceNumber number(12),
	firstTimeOnPar char(1),
	previousLocation varchar2(64),
    previousLocationtype number(3), 
    totalPendingCancelQuantity number(10),
    cancelledOrderId number(20),
	replacedOrderId number(20),	
	totalRejectOrTimeoutQuantity number(10),
	autobookIndicator	char(1)	     ,
	 stopOrderTriggered CHAR(1),
	 parPendingCancelQuantity number(10),
	 orderEverNBBORejected char(1)
     );

     drop index SBTORDER_I1;
     
	 create index SBTOrder_i1  
	 on SBTOrder(classKey,state,orderOriginType,contingencyType,orsid);
	