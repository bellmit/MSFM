set lines 100;

select 	to_char(convert_java_time(starttime), 'HH24:MI:SS') TIME, 
	auction_count as AUC_COUNT, trunc(auction_count/60,2) AUC_RATE, 
	trunc(totalTradeMillis/auction_count,0) as AVG_TRADING_MILLIS 
from (	
	select count(*) auction_count, 
	(trunc(starttime /60000) * 60000) starttime, 
	sum(endtime - expiredtime) totalTradeMillis 
	from sbtauction group by (trunc(starttime / 60000) * 60000) 
      ) ;