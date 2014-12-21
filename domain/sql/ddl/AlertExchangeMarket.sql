whenever sqlerror continue

drop table sbt_alert_exchange_market;

whenever sqlerror exit failure

create table sbt_alert_exchange_market
(
	databaseIdentifier	    number(20) not null,
	alertId                     number(20) not null,
	marketInfoType              number(3),
	bestBidPrice                varchar2(20),
    bestAskPrice                varchar2(20),
    bidVolumes                  varchar2(200),
    askVolumes                  varchar2(200),
    time                        number(24),
    usedForTradeThrough         char(1)
)
/* tablespace sbtg_sm_data01 */
;

alter table sbt_alert_exchange_market
add constraint sbt_alert_exchange_market_pk
primary key (databaseIdentifier);



