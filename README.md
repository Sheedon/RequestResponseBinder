# RequestResponseBinder
```tex
A framework for matching observer mode request behavior to request-response mode.
```
[中文文档](README_CN.md)

By requesting and responding client module, the behavior that should be monitored by the developer is transferred to the current module to achieve binding, reducing the writing of module code.

![绑定方式](https://raw.githubusercontent.com/Sheedon/RequestResponseBinder/99038d4cfdfd1d1415f18361d69e3c7ffb7d224c/image/structure_chart.svg)



## ONE、How to use

#### Step 1: Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2: Add core dependencies

```groovy
dependencies {
    // 调度库
    implementation 'com.github.Sheedon.RequestResponseBinder:rr-dispatcher:0.1-alpha'
  	// android 环境下的超时处理者，java环境下正在开发中
  	implementation 'com.github.Sheedon.RequestResponseBinder:rr-timeout-android:0.1-alpha'
}
```



#### Step 3: Configure Client

```java
public class BinderClient extends AbstractClient<String /*CallTopic*/,
        String /*MessageID*/, 
        String /*request data*/, 
        TestMessage /*response data*/> {

    /**
     * Create a call for a request-response
     *
     * @param request request object
     * @return Call The action used to perform the enqueue submit request
     */
    @Override
    public Call newCall(IRequest<String, String> request) {
        return RealCallWrapper.newCall(this, (Request) request);
    }

    /**
     * An observer Observable that creates information
     * @param request request object
     * @return Observable Subscribe to a topic and listen for messages from that topic
     */
    @Override
    public Observable newObservable(IRequest<String, String> request) {
        return RealObserverWrapper.newRealObservable(this, (Request) request);
    }

  
    public static class Builder extends AbstractClient.Builder<String, String, String, TestMessage> {
        // Various other client configurations
        // For example, the client needs to configure the monitoring content, reconnection mechanism, etc.
      	.....

        @Override
        public BinderClient build() {
          	// Configure the environment for executing events, UI thread sub-thread thread pool. .
            if (behaviorServices.isEmpty()) {
                behaviorServices.add(new DefaultEventBehaviorService());
            }
          	// Configure Event Manager
            if (eventManagerPool.isEmpty()) {
                eventManagerPool.add(new DefaultEventManager<>());
            }
          	// timeout manager
            if (timeoutManager == null) {
                timeoutManager = new TimeOutHandler<>();
            }
          	// Dispatch Adapter: Request Adapter + Response Listener
            if (dispatchAdapter == null) {
                dispatchAdapter = new WrapperClient(baseUrl);
            }
          	// Response Adapter Handling Feedback Actions
            if (responseAdapter == null) {
                responseAdapter = new TestResponseAdapter();
            }
          	// request response dispatcher
            if (dispatcher == null) {
                dispatcher = new Dispatcher<>(behaviorServices, eventManagerPool,
                        timeoutManager, dispatchAdapter, responseAdapter);
            }
            return builder();
        }
    }

}
```



#### Step 4：Build the request and listen for the result

```java
// Build the request object
Request request = new Request.RequestBuilder()
  .backTopic("test")
  .delaySecond(10)
  .body("哈哈哈哈")
  .build();

// Get Call through the bound client
Call call = binderClient.newCall(request);

// scheduling request
call.enqueue(new Callback() {
  @Override
  public void onFailure(Throwable e) {
  }
  
  @Override
  public void onResponse(Request request, Response response) {
  }
});
```



## TWO、Scheduling timing diagram

![时序图](https://raw.githubusercontent.com/Sheedon/RequestResponseBinder/bcb0cd5831cc140349107af453cc6c3f5c792be8/image/%E6%97%B6%E5%BA%8F%E5%9B%BE.svg)
