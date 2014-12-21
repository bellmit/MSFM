whenever sqlerror continue

drop table order_exchange_market;

whenever sqlerror exit failure

create table order_exchange_market
(
	databaseIdentifier      number(20) not null,
    	marketInfoType		number(3),
	bestBidPrice            varchar2(20),
	bidExchangeVolumes 	varchar2(200),
    	bestAskPrice            varchar2(20),
	askExchangeVolumes 	varchar2(200),
        creat_rec_time          timestamp default systimestamp
)
/* tablespace sbtb_sm_data01 */
;

alter table order_exchange_market
add constraint order_exchange_market_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

CREATE INDEX order_exchange_market_i1 ON
order_exchange_market(CREAT_REC_TIME);

whenever sqlerror continue

