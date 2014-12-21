whenever sqlerror continue
whenever sqlerror exit failure
ALTER TABLE sbt_tradereportentry
ADD( 
       buy_supression_reason         number(3),
       sell_supression_reason        number(3),
       buy_away_exchange_acronym   varchar2(5),
       sell_away_exchange_acronym  varchar2(5),
       buy_orderdate                 varchar2(8),
       sell_orderdate                varchar2(8),
       buy_orsid                     varchar2(6),
       sell_orsid                    varchar2(6),
       buy_auct_trade_ind char(1),
       sell_auct_trade_ind char(1),
	CREAT_REC_TIME         TIMESTAMP DEFAULT SYSTIMESTAMP
       );
