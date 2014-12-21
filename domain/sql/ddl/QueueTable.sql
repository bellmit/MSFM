whenever sqlerror continue

drop table queueTable;

whenever sqlerror exit failure

create table queueTable
(
 DATABASEIDENTIFIER     NUMBER(20) not null,
 LISTNAME               VARCHAR2(256) not null,
 FIRSTELEMENT           NUMBER(20) ,
 LASTELEMENT            NUMBER(20) ,
 FREEELEMENTPOOL        NUMBER(20) ,
 LISTSIZE               NUMBER(12),
 TOTALNBRELEMENTS       NUMBER(12),
 MAXIMUMSIZE            NUMBER(12),
 DEFAULTTIMEOUT         NUMBER(12)
)
/* tablespace sbtg_sm_data01 */
;

alter table queueTable add constraint queueTable_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table queueTable add constraint queueTable_u1
unique (listname)
/* using index tablespace sbtg_sm_indx01 */
;
