whenever sqlerror continue

drop table SBTTradeHistory;

whenever sqlerror exit failure

create table SBTTradeHistory
(
	databaseIdentifier number(20) not null,
	tradeId number(20), 
	entryType char(1),
	entryTime number(24),
	entry varchar2(1024),
    session_name varchar2(30),
    oldMatchedSequenceNumber number(12),
    tradeTime number(24),
    productKey number(10),
    price varchar2(20),
    quantity number(20),
    buyerOriginType char(1),
    buyerCmta varchar2(5),
	buy_cmta_exch varchar2(5),
    buyerPositionEffect char(1),
    buyerSubAccount varchar2(10),
    buyer varchar2(10),
    buy_broker_exch varchar2(5),
    buyFirm varchar2(5),
	buy_firm_exch varchar2(5),
    buyerOptionalData varchar2(128),
    sellerOriginType char(1),
    sellerCmta varchar2(5),
	sell_cmta_exch varchar2(5),
    sellerPositionEffect char(1),
    sellerSubAccount varchar2(10),
    seller varchar2(10),
    sell_broker_exch varchar2(5),
    sellFirm varchar2(10),
	sell_firm_exch varchar2(5),
    sellerOptionalData varchar2(128)
)
/* tablespace sbtb_me_data03 */
;

alter table SBTTradeHistory
add constraint SBTTradeHistorypk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;
