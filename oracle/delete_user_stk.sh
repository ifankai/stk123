sqlplus system/password1@localhost:1539/xepdb1  <<ENDOFSQL 
    DECLARE
      lc_username   VARCHAR2 (32) := 'STK';
    BEGIN
      FOR ln_cur IN (SELECT sid, serial# FROM v\$session WHERE username = lc_username)
      LOOP
        EXECUTE IMMEDIATE ('ALTER SYSTEM KILL SESSION ''' || ln_cur.sid || ',' || ln_cur.serial# || ''' IMMEDIATE');
      END LOOP;
    END;
    /
    alter session set container=XEPDB1;
    DROP USER stk CASCADE; 
    create user stk identified by stkpwd default tablespace stk_tablespace_1 temporary tablespace stk_tablespace_temp; 
    grant connect,resource,dba to stk; 
    CREATE OR REPLACE DIRECTORY DPUMP_DIR AS '/var/stk/oracle'; 
    grant read,write on directory DPUMP_DIR to public; 
    exit; 
ENDOFSQL

