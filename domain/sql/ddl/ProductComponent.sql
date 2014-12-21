whenever sqlerror continue

drop table prod_comp;

rem whenever sqlerror exit failure

create table prod_comp
(
	databaseIdentifier	number(20) not null,
	composite_prod_key	number(20) not null,
	component_prod_key	number(20) not null,
	quantity			number(20,8) not null,
	side				char
)
/* tablespace sbtg_sm_data01 */
;

alter table prod_comp
add constraint prod_comp_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table prod_comp
add constraint prod_comp_u1
unique (composite_prod_key, component_prod_key)
/* using index tablespace sbtg_sm_indx01 */
;

