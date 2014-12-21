whenever sqlerror continue

whenever sqlerror exit failure

ALTER TABLE SbtOrderLegDetail
ADD (
	totalRejectOrTimeoutQuantity number(10)
);
