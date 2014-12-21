whenever sqlerror continue

drop table recap;

whenever sqlerror exit failure

create table recap
(
	databaseIdentifier	number(20) not null,
	last_sale_price		varchar2(12),
	last_sale_vol		number(10),
	last_sale_price_vol		number(10),
	total_vol		number(10),
	trade_time		number(16),
	tick_dir		char(1),
	tick_amt		varchar2(12),
	net_chg			varchar2(12),
	net_chg_dir		char(1),
	bid_price		varchar2(12),
	bid_size		number(10),
	bid_time		number(16),
	bid_dir			char(1),
	ask_price		varchar2(12),
	ask_size		number(10),
	ask_time		number(16),
	otc_ind			char(1),
	recap_prefix	varchar2(20),
	high_price		varchar2(12),
	high_price_vol  number(10),
	low_price		varchar2(12),
	low_price_vol   number(10),
	open_price		varchar2(12),
	open_price_vol  number(10),
	close_price		varchar2(12),
	has_been_traded_ind	char(1),
	close_price_suffix char(1),
        yesterdays_close_price varchar2(12),
        yesterdays_close_price_suffix char(1)
);

alter table recap
add constraint recap_pk
primary key (databaseIdentifier);

