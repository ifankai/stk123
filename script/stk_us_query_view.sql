create or replace view stk_us_query_view as
with todayk as                                       
 (select t.close,t.code,t.kline_date                      
 from (select s.*,ROW_NUMBER() over(PARTITION by code order by kline_date desc) as num                  
 from stk_kline_us s where code=p_view_param.get_param('code') ) t where t.num = 1)     

select fn.code,fn.fn_date, "4004" as "ROE","1001" as "Ӫ���ܶ�",
 round((decode(sum("1001") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1001"/sum("1001") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "Ӫ��������",     
 nvl("4002",round(decode("1001",0,0,"1002"/"1001")*100,2))||'%' as "ë����",            
 "1008" as "������",                           
 round((decode(sum("1008") over (order by fn_date desc rows between 4 following and 4 following),0,0,"1008"/sum("1008") over (order by fn_date desc rows between 4 following and 4 following))-1)*100,2)||'%' as "������������",            
 "2001" as "���ʲ�",                              
 round(decode("2001",0,0,"2002"/"2001")*100,2)||'%' as "��ծ��",   
 "2020" as "�����ʲ�",               
 "2020"-"2002" as "Ӫ���ʽ�",
 "1003" as       "�з�����",                           
 abs(round(decode("1001",0,0,"1003"/"1001")*100,2))||'%' as "�з�ռ��",            
 "2005" as "Ӧ���˿�",                            
 "2010" as "Ӧ���ʿ�",                          
 "2006" as "���",                            
 "2007" as "�ֽ��ֽ�ȼ���",
 "2008" as "����Ͷ��",                           
 "2009" as "����Ͷ��",                           
 "3001" as "��Ӫ��������ֽ�",                 
 "3002" as "Ͷ�ʻ�������ֽ�",                 
 "3003" as "���ʻ�������ֽ�",              
 "3004" as "�ֽ�������",                       
 "4014" as "�ٶ�����",                            
 "4012" as "��������",                       
 "4008" as "EPS",                            
 "4005" as "ÿ������",   
 "4013" as "ÿ�ɼ�ֵ",
 round(decode("4008",0,0,k.close/"4008"),2) as "PE",  
 round(decode("4005",0,0,k.close/"4005"),2) as "PS",  
 round(decode("4013",0,0,k.close/"4013"),2) as "PB",  
 "4018" as "PEG"               

from (                                               
 select * from (select type,fn_value,code,fn_date,fiscal_year_ends from stk_fn_data_us where code=p_view_param.get_param('code')
     ) pivot (sum(fn_value) for type in ( 
    4004, /*ROE(ttm)*/                                    
    1002, /*ë��          */                             
    4002, /*ë��          */                               
    1001, /*Ӫ���ܶ�     */                   
    1008, /*������                   */                   
    2001, /*�ʲ��ܶ�                */        
    2002, /*ծ���ܶ�                */       
    2020, /*�����ʲ�",                   */
    1003, /*�з�����                 */                   
    2005, /*Ӧ���˿��ܼ�(����)*/                           
    2010, /*Ӧ���ʿ�                    */                 
    2006, /*����ܶ�                 */                    
    2007, /*�ֽ��ֽ�ȼ���   */                          
    2008, /*����Ͷ��        */                      
    2009, /*����Ͷ��                */                    
    3001, /*��Ӫ��������ֽ�  */                    
    3002, /*Ͷ�ʻ�������ֽ� */                         
    3003, /*���ʻ�������ֽ�  */        
    3004, /*�ֽ�������  */                              
    4014, /*�ٶ�����        */                            
    4012, /*��������(mrq) */                               
    4008, /*EPS(ttm)   */                                  
    4005, /*ÿ������(ttm)  */                              
    4018, /*PEG    */                                      
    4013 /*�����ֵ(mrq)*/                             
  ))                                                   
order by fn_date desc) fn, todayk k where fn.code=k.code
