package hbk.stringmatcher.bruteforce.multiplenode;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

public class SuperLongReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, Text> {

    // Char in java is 2 byte, 16 bit
    // Allow maximum of 2M / value pair = 1048576 in string length

    private Text outValue = new Text();
    private static final int MAX_VALUE_LIMIT = 1048576;

    @Override
    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, Text> output, Reporter reporter)
            throws IOException {

        StringBuilder sb = new StringBuilder();
        boolean isFirstElementPassed = false;

        while( values.hasNext() ) {

            if(!isFirstElementPassed) {
                sb.append(values.next().get());
                isFirstElementPassed = true;
            }
            else {
                sb.append(',').append(values.next().get());
            }

            // if sb.length more than limit , collect once
            if(sb.length() > MAX_VALUE_LIMIT) {
                // Debug
                System.out.println("Reduce Output: " + key + "<>" + sb.toString());

                outValue.set(sb.toString());
                output.collect(key, outValue);
                sb = new StringBuilder();
            }
        }

        outValue.set(sb.toString());
        // Debug
        System.out.println("Reduce Output: " + key + "<>" + sb.toString());
        // Key is string, value are offset and fileName
        output.collect(key, outValue);

    }
}