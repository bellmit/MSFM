SPOOL ../tmp/SBTPRODCONV.TXT  

SELECT 'Spool file saved at tmp/SBTPRODCONV.TXT.' MESSAGE FROM DUAL;	

SELECT TO_CHAR(SYSDATE, 'HH24:MI:SS') || ' Updating PRODUCT.INACT_DATE...' MESSAGE FROM DUAL;

UPDATE PRODUCT 
 SET LMOD_TIME  = CONVERT_DATE(SYSDATE),
     INACT_DATE = CONVERT_DATE(TO_DATE(EXPR_DATE, 'YYYYMMDD'))
WHERE PROD_TYPE_CODE IN (4, 7);

SELECT TO_CHAR(SYSDATE, 'HH24:MI:SS') || ' Complete. ' MESSAGE FROM DUAL;	

SELECT 'Checking if still has products inactive today' MESSAGE FROM DUAL;	

SELECT COUNT(*) FROM product
WHERE list_state_code=1
      AND inact_date <= convert_date(SYSDATE)
      AND (prod_type_code = 4 OR prod_type_code = 7)

SELECT 'You need to commit or rollback the changes based on the count above.' MESSAGE FROM DUAL;	

SELECT 'Checking and Updating settlement_type to 2 if is 0 or is NULL...' MESSAGE FROM DUAL;

select count(*) from prod_class where SETTLEMENT_TYPE = 0 or SETTLEMENT_TYPE is NULL;

update prod_class set SETTLEMENT_TYPE = 2 where SETTLEMENT_TYPE = 0 or SETTLEMENT_TYPE is NULL;

SELECT 'REMINDER again:  Do not exit.  You need to commit or rollback for these changes explicitly!!!' MESSAGE FROM DUAL;


SPOOL OFF

