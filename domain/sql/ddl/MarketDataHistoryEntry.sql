whenever sqlerror continue

drop table mkt_data_hist;

whenever sqlerror exit failure

create table mkt_data_hist
(
	databaseIdentifier	number(20) not null,
	prod_key		number(20) not null,
	entry_type		number(2) not null,
	entry_time		number(16) not null,
	bid_price		varchar2(12),
	bid_size		number(10),
	ask_price		varchar2(12),
	ask_size		number(10),
	non_cont_bid_price		varchar2(12),
	non_cont_bid_size		number(10),
	non_cont_ask_price		varchar2(12),
	non_cont_ask_size		number(10),
	best_pub_bid_price		varchar2(12),
	best_pub_bid_size		number(10),
	best_pub_ask_price		varchar2(12),
	best_pub_ask_size		number(10),
	last_sale_price		varchar2(12),
	last_sale_vol		number(10),
	undly_last_sale_price	varchar2(12),
	eop_type		    number(2),
	imbalance_qty		number(10),
	product_state		number(2),
	ticker_prefix		varchar2(4),
        session_name        varchar2(30),
        override_indicator  char(1),
        nbbo_ask_price      varchar2(12),
        nbbo_bid_price      varchar2(12),
        nbbo_ask_exchanges  varchar2(200),
        nbbo_bid_exchanges  varchar2(200),
        botr_ask_price      varchar2(12),
        botr_bid_price      varchar2(12),
        botr_ask_exchanges  varchar2(200),
        botr_bid_exchanges  varchar2(200),
        best_pub_ask_cust_size number(10),
        best_pub_bid_cust_size number(10),
        trade_through_indicator char(1),
        exchanges_indicators   varchar2(200),
        broker               varchar2(200),
        contra               varchar2(200),
        physical_location    varchar2(20),
        trade_ID             number(20),
        dayofweek number(1) not null,       /* NEW FIELD: day of the week for the entry, 1- sunday 2-Mon,etc */
        trade_server_id number(2),
        CREAT_REC_TIME timestamp default systimestamp    /* new field, used by CDB application.*/
)
storage (freelists 12 freelist groups 4)
partition by range (dayofweek)
(partition SBTMKTHISTP_1 values less than (2) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_2 values less than (3) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_3 values less than (4) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_4 values less than (5) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_5 values less than (6) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_6 values less than (7) storage (freelists 12 freelist groups 4),
partition SBTMKTHISTP_7 values less than (8) storage (freelists 12 freelist groups 4)
);

create index mkt_data_hist_i1
on mkt_data_hist(prod_key, entry_time)
storage (freelists 12 freelist groups 4)
local;


create index mkt_data_hist_i2
on mkt_data_hist(databaseidentifier)
storage (freelists 12 freelist groups 4)
local;

CREATE INDEX MKT_DATA_HIST_I3 ON MKT_DATA_HIST
(CREAT_REC_TIME,DAYOFWEEK) LOCAL;


