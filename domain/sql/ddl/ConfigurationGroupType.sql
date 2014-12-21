whenever sqlerror continue

drop table configurationGroupType;

create table configurationGroupType
(
	databaseIdentifier	number(20) not null,
	groupType			number(10) not null,
	groupTypeDescription	varchar2(50) not null,
	exclusiveMembership	char(1) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table configurationGroupType
add constraint grouptype_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table configurationGroupType
add constraint unique_gt
unique(groupType)
/* using index tablespace sbtg_sm_indx01 */
;

