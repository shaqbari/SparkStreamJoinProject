package com.sist.sparkmy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import scala.Tuple2;
import twitter4j.Status;


/*
 * NameNode : Master
 * SecondaryNode : Backup
 * Data
 * 
 * SparkSQL
 * */
public class DaumRank {
	private JavaStreamingContext jsc; 
	
	public void daumNaverStart(String type) {
		try {
			Configuration hConf=new Configuration();
			hConf.set("fs.default.name", "hdfs://NameNode:9000");
			JobConf jc=new JobConf(hConf);
			
			SparkConf conf=new SparkConf().setAppName("DaumTwitter").setMaster("local[2]");
			//local[0] 0번 마스터 1번 sencondary데이터를 수집하는 녀석이 아니다. 2번부터 datanode로 데이터 수집하는 역할
			
			
			String[] filter={
					"z3oFviZHurO6w9PrkBJOKphHA",
					"247l86gk1EQgDSKWc4ud6bLFUTrnr3SHK3YGMBpnSzfBcPOvtq",
					"867997182044942336-lzbX9QjWRdsUdf4Zn8X7PzofyfyAguK",
					"tES2gZmfDRGQWnieKnPUIDqyeCJw9VdYi9S8061v768EI"
				};
				
				JavaStreamingContext jsc=new JavaStreamingContext(conf, new Duration(10000));
				
				String[] prop={
					"twitter4j.oauth.consumerKey",
					"twitter4j.oauth.consumerSecret",
					"twitter4j.oauth.accessToken",
					"twitter4j.oauth.accessTokenSecret"
						
				};			
				for (int a = 0; a < prop.length; a++) {
					System.setProperty(prop[a], filter[a]);
					
				}
				
				List<String> list=RankData.naverRank();
				String[] data=new String[list.size()];
				int i=0;
				for (String s : list) {
					data[i]=s;
					i++;
				}
				
				//외부에서 받는게 아래 클래스, 파일에서 받는게 JavaRDD
				JavaReceiverInputDStream<Status> twitterStream=TwitterUtils.createStream(jsc, data);
				
				
				/*데이터 수집 방법 
				 * Spark : 
				 * 1. File읽기 JavaRDD<String>
				 * 2. Stream JavaDSream<String>
				 * 	
				 * */
				JavaDStream<String> datas=twitterStream.map(new Function<Status, String>() {

					@Override
					public String call(Status status) throws Exception {
						
						return status.getText();
					}
				});
				
				final Pattern[] p=new Pattern[data.length];
				for (int a = 0; a < p.length; a++) {
					p[a]=Pattern.compile(data[a]);
				}
				
				//하둡
				final Matcher[] m=new Matcher[data.length];
				
				//단어분리
				JavaDStream<String> words=datas.flatMap(new FlatMapFunction<String, String>() {
					List<String> list=new ArrayList<String>();

					@Override
					public Iterable<String> call(String s) throws Exception {
						for (int a = 0; a < m.length; a++) {
							m[a]=p[a].matcher(s);
							while (m[a].find()) {
								list.add(m[a].group().replace(" ", ""));//나중에 stringtokenizer이용하려고
								//trim은 좌우의 공백만 제거
								
							}
						}
						
						return list;
					}
				});
				
				JavaPairDStream<String, Integer> counts=words.mapToPair(new PairFunction<String, String, Integer>() {

					@Override						//s내가 자른단어가 들어온다.
					public Tuple2<String, Integer> call(String s) throws Exception {
						
						return new Tuple2<String, Integer>(s, 1);
					}
				});
				
				//sum=>sum+i;
				/*
				 * int sum=0;
				 * int j=0;
				 * for(var i=1;i<=10; i++){
				 * 		j=sum;
				 * 		sum=j+i
				 * }
				 * */
				JavaPairDStream<String, Integer> reduces=counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
					
					@Override
					public Integer call(Integer sum, Integer i) throws Exception {

						return sum+i;
					}
				});
				
				reduces.print();//console에서만 볼 수 있다.
				reduces.saveAsHadoopFiles("hdfs://NameNode:9000/user/spark_ns1/data/twitter", "", Text.class, IntWritable.class, TextOutputFormat.class, jc);
				
				jsc.start();
				jsc.awaitTermination();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//web에서는 쓰레드를 쓸 수 없다. 대신 task를 쓴다.
	public void daumNaverStop() {
		
	}
}
