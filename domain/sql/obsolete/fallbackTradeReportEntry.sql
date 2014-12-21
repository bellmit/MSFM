whenever sqlerror exit failure rollback

REM OPTIONAL DATA FOR MARKET MAKERS
REM Optional data conversion for Market Maker sides must be done before
REM subaccount conversion because the Version 2.0 subaccount value is needed in
REM Version 1.1 Optional Data and will be overwritten with the account.
REM
REM Filler          (length: 5)
REM Subaccount      (length: 6)
REM Filler          (length: 5)

update sbt_tradereportentry
set buyeroptionaldata=
(
    '     ' || nvl(rpad(buyersubaccount, 6, ' '), '      ') || '     '
)
where buyerorigintype='M';

update sbt_tradereportentry
set selleroptionaldata=
(
    '     ' || nvl(rpad(sellersubaccount, 6, ' '), '      ') || '     '
)
where sellerorigintype='M';

REM OPTIONAL DATA FOR RETAIL TRADES
REM Firm Branch                 (length: 3)
REM Firm Branch Sequence Number (length: 4)
REM Marketable Indicator        (length: 1, blank)
REM Contra Number               (length: 1, value '1')
REM Correspondent Firm Id       (length: 3)
REM Optional Data               (length: 4, value: first 4 bytes of optional data)

update sbt_tradereportentry
set buyeroptionaldata=
(
    nvl(rpad(buy_firm_branch, 3, ' '), '   ') ||
    nvl(lpad(to_char(buy_firm_branch_seq_no), 4, '0'), '0000') ||
    ' 1' ||
    nvl(rpad(buy_corr_id, 3, ' '), '   ') ||
    nvl(rpad(buyeroptionaldata, 4, ' '), '    ')
)
where buyerorigintype != 'M';

update sbt_tradereportentry
set selleroptionaldata=
(
    nvl(rpad(sell_firm_branch, 3, ' '), '   ') ||
    nvl(lpad(to_char(sell_firm_branch_seq_no), 4, '0'), '0000') ||
    ' 1' ||
    nvl(rpad(sell_corr_id, 3, ' '), '   ') ||
    nvl(rpad(selleroptionaldata, 4, ' '), '    ')
)
where sellerorigintype != 'M';

REM ORIGIN TYPE
REM
REM Shouldn't be needed, validation should ensure that origin types
REM for options are valid (i.e. new origin codes for CME not allowed).

REM SUBACCOUNT
REM Optional data conversion for Market Maker sides must be done before
REM subaccount conversion because the Version 2.0 subaccount value is needed in
REM Version 1.1 Optional Data and will be overwritten with the account.
REM
REM For all trade sides resulting from a market maker order,
REM or for all trade sides resulting from a quote,
REM set the subaccount field to the value of the account field.

update sbt_tradereportentry
set buyersubaccount=buy_acct
where buyerorigintype='M';

update sbt_tradereportentry
set sellersubaccount=sell_acct
where sellerorigintype='M';

REM CMTA
REM
REM For all trade sides resulting from an order,
REM set the CMTA to the correspondent firm id
REM where the CMTA is blank or not numeric.
REM
REM For all trades sides resulting from a quote,
REM blank out the CMTA.

update sbt_tradereportentry
set buyercmta=buy_corr_id
where
(
    length(buyercmta) is null
    or
    length(translate(buyercmta, 'A1234567890', 'A')) is not null
)
and buyorderid != 0;

update sbt_tradereportentry
set sellercmta=sell_corr_id
where
(
    length(sellercmta) is null
    or
    length(translate(sellercmta, 'A1234567890', 'A')) is not null
)
and sellorderid != 0;

update sbt_tradereportentry
set buyercmta=''
where buyquoteid != 0;

update sbt_tradereportentry
set sellercmta=''
where sellquoteid != 0;


REM POSITION EFFECT
REM
REM For all trade sides resulting from a quote,
REM set the position effect to 'N'.

update sbt_tradereportentry
set buyerpositioneffect='N'
where buyquoteid != 0;

update sbt_tradereportentry
set sellerpositioneffect='N'
where sellquoteid != 0;

commit;

whenever sqlerror continue;
