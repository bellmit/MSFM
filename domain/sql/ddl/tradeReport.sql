whenever sqlerror continue

drop table sbt_tradereport;

whenever sqlerror exit failure

create table sbt_tradereport
(
	databaseIdentifier 	number(20) not null,
	transactionSequenceNumber	number(20) not null,
	quantity 		number(20),
	time			number(20),
	price			varchar2(12),
	product			number(20),
    classkey        number(20),
	parent			number(20),
    	prefix          	varchar(4),
	tradeSource		varchar2(20),
	trade_type      	char(1),
	settlementDate  	varchar2(8),
    	reportedTradeDate  	varchar2(8),
    	transactionTime 	number(20),
    	asOfFlag        	char(1),
	extensions		varchar2(256),
	CREAT_REC_TIME         TIMESTAMP DEFAULT SYSTIMESTAMP
)
/* tablespace sbtb_me_data03 */
;

alter table sbt_tradereport
add constraint sbt_tradereportpk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

create index sbt_tradereport_i1
on sbt_tradereport(parent)
/* using index tablespace sbtb_me_indx03 */
;

CREATE INDEX sbt_tradereport_i2 ON
sbt_tradereport(CREAT_REC_TIME);


