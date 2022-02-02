package org.sheedon.rr.core

/**
 * 请求适配器，主要执行请求数据核实，以及提交请求数据
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/1/8 5:10 下午
 */
interface RequestAdapter<Data> {

    fun bindSender(sender: IRequestSender)

    /**
     * 核实请求数据，并且将处理后的请求数据返回
     *
     * @param data 请求数据
     * @return 核实组合后的请求数据
     */
    fun checkRequestData(data: Data): Data

    /**
     * 提交请求的行为
     *
     * @param data 请求数据
     */
    fun publish(data: Data): Boolean


    abstract class AbstractRequestImpl<Data> :
        RequestAdapter<Data> {
        protected var sender: IRequestSender? = null

        override fun bindSender(sender: IRequestSender) {
            this.sender = sender
        }

    }
}