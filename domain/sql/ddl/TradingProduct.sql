whenever sqlerror continue

drop table trading_prod;

whenever sqlerror exit failure

create table trading_prod
(
	databaseIdentifier	number(20) not null,
	prod_key		number(20) not null,
	trading_class		number(20) not null,
	rpt_class_key		number(20) not null,
	target_state_code	number(2),
	cur_state_code		number(2),
	init_opn_occurred	char(1),
	enab_for_session	char(1),
	trade_in_session	char(1),
    	last_trade_price    	varchar2(12),
	close_bid           	varchar2(12),
	close_ask           	varchar2(12),
	strategy_legs		varchar2(2000),
	prod_sub_type_code  	number(2),
	closing_bid_size 	number(20),
        closing_ask_size 	number(20)
)
/* tablespace sbtb_sm_data01 */
;

alter table trading_prod
add constraint trading_prod_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

create index trading_prod_i1
on trading_prod(prod_key)
/* using index tablespace sbtb_sm_indx01 */
;
