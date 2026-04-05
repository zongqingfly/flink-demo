package com.fly.flinkcdc;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
import org.apache.flink.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MysqlSourceDemo {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(MysqlSourceDemo.class);


        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 开启 checkpoint :支撑 CDC 的全量+增量无缝切换（对 Flink CDC 2.x+ 特别重要）
        env.enableCheckpointing(3000);

        // 3. 添加 source
        MySqlSource<String> source = MySqlSource.<String>builder()
                .hostname("host.docker.internal")
                .port(13306)
                .username("zxhacker")
                .password("18813015780")
                .databaseList("demo1")
                .tableList("demo1.user")
                .startupOptions(StartupOptions.initial())
                .serverTimeZone("Asia/Shanghai") // 设置服务器时区，确保时间戳正确解析
                .deserializer(new JsonDebeziumDeserializationSchema()) // 将捕获到的变更数据转换为 JSON 字符串
                .build();

        // 4. 从 source 创建 DataStream
        DataStreamSource<String> dataStreamSourceMysql = env.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "MySQL Source"
        );

        // 5. 打印输出
//        dataStreamSourceMysql.print();

        dataStreamSourceMysql.map(record -> {
            logger.info("Received record: {}", record);
            return record;
        }).print();

        // 6. 执行任务
        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
