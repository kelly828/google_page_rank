import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MatrixCellMultiplication {

    public static class TransitionMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) 
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            String[] fromTo = line.split("\t");

            if (fromTo.length == 1 || fromTo[1].trim().length() == 0) {
                String info = String.format(
                    "Warning: web page %s has no links to other pages. Origin line: %s", 
                    fromTo[0], line);
                context.getCounter("Warnings", info);
                return;
            }

            String fromId = fromTo[0];
            String[] toIds = fromTo[1].split(",");
            double prob = ((double) 1) / toIds.length;

            for (String toId : toIds) {
                context.write(new Text(fromId), new Text(toId + '=' + prob));
            }
        }
    }

    public static class PRMapper extends Mapper<Object, Text, Text, Text> {

        @Override
        public void map(Object key, Text value, Context context) 
                throws IOException, InterruptedException {

            String[] pr = value.toString().trim().split("\t");
            context.write(new Text(pr[0]), new Text(pr[1]));
        }
    }

    public static class MultiplicationReducer extends Reducer<Text, Text, Text, Text> {

        private float beta;

        @Override
        public void setup(Context context) {
            Configuration conf = context.getConfiguration();
            beta = conf.getFloat("beta", 0.2f);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) 
                throws IOException, InterruptedException {

            List<String> transitionUnits = new ArrayList<>();
            double prUnit = 0;

            for (Text value : values) {
                String val = value.toString();
                if (val.contains("=")) {
                    transitionUnits.add(val);
                } else {
                    prUnit = Double.parseDouble(val);
                }
            }

            for (String unit : transitionUnits) {
                String outputKey = unit.split("=")[0];
                double relation = Double.parseDouble(unit.split("=")[1]);

                // Transition Matrix * PageRank Matrix * (1 - beta)
                String outputVal = String.valueOf(relation * prUnit * (1 - beta));
                context.write(new Text(outputKey), new Text(outputVal));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Path transitionMatrixPath = new Path(args[0]);
        Path pageRankMatrixPath = new Path(args[1]);
        Path outputPath = new Path(args[2]);
        float beta = 0.2f;

        if (args.length > 3) {
            beta = Float.parseFloat(args[3]);
        }

        Configuration conf = new Configuration();
        conf.setFloat("beta", beta);

        Job job = Job.getInstance(conf);
        job.setJarByClass(MatrixCellMultiplication.class);

        ChainMapper.addMapper(job, TransitionMapper.class, Object.class, Text.class, Text.class, Text.class, conf);
        ChainMapper.addMapper(job, PRMapper.class, Object.class, Text.class, Text.class, Text.class, conf);

        job.setReducerClass(MultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        MultipleInputs.addInputPath(job, transitionMatrixPath, TextInputFormat.class, TransitionMapper.class);
        MultipleInputs.addInputPath(job, pageRankMatrixPath, TextInputFormat.class, PRMapper.class);

        FileOutputFormat.setOutputPath(job, outputPath);
        job.waitForCompletion(true);
    }

}