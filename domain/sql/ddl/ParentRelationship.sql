whenever sqlerror continue

drop table parentChildRelationship;

create table parentChildRelationship
(
	databaseIdentifier	number(20) not null,
	parent			number(20) not null,
	child				number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table parentChildRelationship
add constraint pc_key
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index parentChildRelationship_i1
on parentChildRelationship(parent,child)
/* tablespace sbtg_sm_indx01 */
;
