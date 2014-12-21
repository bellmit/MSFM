select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') RCV_TIME,
       order_count,
       trunc(order_count / 60.0, 2) order_rate,
       trade_count,
       trunc(trade_count / 60.0, 2) trade_rate,
       trunc(trade_count * 100.0 / greatest(order_count, 1), 2) trade_pct
from (select (trunc(eventtime / 60000) * 60000) rcv_time,
             count(decode(eventtype, 1, 1)) order_count,
             sum(decode(eventtype, 2, .5)) trade_count
      from sbtorderhistory
      group by (trunc(eventtime / 60000) * 60000))
/
