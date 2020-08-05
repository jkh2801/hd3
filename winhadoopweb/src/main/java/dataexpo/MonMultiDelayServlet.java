package dataexpo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


@WebServlet("/MonMultiServlet")
public class MonMultiDelayServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	public MonMultiDelayServlet () {
		super();
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=UTF-8");
		String year = req.getParameter("year");
		String input = "d:/ubuntushare/dataexpo/" + year + ".csv";
		String output = req.getSession().getServletContext().getRealPath("/") + "/monmultiout" + year;
		System.out.println(input);
		System.out.println(output);
		Configuration conf = new Configuration();
		try {
			Job job = new Job(conf, "MonMultiDelayServlet");
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			job.setJarByClass(MonMultiDelayServlet.class); // getClass() : Object 클래스의 멤버 (해당 객체의 클래스 정보를 리턴)
			job.setMapperClass(MultiDelayMapper.class);
			job.setReducerClass(MultiDelayReducer.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			MultipleOutputs.addNamedOutput(job, "departure", TextOutputFormat.class, Text.class, IntWritable.class);
			MultipleOutputs.addNamedOutput(job, "arrival", TextOutputFormat.class, Text.class, IntWritable.class);
			job.waitForCompletion(true);
		} catch (FileAlreadyExistsException e) {
			System.out.println("기존 파일 존재 : " + output);
		} catch (Exception e ) {
			e.printStackTrace();
		}
		String departure = "departure-r-00000";
		String arrival = "arrival-r-00000";
		req.setAttribute("file", year);
		Path outD = new Path(output+"/"+departure);
		Path outA = new Path(output+"/"+arrival);
		FileSystem fs = FileSystem.get(conf);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(outD)));
		Map <String, Integer> map1 = new TreeMap<String, Integer>((a,b) -> Integer.parseInt(a.split("-")[1]) - Integer.parseInt(b.split("-")[1]));
		String line = null;
		while((line = br.readLine()) != null) {
			String [] v = line.split("\t");
			map1.put(v[0].trim(), Integer.parseInt(v[1].trim())); 
		}
		req.setAttribute("map1", map1);
		br = new BufferedReader(new InputStreamReader(fs.open(outA)));
		Map <String, Integer> map2 = new TreeMap<String, Integer>((a,b) -> Integer.parseInt(a.split("-")[1]) - Integer.parseInt(b.split("-")[1]));
		line = null;
		while((line = br.readLine()) != null) {
			String [] v = line.split("\t");
			map2.put(v[0].trim(), Integer.parseInt(v[1].trim())); 
		}
		req.setAttribute("map2", map2);
		RequestDispatcher dispatcher = req.getRequestDispatcher("/dataexpo/dataexpo3.jsp");
		dispatcher.forward(req, res);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
	
}
