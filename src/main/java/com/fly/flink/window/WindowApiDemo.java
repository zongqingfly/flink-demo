package com.fly.flink.window;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Duration;

public class WindowApiDemo {
    public static void main(String[] args) throws Exception {
        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 2. 从socket流中读取数据
        DataStreamSource<String> source = env.socketTextStream("localhost", 9999);

        KeyedStream<Tuple2<String, Integer>, String> keyedStream = source.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (in, out) -> {
                    String[] words = in.split("\\s+");
                    for (String word : words) {
                        // 3.2 转换
                        Tuple2<String, Integer> wordCountOne = Tuple2.of(word, 1);
                        out.collect(wordCountOne);
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT)) // attention: 需要指定返回类型
                .keyBy(tuple -> tuple.f0);// 3.3


        // 基于时间的窗口
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = keyedStream.window(TumblingProcessingTimeWindows.of(Duration.ofSeconds(5)));
//        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = keyedStream.window(SlidingProcessingTimeWindows.of(Duration.ofSeconds(5), Duration.ofSeconds(3)));
//        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> window = keyedStream.window(ProcessingTimeSessionWindows.withGap(Duration.ofSeconds(5)));


        // 基于计数的窗口
//        WindowedStream<Tuple2<String, Integer>, String, GlobalWindow> window = keyedStream.countWindow(3);// 滑动滚动窗口
//        WindowedStream<Tuple2<String, Integer>, String, GlobalWindow> window = keyedStream.countWindow(3,1); // 滑动计数窗口
//        keyedStream.window(GlobalWindows.create()) // 全局窗口

        window.sum(1).print();

        // 5. 执行
        env.execute();

    }
}
