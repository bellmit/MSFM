whenever sqlerror continue

drop table childParentRelationship;

create table childParentRelationship
(
	databaseIdentifier	number(20) not null,
	child				number(20) not null,
	parent			number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table childParentRelationship
add constraint childParentRelationship_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index childParentRelationship_i1
on childParentRelationship(child,parent)
/* tablespace sbtg_sm_indx01 */
;
