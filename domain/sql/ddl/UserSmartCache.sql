whenever sqlerror continue

drop table user_smart_cache;

whenever sqlerror exit failure

create table user_smart_cache
(
	databaseIdentifier     number(20) not null,
	userId                 varchar2(15) not null,
	userKey                number(20),
	lastActionTime         number(20),
	TRADESERVERID 		   NUMBER(1)
)
/* tablespace sbtb_me_data03 */
;

alter table user_smart_cache
add constraint user_smart_cache_pk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;

alter table user_smart_cache
add constraint user_smart_cache_u1
unique (userId,tradeserverid)
/* using index tablespace sbtb_me_indx03 */
;
