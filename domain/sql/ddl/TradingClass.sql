whenever sqlerror continue

drop table trading_class;

whenever sqlerror exit failure

create table trading_class
(
	databaseIdentifier	number(20) not null,
	prod_class_key		number(20) not null,
	prod_type_code		number(2) not null,
	prod_class_sym		varchar2(10) not null,
	cur_sess_name		varchar2(64),
	prem_break_point		varchar2(20),
	tick_size_below		varchar2(20),
	tick_size_above		varchar2(20),
    has_undly_prod_ind  char(1),
    undly_prod_key      number(20),
    undly_prod_class_key    number(20),
    undly_rpt_class_key number(20),
    undly_prod_type_code    number(2),
    undly_sess_name         varchar2(64),
    prod_close_time number(20)
)
/* tablespace sbtb_sm_data01 */
;

alter table trading_class
add constraint trading_class_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_sm_indx01 */
;

create index trading_class_i1
on trading_class(prod_class_key)
/* using index tablespace sbtb_sm_indx01 */
;
