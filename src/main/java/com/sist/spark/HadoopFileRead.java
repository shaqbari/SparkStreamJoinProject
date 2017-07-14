package com.sist.spark;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopFileRead {
	public static void fileRead(String type) {
		TwitterDAO dao=new TwitterDAO(type);
		
		try {
			Configuration conf=new Configuration();
			conf.set("fs.default.name", "hdfs://NameNode:9000");
			FileSystem fs=FileSystem.get(conf);
			
			String def_path="/user/spark_ns1";
			FileStatus[] status=fs.listStatus(new Path(def_path));
			for (FileStatus s : status) {
				//sssssSystem.out.println(s.getPath().getName());
				String dir=s.getPath().getName();
				//오전에 작업했던 폴더
				if (dir.equals("data")) {
					continue;
				}
				
				String dirType=dir.substring(0, dir.lastIndexOf("-"));
				
				if (dirType.equals(type)) {
					FileStatus[] ss=fs.listStatus(new Path(def_path+"/"+s.getPath().getName()));
					for (FileStatus sss : ss) {
						if (!sss.getPath().getName().equals("_SUCCESS")) {//이파일을 제외하고
							//System.out.println(sss.getPath().getName());
							
							
							FSDataInputStream is=fs.open(new Path(def_path+"/"+s.getPath().getName()+"/"+sss.getPath().getName()));
							//BufferedReader br=new BufferedReader(new InputStreamReader(is, "UTF-8"));//한글 안깨지게 filter
							BufferedReader br=new BufferedReader(new InputStreamReader(is));//한글 안깨지게 filter
							//String data="";
							while (true) {
								String line=br.readLine();
								if (line==null) {
									break;
								}
								System.out.println(line);
								StringTokenizer st=new StringTokenizer(line);
								//while(st.hasmoreTokens()){}갯수모를때
								TwitterVO vo=new TwitterVO();
								vo.setRankdata(st.nextToken());
								vo.setCount(Integer.parseInt(st.nextToken().trim()));
								dao.naverRankInsert(vo);
								
								//data+=line+"\n";
							}
							//System.out.println(data);
							br.close();
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
