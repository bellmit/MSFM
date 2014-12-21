clear
break on userid skip 2 on report
compute count of PRODUCTKEY on userid
compute sum  of NO_OF_PROD_TRADED  on userid

select rpt.USERID, rpt.PRODUCTKEY , cls.prod_class_sym, rpt.NO_OF_PROD_TRADED
from
(
select userid, PRODUCTKEY, count(*) NO_OF_PROD_TRADED  from sbt_user_report 
--where EVENTTYPE = 106
where userid in ( 'NUM','BAT','AAD')
group by PRODUCTKEY,USERID
order by UserId
) rpt, trading_prod prod, trading_class cls
where rpt.PRODUCTKEY = prod.PROD_KEY
and prod.trading_class = cls.PROD_CLASS_KEY
--and UserID like 'CME%'
