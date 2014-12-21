whenever sqlerror continue

drop table group_relationship;

whenever sqlerror exit failure

create table group_relationship
(
	databaseIdentifier	number(20)	not null,
	groupElementKey		number(20)	not null,
	childElementKey		number(20)	not null	
)
/* tablespace sbtg_sm_data01 */
;


alter table group_relationship add constraint group_relationship_pk primary key (databaseIdentifier)
/* tablespace sbtg_sm_indx01 */
;

