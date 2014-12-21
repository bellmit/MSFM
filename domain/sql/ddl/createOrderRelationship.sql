whenever sqlerror continue

drop table OrderRelationship;

whenever sqlerror exit failure

create table OrderRelationship
(
	databaseIdentifier	    number(20) not null,
	oid1                    number(20) not null,
	oid2                    number(20) not null,
	type                    number(3) not null,
	relat_sess_name			varchar2(30),
	CREAT_REC_TIME         TIMESTAMP DEFAULT SYSTIMESTAMP
)
;

alter table OrderRelationship
add constraint sbt_orderrelationship_pk
primary key (databaseIdentifier)
;

CREATE INDEX OrderRelationship_i1 ON
OrderRelationship(CREAT_REC_TIME);

