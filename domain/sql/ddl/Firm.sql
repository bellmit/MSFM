whenever sqlerror continue

drop table firm;

whenever sqlerror exit failure

create table firm
(
	databaseIdentifier	number(20) not null,
	firmAcronym		varchar2(3) not null,
	exchangeAcronym         varchar2(5),
	firmNumber 		varchar2(5) not null,
	versionNumber		number(10),
	isClearingFirm		char(1),
	isActive		char(1),
	fullName		varchar2(200),
	membershipKey		number(20),
	lastModifiedTime	number(16) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table firm
add constraint firm_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create UNIQUE index firm_i1
on firm(membershipKey)
/* tablespace sbtg_sm_indx01 */
;
