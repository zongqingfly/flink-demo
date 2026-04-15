package com.fly.flink.runtimeModule;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ParallelismDemo {
    public static void main(String[] args) throws Exception {
        // 1. 创建本地环境，webui访问： http://localhost:8081
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(2); // attention 全局指定并行度

        // 2. 从socket流中读取数据
        // Tip: 在wsl上server监听：nc -lk 9999
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
                .setParallelism(3) // attention 指定算子并行度，优先级高于全局指定的并行度
                .keyBy(tuple -> tuple.f0)// 3.3
                .sum(1)// 3.4 聚合
                .print();

        // 5. 执行
        env.execute();

        /**
         *attention 算子优先级: 算子 > env全局设定 > 提交jar时包指定参数 > flink-conf.yaml配置
         */

        env.close();
    }
}
