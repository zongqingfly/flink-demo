package com.fly.flink;

import com.fly.common.utils.PathUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


import java.io.IOException;
import java.net.URISyntaxException;

public class WordCountStreamDemo {
    public static void main(String[] args) throws Exception {
        String filePath = PathUtils.loadClassPath("words.txt");

        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 从文件中读取数据
        DataStreamSource<String> stringDataStreamSource = env.readTextFile(filePath);

        // 3. 进行单词计数: 切分、转换、分组、聚合
        // 3.1 切分
        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2SingleOutputStreamOperator = stringDataStreamSource.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String in, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] words = in.split(" ");
                for (String word : words) {
                    // 3.2 转换
                    Tuple2<String, Integer> wordCountOne = Tuple2.of(word, 1);
                    out.collect(wordCountOne);
                }
            }
        });


        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = tuple2SingleOutputStreamOperator
                .keyBy(tuple -> tuple.f0)// 3.3 分组
                .sum(1);// 3.4 聚合

        // 4. 打印输出
        sum.print();
        // 5. 执行
        env.execute();

    }
}
