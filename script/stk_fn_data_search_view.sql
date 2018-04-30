create or replace view stk_fn_data_search_view as

select a.*,
max(net_profit_one_quarter) over (order by a.code,a.fn_date desc rows between 0 following and 7 following) net_profit_max,
max(revenue_one_quarter) over (order by a.code,a.fn_date desc rows between 0 following and 7 following) revenue_max,
sum(net_profit_one_quarter) over (order by a.code,a.fn_date desc rows between 3 following and 3 following) last_net_profit_one_quarter  --for earning
 from(             
select fn.code,fn.fn_date,                          
case 
  when substr(fn_date, -4)='0331' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0630' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0930' then "109"+sum("109") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)-sum("109") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='1231' then "109"
end as ROE,
round("300"/100000000,2) as revenue, 

case 
  when substr(fn_date, -4)='0331' then round("300"/100000000,2)
  else round(("300" - sum("300") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following))/100000000,2)
end as revenue_one_quarter,


--round((decode(sum("300") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"300"/sum("300") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2) 
round("110", 2) as Revenue_growth_rate,

round("106",2) as Gross_Profit_Margin, 
round("126",2) as Sale_Profit_Margin,
--"4001" as Profit_Margin,
round("124",2) as Operating_Margin,          
round("303"/100000000,2) as Net_profit,

case 
  when substr(fn_date, -4)='0331' then round("303"/100000000,2)
  else round(("303" - sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following))/100000000,2)
end as net_profit_one_quarter,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
  when substr(fn_date, -4)='0630' then sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
end as net_profit_er_0930,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
  when substr(fn_date, -4)='1231' then "303"-sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
end as net_profit_er_0630,

case  
  when substr(fn_date, -4)='1231' then "303"-sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
end as net_profit_er_0331,
  
sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following) as last_net_profit,--for earning

sum(fn.fn_date) over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following) as last_net_profit_fn_date, --for earning


--case 
--  when "303"<0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) < 0 then -999  /*����*/
--  when "303">0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) < 0 then 999   /*Ť��*/
--  when "303"<0 and sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following) > 0 then -9999 /*ת��*/
--  else                         
--    round((decode(sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following),0,0,"303"/sum("303") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following))-1)*100,2)
--end 

round("111",2) as net_profit_Growth_rate,

case 
  when substr(fn_date, -4)='0331' then sum("303") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)
  when substr(fn_date, -4)='0630' then sum("303") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)
  when substr(fn_date, -4)='0930' then sum("303") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)
  when substr(fn_date, -4)='1231' then "303"
end as net_profit_last_year,

round("203"/100000000,2) as assets,                             
round("121",2) as debt_rate,
"206" as  Research_Development,
round(decode("300",0,0,"206"/"300")*100,2) as   Research_Rate,                        
"200" as "Ӧ���˿�",                           
"204" as "Ӧ���ʿ�",                           
"202" as "���",                           
--"2007" as "�ֽ��ֽ�ȼ���",                   
--"2008" as  "����Ͷ��",                          
--"2009" as  "����Ͷ��",                          
"400" as  "��Ӫ��������ֽ�",                
"401" as  "Ͷ�ʻ�������ֽ�",                
"402" as  "���ʻ�������ֽ�",                
"403" as  "�ֽ�������",                      
"118" as "�ٶ�����",                           
"117" as "��������",                      
--"101" as "EPS",
case 
  when substr(fn_date, -4)='0331' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 1 following and 1 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0630' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 2 following and 2 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='0930' then "101"+sum("101") over (order by fn.code,fn.fn_date desc rows between 3 following and 3 following)-sum("101") over (order by fn.code,fn.fn_date desc rows between 4 following and 4 following)
  when substr(fn_date, -4)='1231' then "101"
end as "EPS",                   
--"4005" as "ÿ������",                      
"102" as "ÿ�ɼ�ֵ",                      
--round(decode(EPS,0,0,k.close/EPS),2) as "PE", 
--round(decode("4005",0,0,k.close/"4005"),2) as "PS", 
--round(decode("102",0,0,k.close/"102"),2) as "PB", 
--"4018" as "PEG",
--k.close,
"104" as Capital_reserve_per_share,
"105" as Undistributed_profit_per_share,
"123"*100 as cash_net_profit_rate,

row_number() over(partition by fn.code order by fn.fn_date desc) rn                       
from (      
                                        
select * from (select type,fn_value,code,fn_date from stk_fn_data where fn_date >= to_char(sysdate-1000,'yyyyMMDD')
) pivot (sum(fn_value)                              
for type in (                                       
109, /*ROE(ttm)*/                                   
106, /*ë����         */                              
126, /*���۾�����*/
300, /*Ӫ���ܶ�     */   
110, /*��Ӫҵ������������(%)*/                            
303, /*������                   */ 
111, /* ������������(%)*/                 
203, /*�ʲ��ܶ�                */                    
121, /*ծ����                */                    
206, /*�з�����                 */                  
200, /*Ӧ���˿��ܼ�(����)*/                          
204, /*Ӧ���ʿ�                    */                
202, /*����ܶ�                 */                   
--2007, /*�ֽ��ֽ�ȼ���   */                         
--2008, /*����Ͷ��              */                     
--2009, /*����Ͷ��                */                   
400, /*��Ӫ��������ֽ�  */                       
401, /*Ͷ�ʻ�������ֽ� */                        
402, /*���ʻ�������ֽ�  */                       
403, /*�ֽ�������  */                             
118, /*�ٶ�����        */                           
117, /*��������(mrq) */                              
101, /*EPS(ttm)   */                                 
--4005, /*ÿ������(ttm)  */                             
--4018, /*PEG    */  
--4001, /*��������(ttm)*/ 
124, /*Ӫҵ������(ttm)*/                                   
102, /*�����ֵ(mrq)*/  
104, /*ÿ���ʱ�������(Ԫ)*/  
105, /*ÿ��δ��������(Ԫ)*/         
123 /*��Ӫ�ֽ������뾻����ı���(%)*/
))  
                                                
order by fn_date desc) fn   -- where code='000997'
) a where a.rn <= 10

