select to_char(convert_java_time(entry_minute), 'HH24:MI:SS') entry_time,
       cur_mkt_count,
       trunc(cur_mkt_count / 60.0, 2) cur_mkt_rate,
       last_sale_count,
       trunc(last_sale_count / 60.0, 2) last_sale_rate,
       trunc((cur_mkt_count + last_sale_count) / 60.0, 2) total_rate
from (select (trunc(entry_time / 60000) * 60000) entry_minute,
             count(decode(entry_type, 1, 1)) cur_mkt_count,
	     count(decode(entry_type, 2, 1)) last_sale_count
      from mkt_data_hist
      group by (trunc(entry_time / 60000) * 60000))
order by 1;
