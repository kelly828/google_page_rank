import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.text.DecimalFormat;


public class MatrixCellSummation {

    public static class PassMapper extends Mapper<Object, Text, Text, DoubleWritable> {

        @Override
        public void map(Object key, Text value, Context context) 
                throws IOException, InterruptedException {

            String[] pageSubrank = value.toString().trim().split("\t");
            double subRank = Double.parseDouble(pageSubrank[1]);
            context.write(new Text(pageSubrank[0]), new DoubleWritable(subRank));
        }
    }

    public static class BetaMapper extends Mapper<Object, Text, Text, DoubleWritable> {

        private float beta;

        @Override
        public void setup(Context context) {
            Configuration conf = context.getConfiguration();
            beta = conf.getFloat("beta", 0.2f);
        }

        @Override
        public void map(Object key, Text value, Context context) 
                throws IOException, InterruptedException {

            String[] pageRank = value.toString().trim().split("\t");
            double betaRank = Double.parseDouble(pageRank[1]) * beta;
            context.write(new Text(pageRank[0]), new DoubleWritable(betaRank));
        }
    }

    public static class SummationReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        @Override
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
                 throws IOException, InterruptedException {


            double sum = 0;
            for (DoubleWritable value : values) {
                sum += value.get();
            }

            DecimalFormat formatter = new DecimalFormat("#.0000");
            sum = Double.valueOf(formatter.format(sum));
            context.write(key, new DoubleWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {

        Path subPageRankPath = new Path(args[0]);
        Path prevPageRankPath = new Path(args[1]);
        Path outputPath = new Path(args[2]);
        float beta = 0.2f;

        if (args.length > 3) {
            beta = Float.parseFloat(args[3]);
        }

        Configuration conf = new Configuration();
        conf.setFloat("beta", beta);

        Job job = Job.getInstance(conf);
        job.setJarByClass(MatrixCellSummation.class);

        ChainMapper.addMapper(job, PassMapper.class, Object.class, Text.class, Text.class, DoubleWritable.class, conf);
        ChainMapper.addMapper(job, BetaMapper.class, Text.class, DoubleWritable.class, Text.class, DoubleWritable.class, conf);

        job.setReducerClass(SummationReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        MultipleInputs.addInputPath(job, subPageRankPath, TextInputFormat.class, PassMapper.class);
        MultipleInputs.addInputPath(job, prevPageRankPath, TextInputFormat.class, BetaMapper.class);

        FileOutputFormat.setOutputPath(job, outputPath);
        job.waitForCompletion(true);
    }

}