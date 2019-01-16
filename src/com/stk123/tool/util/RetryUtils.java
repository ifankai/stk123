package com.stk123.tool.util;

public class RetryUtils {

	public static void main(String[] args) throws Exception {
		RetryUtils.retry(3, new Retry(){
			@Override
			public void run() {
				System.out.println("www");
			}
		});
		
		RetryUtils.retryIfException(new Retry(){
			@Override
			public void run() throws Exception {
				System.out.println("wwwqqq");
				throw new Exception("dd");
			}
		});
	}
	
	public static void retry(int time, Retry retry) throws Exception{
		do{
			retry.run();
		}while(--time > 0);
	}
	
	public static void retryIfException(int time, long sleep, Retry retry) throws Exception{
		do{
			boolean throwException = false;
			try{
				retry.run();
			}catch(Exception e){
				throwException = true;
			}
			if(!throwException)break;
			Thread.sleep(sleep);
		}while(--time > 0);
	}
	
	public static void retryIfException(Retry retry) throws Exception{
		try{
			retry.run();
		}catch(Exception e){
			retry.run();
		}
	}

}


