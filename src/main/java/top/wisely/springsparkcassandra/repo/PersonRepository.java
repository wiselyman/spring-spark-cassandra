package top.wisely.springsparkcassandra.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import top.wisely.springsparkcassandra.domain.Person;

@Repository
@Slf4j
public class PersonRepository {
    @Autowired
    CassandraSQLContext cassandraSQLContext;

//    @Autowired
//    JavaRDD<Person> personTable;

    public Long countPerson(){
//        return personTable.count();
        log.info("-----------" +cassandraSQLContext.getCluster() + "-----------");
        log.info("-----------" +cassandraSQLContext.getKeyspace() + "-----------");

        DataFrame people = cassandraSQLContext.sql("select * from person order by id");
        log.info("-----------" +people.count() + "-----------");
        return people.count();
    }

}
