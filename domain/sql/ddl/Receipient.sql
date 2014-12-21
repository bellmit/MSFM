whenever sqlerror continue

drop table Receipient;

create table Receipient
(
	databaseIdentifier	number(20) not null,
	receipient			varchar2(50) not null,
	receipientType		number(20) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table Receipient
add constraint rcp_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index Receipient_i1
on Receipient( receipient )
/* tablespace sbtg_sm_indx01 */
;

create index Receipient_i2
on Receipient( receipientType )
/* tablespace sbtg_sm_indx01 */
;
