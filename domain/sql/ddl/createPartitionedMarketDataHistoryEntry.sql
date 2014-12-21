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
	last_sale_price		varchar2(12),
	last_sale_vol		number(10),
	undly_last_sale_price	varchar2(12),
	eop_type		number(2),
	imbalance_qty		number(10),
	product_state		number(2),
	ticker_prefix		varchar2(4),
    session_name        varchar2(30),
    dayofweek number(1) not null,       /* NEW FIELD: day of the week for the entry, 1- sunday 2-Mon,etc */ 
        trade_server_id         number(2),
        creat_rec_time          timestamp default systimestamp
)
    partition by range (dayofweek)
        (partition sbtmkthistp_1 values less than (2),
         partition sbtmkthistp_2 values less than (3),
         partition sbtmkthistp_3 values less than (4),
         partition sbtmkthistp_4 values less than (5),
         partition sbtmkthistp_5 values less than (6),
         partition sbtmkthistp_6 values less than (7),
         partition sbtmkthistp_7 values less than (8)
        );

/* tablespace sbtb_me_data03 */


create index mkt_data_hist_i1
on mkt_data_hist(databaseIdentifier) LOCAL;

create index mkt_data_hist_i2
on mkt_data_hist(prod_key, entry_time) LOCAL;



