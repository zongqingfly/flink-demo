package com.fly.flinkcdc.mysql;

import com.fly.common.EnvProperties;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class MysqlSourceSQLDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(3000);

        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
        String sourceDDL = "CREATE TABLE mysql_source (\n" +
                "    id BIGINT,\n" +
                "    name STRING,\n" +
                "    sex STRING,\n" +
                "    age INT,\n" +
                "    PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    'connector' = 'mysql-cdc',\n" +
                "    'server-time-zone' = 'Asia/Shanghai',\n" +
                "    'hostname' = '${ip}',\n" +
                "    'port' = '${port}',\n" +
                "    'username' = '${username}',\n" +
                "    'password' = '${password}',\n" +
                "    'database-name' = 'demo1',\n" +
                "    'table-name' = 'user'\n" +
                ")";
        sourceDDL = sourceDDL.replace("${ip}", EnvProperties.ip())
                .replace("${port}", EnvProperties.mysqlPort())
                .replace("${username}", EnvProperties.mysqlUser())
                .replace("${password}", EnvProperties.mysqlPassword());


        tableEnvironment.executeSql(sourceDDL);

        Table table = tableEnvironment.sqlQuery("select * from mysql_source");

        table.execute().print();
    }

}
