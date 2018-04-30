create or replace view stk_industry_search_view as
select b.*,
  market_cap/round(NET_PROFIT_LAST_YEAR/100000000,2) pe_yoy
from (
with fnDate 
as
(select t.code,t.fn_date,t.num
    from (select s.code,s.fn_Date,
                 ROW_NUMBER() over(PARTITION by code order by fn_date desc) as num
            from (select distinct code,fn_date from stk_fn_data where fn_date>=to_char(sysdate-1000,'yyyyMMDD')) s ) t
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
                           and st.source in ('cnindex', 'qq_conception', '10jqka_thshy','10jqka_gn') order by st.name)
                CONNECT BY code = PRIOR code
                       AND rn - 1 = PRIOR rn))
 WHERE rn = 1
),
myIndustry
as
(
select distinct si.code,st.name from stk_industry_type st, stk_industry si
  where si.industry = st.id and st.source = 'my_industry_fntype'
),
kline
as
(
select k.* from stk_kline k,(select code,max(kline_date) kdate from stk_kline where kline_date>=to_char(sysdate-500,'yyyyMMDD') group by code) mk
where k.code=mk.code and k.kline_date=mk.kdate
)     
select 

s.code,s.name,i.name industry, mi.name my_industry, fd.fn_date /*²ÆÎñ¼¾¶È*/,
a.roe /*ttm*/, k.pe_ttm pe, k.pb_ttm pb, k.PS_TTM ps, round((k.close*s.total_capital)/10000,2) market_cap, 
a.REVENUE_GROWTH_RATE, a.GROSS_PROFIT_MARGIN, a.NET_PROFIT_GROWTH_RATE, a.DEBT_RATE, a.RESEARCH_RATE, 
k.pe_ntile, k.pb_ntile, k.ps_ntile, (k.pe_ntile+k.pb_ntile+k.ps_ntile) ntile,
trunc(sysdate-to_date(listing_date,'yyyymmdd')) Listing_days,
a.NET_PROFIT_ER_0331, a.NET_PROFIT_ER_0630, a.NET_PROFIT_ER_0930, 
a.last_net_profit, a.last_net_profit_fn_date , a.net_profit_max,
(case when a.NET_PROFIT_ONE_QUARTER>=a.net_profit_max then 1 else 0 end) net_profit_max_flag,
(case when a.REVENUE_ONE_QUARTER>=a.revenue_max then 1 else 0 end) revenue_max_flag,
a.last_net_profit_one_quarter,a.NET_PROFIT_LAST_YEAR,a.CASH_NET_PROFIT_RATE

from stk_cn s,stk_fn_data_search_view a, fnDate fd, industry i, kline k, myIndustry mi
where s.code = a.code and a.code=fd.code and a.fn_date=fd.fn_date 
and s.code = i.code(+) and s.code = k.code and s.code=mi.code(+)
) b
