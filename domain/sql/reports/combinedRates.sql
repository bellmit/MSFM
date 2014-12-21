set linesize 140

select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') TIME, sum(order_count) ORD_COUNT, trunc(sum(order_count)/60,2) ORD_RATE, sum(quote_count) QUOTECOUNT, trunc(sum(quote_count)/60,2) QUOTERATE, trunc(sum(quote_count + order_count)/60,2) TOTALRATE, sum(auction_count) AUC_COUNT, trunc(sum(auction_count)/60,2) AUC_RATE, sum(trade_count) TRADECOUNT, trunc((sum(trade_count)/60),2) TRADERATE, trunc(sum(trade_count) * 100/ greatest(sum(order_count) + sum(quote_count), 1), 2 ) TRADE_PCT
from (
select count(*) order_count, 0 quote_count, 0 auction_count, 0 trade_count,(trunc(eventtime / 60000) * 60000) rcv_time
from sbtOrderHistory
where eventtype = 1
group by (trunc(eventtime / 60000) * 60000)
union all
select 0 order_count, count(*) quote_count, 0 auction_count, 0 trade_count,(trunc(eventtime / 60000) * 60000) rcv_time
from sbtQuoteHistory
where eventtype in (101, 106)
group by (trunc(eventtime / 60000) * 60000)
union all
select 0 order_count, 0 quote_count, count(*) auction_count, 0 trade_count, (trunc(starttime /60000) * 60000) starttime from sbtauction
group by (trunc(starttime / 60000) * 60000)
union all
select 0 order_count, 0 quote_count, 0 auction_count, count(*) trade_count, (trunc(entry_time / 60000) * 60000) rcv_time
from mkt_data_hist
where entry_type = 2
group by (trunc(entry_time / 60000) * 60000)
)
group by rcv_time
/
