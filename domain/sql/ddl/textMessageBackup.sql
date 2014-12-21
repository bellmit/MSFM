whenever sqlerror continue

drop table textMessageBackup;

create table textMessageBackup
(
	databaseIdentifier	number(20) not null,
	sender			varchar2(50) not null,
	messageText			varchar2(4000),
	messageSubject		varchar2(2000),
	messageTimeStamp		number(38) not null,
	timeToLive			number(10) not null,
	originalMessageKey	number(20) not null,
	replyRequested		char(1) not null,
    backupDate          date not null
)
/* tablespace sbtg_sm_data01 */
;
