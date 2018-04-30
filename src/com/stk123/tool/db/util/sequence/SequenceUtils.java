package com.stk123.tool.db.util.sequence;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/***
DROP TABLE IF EXISTS stk_sequence; 
CREATE TABLE stk_sequence ( 
  seq_name varchar(24) NOT NULL COMMENT 'Sequence name', 
  seq_value bigint(20) DEFAULT '10000' COMMENT 'Sequence value', 
  PRIMARY KEY (seq_name) 
);
insert into stk_sequence values('s_keyword_id',20000000);
insert into stk_sequence values('s_keyword_link_id',20000000);
insert into stk_sequence values('s_label_id',200000);
insert into stk_sequence values('s_label_text_id',20000000);
insert into stk_sequence values('s_text_id',20000000);
insert into stk_sequence values('s_data_ppi_type_id',20000);
insert into stk_sequence values('s_organization_id',200000);
insert into stk_sequence values('s_organization_type_id',2000);
insert into stk_sequence values('s_sync_id',200000);
insert into stk_sequence values('s_sync_task_id',2000);
insert into stk_sequence values('s_user_id',200000);
 */

public class SequenceUtils {
	
	public final static String SEQ_KEYWORD_ID = "s_keyword_id"; //start from 20000000
	public final static String SEQ_KEYWORD_LINK_ID = "s_keyword_link_id"; //start from 20000000
	public final static String SEQ_LABEL_ID = "s_label_id";//start from 200000
	public final static String SEQ_LABEL_TEXT_ID = "s_label_text_id"; //start from 20000000
	public final static String SEQ_TEXT_ID = "s_text_id"; //start from 20000000
	public final static String SEQ_SYNC_ID = "s_sync_id";
	public final static String SEQ_SYNC_TASK_ID = "s_sync_task_id";
	public final static String SEQ_USER_ID = "s_user_id";
	
	private static Map<String, SeqInfo> keyMap = new HashMap<String, SeqInfo>(20); // Sequence
	private static final int POOL_SIZE = 20; // Sequence 

	private SequenceUtils() {
	}

	public static synchronized long getSequenceNextValue(String seqName) {
		SeqInfo keyInfo = null;
		Long keyObject = null;
		try {
			if (keyMap.containsKey(seqName)) {
				keyInfo = keyMap.get(seqName);
			} else {
				keyInfo = new SeqInfo(seqName, POOL_SIZE);
				keyMap.put(seqName, keyInfo);
			}
			keyObject = keyInfo.getNextKey();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return keyObject;
	}
	
	
	public static void main(String args[]) { 
        System.out.println("----------test()----------"); 
        for (int i = 0; i < 30; i++) { 
            long x = SequenceUtils.getSequenceNextValue("s_test"); 
            System.out.println(x); 
        } 
    } 
}
