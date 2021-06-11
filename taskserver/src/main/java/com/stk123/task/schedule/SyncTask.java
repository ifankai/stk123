package com.stk123.task.schedule;

import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import com.stk123.common.util.ScpUtils;
import com.stk123.task.tool.TaskUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@PropertySource("classpath:password.properties")
public class SyncTask extends AbstractTask {

    private String host = "81.68.255.181";
    private int port = 22;
    private String username = "root";
    @Value("${server.password}")
    private String password;
    private String localDir = "D:/share/workspace/stk123/oracle/";
    private String remoteDir = "/var/stk/oracle/";

    @Setter
    private String table;

    private static Map<String, String> SYNC_TABLES = new HashMap<>();
    static {
        SYNC_TABLES.put("stk_text", "where insert_time>sysdate-1");
        SYNC_TABLES.put("stk_task_log", "where insert_time>sysdate-1");
    }

    @Override
    public void register() {
        this.run(this::execute);
    }

    public void execute() {
        try {
            SYNC_TABLES.entrySet().stream().forEach(e -> {
                String tableName = e.getKey();
                if(StringUtils.containsIgnoreCase(tableName, table)) {
                    String whereClause = e.getValue();
                    syncTable(tableName, whereClause);
                }
            });
            if(table == null){
                syncDatabase();
            }
        } catch (Exception e) {
            log.error("XueqiuStockArticleTask", e);
        }
    }

    public void syncDatabase(){
        String dpFile = "expdp_stk.dp";
        String expdp = "expdp stk/stkpwd@XE directory=DPUMP_DIR dumpfile="+dpFile+" SCHEMAS=stk PARALLEL=4";
        log.info(expdp);
        TaskUtils.cmd(expdp);

        uploadFile(dpFile);

        StringBuilder impdp = new StringBuilder();
        impdp.append("sqlplus system/password1@localhost:1539/xepdb1  <<ENDOFSQL \\n");
        impdp.append("alter session set container=XEPDB1; \\n");
        impdp.append("DROP USER stk CASCADE; \\n");
        impdp.append("create user stk identified by stkpwd default tablespace stk_tablespace_1 temporary tablespace stk_tablespace_temp; \\n");
        impdp.append("grant connect,resource,dba to stk; \\n");
        impdp.append("CREATE OR REPLACE DIRECTORY DPUMP_DIR AS '/var/stk/oracle'; \\n");
        impdp.append("grant read,write on directory DPUMP_DIR to public; \\n");
        impdp.append("exit; \\n");
        impdp.append("ENDOFSQL \\n");
        impdp.append("impdp stk/stkpwd@localhost:1539/xepdb1 directory=DPUMP_DIR dumpfile="+dpFile+" SCHEMAS=stk logfile="+dpFile+".log PARALLEL=2");
        log.info(impdp);
        ssh(impdp.toString());
    }

    public void syncTable(String tableName, String whereClause){
        String command = getExpCommand(tableName, whereClause);
        log.info(command);
        TaskUtils.cmd(command);
        uploadFile(tableName+".dmp");
        command = getImpCommand(tableName);
        log.info(command);
        ssh(command);
        //TODO
        //ssh("curl localhost:8088/elasticsearch/tablename");
    }

    public void ssh(String command){
        Session session = null;
        try {
            session = JschUtil.getSession(host, port, username, password);
            String cmd = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; . oraenv; " + command;
            String output = JschUtil.exec(session, cmd, Charset.forName("UTF-8"));
            log.info(output);
        }finally {
            JschUtil.close(session);
        }
    }

    public void uploadFile(String tableName) {
        ScpUtils scpclient = ScpUtils.getInstance(host, port, username, password);
        scpclient.putFile(localDir+ tableName, remoteDir);
    }

    public String getExpCommand(String tableName, String whereClause){
        if(!StringUtils.containsIgnoreCase(whereClause, "where")){
            throw new RuntimeException("whereClause is not start with 'where'");
        }
        String command = "exp stk/stkpwd@XE tables=(%s) query=\\\"%s\\\" file="+localDir+"%s.dmp log="+localDir+"%s.log";
        return String.format(command, tableName, whereClause, tableName, tableName);
    }
    public String getImpCommand(String tableName){
        String command = "imp userid=stk/stkpwd@localhost:1539/xepdb1 file='"+remoteDir+"%s.dmp' log='"+remoteDir+"%s.log' full=y ignore=y CONSTRAINTS=y";
        return String.format(command, tableName, tableName);
    }


    public static void main(String[] args) {
        SyncTask task = new SyncTask();
        task.execute();
    }
}
