whenever sqlerror continue

drop table prod_class;

whenever sqlerror exit failure

create table prod_class
(
	databaseIdentifier	number(20) not null,
	class_sym		varchar2(10) not null,
	prod_type_code		number(2) not null,
	prod_desc		number(20),
	undly_prod		number(20),
	prim_exch_sym		varchar2(5),
	sess_code		varchar2(20),
	list_state		number(2) not null,
        is_test_class		char(1),
	default_trans_fee_code  char(2),
	settlement_type  	number(2),
	act_date		number(16),
	inact_date		number(16),
	create_time		number(16) not null,
	lmod_time		number(16) not null,
	location 		varchar2(32),
	qpe_flag        	char(1),
	extensions		varchar2(128),
	multilist		char(1),
	linkage_indicator char(1)
)
/* tablespace sbtg_sm_data01 */
;

alter table prod_class
add constraint prod_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;


create index prod_class_i1
on prod_class(prod_type_code, class_sym)
/* tablespace sbtg_sm_indx01 */
;

