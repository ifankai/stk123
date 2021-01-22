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
main_industry
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
                           and st.source in ('10jqka_thshy') order by st.name)
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
kchange as
(
select k.code,k.kline_date
,sum(close_change) over (order by k.code,k.kline_date desc rows between 9 following and 9 following) close_change_10
,sum(close_change) over (order by k.code,k.kline_date desc rows between 19 following and 19 following) close_change_20
,sum(close_change) over (order by k.code,k.kline_date desc rows between 29 following and 29 following) close_change_30
,sum(close_change) over (order by k.code,k.kline_date desc rows between 59 following and 59 following) close_change_60
,sum(close_change) over (order by k.code,k.kline_date desc rows between 119 following and 119 following) close_change_120
,avg(volumn) over (order by k.code,k.kline_date desc rows between 0 following and 4 following) volume_avg_5
from stk_kline k where kline_date>=to_char(sysdate-500,'yyyyMMDD')
),
kline
as
(
select k.*
,round((k.close_change/kc.close_change_10-1)*100,2) close_change_10
,round((k.close_change/kc.close_change_20-1)*100,2) close_change_20
,round((k.close_change/kc.close_change_30-1)*100,2) close_change_30
,round((k.close_change/kc.close_change_60-1)*100,2) close_change_60
,round((k.close_change/kc.close_change_120-1)*100,2) close_change_120
,round(kc.volume_avg_5/k.volumn,2) volume_avg_5  --5日均量是今天量能多少倍，体现出缩量了多少
from stk_kline k, kchange kc,
(select code,max(kline_date) kdate from stk_kline where kline_date>=to_char(sysdate-500,'yyyyMMDD') group by code) mk
where k.code=mk.code and k.kline_date=mk.kdate and k.code=kc.code(+) and k.kline_date=kc.kline_date(+)
)     
select 

s.code,s.name,s.market,s.f9,s.hot,i.name industry,mai.name main_industry, mi.name my_industry, fd.fn_date /*财务季度*/,a.roe /*ttm*/, 
k.close_change_10,k.close_change_20,k.close_change_30,k.close_change_60,k.close_change_120,
k.volume_avg_5,
k.pe_ttm pe, k.pb_ttm pb, k.PS_TTM ps, round((k.close*s.total_capital)/10000,2) market_cap, 
a.REVENUE_GROWTH_RATE, a.GROSS_PROFIT_MARGIN,a.Sale_Profit_Margin, a.NET_PROFIT_GROWTH_RATE, a.DEBT_RATE, a.RESEARCH_RATE, 
k.pe_ntile, k.pb_ntile, k.ps_ntile, (k.pe_ntile+k.pb_ntile+k.ps_ntile) ntile,
trunc(sysdate-to_date(listing_date,'yyyymmdd')) Listing_days,
a.NET_PROFIT_ER_0331, a.NET_PROFIT_ER_0630, a.NET_PROFIT_ER_0930, 
a.last_net_profit, cast( a.last_net_profit_fn_date as varchar2(10)) last_net_profit_fn_date, a.net_profit_max,
(case when a.NET_PROFIT_ONE_QUARTER>=a.net_profit_max then 1 else 0 end) net_profit_max_flag,
(case when a.REVENUE_ONE_QUARTER>=a.revenue_max then 1 else 0 end) revenue_max_flag,
a.last_net_profit_one_quarter,a.NET_PROFIT_LAST_YEAR,a.Capital_reserve_per_share,a.Undistributed_profit_per_share,
 a.CASH_NET_PROFIT_RATE

from stk_cn s,stk_fn_data_search_view a, fnDate fd, industry i, main_industry mai, kline k, myIndustry mi
where s.code = a.code and a.code=fd.code and a.fn_date=fd.fn_date 
and s.code = i.code(+) and s.code = mai.code(+) and s.code = k.code and s.code=mi.code(+)
) b
