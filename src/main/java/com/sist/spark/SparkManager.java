package com.sist.spark;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;

import au.com.bytecode.opencsv.CSVReader;
import scala.Tuple2;

public class SparkManager {

	public static void main(String[] args) {
		/*HadoopFileRead h=new HadoopFileRead();
		h.fileRead("daum");
		h.fileRead("naver");
		System.out.println("저장완료");*/
		
		try {
			SparkConf conf=new SparkConf().setAppName("daum").setMaster("local");
			JavaSparkContext sc=new JavaSparkContext(conf);
			JavaRDD<String> files=sc.textFile("./daum/daum.csv");//다시 실행하려면 파일을 지워야한다 aop로 처리
			//csv는 이미 잘려져 있다.
			
			//csv는 다 String으로 읽어온다.
			JavaPairRDD<String, String> daum=files.mapToPair(new PairFunction<String, String, String>() {

				@Override
				public Tuple2<String, String> call(String s) throws Exception {
					
					CSVReader csv=new CSVReader(new StringReader(s));
					
					try {
						String[] d=csv.readNext();
						return new Tuple2<String, String>(d[0], d[1]);
						
					} catch (Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
					}
					
					
					
					
					
					
					
					
					return new Tuple2<String, String>("-1", "1"); //앞의 -1은 데이터가 없다는 뜻이다.
				}
			});
			 files=sc.textFile("./naver/naver.csv");
			   
			  JavaPairRDD<String, String> naver=
					   files.mapToPair(new PairFunction<String,String, String>() {

						@Override
					   public Tuple2<String, String> call(String s) throws Exception {
									// TODO Auto-generated method stub
							CSVReader csv=new CSVReader(new StringReader(s));
							try
							{
								String[] d=csv.readNext();
								return new Tuple2<String, String>(d[0], d[1]);
							}catch(Exception ex)
							{
								System.out.println(ex.getMessage());
							
							}
							return new Tuple2<String, String>("-1", "1");
						}
						
						 
			  });
			  JavaPairRDD<String, Tuple2<String, String>> join=daum.join(naver);
			
			 join.saveAsTextFile("./daum_naver");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
		}
	}

}
