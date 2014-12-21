clear
break on userid skip 2 on report
compute count of PRODUCTKEY on userid

select rpt.USERID, cls.PROD_CLASS_SYM, rpt.PRODUCTKEY , rpt.FILL_REPORT
from
(
select userid, PRODUCTKEY, count(*) FILL_REPORT  from sbt_user_report 
where 
EVENTTYPE = 2
and UserID like 'CME%'
--userid = 'BAT'
group by PRODUCTKEY,USERID
order by UserId,productKey
) rpt, trading_prod prod, trading_class cls
where
rpt.PRODUCTKEY = prod.PROD_KEY 
and
prod.Trading_class = cls.DATABASEIDENTIFIER
order by 1,2;
