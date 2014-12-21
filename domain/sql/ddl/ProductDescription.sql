whenever sqlerror continue

drop table prod_desc;

rem whenever sqlerror exit failure

create table prod_desc
(
	databaseIdentifier	number(20) not null,
	create_time			number(16) not null,
	lmod_time			number(16) not null,
	desc_name		varchar2(30) not null,
	base_desc_key		number(20),
	min_strike_frac		varchar2(20) not null,
	max_strike_price	varchar2(20) not null,
	prem_break_point	varchar2(20) not null,
	min_above_frac		varchar2(20) not null,
	min_below_frac		varchar2(20) not null,
	price_disp_type		char(1) not null,
	prem_price_format	char(1) not null,
	strike_price_format	char(1) not null,
	undly_price_format	char(1) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table prod_desc
add constraint prod_desc_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table prod_desc
add constraint prod_desc_u1
unique (desc_name)
/* using index tablespace sbtg_sm_indx01 */
;
