  select to_char(convert_java_time(entry_minute), 'HH24:MI:SS') entry_time,
 best_pub_count, trunc(best_pub_count/ 60.0, 2) best_pub_rate
 from (select (trunc(entry_time / 60000) * 60000) entry_minute,
 count(decode(entry_type, 1, 1)) best_pub_count from mkt_data_hist
  where BEST_PUB_BID_SIZE !=0 or BEST_PUB_ASK_SIZE !=0
group by (trunc(entry_time / 60000) * 60000))
order by 1
/

