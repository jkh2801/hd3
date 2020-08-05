package test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MonDelayCntServlet {
	public static void main(String[] args) {
		String input = "d:/ubuntushare/dataexpo/1988.csv";
		String output = "C:/Users/GDJ24/1988_a";
		Configuration conf = new Configuration();
		try {
			Job job = new Job(conf, "MonDelayCntServlet");
			job.setJarByClass(MonDelayCntServlet.class);
			job.setMapperClass(ArrivalDelayMapper.class);
			job.setReducerClass(DelayCountReducer.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			job.waitForCompletion(true);
		}catch (FileAlreadyExistsException e) {
			System.out.println("기존 파일 존재: " + output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
