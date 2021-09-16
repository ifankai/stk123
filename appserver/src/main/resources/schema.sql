update stk_fn_type set status=0 where market=1 and disp_order = 1000;
update STK_FN_TYPE set DISP_NAME='扣非每股收益(元)' where MARKET=1 and type=125;

update stk_text set type=5 where sub_type in (100,110);

alter table stk_dictionary modify remark varchar2(4000);

insert into stk_dictionary select 500,'xueqiu',null,null,null,null,null,null,null from stk_dictionary 
where not exists (select * from stk_dictionary where type=500 and key='xueqiu');
insert into stk_dictionary select 500,'iwencai',null,null,null,null,null,null,null from stk_dictionary 
where not exists (select * from stk_dictionary where type=500 and key='iwencai');
