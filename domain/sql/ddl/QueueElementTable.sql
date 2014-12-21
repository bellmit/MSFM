whenever sqlerror continue

drop table queueElementTable;
drop table queueElementDataTable;

whenever sqlerror exit failure

create table queueElementTable
(
 DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
 NEXTELEMENT          NUMBER(20) ,
 DATABYTES            NUMBER(20)
)
/* tablespace sbtg_sm_data01 */
;

alter table queueElementTable add constraint queueElementTable_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create table queueElementDataTable
(
 DATABASEIDENTIFIER   NUMBER(20) NOT NULL,
 DATABYTES            LONG RAW
)
/* tablespace sbtg_sm_data01 */
;

alter table queueElementDataTable add constraint queueElementDataTable_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;
