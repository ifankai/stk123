--SQL>conn system/system@XE as sysdba;

select * from tab;

--查看表空间路径
select tablespace_name,file_id,file_name from dba_data_files order by 1,2;

select tablespace_name,sum(bytes)/1024/1024 || 'M' from dba_free_space group by tablespace_name;
--查看表的数据大小
select segment_name, sum(bytes)/1024/1024 Mbytese from user_segments where segment_type='TABLE' group by segment_name order by Mbytese desc;

--查看表空间的利用率
SELECT D.TABLESPACE_NAME,
       SPACE || 'M' "SUM_SPACE(M)",
       BLOCKS "SUM_BLOCKS",
       SPACE - NVL(FREE_SPACE, 0) || 'M' "USED_SPACE(M)",
       ROUND((1 - NVL(FREE_SPACE, 0) / SPACE) * 100, 2) ||

       '%' "USED_RATE(%)",
       FREE_SPACE || 'M' "FREE_SPACE(M)"
  FROM (SELECT TABLESPACE_NAME,
               ROUND(SUM(BYTES) / (1024 * 1024), 2) SPACE,
               SUM(BLOCKS) BLOCKS
          FROM DBA_DATA_FILES
         GROUP BY TABLESPACE_NAME) D,
       (SELECT TABLESPACE_NAME,
               ROUND(SUM(BYTES) / (1024 * 1024), 2)

               FREE_SPACE
          FROM DBA_FREE_SPACE
         GROUP BY TABLESPACE_NAME) F
 WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+)
UNION ALL

--如果有临时表空间
SELECT D.TABLESPACE_NAME,
       SPACE || 'M' "SUM_SPACE(M)",
       BLOCKS SUM_BLOCKS,
       USED_SPACE || 'M' "USED_SPACE(M)",
       ROUND(NVL(USED_SPACE, 0) / SPACE * 100, 2) || '%'

       "USED_RATE(%)",
       NVL(FREE_SPACE, 0) || 'M' "FREE_SPACE(M)"
  FROM (SELECT TABLESPACE_NAME,
               ROUND(SUM(BYTES) / (1024 * 1024), 2) SPACE,
               SUM(BLOCKS) BLOCKS
          FROM DBA_TEMP_FILES
         GROUP BY TABLESPACE_NAME) D,
       (SELECT TABLESPACE_NAME,
               ROUND(SUM(BYTES_USED) / (1024 * 1024), 2)

               USED_SPACE,
               ROUND(SUM(BYTES_FREE) / (1024 * 1024), 2)

               FREE_SPACE
          FROM V$TEMP_SPACE_HEADER
         GROUP BY TABLESPACE_NAME) F
 WHERE D.TABLESPACE_NAME = F.TABLESPACE_NAME(+)
 ORDER BY 1;

--释放表空间
alter tablespace STK_TABLESPACE_TEMP shrink space;
alter database tempfile 'D:\oradata\stk_temp.dbf' resize 1024M;

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

/etc/init.d/oracle-xe-18c stop
/etc/init.d/oracle-xe-18c start

lsnrctl status

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

81.68.255.181:
SYS, SYSTEM and PDBADMIN
password1
/etc/init.d/oracle-xe-18c start
/etc/init.d/oracle-xe-18c stop

and set the oracle-xe-18c service to start on boot:
systemctl daemon-reload
systemctl enable oracle-xe-18c

--https://mikesmithers.wordpress.com/2019/01/03/installing-and-configuring-oracle-18cxe-on-centos/
echo $ORACLE_SID
echo $ORACLE_HOME
echo $ORACLE_BASE
echo $PATH

select sys_context('userenv', 'con_name') from dual;
select con_id, dbid, guid, name , open_mode from v$pdbs;
alter session set container=XEPDB1;

C:\Users\KaiFan>lsnrctl status

C:\Users\KaiFan>sqlplus system/password1 as sysdba
SQL> alter session set container=XEPDB1;
impdp stk/stkpwd@localhost:1521/XEPDB1 directory=DPUMP_DIR dumpfile=DB_STK.DP SCHEMAS=stk logfile=DB_STK.DP.log table_exists_action=replace

--create stk table space
drop tablespace stk_tablespace_2;
create tablespace stk_tablespace_1 datafile 'D:\tech\oradata\stk_data_1.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
create tablespace stk_tablespace_2 datafile 'E:\tech\oradata\stk_data_2.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
create temporary tablespace stk_tablespace_temp tempfile 'D:\tech\oradata\stk_temp.dbf' size 1024M autoextend on next 200M maxsize 10240m extent management local;
create user stk identified by stkpwd default tablespace stk_tablespace_1 temporary tablespace stk_tablespace_temp;
grant connect,resource,dba to stk;


select file_name,tablespace_name,status from dba_data_files;
select tablespace_name,file_name,bytes/1024/1024 file_size,autoextensible,status from dba_temp_files;
select group#,sequence#,bytes/1024/1024 sizeMB,members,status from v$log;
select group#,status,type,member from v$logfile;

--centos:
. oraenv
sqlplus system/password1
sqlplus stk/stkpwd@localhost:1539/xepdb1

create tablespace stk_tablespace_1 datafile '/opt/oracle/oradata/XE/stk_data_1.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
create tablespace stk_tablespace_2 datafile '/opt/oracle/oradata/XE/stk_data_2.dbf' size 2048M autoextend on next 200M maxsize 10240M extent management local;
create temporary tablespace stk_tablespace_temp tempfile '/opt/oracle/oradata/XE/stk_temp.dbf' size 1024M autoextend on next 200M maxsize 10240m extent management local;

--drop user and then create user
sqlplus system/password1@localhost:1539/xepdb1  <<ENDOFSQL
alter session set container=XEPDB1;
--Select username from ALL_USERS;
DROP USER stk CASCADE;
create user stk identified by stkpwd default tablespace stk_tablespace_1 temporary tablespace stk_tablespace_temp;
grant connect,resource,dba to stk;
CREATE OR REPLACE DIRECTORY DPUMP_DIR AS '/var/stk/oracle';
grant read,write on directory DPUMP_DIR to public;
  exit;
ENDOFSQL


CREATE OR REPLACE DIRECTORY DPUMP_DIR AS 'D:\IdeaProjects\stk123\oracle\';
grant read,write on directory DPUMP_DIR to public;
--EXCLUDE=TABLE:\"IN\(\'STK_ERROR_LOG\'\)\" 
expdp stk/stkpwd@XE directory=DPUMP_DIR dumpfile=db_stk.dp REUSE_DUMPFILES=Y SCHEMAS=stk QUERY=STK_ERROR_LOG:\"WHERE 1<>1\",STK_KLINE_US:\"WHERE kline_date>=\'20210101\'\",STK_KLINE:\"WHERE kline_date>=\'20210101\'\",STK_DATA_EASTMONEY_GUBA:\"WHERE 1<>1\",STK_FN_DATA_BAK:\"WHERE 1<>1\",STK_DATA_PPI:\"WHERE 1<>1\",STK_CAPITAL_FLOW:\"WHERE 1<>1\"
impdp stk/stkpwd@localhost:1539/xepdb1 directory=DPUMP_DIR dumpfile=DB_STK.DP SCHEMAS=stk logfile=DB_STK.DP.log table_exists_action=replace

select * from dba_directories;

--elasticsearch-7.10.2:
# please execute the following statements to configure elasticsearch service to start automatically using systemd
 sudo systemctl daemon-reload
 sudo systemctl enable elasticsearch.service
# You can start elasticsearch service by executing
 sudo systemctl start elasticsearch.service
 sudo systemctl stop elasticsearch.service
--log
/var/log/elasticsearch

--测试elasticsearch是否正常：
http://localhost:9200

D:\apps\elasticsearch-7.10.1\bin\elasticsearch-service.bat 后面还可以执行这些命令
install: 安装Elasticsearch服务
remove: 删除已安装的Elasticsearch服务（如果启动则停止服务）
start: 启动Elasticsearch服务（如果已安装）
stop: 停止服务（如果启动）
manager:启动GUI来管理已安装的服务

grep "Out of memory" /var/log/messages

--查看liunx内存
top
free -m

ORA-28040: No matching authentication protocol
https://logic.edchen.org/how-to-resolve-ora-28040-no-matching-authentication-protocol/

java.sql.SQLException: ORA-01017: invalid username/password; logon denied
Remove all ojdbc6.jar from war/jar

--nginx
ps -ef | grep nginx
netstat -anp | grep :80

systemctl enable nginx # 设置开机启动
service nginx start # 启动 nginx 服务
service nginx stop # 停止 nginx 服务
service nginx restart # 重启 nginx 服务
service nginx reload # 重新加载配置，一般是在修改过 nginx 配置文件时使用。

/etc/nginx/nginx.conf
/var/log/nginx/error.log
