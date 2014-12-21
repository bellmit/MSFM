whenever sqlerror continue

drop table trading_property;

whenever sqlerror exit failure

create table trading_property
(
	databaseIdentifier	number(20) not null,
	session_name		varchar2(30),
	class_key			number(20),
	prop_type			number(5),
	seq_id				number(20),
	int_val1			number(12),
	int_val2			number(12),
	int_val3			number(12),
	dbl_val1			number(20,8),
	dbl_val2			number(20,8),
	dbl_val3			number(20,8)
)
/* tablespace sbtg_sm_data01; */
;

alter table trading_property
add constraint trading_property
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

