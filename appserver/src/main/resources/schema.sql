update stk_keyword set insert_time=sysdate-1000 where INSERT_TIME is null;

delete from STK_KEYWORD_LINK where KEYWORD_ID in (select id from STK_KEYWORD where name is null);
delete from STK_KEYWORD where name is null;

create table stk_dictionary_header(
  type number(8),
  name varchar2(400),
  status number(2),
  insert_time date,
  update_time date
);
alter table stk_dictionary_header add constraint pk_dictionary_header_type primary key (type);
create sequence s_dict_type INCREMENT BY 1 START WITH 5000 NOMAXVALUE NOCYCLE CACHE 10;

update STK_DICTIONARY set type=5 where type=4 and key='7146274836';
insert into stk_dictionary_header select 1,'k线价格类型', 0, sysdate, null from dual;
insert into stk_dictionary_header select 2,'算术符号', 0, sysdate, null from dual;
insert into stk_dictionary_header select 3,'雪球用户', 0, sysdate, null from dual;
insert into stk_dictionary_header select 5,'雪球主贴', 0, sysdate, null from dual;
insert into stk_dictionary_header select 10,'internet search type', 0, sysdate, null from dual;
insert into stk_dictionary_header select 20,'牛散', 0, sysdate, null from dual;
insert into stk_dictionary_header select 21,'牛基', 0, sysdate, null from dual;
insert into stk_dictionary_header select 200,'指标', 0, sysdate, null from dual;
insert into stk_dictionary_header select 300,'行业分类来源', 0, sysdate, null from dual;
insert into stk_dictionary_header select 400,'文档子类型', 0, sysdate, null from dual;
insert into stk_dictionary_header select 500,'Cookie信息', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1000,'多股同列下显示的列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1001,'自选股列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1002,'非公开发行、员工持股监控列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1003,'盈利预测列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1005,'业绩预告列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1006,'雪球帖子列名', 0, sysdate, null from dual;
insert into stk_dictionary_header select 1200,'关键字', 0, sysdate, null from dual;
insert into stk_dictionary_header select 2000,'stk_news.type', 0, sysdate, null from dual;

select * from stk_dictionary_header order by type asc;

alter table STK_DICTIONARY
    add constraint fk_dictionary_type foreign key (type)
        references STK_DICTIONARY_HEADER (type);


