whenever sqlerror continue

drop table SBT_Quote;

whenever sqlerror exit failure

create table SBT_Quote
(
	databaseIdentifier number(20) not null,
	quote_Key number(20),
	session_name varchar2(30),
	product_Key number(20),
	class_key number(20),
	cancel_Report_Quote_Key number(12),
	member_Key varchar2(3),
	userId varchar2(15),
	userKey number(20),
	clearing_Firm_Key varchar(20),
	ask_Price varchar2(20),
	ask_Quantity number(20),
	ask_BookedTime number(20),
	ask_BookedStatus number(3),
	bid_Price varchar2(20),
	bid_Quantity number(20),
	bid_BookedTime number(20),
	bid_BookedStatus number(3),
	transactionSequenceNumber number(20),
	userAssignedId varchar2(256),
	updatecontrolid number(5),
    state number(3)
)
/* tablespace sbtb_me_data03; */
;

alter table SBT_Quote
add constraint SBTQuotepk
primary key (databaseIdentifier)
/* using index tablespace sbtb_me_indx03 */
;
