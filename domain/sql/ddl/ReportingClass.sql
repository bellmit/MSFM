whenever sqlerror continue

drop table rpt_class;

whenever sqlerror exit failure

create table rpt_class
(
	databaseIdentifier	number(20) not null,
	class_sym		varchar2(10) not null,
	prod_type_code		number(2) not null,
	prod_class		number(20) not null,
	contr_size		number(5),
	list_state		number(2) not null,
    trans_fee_code  char(2),
	act_date		number(16),
	inact_date		number(16),
	create_time		number(16) not null,
	lmod_time		number(16) not null,
	extensions              varchar2(128)
)
/* tablespace sbtg_sm_data01 */
;

alter table rpt_class
add constraint rpt_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index rpt_class_i1
on rpt_class(prod_type_code, class_sym)
/* tablespace sbtg_sm_indx01 */
;

