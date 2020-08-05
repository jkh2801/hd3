package dataexpo;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

public class MultiDelayReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	private MultipleOutputs<Text, IntWritable> mos;
	private Text outKey = new Text();
	private IntWritable result = new IntWritable();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<Text, IntWritable>(context);
	}

	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		String[] columns = key.toString().split("-");
		if (columns[0].contains("M")) {
			outKey.set(columns[1] + "-" + columns[2]);
			if (columns[0].equals("MD")) {
				int sum = 0;
				for (IntWritable v : values)
					sum += v.get();
				result.set(sum);
				mos.write("departure", outKey, result);
			} else {
				int sum = 0;
				for (IntWritable v : values)
					sum += v.get();
				result.set(sum);
				mos.write("arrival", outKey, result);
			}
		}
		if (columns[0].contains("C")) {
			outKey.set(columns[1]);
			if (columns[0].equals("CD")) {
				int sum = 0;
				for (IntWritable v : values)
					sum += v.get();
				result.set(sum);
				mos.write("departure", outKey, result);
			} else {
				int sum = 0;
				for (IntWritable v : values)
					sum += v.get();
				result.set(sum);
				mos.write("arrival", outKey, result);
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		mos.close();
	}

}
