select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') RCV_TIME,
       quote_count,
       trunc(quote_count / 60, 2) quote_rate,
       trade_count,
       trunc(trade_count / 60, 2) trade_rate,
       trunc(trade_count * 100 / (quote_count+1), 2) trade_pct
from (select (trunc(eventtime / 60000) * 60000) rcv_time,
             count(decode(eventtype, 101, 1, 106, 1)) quote_count,
             sum(decode(eventtype, 102, .5)) trade_count
      from sbtquotehistory
      group by (trunc(eventtime / 60000) * 60000))
/
