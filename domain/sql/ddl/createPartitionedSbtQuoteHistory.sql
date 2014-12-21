whenever sqlerror continue

drop table SBTQuoteHistory;

rem whenever sqlerror exit failure

create table SBTQuoteHistory
(
    databaseIdentifier number(20) not null,
    /* prefix used by every record */
    memberKey varchar2(5),
    userId varchar2(15),
    userKey number(20),
    productKey number(20),
    classKey number(20),
    eventType number(3),
    eventTime number(24),
    state char(1),                      /* ACTIVE, PURGED (internal use) */
    quoteKey number(20),                /* link to quote table (all records) */
    /* fields common to most event types */
    userAssignedId varchar2(256),
    transactionSequenceNumber number(20),
    /* field(s) commented by event */
    side char(1),                       /* FILL_QUOTE BUST_QUOTE_FILL */
    bidPrice varchar2(20),              /* NEW_QUOTE UPDATE_QUOTE */
    bidQuantity number(20),             /* NEW_QUOTE UPDATE_QUOTE */
    askPrice varchar2(20),              /* NEW_QUOTE UPDATE_QUOTE */
    askQuantity number(20),             /* NEW_QUOTE UPDATE_QUOTE */
    tradePrice varchar2(20),            /* FILL_QUOTE BUST_QUOTE_FILL */
    tradedQuantity number(10),          /* FILL_QUOTE */
    leavesQuantity number(10),          /* FILL_QUOTE */
    /* link to trade table */
    tradeDBId number(20),               /* FILL_QUOTE BUST_QUOTE_FILL */
    cancelReason number(3),             /* CANCEL_QUOTE, CANCEL_ALL_QUOTES */
    bustedQuantity number(10),          /* BUST_QUOTE_FILL */
    session_name varchar2(30),
    dayofweek number(1) not null       /* NEW FIELD: day of the week for the entry, 1- sunday 2-Mon,etc */
)
    partition by range (dayofweek)
        (partition sbtquotehistp_1 values less than (2),
         partition sbtquotehistp_2 values less than (3),
         partition sbtquotehistp_3 values less than (4),
         partition sbtquotehistp_4 values less than (5),
         partition sbtquotehistp_5 values less than (6),
         partition sbtquotehistp_6 values less than (7),
         partition sbtquotehistp_7 values less than (8)
        );

/* tablespace sbtb_me_data03 */

create index SBTQuoteHistory_i1
on SBTQuoteHistory(databaseIdentifier) LOCAL;

create index SBTQuoteHistory_i2
on SBTQuoteHistory(eventTime) LOCAL;
