whenever sqlerror exit failure rollback;

delete from product p 
where p.prod_type_code = 11
and
p.list_state_code != 1;

delete from product p
where exists
(
    select '' from prod_comp pc
    where p.databaseidentifier = pc.composite_prod_key
    and not exists
    (
        select '' from product pp
        where pp.databaseidentifier = pc.component_prod_key
    )
);

delete from prod_comp pc
where not exists
(
    select '' from product p
    where p.databaseidentifier = pc.composite_prod_key
);

delete from product p
where p.prod_type_code = 11
and
not exists
(
    select * from prod_comp pc
    where p.databaseIdentifier = pc.composite_prod_key
);   

delete from trading_property tp
where not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = tp.class_key
)
and tp.class_key != 0;

delete from groupclassrelationship gcr
where not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = gcr.classkey
);

delete from profile p
where not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = p.class_key
)
and p.class_key != 0;

delete from template_class tc
where not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = tc.prod_class_key
);

delete from user_quote_risk_profiles uqrp
where not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = uqrp.classkey
)
and uqrp.classkey != 0;

delete from receipient r
where r.receipienttype = 3
and not exists
(
    select '' from prod_class pc
    where pc.databaseidentifier = to_number(r.receipient)
);

delete from price_adj pa
where not exists
(
   select '' from product p
   where p.databaseidentifier = pa.adj_prod_key
)
or
trunc(convert_java_time(pa.run_date)) <= trunc(sysdate);

delete from rpt_class_adj rca
where not exists
(
    select '' from price_adj pa
    where pa.databaseidentifier = rca.price_adj_key
);

delete from prod_adj pa
where not exists
(
    select '' from rpt_class_adj rca
    where rca.databaseidentifier = pa.rpt_class_adj_key
);

delete from prod_adj pa
where not exists
(
    select '' from product prd
    where pa.adj_prod_key = prd.databaseidentifier
);

update PROD_CLASS set LINKAGE_INDICATOR = 1 where PROD_TYPE_CODE in (7,11) 
;

commit;
