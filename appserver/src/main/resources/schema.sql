drop table stk_report_detail;
create table stk_report_detail(
                                  id number(6),
                                  header_id number(8),
                                  strategy_date varchar2(10),
                                  strategy_code varchar2(20),
                                  strategy_output varchar2(2000),
                                  code varchar2(10),
                                  rps_code  varchar2(100), --rps_01;rps_03
                                  rps_percentile  varchar2(100), --98.03;91.42
                                  rps_bk_code  varchar2(100), --BK0695;BK0696
                                  rps_stock_code  varchar2(1000), --600468,600622,000737,601069,600362,688456,601020,000975,002009,603399,600367,000603,002297,300340,300409
                                  text varchar2(2000)
);
alter table stk_report_detail add constraint pk_report_detail_id primary key (id);
alter table stk_report_detail
    add constraint fk_report_detail__header_id foreign key (header_id)
        references stk_report_header (id);