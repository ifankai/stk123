--system/admin@XE
--stkdaily.com/dailystk.com/ifankai.com/daydaystk.com/stkcart.com
select * from tab;

select tablespace_name,sum(bytes)/1024/1024 || 'M' from dba_free_space group by tablespace_name;
--查看表的数据大小
select segment_name, sum(bytes)/1024/1024 Mbytese from user_segments where segment_type='TABLE' group by segment_name order by Mbytese desc;

--查看当前有哪些用户正在使用数据
SELECT osuser, a.username,cpu_time/executions/1000000||'s', sql_fulltext,machine 
from v$session a, v$sqlarea b
where a.sql_address =b.address order by cpu_time/executions desc;

select count(*) from v$process; --当前的连接数
select value from v$parameter where name = 'processes'; --数据库允许的最大连接数

--修改最大连接数:
alter system set processes = 300 scope = spfile;

--重启数据库:
shutdown immediate;
startup;


select * from nls_database_parameters where parameter='NLS_CHARACTERSET';


/*
drop table STK_BILLBOARD;
drop table STK_DEPT_TYPE;
drop table STK_EARNINGS_FORECAST;
drop table STK_ERROR_LOG;
drop table STK_FN_DATA;
drop table STK_FN_TYPE;
drop table STK_HOLDER;
drop table STK_IMPORT_INFO;
drop table STK_IMPORT_INFO_TYPE;
drop table STK_INDUSTRY;
drop table STK_INDUSTRY_TYPE;
drop table STK_INFO_LOG;
drop table STK_INTERNET_SEARCH;
drop table STK_KLINE;
drop table STK_PE;
 */
--alter database datafile 'C:\oraclexe\app\oracle\oradata\XE\SYSTEM.DBF' resize 5000M;

select * from user_constraints where table_name like 'STK%' and constraint_name like 'FK%';

select * from user_segments;

--create stk table space
create tablespace stk_tablespace_1 datafile 'D:\oradata\stk_data_1.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
create temporary tablespace stk_tablespace_temp tempfile 'D:\oradata\stk_temp.dbf' size 1024M autoextend on next 200M maxsize 10240m extent management local;
create user stk identified by stkpwd default tablespace stk_tablespace_1 temporary tablespace stk_tablespace_temp;
grant connect,resource,dba to stk;
create tablespace stk_tablespace_2 datafile 'E:\oradata\stk_data_2.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
