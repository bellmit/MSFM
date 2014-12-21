whenever sqlerror continue

drop table LinkageOrderRelationship;

whenever sqlerror exit failure

create table LinkageOrderRelationship
(
	databaseIdentifier	    number(20) not null,
	oid1                    number(20) not null,
	oid2                    number(20) not null,
	type                    number(3) not null,
	CREAT_REC_TIME timestamp default systimestamp   /* new field, used by CDB application.*/
)
/* tablespace sbtb_me_data03 */
;

alter table LinkageOrderRelationship
add constraint sbt_lnkorderrelationship_pk
primary key (databaseIdentifier)
;

Create index sbtlnkorderrelationship_i1  ON  LinkageOrderRelationship(CREAT_REC_TIME)
/* using index tablespace sbtb_me_indx03 */
;
