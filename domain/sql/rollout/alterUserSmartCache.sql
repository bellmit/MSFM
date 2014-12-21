whenever sqlerror exit failure 

ALTER TABLE user_smart_cache ADD ( TRADESERVERID NUMBER(1) );

alter table user_smart_cache drop constraints USER_SMART_CACHE_U1;

alter table user_smart_cache add constraints USER_SMART_CACHE_U1 unique (userId,tradeserverid);

update user_smart_cache set TRADESERVERID = 1;



