package com.fly.flinkcdc;

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
                "    'hostname' = 'host.docker.internal',\n" +
                "    'port' = '13306',\n" +
                "    'username' = 'zxhacker',\n" +
                "    'password' = '18813015780',\n" +
                "    'database-name' = 'demo1',\n" +
                "    'table-name' = 'user'\n" +
                ")";
        tableEnvironment.executeSql(sourceDDL);

        Table table = tableEnvironment.sqlQuery("select * from mysql_source");

        table.execute().print();
    }

}
