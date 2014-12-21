whenever sqlerror continue

drop table price_adj;

whenever sqlerror exit failure

create table price_adj
(
	databaseIdentifier	number(20) not null,
	adj_prod_key		number(20) not null,
	adj_type_code		number(2) not null,
	source		        number(2),
	new_prod_sym		varchar2(10),
	act_ind			char(1) not null,
	eff_date		number(16) not null,
	run_date		number(16) not null,
	split_num		number(3),
	split_denom		number(3),
	cash_div		varchar2(12),
	stock_div		varchar2(12),
	low_range		varchar2(12),
	high_range		varchar2(12),
	create_time		number(16),
	lmod_time		number(16),
	orderAction	        number(2)
)
/* tablespace sbtg_sm_data01 */
;

alter table price_adj
add constraint price_adj_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table price_adj
add constraint price_adj_u1
unique (adj_prod_key)
/* using index tablespace sbtg_sm_indx01 */
;

