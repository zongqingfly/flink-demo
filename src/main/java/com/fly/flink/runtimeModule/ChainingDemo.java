package com.fly.flink.runtimeModule;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ChainingDemo {
    public static void main(String[] args) throws Exception {
        // 1. 创建本地环境，webui访问： http://localhost:8081
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);
        env.disableOperatorChaining(); // attention 全局禁用算子链

        // 2. 从socket流中读取数据
        // Tip: 在wsl上server监听：nc -lk 9999
        DataStreamSource<String> source = env.socketTextStream("localhost", 9999);

        // 3.1 切分
        source.flatMap((FlatMapFunction<String, String>) (in, out) -> {
                    String[] words = in.split("\\s+");
                    for (String word : words) {
                        out.collect(word);
                    }
                })
                .startNewChain() // attention 从此算子开始新链，指定 flatMap 与前面断开。
                .returns(Types.STRING) // attention 必须指定返回类型
                .map(word -> Tuple2.of(word, 1))// 3.2 转换
//                .disableChaining() // attention 禁止算子链
                .returns(Types.TUPLE(Types.STRING, Types.INT))// attention 必须指定返回类型
                .keyBy(tuple -> tuple.f0)// 3.3 分组
                .sum(1)// 3.4 聚合
                .print();

        // 5. 执行
        env.execute();

        env.close();
    }
}
