whenever sqlerror continue

drop table NBBOAgentMap;

whenever sqlerror exit failure

create table NBBOAgentMap
(
    databaseIdentifier  number(20) not null,
	classKey	        number(20) not null,
    sessionName         varchar2(30) not null,
	agentId 	        varchar2(15) not null,
	status			    char(1),
	registerTime		number(16),
	unregisterTime		number(16),
        creat_rec_time          timestamp default systimestamp
)
/* tablespace SBTG_SM_DATA01; */
;

alter table NBBOAgentMap
add constraint NBBOAgentMap_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

create index NBBOAgentMap_i1
on NBBOAgentMap(classKey, sessionName)
/* using index tablespace sbtg_sm_indx01 */
;

CREATE INDEX nbboagentmap_i2 ON
nbboagentmap(CREAT_REC_TIME);

