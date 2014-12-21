whenever sqlerror continue

drop table ReceipientTextMessageBackup;

create table ReceipientTextMessageBackup
(
	databaseIdentifier		number(20) not null,
	messageKey				varchar2(50) not null,
	messageState			number(20) not null,
	replySent				char(1) not null,
	message				number(20) not null,
	receipient				number(20) not null,
    backupDate              date not null
)
/* tablespace sbtg_sm_data01 */
;
