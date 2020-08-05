package dataexpo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class MultiTypeMapper2 extends Mapper<LongWritable, Text, Text, IntWritable>{
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
			outkey.set("DI-"+al.getUniqueCarrier());
			distance.set(al.getDistance());
			context.write(outkey, distance);
		}
		switch (workType) {
		case "d": // 지연정보
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() > 0) {
				outkey.set("DD-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() == 0) {
				outkey.set("DS-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			if(al.isDepartureDelayAvailable() && al.getDepartureDelayTime() < 0) {
				outkey.set("DE-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			break;
		case "a": // 정시정보
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() > 0) {
				outkey.set("AD-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() == 0) {
				outkey.set("AS-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			if(al.isArriveDelayAvailable() && al.getArriveDelayTime() < 0) {
				outkey.set("AE-" + al.getUniqueCarrier());
				context.write(outkey, one);
			}
			break;
		}
	}
	
	
}
