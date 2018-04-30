package com.stk123.model.quartz.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stk123.model.Index;
import com.stk123.model.IndexUtils;
import com.stk123.model.K;
import com.stk123.model.mock.Score;
import com.stk123.model.mock.Trade;
import com.stk123.tool.db.util.DBUtil;
import com.stk123.tool.util.EmailUtils;

public class StockRealTimeJob implements Job {
	
	/**TODO
	 * [+3]MACD�ײ�����, [-1]60,30,20,10�վ��߿�ͷ����]������һ���Ǽ�1�֣�
	 * K�߲��Ƽӷ�
	 * �ʽ�����ӷ�
	 * ������Դ���������ָ����AR��
����ָ�����Ե��쿪�м�Ϊ���������Ե����м۷ֱ�Ƚϵ�����ߡ���ͼۣ�ͨ��һ��ʱ���ڿ��м��ڹɼ��еĵ�λ����ӳ�г���������������㹫ʽ���£�
AR=N���ڣ�������߼ۡ����տ��мۣ�֮�� / N���ڣ����տ��мۡ�������ͼۣ�֮��
NΪ��ʽ�е��趨������һ���趨Ϊ26�ա�
ʹ�÷���
��1��ARֵ��100Ϊ���ĵش������20֮�䣬��ARֵ��80��120֮�䲨��ʱ�������������飬�ɼ����ƱȽ�ƽ�ȣ�������־��Ҳ�����
��2��ARֵ�߸�ʱ��ʾ�����Ծ��������ʢ���������ʾ�ɼ۽���߼ۣ�Ӧѡ��ʱ���˳���ARֵ�ĸ߶�û�о����׼��һ������£�ARֵ������150����ʱ���ɼ���ʱ���ܻص��µ���
��3��ARֵ�ߵ�ʱ��ʾ����˥�ˣ���Ҫ��ʵ��������ʾ�ɼۿ��ܵ���͹ȣ��ɿ����Ż����룬һ��ARֵ����70����ʱ���ɼ��п�����ʱ����������
��4����AR���߿��Կ���һ��ʱ�ڵ��������ƣ����������ڹɼ۵��������ȵ׵Ĺ��ܣ���ͼʱ��Ҫƾ�辭�飬�Լ�����������ָ�����ʹ�ã�Ҳ���ҽ�Ҫʵ�ֵģ�
�ز�α��
1.���ù�Ʊ�أ���׼�Լ�������ʼ������
2.����AR������returnAR
3.��handle_data�н��в�����AR>150������AR<80����
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			Index idx =  new Index(conn,"603939","");
			//System.out.println(idx.getCode());
			idx.getKsRealTimeOnHalfHour();
			K k = idx.getK();
			
			while(true){
				Trade trade = new Trade(idx, k);
				Score score = trade.getBuyScore();
				if(score.points > 0){
					System.out.println("date="+k.getDate()+"- "+score);
				}
				k = k.before(1);
				if(k.getDate().equals(idx.getKFirstOfDate().getDate())){
					break;
				}
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		
	}
	
	//private static List<String> stks = null;
	
	private SimpleDateFormat DateFormat = new SimpleDateFormat("MM-dd HH:mm");  
	private Date d = new Date();  
	private String returnstr = DateFormat.format(d);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {  
		Connection conn = null;
		try{
			conn = DBUtil.getConnection();
			List<String> stks = IndexUtils.getCareStkFromXueQiu("��עC");
			//System.out.println("["+returnstr+"]"+stks);
			StringBuffer sb = new StringBuffer();
			int cnt = 0;
			for(String code : stks){
				int loc = code.startsWith("SH")?Index.SH:code.startsWith("SZ")?Index.SZ:0;
				Index index =  new Index(conn,code.substring(2),loc);
				index.getKsRealTimeOnHalfHour();
				K k = index.getK();
				
				Trade trade = new Trade(index,k);
				Score score = trade.getBuyScore();
				if(score.points > 0){
					String s = "["+returnstr+"]<br>stk="+(index.getStock()!=null?index.getName():"")+"["+code+"]<br>time="+k.getDate()+"<br>"+score;
					System.out.println(s);
					sb.append(s).append("<br><br>");
					cnt++;
				}
			}
			if(sb.length() > 0){
				EmailUtils.send(EmailUtils.IMPORTANT+"[����]K�߼��["+cnt+"]", JobUtils.getMoneyFlow()+"<br><br>"+sb.toString());
			}
		} catch (Exception e){
			throw new JobExecutionException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
    }
}


