package com.stk123.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class ExpData {
	
	private final static String output = "_output";

	public static void main(String[] args) throws Exception {
		File expdata = new File("./expdata");
		FilenameFilter ff = new FilenameFilter(){
			public boolean accept(File arg0, String arg1) {
				return arg1.indexOf(output) == -1;
			}};
		for(File file : expdata.listFiles(ff)){
			java.io.FileReader fr = new FileReader(file);
			java.io.BufferedReader br = new BufferedReader(fr);
			String str = null;
			List<List<String>> datas = new ArrayList<List<String>>(); 
			int i = 0;
			while((str = br.readLine()) != null){
				if(i++ == 0)continue;
				datas.add(new ArrayList(Arrays.asList(str.split("	"))));
			}
			List<String> lastData = datas.get(datas.size()-1);
			for(List<String> data:datas){
				double gap = 0.0;
				for(int j=1;j<data.size();j++){
					gap += Math.abs(Double.parseDouble(lastData.get(j))-Double.parseDouble(data.get(j)));
				}
				data.add(String.valueOf(gap));
			}
			Collections.sort(datas, new Comparator(){
				public int compare(Object arg0, Object arg1) {
					double d0 = Double.parseDouble(String.valueOf(((List)arg0).get(4)));
					double d1 = Double.parseDouble(String.valueOf(((List)arg1).get(4)));
					return (int)((d0-d1)*100);
				}
			});
			/*for(List<String> data:datas){
				System.out.println(data);
			}*/
			PrintWriter pw = new PrintWriter(
					new File(expdata.getAbsolutePath()+File.separator+
							StringUtils.replace(file.getName(), ".", output+".") ));
			IOUtils.writeLines(datas, "\n", pw);
		}
	}

}

