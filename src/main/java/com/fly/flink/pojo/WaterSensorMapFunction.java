package com.fly.flink.pojo;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.types.Row;

public class WaterSensorMapFunction implements MapFunction<String, WaterSensor> {
    @Override
    public WaterSensor map(String value) throws Exception {
        String[] split = value.split(",");
        return new WaterSensor(split[0], Long.parseLong(split[1]), Integer.parseInt(split[2]));
    }

}
