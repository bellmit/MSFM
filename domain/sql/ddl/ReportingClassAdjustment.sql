whenever sqlerror continue

drop table rpt_class_adj;

rem whenever sqlerror exit failure

create table rpt_class_adj
(
	databaseIdentifier	number(20) not null,
	adj_class_key		number(20),
	act_type_code		number(2) not null,
	price_adj_key		number(20) not null,
	new_class_sym		varchar2(10),
	prod_type_code		number(2),
	after_contr_size	number(5),
	create_time		number(16),
	lmod_time		number(16)
)
/* tablespace sbtg_sm_data01 */
;

alter table rpt_class_adj
add constraint rpt_class_adj_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index rpt_class_adj_i1
on rpt_class_adj(price_adj_key)
/* tablespace sbtg_sm_indx01 */
;

