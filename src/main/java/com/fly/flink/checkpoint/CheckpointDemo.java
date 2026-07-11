package com.fly.flink.checkpoint;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.execution.CheckpointType;
import org.apache.flink.core.execution.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CheckpointDemo {

    public static void main(String[] args) throws Exception {
        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE); // 启用检查点
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage("hdfs://hadoop102:8082/flink/checkpoint"); // 设置检查点存储路径
        checkpointConfig.setCheckpointTimeout(5000); // 设置检查点超时时间
        checkpointConfig.setMaxConcurrentCheckpoints(3); // 设置最大并发检查点数

        // 2. 从socket流中读取数据
        DataStreamSource<String> source = env.socketTextStream("localhost", 9999);

        source.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (in, out) -> {
                    String[] words = in.split("\\s+");
                    for (String word : words) {
                        // 3.2 转换
                        Tuple2<String, Integer> wordCountOne = Tuple2.of(word, 1);
                        out.collect(wordCountOne);
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(tuple -> tuple.f0)// 3.3
                .sum(1)// 3.4 聚合
                .print();

        // 5. 执行
        env.execute();

    }
}

