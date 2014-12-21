whenever sqlerror exit failure rollback;

update Product
set YESTERDAYS_CLOSE_PRICE = SETTLEMENT_PRICE,
YESTERDAYS_CLOSE_PRICE_SUFFIX = SETTLEMENT_PRICE_SUFFIX;

commit;
