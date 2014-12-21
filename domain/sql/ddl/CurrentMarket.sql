whenever sqlerror continue

drop table cur_mkt;

whenever sqlerror exit failure

create table cur_mkt
(
	databaseIdentifier	number(20) not null,
	bid_price		varchar2(10),
	bid_vols		varchar2(200),
	bid_nbbo_ind		char(1),
	ask_price		varchar2(10),
	ask_vols		varchar2(200),
	ask_nbbo_ind		char(1),
	updt_time		number(16),
    exchange        varchar2(5),
	legal_market		char(1)
);

alter table cur_mkt
add constraint cur_mkt_pk
primary key (databaseIdentifier);

