whenever sqlerror continue 
 
drop table SBTAuction; 
 
rem whenever sqlerror exit failure 
 
create table SBTAuction 
( 
databaseIdentifier 	number(20) not null, 
  	productKey number(20), 
	auctionedOrderId 	number(20) not null, 
	startingPrice 		varchar2(12), 
	auctionQuantity 		number(10), 
	startTime 			number(20),	 
	expiredTime 		number(20), 
	endTime 			number(20), 
	timeToLive 			number(10), 
	state				char(1), 
	auctionType 		number(2),	 
	auctionTerminatedReason number(2), 
        terminateQuoteId        number(20), 
        terminateQuoteKey       number(20), 
        terminateQuoteUserId    varchar2(15), 
        terminateOrderId        number(20),
        ORSID                  VARCHAR2(6),
        SIDE                   CHAR(1),
        OPPSIDECBOEQUANTITY    NUMBER(10),
        OPPSIDECBOEPRICE       VARCHAR2(12),
        OPPSIDEBOTRQUANTITY    NUMBER(10),
        OPPSIDEBOTRPRICE       VARCHAR2(12),
        AUCTIONINFO            NUMBER(2),
        CLASSKEY               NUMBER(20),
	CREAT_REC_TIME         TIMESTAMP DEFAULT SYSTIMESTAMP,
	AUCTIONSTARTINGREASON      NUMBER(2)
); 
/* tablespace sbtb_me_data03 */ 
; 
 
alter table SBTAuction 
add constraint SBTAuctionpk 
primary key (databaseIdentifier) 
/* using index tablespace sbtb_me_indx03 */ 
; 
 
create index SBTAuction_i1 
on SBTAuction(auctionedOrderId) 
/* tablespace sbtb_me_indx03 */ 
; 

CREATE INDEX SBTAuction_i2 ON
SBTAuction(CREAT_REC_TIME);

 
