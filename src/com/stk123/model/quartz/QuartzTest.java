package com.stk123.model.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.stk123.model.quartz.job.TestJob;

public class QuartzTest {
	public static void main(String[] args) {  
        // TODO Auto-generated method stub  
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");  
        Date d = new Date();  
        String returnstr = DateFormat.format(d);          
          
        TestJob job = new TestJob();  
        String job_name ="11";  
        try {  
            System.out.println(returnstr+ "��ϵͳ������");  
            QuartzManager.addJob(job_name,job,"0/2 * * * * ?"); //ÿ2����ִ��һ��  

//            Thread.sleep(10000);  
//            System.out.println("���޸�ʱ�䡿");  
//            QuartzManager.modifyJobTime(job_name,"0/10 * * * * ?");  
//            Thread.sleep(20000);  
//            System.out.println("���Ƴ���ʱ��");  
//            QuartzManager.removeJob(job_name);  
//            Thread.sleep(10000);  
//              
//            System.out.println("/n����Ӷ�ʱ����");  
//            QuartzManager.addJob(job_name,job,"0/5 * * * * ?");  
              
        }  catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
