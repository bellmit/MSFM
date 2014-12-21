whenever sqlerror continue

drop table textMessageTemplate;

create table textMessageTemplate
(
	databaseIdentifier	number(20) not null,
	templateName		varchar2(100) not null,
	templateText		varchar2(4000) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table textMessageTemplate
add constraint template_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index textMessageTemplate_i1
on textMessageTemplate( templateName )
/* using index tablespace sbtg_sm_indx01 */
;
