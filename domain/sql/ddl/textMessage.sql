whenever sqlerror continue

drop table textMessage;

create table textMessage
(
	databaseIdentifier	number(20) not null,
	sender			varchar2(50) not null,
	messageText			varchar2(4000),
	messageSubject		varchar2(2000),
	messageTimeStamp		number(38) not null,
	timeToLive			number(10) not null,
	originalMessageKey	number(20) not null,
	replyRequested		char(1) not null
)
/* tablespace sbtg_sm_data01 */
;

alter table textMessage
add constraint message_key
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index textMessage_i1
on textMessage( timeToLive )
/* using index tablespace sbtg_sm_indx01 */
;
