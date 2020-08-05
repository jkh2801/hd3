package dataexpo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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


@WebServlet("/MonMultiServlet2")
public class MonMultiDelayServlet2 extends HttpServlet{
	private static final long serialVersionUID = 1L;
	public MonMultiDelayServlet2 () {
		super();
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html; charset=UTF-8");
		String year = req.getParameter("year");
		String kbn = req.getParameter("kbn");
		String input = "d:/ubuntushare/dataexpo/" + year + ".csv";
		String output = req.getSession().getServletContext().getRealPath("/") + "monmultiout2/" + year+"_"+kbn;
		System.out.println(input);
		System.out.println(output);
		Configuration conf = new Configuration();
		conf.set("workType", kbn);
		try {
			Job job = new Job(conf, "MonMultiDelayServlet2");
			FileInputFormat.addInputPath(job, new Path(input));
			FileOutputFormat.setOutputPath(job, new Path(output));
			job.setJarByClass(MonMultiDelayServlet2.class); // getClass() : Object 클래스의 멤버 (해당 객체의 클래스 정보를 리턴)
			job.setMapperClass(MultiTypeMapper.class);
			job.setReducerClass(MultiTypeReducer.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
			MultipleOutputs.addNamedOutput(job, "departure", TextOutputFormat.class, Text.class, IntWritable.class);
			MultipleOutputs.addNamedOutput(job, "arrival", TextOutputFormat.class, Text.class, IntWritable.class);
			MultipleOutputs.addNamedOutput(job, "distance", TextOutputFormat.class, Text.class, IntWritable.class);
			job.waitForCompletion(true);
		} catch (FileAlreadyExistsException e) {
			System.out.println("기존 파일 존재 : " + output);
		} catch (Exception e ) {
			e.printStackTrace();
		}
		String [] files = {"departure-r-00000", "arrival-r-00000", "distance-r-00000"};
		List<Map<String, Integer>> list = new ArrayList<Map<String,Integer>>();
		req.setAttribute("file", year);
		for(String f : files) {
			Path out = new Path(output+"/"+f);
			System.out.println(output+"/"+f);
			FileSystem fs = FileSystem.get(conf);
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(out)));
			Map <String, Integer> map = new TreeMap<String, Integer>((a,b) -> Integer.parseInt(a.split("-")[1]) - Integer.parseInt(b.split("-")[1]));
			String line = null;
			while((line = br.readLine()) != null) {
				String [] v = line.split("\t");
				map.put(v[0].trim(), Integer.parseInt(v[1].trim())); 
			}
			list.add(map);
		}
		String view = "5";
		if(req.getParameter("view").equals("7")) view = "7";
		req.setAttribute("list", list);
		RequestDispatcher dispatcher = req.getRequestDispatcher("/dataexpo/dataexpo" + view + ".jsp");
		dispatcher.forward(req, res);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
	
}
