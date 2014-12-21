whenever sqlerror continue

drop table sbt_user;

whenever sqlerror exit failure

create table sbt_user
(
	databaseIdentifier	number(20) not null,
	lastModifiedTime	number(16) not null,
	user_name			varchar2(15),
	userId				varchar2(15) not null,
	user_type			number(5),
	full_name			varchar2(200),
	firm_key			number(20),
	mbr_key				number(20),
	acr				varchar2(10),
	exchange_acronym                varchar2(5),
	role				char(1),
	active				char(1),
	versionNumber		number(5),
	quoteRiskManagementEnabled varchar2(2),
	testClassesOnly			char(1)
)
/* tablespace SBTG_SM_DATA01; */
;

alter table sbt_user
add constraint sbt_user_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

alter table sbt_user
add constraint sbt_user_u1
unique (userId)
/* using index tablespace sbtg_me_indx01 */
;

alter table sbt_user
add constraint sbt_user_u2
unique (acr,exchange_acronym)
/* using index tablespace sbtg_me_indx01 */
;

create bitmap index sbt_user_b2 on sbt_user(user_type); /* using index tablespace sbtg_me_indx01 */

create bitmap index sbt_user_b3 on sbt_user(role); /* using index tablespace sbtg_me_indx01 */


