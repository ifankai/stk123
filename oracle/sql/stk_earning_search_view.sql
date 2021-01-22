create or replace view stk_earning_search_view as
with earning 
as(
select a.* from stk_earnings_notice a,
(select code,max(fn_date) fn_date from stk_earnings_notice group by code) b
where a.code=b.code and a.fn_date=b.fn_date order by a.fn_date desc
),
holder 
as(
select a.* from stk_holder a,
(select code,max(fn_date) fn_date from stk_holder group by code) b
where a.code=b.code and a.fn_date=b.fn_date order by a.fn_date desc
),
forecast
as(
select * from (select code,forecast_year,forecast_net_profit from stk_earnings_forecast) pivot (sum(forecast_net_profit)                              
for forecast_year in (2018 as this_year, 2019 as next_year))
)


select v.*,e.fn_date er_date,e.er_low,e.er_high,e.last_amount,h.holder,
market_cap/
nullif((case
  when v.fn_date<e.fn_date then /* 只有预期季度大于当前实际最大季度 */
    case
      when e.fn_date=to_char(add_months(to_date(v.last_net_profit_fn_date,'yyyymmdd'),12),'yyyymmdd') then /*预期季度正好是实际季度后一季，如果后二季就不用算了*/
        case 
          when substr(e.fn_date, -4)='0331' then (v.NET_PROFIT_ER_0331 + v.last_net_profit*(1+(e.er_low+e.er_high)/2/100))/100000000
          when substr(e.fn_date, -4)='0630' then (v.NET_PROFIT_ER_0630 + v.last_net_profit*(1+(e.er_low+e.er_high)/2/100))/100000000
          when substr(e.fn_date, -4)='0930' then (v.NET_PROFIT_ER_0930 + v.last_net_profit*(1+(e.er_low+e.er_high)/2/100))/100000000
          else v.last_net_profit*(1+(e.er_low+e.er_high)/2/100)/100000000
        end
      else
        case 
          when substr(e.fn_date, -4)='0331' then (v.NET_PROFIT_ER_0331/10000 + e.last_amount*(1+(e.er_low+e.er_high)/2/100))/10000
          when substr(e.fn_date, -4)='0630' then (v.NET_PROFIT_ER_0630/10000 + e.last_amount*(1+(e.er_low+e.er_high)/2/100))/10000
          when substr(e.fn_date, -4)='0930' then (v.NET_PROFIT_ER_0930/10000 + e.last_amount*(1+(e.er_low+e.er_high)/2/100))/10000
          else e.last_amount*(1+(e.er_low+e.er_high)/2/100)/10000
        end
    end    
  else null 
end),0) as er_pe,
case when (case
  when v.fn_date<e.fn_date then
    v.last_net_profit_one_quarter*(1+(e.er_low+e.er_high)/2/100) else null
end) >= v.net_profit_max then 1 else 0 end
as er_net_profit_max_flag,

round(case when v.PE >0 and (e.er_low+e.er_high)/2 > 0 then v.PE/(e.er_low+e.er_high)/2 else null end, 2) peg,
(case when f.this_year != 0 then market_cap/f.this_year else null end) as forecast_pe_this_year, 
(case when f.next_year != 0 then market_cap/f.next_year else null end) as forecast_pe_next_year

from stk_industry_search_view v, earning e, forecast f, holder h
where v.code=e.code(+) and v.code=f.code(+) and v.code=h.code(+)
--and e.code in ('300247') ; 
