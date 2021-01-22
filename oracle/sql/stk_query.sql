select * from stk_fn_data_us_search_view where code='JMEI';
select * from stk_us_search_view where code in ('JMEI','XNET');
select * from stk_us_search_view where market_cap_cash_rate<1 and market_cap_cash_rate>0;
select * from stk_us_search_view where market_cap_work_capital_rate < 0.7 and market_cap_work_capital_rate>0;



select * from stk_fn_data_search_view where code='300373';
select * from stk_industry_search_view where code='300373';
select * from stk_earning_search_view where code='300373';
select * from stk_search_mview where code='600499';


select * from stk_cn;
select * from stk_search_condition;

select * from stk_search_mview v,stk_cn s,stk_kline k where v.code=s.code and s.code=k.code
and k.kline_date='20170922' and s.TOTAL_CAPITAL<20000 and k.close>=20
and s.LISTING_DATE>to_char(add_months(sysdate,-36),'yyyymmdd') and v.capital_reserve_per_share>=3 
and v.undistributed_profit_per_share>=2;

select * from stk_search_mview where 1=1 
and (net_profit_growth_rate>=30 or revenue_growth_rate>=30) and code in (select code from stk_investigation where investigator_count>=100 and insert_date>=add_months(sysdate,-6))

select * from stk_kline where code='300373' order by kline_date desc;
select * from stk_earning_search_view where code='300083';

select * from stk_us where code='BZUN';
select code,name from stk where market=2 and cate=2 order by code;
select * from stk_kline where code='399300' order by kline_date desc;
select * from stk_industry_type st, stk_industry si where si.industry=st.id and st.source='sina_meigu'

select * from stk where market=1 and cate=2;
select * from stk_cn where trunc(sysdate-to_date(listing_date,'yyyymmdd'))>360 order by hot asc;
select distinct source from stk_industry_type;
 
select code,name from stk_us where market=2 order by code;
select code,name from stk_us where market=2 and code>='AAN' order by code;
select to_char('续亏') from dual;
select * from stk_kline_us where code='AAOI' order by kline_date desc;
select * from stk_fn_type where market=1 order by disp_order;
select * from stk_fn_data_us where code='IRBT' and type=4005 order by fn_date desc;

select * from stk_search_view v where 1=1  and v.code in ('300408','002025','002594','002217','002179','000413','002635'
,'002384','300024','002475','300115','300433','000823','300136','300083','002456','002426','002055','300256','603328');

select * from stk_search_view v where 1=1  and v.code in ('002025');
select * from stk_fn_data_search_view v where 1=1  and v.code in ('002025');

select CAST(15.2 as decimal(18,2)) from dual;

select * from stk_text where insert_time>=sysdate-700;

select * from stk_fn_type where market=2;
select * from stk_fn_type where market=1 order by disp_order;
select * from stk_fn_data where code='300373' and type=123 order by fn_date desc;  
     
select k.*,sum(close) over (order by k.code,k.kline_date desc rows between 10 following and 10 following) 
from stk_kline k where code='300373';

with kchange as
(
select k.code,k.kline_date,sum(close) over (order by k.code,k.kline_date desc rows between 10 following and 10 following) close_change_10
from stk_kline k where kline_date>=to_char(sysdate-500,'yyyyMMDD')
)

select k.*, kc.close_change_10
from stk_kline k, kchange kc,
(select code,max(kline_date) kdate from stk_kline where kline_date>=to_char(sysdate-500,'yyyyMMDD') group by code) mk
where k.code=mk.code and k.kline_date=mk.kdate and k.code=kc.code(+) and k.kline_date=kc.kline_date(+) and k.code='300373';

with firstrow as 
(select * from (select sk.*,rownum num from stk_kline sk where sk.code='300373' and sk.kline_date between '20170601' and '20170703' order by kline_date asc) a where a.num=1)
select k.kline_date time, 
(avg(k.close_change) over (order by k.kline_date desc rows between 0 following and 4 following)/f.close_change-1)*100 close, 
(avg(k.volumn) over (order by k.kline_date desc rows between 0 following and 4 following)/f.volumn-1)*10 volumn
,(case when k.close>k.open then 1 when k.close<k.open -1 else 0 end) change 
from stk_kline k, firstrow f 
where k.code=f.code and k.kline_date between '20170601' and '20170703' order by k.kline_date


select * from stk_data_ppi_type where name like '%船用油180%';
select * from stk_data_ppi where type_id=1380 and ppi_date between to_char(sysdate-350,'yyyymmdd') and to_char(sysdate,'yyyymmdd') order by ppi_date desc;
select max(value) from stk_data_ppi where type_id=1380 and ppi_date between to_char(sysdate-350,'yyyymmdd') and to_char(sysdate,'yyyymmdd')

select * from stk_index_node;

select * from stk_text where text like '%***+2%' order by insert_time desc;

select * from stk_industry_type where name like '%中%';
select * from stk_industry_type where source='10jqka_gn';

select * from stk_industry_type where id= 885757;
select * from stk_industry where industry=885757;
select * from stk_search_condition
select * from stk_kline_us where code='IRBT' order by kline_date desc;
select avg(pe_ttm) from stk_kline_us where kline_date='20170804' and code in (select code from stk_industry where industry=885739) and pe_ttm is not null and pe_ttm>3 and pe_ttm<200





select k.code,k.kline_date
,avg(volumn) over (order by k.code,k.kline_date desc rows between 0 following and 0 following) volume_avg_5
from stk_kline k where k.code='000001' and kline_date>=to_char(sysdate-500,'yyyyMMDD');

select * from stk_kline where code='000001' order by kline_date desc;


select * from stk_search_mview where code='600499'
union all
select code,name,market,f9,null as industry,null as my_industry,null as fn_date,null as roe,null as close_change_10,null as close_change_20,null as close_change_30,null as close_change_60,null as close_change_120,null as volume_avg_5,null as pe,null as pb,null as ps,null as market_cap,null as revenue_growth_rate,null as gross_profit_margin,null as net_profit_growth_rate,null as debt_rate,null as research_rate,null as pe_ntile,null as pb_ntile,null as ps_ntile,null as ntile,null as listing_days,null as net_profit_er_0331,null as net_profit_er_0630,null as net_profit_er_0930,null as last_net_profit,null as last_net_profit_fn_date,null as net_profit_max,null as net_profit_max_flag,null as revenue_max_flag,null as last_net_profit_one_quarter,null as net_profit_last_year,null as cash_net_profit_rate,null as pe_yoy,null as er_date,null as er_low,null as er_high,null as last_amount,null as er_pe,null as er_net_profit_max_flag,null as peg,null as forecast_pe_this_year,null as forecast_pe_next_year from stk_hk;

select * from stk_hk;
select * from stk_kline_hk for update;
select * from stk_kline_us where code='IRBT' order by kline_date desc;

select * from stk_industry_type where source='10jqka_thshy';

select distinct source from stk_industry_type;

select * from stk_search_mview v where 1=1  and market=1 and listing_days>60 and close_change_20>=18 order by close_change_20 desc;
select * from stk_fn_data_us where type=2020;

select  * from stk_industry_type where id=885758 for update;
select * from stk_industry_type where name='租售同权'
select * from stk_text where sub_type=100;

select * from stk_industry where industry=1783;
select * from stk_dictionary where type=400 for update;

select * from stk_kline_hk where code='01458' order by kline_date desc;


select * from stk_ownership where org_id=238374;
select * from stk_organization where name like '中国汽车工业%';
