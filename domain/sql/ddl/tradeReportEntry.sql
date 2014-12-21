whenever sqlerror continue

drop table sbt_tradereportentry;

whenever sqlerror exit failure

create table sbt_tradereportentry
(
	databaseIdentifier	number(20) not null,
	matchedSequenceNumber	number(20) not null,
	active              char(1) not null, /* boolean */
	entry_type          char(1),
	entry_time          number(24),
	last_entry_type     char(1),
	last_update_time    number(24),
        buyerUserKey        number(20),
	buyer				varchar2(10),   /* change this to buy_broker_acr someday */
        buy_broker_exch             varchar2(5),
        sellerUserKey       number(20),
	seller				varchar2(10), /* change this to sell_broker_acr someday */
    sell_broker_exch            varchar2(5),
    buyFirm			varchar2(10),
    buy_firm_exch              varchar2(5),
    buy_firm_branch             varchar2(5),
    buy_firm_branch_seq_no      number(10) not null,
	buyOrderId			number(20) not null,
	buyQuoteId			number(20) not null,
	buyReinstatable		char(1) not null,
	buyerOriginType			char(1),
	buyerCmta			varchar2(5),
    buy_cmta_exch               varchar2(5),
	buyerPositionEffect		char(1),
    buy_acct                   varchar2(16),
	buyerSubAccount			varchar2(10),
	buyerOptionalData		varchar2(128),
	buy_auct_trade_ind		char(1),
        buy_orderdate           varchar2(8),
        buy_orsid                varchar2(6),
	sellFirm			varchar2(10),
    sell_firm_exch              varchar2(5),
    sell_firm_branch            varchar2(5),
    sell_firm_branch_seq_no     number(10) not null,
	sellOrderId			number(20) not null,
	sellQuoteId			number(20) not null,
	sellReinstatable	char(1) not null,
	sellerOriginType		char(1),
	sellerCmta			varchar2(5),
    sell_cmta_exch              varchar2(5),
	sellerPositionEffect		char(1),
    sell_acct               varchar2(16),
	sellerSubAccount		varchar2(10),
	sellerOptionalData		varchar2(128),
	sell_auct_trade_ind		char(1),
        sell_orderdate           varchar2(8),
        sell_orsid                varchar2(6),
	quantity			number(20),
	tradeReportForEntry     number(20),
    session_name    varchar2(30),
    buy_corr_id                 varchar2(5),
    buy_originator              varchar2(5),
    buy_originator_exch         varchar2(5),
    sell_corr_id                varchar2(5),
    sell_originator             varchar2(5),
    	sell_originator_exch        varchar2(5),
    	buy_bill_type_code          char(1),
    	sell_bill_type_code         char(1),
    	round_lot_qty          	    number(20),
    	extensions          	    varchar2(256),
    	buy_away_exch_text          varchar2(256),
    	sell_away_exch_text         varchar2(256),
    	seller_clear_type           char(1),
	buyer_clear_type            char(1),
    buyer_session_name          varchar2(30),
    seller_session_name         varchar2(30),
    buyerExternalOrderId        number(20),
    sellerExternalOrderId       number(20),
    buy_supression_reason       number(3),
    sell_supression_reason      number(3),
    buy_away_exchange_acronym   varchar2(5),
    sell_away_exchange_acronym  varchar2(5),
    sell_side_ind 		varchar2(1),
    CREAT_REC_TIME         TIMESTAMP DEFAULT SYSTIMESTAMP,
    outboundVendor varchar2(10),    /* linkage router vendor for multiple routers */
    buy_user_id                 varchar2(10),
    sell_user_id                varchar2(10),
    workstation                 varchar2(4)
)
/* tablespace sbtb_me_data03 */
;

alter table sbt_tradereportentry
add constraint sbt_tradereportentry_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

create index sbt_tradereportentry_i1
on sbt_tradereportentry(tradeReportForEntry)
/* using index tablespace sbtb_me_indx03 */
;

create index sbt_tradereportentry_i2
on sbt_tradereportentry(matchedSequenceNumber)
;

CREATE INDEX sbt_tradereportentry_i3 ON
sbt_tradereportentry(CREAT_REC_TIME);

