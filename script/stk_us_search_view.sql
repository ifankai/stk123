create or replace view stk_us_search_view as
select b.*, 
case when 'CNY'=b.fn_currency then round(decode(Cash_Cash_Equivalents,0,0,market_cap/(Cash_Cash_Equivalents/100/6.8)), 2) 
  else round(decode(Cash_Cash_Equivalents,0,0,market_cap/(Cash_Cash_Equivalents/100)), 2) end market_cap_cash_rate,
case when 'CNY'=b.fn_currency then round(decode(working_capital,0,0,market_cap/(working_capital/100/6.8)), 2) 
  else round(decode(working_capital,0,0,market_cap/(working_capital/100)), 2) end market_cap_work_capital_rate  
from (
with fnDate 
as
(select t.code,t.fn_date,t.num
    from (select s.code,s.fn_Date,
                 ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num
            from (select distinct code,fn_date from stk_fn_data_us where fn_date>to_char(sysdate-1000,'yyyyMMDD')) s ) t
       where t.num = 1),
industry
as
(
SELECT code, substr(name,2) name
  FROM (SELECT ROW_NUMBER() OVER(PARTITION BY code ORDER BY rn DESC) rn,code,name        
          FROM (select code, sys_connect_by_path(name, ',') name, rn
                  from (select st.name,
                               si.code,
                               row_number() over(PARTITION by si.code order by si.industry desc) rn
                          from stk_industry_type st, stk_industry si
                         where si.industry = st.id
                           and st.source in ('sina_meigu','easymoney_meigu'))
                CONNECT BY code = PRIOR code
                       AND rn - 1 = PRIOR rn))
 WHERE rn = 1
),
kline
as
(
select * from stk_kline_us where kline_date = (select max(kline_date) kdate from stk_kline_us where code in ('.DJI','.INX'))
)     
select s.code,s.name,s.fn_currency,i.name industry, fd.fn_date /*²ÆÎñ¼¾¶È*/, a.Cash_Cash_Equivalents,
a.roe /*ttm*/, a.PE, a.PB, a.PS, a.PEG, round((k.close*s.total_capital)/10000,2) market_cap, 
a.GROSS_PROFIT_MARGIN, a.NET_PROFIT_GROWTH_RATE, a.DEBT_RATE, a.RESEARCH_RATE,
(a.current_assets-a.debt) as working_capital
from stk_us s,stk_fn_data_us_search_view a, fnDate fd, industry i, kline k
where s.code = a.code and a.code=fd.code and a.fn_date=fd.fn_date 
and s.code = i.code(+) and s.code = k.code) b
