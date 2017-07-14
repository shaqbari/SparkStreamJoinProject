package com.sist.spark;

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

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 *    NameNode : Master  =====> local[0]
 *    SecondaryNode : Backup local[1]
 *    DataNode01 :Slave 2
 *    DataNode02 :Slave 3
 *    DataNode03 :Slave 4
 *    DataNode04 :Slave 5
 *    DataNode05 :Slave 6
 *    
 *     => SparkSQL
 */
public class DaumRankTemplete{
   //private JavaStreamingContext jsc;다른곳에서 호출하면 안된다?
   public static void main (String[] args)
   {
	   try
	   {
		   String type="naver";
		   Configuration hconf=new Configuration();
		   hconf.set("fs.default.name", "hdfs://NameNode:9000");
		   JobConf jconf=new JobConf(hconf);
		   SparkConf conf=new SparkConf().setAppName("DaumTwitter").setMaster("local[2]");
		   JavaStreamingContext jsc=new JavaStreamingContext(conf,new Duration(3000));
	      String[] filter={
	    			"",
					"",
					"",
					""
	        };
	      String[] prop={
	        		"twitter4j.oauth.consumerKey",
	        		"twitter4j.oauth.consumerSecret",
	        		"twitter4j.oauth.accessToken",
	        		"twitter4j.oauth.accessTokenSecret"
	        	};
	      for(int i=0;i<4;i++)
	       {
	        System.setProperty(prop[i],filter[i]);
	       }
	      List<String> list=new ArrayList<String>();
	      if(type.equals("daum"))
	       {
	    	  list=RankData.daumRank();
	       }
	      else
	       {
	    	  list=RankData.naverRank();
	       }
	      String[] data=new String[list.size()];
	      int i=0;
	      for(String s:list)
	      {
	    	  data[i]=s;
	    	  System.out.println(s);
	    	  i++;
	      }
	      JavaReceiverInputDStream<Status> tstream=
	    		  TwitterUtils.createStream(jsc,data);
	     
	      JavaDStream<String> datas=tstream.map(new Function<Status, String>() {

			@Override
			public String call(Status status) throws Exception {
				// TODO Auto-generated method stub
				return status.getText();
			}
		   });
	        
	       final Pattern[] p=new Pattern[data.length];
	      	for(i=0;i<p.length;i++)
	      	{
	      		p[i]=Pattern.compile(data[i]);
	      	}
	      	final Matcher[] m=new Matcher[data.length];
      	// 단어 분리 
      	   JavaDStream<String> words=datas.flatMap(new FlatMapFunction<String, String>() {
            List<String> list=new ArrayList<String>();
				@Override
				public Iterable<String> call(String s) throws Exception {
					// TODO Auto-generated method stub
					for(int i=0;i<m.length;i++)
					{
						
						m[i]=p[i].matcher(s);
						while(m[i].find())
						{
							list.add(m[i].group().replace(" ", ""));
						}
					}
					return list;
				}
			});
      	JavaPairDStream<String, Integer> counts=words.mapToPair(new PairFunction<String, String, Integer>() {

				@Override
				public Tuple2<String, Integer> call(String s) throws Exception {
					// TODO Auto-generated method stub
					return new Tuple2<String, Integer>(s, 1);
				}
			});
      	// sum => sum+i
      	/*
      	 *   int sum=0;
      	 *   int j=0;
      	 *   for(int i=1;i<=10;i++)
      	 *   {
      	 *      j=sum;
      	 *      sum=j+i;
      	 *   }
      	 */
      	JavaPairDStream<String, Integer> reduces=counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
				
				@Override
				public Integer call(Integer sum, Integer i) throws Exception {
					// TODO Auto-generated method stub
					return sum+i;
				}
			});
	       reduces.print();
      	    reduces.saveAsHadoopFiles("hdfs://NameNode:9000/user/spark_ns1/"+type, "", Text.class, IntWritable.class, TextOutputFormat.class, jconf);
	       jsc.start();
	       jsc.awaitTermination();
	   }catch(Exception ex)
	   {
		   System.out.println(ex.getMessage());
	   }
   }
   public void daumNaverStop()
   {
	  // jsc.stop();
   }
}
