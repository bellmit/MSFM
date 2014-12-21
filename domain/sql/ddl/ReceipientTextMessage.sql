whenever sqlerror continue

drop table ReceipientTextMessage;

create table ReceipientTextMessage
(
	databaseIdentifier		number(20) not null,
	messageKey				varchar2(50) not null,
	messageState			number(20) not null,
	replySent				char(1) not null,
	message				number(20) not null,
	receipient				number(20) not null	
)
/* tablespace sbtg_sm_data01 */
;

alter table ReceipientTextMessage
add constraint rcptxt_pk
primary key (databaseIdentifier)
/* using index tablespace sbtg_sm_indx01 */
;

create index ReceipientTextMessage_i1
on ReceipientTextMessage( messageKey )
/* tablespace sbtg_sm_indx01 */
;

create index ReceipientTextMessage_i2
on ReceipientTextMessage( messageState )
/* tablespace sbtg_sm_indx01 */
;

create index ReceipientTextMessage_i3
on ReceipientTextMessage( message )
/* tablespace sbtg_sm_indx01 */
;

create index ReceipientTextMessage_i4
on ReceipientTextMessage( receipient )
/* tablespace sbtg_sm_indx01 */
;
