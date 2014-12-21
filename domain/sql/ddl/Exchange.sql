whenever sqlerror continue

drop table exchange;

whenever sqlerror exit failure

create table exchange
(
	databaseIdentifier	number(20) not null,
	exchange_name		varchar2(196),
	acronym			varchar2(5),
	membershipKey		number(20)
)
/* tablespace sbtg_sm_data01 */
;

alter table exchange
add constraint exchange_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

