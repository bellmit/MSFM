whenever sqlerror continue

drop table groupClassRelationship;

create table groupClassRelationship
(
	databaseIdentifier	number(20) not null,
	groupKey		number(20) not null,
	classKey		number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table groupClassRelationship
add constraint gc_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index groupClassRelationship_i1
on groupClassRelationship(groupKey,classKey)
/* tablespace sbtg_sm_indx01 */
;
