package org.sheedon.rr.core;

/**
 * 请求适配器，主要执行请求数据核实，以及提交请求数据
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 5:10 下午
 */
public interface RequestAdapter<Data> {

    /**
     * 核实请求数据，并且将处理后的请求数据返回
     *
     * @param data 请求数据
     * @return 核实组合后的请求数据
     */
    Data checkRequestData(Data data);

    /**
     * 提交请求的行为
     *
     * @param data 请求数据
     */
    void publish(Data data);

}
