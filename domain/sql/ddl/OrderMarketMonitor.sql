whenever sqlerror continue

drop table order_market_monitor;

whenever sqlerror exit failure

create table order_market_monitor
(
	databaseIdentifier		number(20) not null,
	order_key			number(20) not null,
	trade_key			number(20),
	entry_type			char(1) not null,
	entry_time			number(16) not null,
	nbbo_exchange_market		number(20),
	bbo_exchange_market		number(20),
	worst_nbbo_exchange_market	number(20),
	worst_bbo_exchange_market	number(20),
	product_State               number(2),
	national_last_sale_price   varchar(20),
	national_last_sale_volume  varchar(200),
        last_sale_tick_direction   char,
	national_last_sale_exchange   varchar(5),
        creat_rec_time                  timestamp default systimestamp
)
/* tablespace sbtb_sm_data01; */
;

alter table order_market_monitor
add constraint order_market_monitor_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01; */
;

CREATE INDEX order_market_monitor_i2 ON
order_market_monitor(CREAT_REC_TIME);

whenever sqlerror continue
