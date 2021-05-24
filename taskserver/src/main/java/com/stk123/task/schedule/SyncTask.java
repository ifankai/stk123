package com.stk123.task.schedule;

import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import com.stk123.common.util.ScpUtils;
import com.stk123.task.tool.TaskUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SyncTask extends AbstractTask {

    private String host = "81.68.255.181";
    private int port = 22;
    private String username = "root";
    private String password = "M5Pbg;Ln2W[pe6";
    private String localDir = "D:/share/workspace/stk123/oracle/";
    private String remoteDir = "/root/oracle/";

    @Setter
    private String table = "stk_";

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
                    sync(tableName, whereClause);
                }
            });
        } catch (Exception e) {
            log.error("XueqiuStockArticleTask", e);
        }
    }

    public void sync(String tableName, String whereClause){
        String command = getExpCommand(tableName, whereClause);
        log.info(command);
        TaskUtils.cmd(command);
        uploadFile(tableName);
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
            String output = JschUtil.exec(session, command, Charset.forName("UTF-8"));
            log.info(output);
        }finally {
            JschUtil.close(session);
        }
    }

    public void uploadFile(String tableName) {
        ScpUtils scpclient = ScpUtils.getInstance(host, port, username, password);
        scpclient.putFile(localDir+ tableName+".dmp", remoteDir);
    }

    public String getExpCommand(String tableName, String whereClause){
        if(!StringUtils.containsIgnoreCase(whereClause, "where")){
            throw new RuntimeException("whereClause is not start with 'where'");
        }
        String command = "exp stk/stkpwd@XE tables=(%s) query=\\\"%s\\\" file="+localDir+"%s.dmp log="+localDir+"%s.log";
        return String.format(command, tableName, whereClause, tableName, tableName);
    }
    public String getImpCommand(String tableName){
        String command = "source /etc/profile;source ~/.bash_profile;source ~/.bashrc; ";
        command += ". oraenv; imp userid=stk/stkpwd@localhost:1539/xepdb1 file='"+remoteDir+"%s.dmp' log='"+remoteDir+"%s.log' full=y ignore=y CONSTRAINTS=y";
        return String.format(command, tableName, tableName);
    }


    public static void main(String[] args) {
        SyncTask task = new SyncTask();
        task.execute();
    }
}
