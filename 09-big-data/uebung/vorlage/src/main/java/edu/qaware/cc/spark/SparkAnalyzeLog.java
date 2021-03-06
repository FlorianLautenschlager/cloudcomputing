package edu.qaware.cc.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;

/**
 * S simple spark use case to analyze a solr log by counting the queries and sum up the query time
 *
 * @author f.lautenschlager
 */
public class SparkAnalyzeLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkAnalyzeLog.class);

    /*
    * TODO: Set the path to the this directory
    */
    private static final String PATH_TO_YOUR_DIRECTORY = "/home/flo/Development/cloud-computing/cloudcomputing/09-big-data/uebung/loesung";
    private static final String PATH_TO_JAR = PATH_TO_YOUR_DIRECTORY + "/spark-lib/user-classes-for-spark.jar";

    /**
     * TODO: Set the path to your local master. Note: localhost does not work.
     */
    private static final String SPARK_MASTER = "spark://flo-ThinkPad-T440p:7077";

    /**
     * A simple parser for our log file
     * No rocket science ;-)
     */
    private static final Function<String, SolrLog> SOLR_LOG_EXTRACTOR =
            new Function<String, SolrLog>() {
                @Override
                public SolrLog call(String s) throws Exception {

                    //Only requests
                    if (s.contains("QTime")) {
                        String[] splits = s.split(" ");

                        if (splits.length == 13) {
                            String path = substringFromChar(splits[8], '=');
                            String params = substringFromChar(splits[9], '=');
                            String status = substringFromChar(splits[11], '=');
                            String qtime = substringFromChar(splits[12], '=');
                            return new SolrLog(path, params, Integer.parseInt(status), Integer.parseInt(qtime));
                        }

                    }
                    return null;
                }

            };


    private static String substringFromChar(String modifed, char character) {
        return modifed.substring(modifed.indexOf(character) + 1);
    }


    /**
     * TODO: Implement the filtering of solr logs entries that are null
     */
    private static final Function<SolrLog, Boolean> FILTER_SOLR_LOGS_THAT_ARE_NULL =
            new Function<SolrLog, Boolean>() {
                @Override
                public Boolean call(SolrLog solrLog) throws Exception {
                    //Implement
                    return null;
                }
            };

    /**
     * TODO: Implement the filtering of solr logs that are greater zero
     */
    private static final Function<SolrLog, Boolean> FILTER_SOLR_LOGS_GREATER_ZERO =
            new Function<SolrLog, Boolean>() {
                @Override
                public Boolean call(SolrLog solrLog) throws Exception {
                    //Implement
                    return null;
                }
            };
    /**
     * TODO: Implement the mapping fo the solr log entries into a tuple of qtime, occurrence
     */
    private static final PairFunction<SolrLog, Integer, Integer> MAP_SOLR_LOGS_TO_PAIRS =
            new PairFunction<SolrLog, Integer, Integer>() {
                @Override
                public Tuple2<Integer, Integer> call(SolrLog solrLog) throws Exception {
                    //Implement
                    return null;
                }
            };

    /**
     * TODO: Implement the reduce step of the tuples to get the total qtime and occurrence
     */
    private static final Function2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>, Tuple2<Integer, Integer>> REDUCE_SOLR_LOG_PAIRS =
            new Function2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>() {
                @Override
                public Tuple2<Integer, Integer> call(Tuple2<Integer, Integer> first, Tuple2<Integer, Integer> second) throws Exception {
                    //Implement
                    return null;
                }
            };

    /**
     * Remember: run maven install before executing this class
     * <p>
     * Note: Before executing this example, set the correct spark master url and start the cluster!!
     *
     * @param args ignored
     * @throws IOException if bad things happen
     */
    public static void main(String[] args) throws IOException {

        SparkConf conf = new SparkConf()
                .setAppName("Cloud Computing")
                .setMaster(SPARK_MASTER);
        JavaSparkContext jsc = new JavaSparkContext(conf);
        //Required to execute the calculation on each worker
        jsc.addJar(new File(PATH_TO_JAR).getPath());


        //Start the computation below
        long start = System.currentTimeMillis();

        //We read the file from the resources directory
        JavaRDD<String> file = jsc.textFile(SparkWordCount.class.getResource("/solr.log").getPath());

        Tuple2<Integer, Integer> totalDurationAndTime = file.map(SOLR_LOG_EXTRACTOR)
                .filter(FILTER_SOLR_LOGS_THAT_ARE_NULL)
                .filter(FILTER_SOLR_LOGS_GREATER_ZERO)
                .mapToPair(MAP_SOLR_LOGS_TO_PAIRS)
                .reduce(REDUCE_SOLR_LOG_PAIRS);

        int totalDuration = totalDurationAndTime._1();
        int nrOfCalls = totalDurationAndTime._2();

        //Collect them to a map. Executes the whole computation!
        //we are done
        long end = System.currentTimeMillis();


        //Print somt stats
        LOGGER.info("================================================================");
        LOGGER.info("Counted {} requests with a total runtime of {} ms in {} ms", nrOfCalls, totalDuration, end - start);
        LOGGER.info("================================================================");


        //Just for you to open the job ui
        LOGGER.info("================================================================");
        LOGGER.info("Type any key to stop...");
        LOGGER.info("================================================================");
        System.in.read();
    }

    /**
     * A class to represent solr log entries
     */
    static final class SolrLog {

        private final String params;
        private final int status;
        private final int qtime;
        private final String path;

        SolrLog(String path, String params, int status, int qtime) {
            this.path = path;
            this.params = params;
            this.status = status;
            this.qtime = qtime;
        }

        public String getParams() {
            return params;
        }

        public int getStatus() {
            return status;
        }

        public int getQtime() {
            return qtime;
        }

        public String getPath() {
            return path;
        }
    }
}
