package top.wisely.springsparkcassandra.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkCassandraConfig {

    @Value("${spark.master}")
    String sparkMasterUrl;
    @Value("${cassandra.host}")
    String cassandraHost;
    @Value("${cassandra.keyspace}")
    String cassandraKeyspace;



    @Bean
    public JavaSparkContext javaSparkContext(){
        SparkConf conf = new SparkConf(true)
                .set("spark.cassandra.connection.host", cassandraHost)
//                .set("spark.cassandra.auth.username", "cassandra")
//                .set("spark.cassandra.auth.password", "cassandra")
                .set("spark.submit.deployMode", "client");

        JavaSparkContext context = new JavaSparkContext(sparkMasterUrl, "SparkDemo", conf);
        //context.addJar("/my/work/dir/spark-shared.jar");

        return context;
    }

    @Bean
    public CassandraSQLContext sqlContext(){
        CassandraSQLContext cassandraSQLContext = new CassandraSQLContext(javaSparkContext().sc());
        cassandraSQLContext.setKeyspace(cassandraKeyspace);
        return cassandraSQLContext;
    }
//
//    @Bean
//    public JavaRDD<Person> personTable(){
//        return CassandraJavaUtil.javaFunctions(javaSparkContext()).cassandraTable(cassandraKeyspace, "person",CassandraJavaUtil.mapRowTo(Person.class));
//    }

}
