whenever sqlerror continue

drop table product;

whenever sqlerror exit failure

create table product
(
	databaseIdentifier	number(20) not null,
	prod_type_code		number(2) not null,
	list_state_code		number(2) not null,
	prod_class		number(20) not null,
	rpt_class		number(20) not null,
	prod_name		varchar2(100) not null,
	create_time		number(16) not null,
	lmod_time		number(16) not null,
	prod_sub_type_code	number(2),
	act_date		number(16),
	inact_date		number(16),
	expr_date		varchar2(8),
	exer_price		varchar2(12),
	settlement_price	varchar2(12),
	open_interest		number(10),
	opt_type_code		varchar2(4),
	opra_month_code		char(1),
	opra_price_code		char(1),
	prod_sym		varchar2(100),
	prod_desc		varchar2(100),
	unit_meas		varchar2(40),
	std_qty			number(10),
	comp_name		varchar2(100),
	matur_date		number(16),
	cusip           	varchar2(20),
	extensions              varchar2(128),
	restrictedIndicator	char(1),
	open_interest_update_time  number(20),
	settlement_price_suffix char(1),
        yesterdays_close_price varchar2(12),
        yesterdays_close_price_suffix char(1)
)
/* tablespace sbtg_me_data01 */
;

alter table product
add constraint product_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_me_indx01 */
;

alter table product
add constraint product_u1
unique (prod_name)
deferrable initially deferred
/* using index tablespace sbtg_me_indx01 */
;

create index product_i1
on product(prod_class)
/* tablespace sbtg_me_indx01 */
;

create index product_i2
on product(rpt_class)
/* tablespace sbtg_me_indx01 */
;

