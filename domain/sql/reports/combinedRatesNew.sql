set linesize 150

select to_char(convert_java_time(rcv_time), 'HH24:MI:SS') TIME,
       trunc(sum(order_count)/60,2) ORD_RATE,
       trunc(sum(cancel_replaces)/60,2) REPLACE_RATE,
       trunc(sum(order_cancels)/60,2) O_CANCEL_RATE,
       trunc(sum(quote_count)/60,2) QUOTERATE,
       trunc(sum(quote_cancels)/60,2) Q_CANCEL_RATE,
       trunc(sum(quote_count + quote_cancels + order_count + cancel_replaces + order_cancels)/60,2) TOTALRATE,
       trunc(sum(auction_count)/60,2) AUC_RATE,
       trunc(sum(trade_count)/60,2) TRADERATE,
       trunc(sum(trade_count) * 100/ greatest(sum(order_count) + sum(quote_count), 1), 2 ) TRADE_PCT,
       sum(trade_volume) TRADE_VOL
from (
select sum(decode(eventtype,1,1,0) - decode(eventtype,6,1,0)) order_count, sum(decode(eventtype,6,1,0)) cancel_replaces, sum(decode(eventtype,3,1,0)) order_cancels, 0 quote_count, 0 quote_cancels, 0 auction_count, 0 trade_count, 0 trade_volume, (trunc(eventtime / 60000) * 60000) rcv_time
from sbtOrderHistory
group by (trunc(eventtime / 60000) * 60000)
union all
select 0 order_count, 0 cancel_replaces, 0 order_cancels, sum(decode(eventtype, 101, 1, 106, 1,0)) quote_count, sum(decode(eventtype,103,1,104,1,105,1,0)) quote_cancels, 0 auction_count, 0 trade_count, 0 trade_volume, (trunc(eventtime / 60000) * 60000) rcv_time
from sbtQuoteHistory
group by (trunc(eventtime / 60000) * 60000)
union all
select 0 order_count, 0 cancel_replaces, 0 order_cancels, 0 quote_count, 0 quote_cancels, count(*) auction_count, 0 trade_count, 0 trade_volume, (trunc(starttime /60000) * 60000) starttime
from sbtauction
group by (trunc(starttime / 60000) * 60000)
union all
select 0 order_count, 0 cancel_replaces, 0 order_cancels, 0 quote_count, 0 quote_cancels, 0 auction_count, count(*) trade_count, sum(last_sale_vol) trade_volume, (trunc(entry_time / 60000) * 60000) rcv_time
from mkt_data_hist
where entry_type = 2
group by (trunc(entry_time / 60000) * 60000)
)
group by rcv_time
order by 1
/
