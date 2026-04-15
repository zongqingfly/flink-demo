package com.fly.flink.source;

//import org.apache.flink.api.common.eventtime.WatermarkStrategy;
//import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.connector.kafka.source.KafkaSource;
//import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;
import java.util.List;


public class SourceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        /**
         * 集合源
         */
        env.fromData("hello", "flink", "streaming").print();
        List<Integer> data = Arrays.asList(1, 22, 3);
        env.fromData(data).print();

        /**
         * 文件源
         */
//        String filePath = PathUtils.loadClassPath("words.txt");
//        DataStreamSource<String> source = env.readTextFile("words.txt" );
//        FileSource<String> fileSource = FileSource.forRecordStreamFormat(
//                new TextLineInputFormat(),
//                new Path(filePath)
//        ).build();
//        env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "words.txt").print();

        /**
         * Socket 源
         */
//        DataStreamSource<String> source = env.socketTextStream("localhost", 9999).print();

        /**
         * Kafka 源
         */
//        KafkaSource<String> kafkaSource = KafkaSource
//                .<String>builder()
//                .setBootstrapServers("localhost:9092")
//                .setTopics("test")
//                .setGroupId("flink-test")
//                .setValueOnlyDeserializer(new SimpleStringSchema())
//                .setStartingOffsets(OffsetsInitializer.earliest())
//                .build();
//        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka source").print();

        env.execute();
        env.close();
    }
}
