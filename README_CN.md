# RequestResponseBinder
```tex
一个用于将观察者模式下的请求行为，支持请求响应模式的框架。
```
[English](README.md)

借由请求响应的客户端模块，将原本需要开发者去监听的行为，转交给当前模块实现绑定，减少模块代码的编写。

![绑定方式](https://raw.githubusercontent.com/Sheedon/RequestResponseBinder/99038d4cfdfd1d1415f18361d69e3c7ffb7d224c/image/structure_chart.svg)



## 一、使用方式

#### 第一步：将 JitPack 存储库添加到您的构建文件中

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```



#### 第二步：添加核心依赖

```groovy
dependencies {
    // 调度库
    implementation 'com.github.Sheedon.RequestResponseBinder:rr-dispatcher:0.1-alpha'
    // android 环境下的超时处理者，java环境下正在开发中
    implementation 'com.github.Sheedon.RequestResponseBinder:rr-timeout-android:0.1-alpha'
}
```



#### 第三步：配置客户端

```java
public class BinderClient extends AbstractClient<String /*反馈主题*/,
        String /*消息ID*/, 
        String /*请求格式*/, 
        TestMessage /*反馈格式*/> {

    
 		/**
     * 创建请求响应的Call
     *
     * @param request 请求对象
     * @return Call 用于执行入队/提交请求的动作
     */
    @Override
    public Call newCall(IRequest<String, String> request) {
        return RealCallWrapper.newCall(this, (Request) request);
    }

    /**
     * 创建信息的观察者 Observable
     * @param request 请求对象
     * @return Observable 订阅某个主题，监听该主题的消息
     */
    @Override
    public Observable newObservable(IRequest<String, String> request) {
        return RealObserverWrapper.newRealObservable(this, (Request) request);
    }

  
    public static class Builder extends AbstractClient.Builder<String, String, String, TestMessage> {
        // 各种其他客户端的配置
        // 例如客户端需要配置监听内容，重连机制等等
      	.....

        @Override
        public BinderClient build() {
          	// 配置执行事件的环境，UI线程/子线程/线程池。。
            if (behaviorServices.isEmpty()) {
                behaviorServices.add(new DefaultEventBehaviorService());
            }
          	// 配置事件管理者
            if (eventManagerPool.isEmpty()) {
                eventManagerPool.add(new DefaultEventManager<>());
            }
          	// 超时执行者
            if (timeoutManager == null) {
                timeoutManager = new TimeOutHandler<>();
            }
          	// 调度适配器 请求适配器+响应监听者
            if (dispatchAdapter == null) {
                dispatchAdapter = new WrapperClient(baseUrl);
            }
          	// 响应适配器 处理反馈操作
            if (responseAdapter == null) {
                responseAdapter = new TestResponseAdapter();
            }
          	// 请求响应调度者
            if (dispatcher == null) {
                dispatcher = new Dispatcher<>(behaviorServices, eventManagerPool,
                        timeoutManager, dispatchAdapter, responseAdapter);
            }
            return builder();
        }
    }

}
```



#### 第四步：构建请求，监听结果

```java
// 构建请求对象
Request request = new Request.RequestBuilder()
  .backTopic("test")
  .delaySecond(10)
  .body("哈哈哈哈")
  .build();

// 通过绑定的客户端获取Call
Call call = binderClient.newCall(request);

// 调度请求
call.enqueue(new Callback() {
  @Override
  public void onFailure(Throwable e) {
  }
  
  @Override
  public void onResponse(Request request, Response response) {
  }
});
```



## 二、调度时序图



![时序图](https://raw.githubusercontent.com/Sheedon/RequestResponseBinder/bcb0cd5831cc140349107af453cc6c3f5c792be8/image/%E6%97%B6%E5%BA%8F%E5%9B%BE.svg)
