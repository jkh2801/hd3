package dataexpo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class MultiTypeMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	private String workType;
	private final static IntWritable one = new IntWritable(1);
	private final static IntWritable distance = new IntWritable();
	private Text outkey = new Text();
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		workType = context.getConfiguration().get("workType");
	}


	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		Airline al = new Airline(value);
		if(al.isDistanceAvailable() && al.getDistance() > 0) {
			outkey.set("DI-"+al.getYear() +"-" + al.getMonth());
			distance.set(al.getDistance());
			context.write(outkey, distance);
		}
		switch (workType) {
		case "d": // 지연정보
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() > 0) {
				outkey.set("D-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() > 0) {
				outkey.set("A-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			break;
		case "s": // 정시정보
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() == 0) {
				outkey.set("D-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() == 0) {
				outkey.set("A-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			break;
		case "e": // 초기정보
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() < 0) {
				outkey.set("D-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() < 0) {
				outkey.set("A-" + al.getYear() + "-" + al.getMonth());
				context.write(outkey, one);
			}
			break;
		}
	}
	
	
}
