whenever sqlerror continue

drop table configurationGroup;

create table configurationGroup
(
	databaseIdentifier	number(20) not null,
	groupName		varchar2(256) not null,
	groupType		number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table configurationGroup
add constraint configurationGroup_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table configurationGroup
add constraint configurationGroup_u1
unique(groupName)
/* using index tablespace sbtg_sm_indx01 */
;
