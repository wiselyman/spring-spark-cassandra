# Spring Boot与Spark、Cassandra集成开发

本文演示以Spark作为分析引擎,Cassandra作为数据存储,而使用Spring Boot来开发驱动程序的示例。

## 1.前置条件

- 安装Spark(本文使用Spark-1.5.1,如安装目录为/opt/spark)
- 安装Cassandra(3.0+)
    - 创建keyspace
    
    ```sql
    CREATE KEYSPACE hfcb WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };
    ```
    
    - 创建table
    
    ```sql
    CREATE TABLE person (
        id text PRIMARY KEY,
        first_name text,
        last_name text
    );
    ```
    
    - 插入测试数据
    
    ```sql
    insert into person (id,first_name,last_name) values('1','wang','yunfei');
    insert into person (id,first_name,last_name) values('2','peng','chao');
    insert into person (id,first_name,last_name) values('3','li','jian');
    insert into person (id,first_name,last_name) values('4','zhang','jie');
    insert into person (id,first_name,last_name) values('5','liang','wei');
    ```
 
## 2.spark-cassandra-connector安装

让Spark-1.5.1能够使用Cassandra作为数据存储，需要加上下面jar包的依赖(示例将包放置于`/opt/spark/managed-lib/`目录，可任意):

```
cassandra-clientutil-3.0.2.jar
cassandra-driver-core-3.1.4.jar
guava-16.0.1.jar
cassandra-thrift-3.0.2.jar 
joda-convert-1.2.jar
joda-time-2.9.9.jar
libthrift-0.9.1.jar
spark-cassandra-connector_2.10-1.5.1.jar
```
在`/opt/spark/conf`目录下，新建`spark-env.sh`文件，输入下面内容

```
SPARK_CLASSPATH=/opt/spark/managed-lib/*
```
## 3.Spring Boot应用开发
- 添加 `spark-cassandra-connector`和`spark`依赖

```xml
		<dependency>
			<groupId>com.datastax.spark</groupId>
			<artifactId>spark-cassandra-connector_2.10</artifactId>
			<version>1.5.1</version>
		</dependency>
		<dependency>
			<groupId>org.apache.spark</groupId>
			<artifactId>spark-core_2.10</artifactId>
			<version>1.5.1</version>
		</dependency>
		<dependency>
			<groupId>org.apache.spark</groupId>
			<artifactId>spark-sql_2.10</artifactId>
			<version>1.5.1</version>
		</dependency>
```

- 在`application.yml`中配置`spark`与`cassandra`路径

```yaml
spark.master: spark://master:7077
cassandra.host: 192.168.1.140
cassandra.keyspace: hfcb
```

此处特别说明`spark://master:7077`是域名形式而不是ip地址，可修改本地`hosts`文件将`master`与`ip`地址映射。
    

- 配置`SparkContext`和`CassandraSQLContext`

```java
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
        return context;
    }

    @Bean
    public CassandraSQLContext sqlContext(){
        CassandraSQLContext cassandraSQLContext = new CassandraSQLContext(javaSparkContext().sc());
        cassandraSQLContext.setKeyspace(cassandraKeyspace);
        return cassandraSQLContext;
    }

}
```

- 简单调用

```java
@Repository
public class PersonRepository {
    @Autowired
    CassandraSQLContext cassandraSQLContext;

    public Long countPerson(){
        DataFrame people = cassandraSQLContext.sql("select * from person order by id");
        return people.count();
    }

}

```

- 启动即可如常规Spring Boot程序一样执行。

- 源码地址:https://github.com/wiselyman/spring-spark-cassandra.git

