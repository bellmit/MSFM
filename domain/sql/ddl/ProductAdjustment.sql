whenever sqlerror continue

drop table prod_adj;

whenever sqlerror exit failure

create table prod_adj
(
	databaseIdentifier	number(20) not null,
	adj_prod_key		number(20),
	act_type_code		number(2) not null,
	rpt_class_adj_key	number(20) not null,
	new_class_sym		varchar2(10),
	new_expr_date		number(16),
	new_exer_price		varchar2(12),
	new_opt_type_code	varchar2(4),
	new_opra_month_code	char(1),
	new_opra_price_code	char(1),
	create_time		number(16),
	lmod_time		number(16)
)
/* tablespace sbtg_sm_data01 */
;

alter table prod_adj
add constraint prod_adj_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index prod_adj_i1
on prod_adj(rpt_class_adj_key)
/* sbtg_sm_indx01 */
;

