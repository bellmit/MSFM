whenever sqlerror continue

drop table group_element;

whenever sqlerror exit failure

create table group_element
(
	databaseIdentifier	number(20)	not null,
	elementName		varchar2(64)	not null,
	elementGroupType	number(2),	
	elementDataType		number(2),	
	elementDataKey		number(20),	
	nodeType		number(2)	not null,
	extensions		varchar2(128),	
	versionNumber		number(8)	not null,
	create_time		number(20)	not null,
	lmod_time		number(20)	not null
)
/* tablespace sbtg_sm_data01 */
;


alter table group_element add constraint group_element_pk primary key (databaseIdentifier)
/* tablespace sbtg_sm_indx01 */
;
