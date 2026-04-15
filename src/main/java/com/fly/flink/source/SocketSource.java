package com.fly.flink.source;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SocketSource {
    public static void main(String[] args) throws Exception {
        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

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
                .returns(Types.TUPLE(Types.STRING, Types.INT)) // attention: 需要指定返回类型
                .keyBy(tuple -> tuple.f0)// 3.3
                .sum(1)// 3.4 聚合
                .print();

        // 5. 执行
        env.execute();

    }
}
