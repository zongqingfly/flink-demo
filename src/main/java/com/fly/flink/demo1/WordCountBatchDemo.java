package com.fly.flink.demo1;

import com.fly.common.utils.PathUtils;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WordCountBatchDemo {
    public static void main(String[] args) throws Exception {
        // 1. 获取环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH); // attention 设置批处理模式

        // 2. 从文件中读取数据
        String filePath = PathUtils.loadClassPath("words.txt");
        FileSource<String> source =
                FileSource.forRecordStreamFormat(
                                new TextLineInputFormat(), new Path(filePath))
                        .build();
        DataStreamSource<String> dataStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");

        // 3. 进行单词计数: 切分、转换、分组、聚合
        // 3.1 切分
        SingleOutputStreamOperator<Tuple2<String, Integer>> tuple2SingleOutputStreamOperator = dataStreamSource
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (in, out) -> {
                    String[] words = in.split(" ");
                    for (String word : words) {
                        // 3.2 转换
                        Tuple2<String, Integer> wordCountOne = Tuple2.of(word, 1);
                        out.collect(wordCountOne);
                    }
                })
                .returns(Types.TUPLE(Types.STRING, Types.INT));// attention 必须指定返回类型;


        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = tuple2SingleOutputStreamOperator
                .keyBy(tuple -> tuple.f0)// 3.3 分组
                .sum(1);// 3.4 聚合

        // 4. 打印输出
        sum.print();
        // 5. 执行
        env.execute();

    }
}
